package view;

import interface_adapter.login.LoginController;
import interface_adapter.login.LoginViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class LoginView extends JPanel implements ActionListener, PropertyChangeListener {

    private static final String VIEW_NAME = "loginView";

    private LoginController loginController;       // AppBuilder will set this
    private final LoginViewModel loginViewModel;

    private final JTextField inputField = new JTextField(20);
    private final JLabel errorLabel = new JLabel("");
    private final JButton loginButton = new JButton("Log in with Spotify");

    // 🔹 AppBuilder: new LoginView(loginViewModel);
    public LoginView(LoginViewModel loginViewModel) {
        this.loginViewModel = loginViewModel;
        this.loginViewModel.addPropertyChangeListener(this);

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0; c.gridy = 0;
        add(new JLabel("Spotify ID / Code (simulated):"), c);

        c.gridy = 1;
        add(inputField, c);

        c.gridy = 2;
        add(loginButton, c);

        c.gridy = 3;
        errorLabel.setForeground(Color.RED);
        add(errorLabel, c);

        loginButton.addActionListener(this);
    }

    // 🔹 AppBuilder: loginView.setLoginController(loginController);
    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }

    // 🔹 AppBuilder: loginView.getViewName() and LoginView.getViewName()
    public static String getViewName() {
        return VIEW_NAME;
    }

    // (Optional, to satisfy instance calls cleanly)
    public String getViewNameInstance() {
        return VIEW_NAME;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (loginController == null) {
            // Not wired yet, avoid NPE
            return;
        }
        loginController.execute(inputField.getText());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        LoginViewModel.State state = loginViewModel.getState();
        errorLabel.setText(state.error == null ? "" : state.error);
    }
}
