package view;

import app.SpotifyAuthConfig;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class LoginView extends JPanel implements ActionListener, PropertyChangeListener {

    private static final String VIEW_NAME = "login";

    private LoginController loginController;       // AppBuilder will set this
    private final LoginViewModel loginViewModel;

    private final JLabel infoLabel = new JLabel(
            "<html>Click the button to log in with Spotify.<br>" +
                    "After you are redirected, copy the full URL from your browser<br>" +
                    "and paste it when asked.</html>"
    );
    private final JLabel errorLabel = new JLabel("");
    private final JButton loginButton = new JButton("Log in with Spotify");

    public LoginView(LoginViewModel loginViewModel) {
        this.loginViewModel = loginViewModel;
        this.loginViewModel.addPropertyChangeListener(this);

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.gridx = 0;
        c.anchor = GridBagConstraints.CENTER;

        c.gridy = 0;
        add(infoLabel, c);

        c.gridy = 1;
        add(loginButton, c);

        c.gridy = 2;
        errorLabel.setForeground(Color.RED);
        add(errorLabel, c);

        loginButton.addActionListener(this);
    }

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }

    public static String getViewName() {
        return VIEW_NAME;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (loginController == null) {
            System.err.println("LoginController not wired.");
            return;
        }

        try {
            // 1) Ensure Spotify config is valid
            SpotifyAuthConfig.validate();

            // 2) Build authorize URL
            String state = "fixed-state"; // TODO: randomize for real CSRF protection if you want

            String authorizeUrl = "https://accounts.spotify.com/authorize"
                    + "?response_type=code"
                    + "&client_id=" + URLEncoder.encode(SpotifyAuthConfig.CLIENT_ID, StandardCharsets.UTF_8)
                    + "&redirect_uri=" + URLEncoder.encode(SpotifyAuthConfig.REDIRECT_URI, StandardCharsets.UTF_8)
                    + "&scope=" + URLEncoder.encode(SpotifyAuthConfig.SCOPES, StandardCharsets.UTF_8)
                    + "&state=" + URLEncoder.encode(state, StandardCharsets.UTF_8);

            // 3) Open browser
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(URI.create(authorizeUrl));
            } else {
                // fallback: show URL so user can paste manually into browser
                JOptionPane.showMessageDialog(
                        this,
                        "Open this URL in your browser to log in:\n" + authorizeUrl,
                        "Open Spotify Login",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

            // 4) Ask user to paste the URL they were redirected to
            String pastedUrl = JOptionPane.showInputDialog(
                    this,
                    "After Spotify redirects, copy the full URL from your browser's address bar\n" +
                            "and paste it here:",
                    "Paste Redirect URL",
                    JOptionPane.PLAIN_MESSAGE
            );

            if (pastedUrl == null || pastedUrl.trim().isEmpty()) {
                return; // user cancelled or gave nothing
            }

            String code = extractCodeFromRedirectUrl(pastedUrl);
            if (code == null || code.isBlank()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Could not find 'code' parameter in the pasted URL.\n" +
                                "Make sure you pasted the full redirect URL from the address bar.",
                        "Code Not Found",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // 5) Hand the authorization code to the Login use case
            loginController.execute(code);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Error starting Spotify login: " + ex.getMessage(),
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Extracts the 'code' query parameter from the pasted redirect URL.
     */
    private String extractCodeFromRedirectUrl(String pastedUrl) {
        try {
            URI uri = URI.create(pastedUrl.trim());
            String query = uri.getQuery();
            if (query == null) {
                return null;
            }

            for (String pair : query.split("&")) {
                String[] kv = pair.split("=", 2);
                if (kv.length == 2 && kv[0].equals("code")) {
                    return URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        LoginViewModel.State state = loginViewModel.getState();
        errorLabel.setText(state.error == null ? "" : state.error);
    }
}
