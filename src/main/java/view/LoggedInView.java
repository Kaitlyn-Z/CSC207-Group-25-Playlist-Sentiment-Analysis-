package view;

import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.logout.LogoutController;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class LoggedInView extends JPanel implements PropertyChangeListener {

    public static final String VIEW_NAME = "logged in";

    private final LoggedInViewModel loggedInViewModel;
    private LogoutController logoutController;

    // Constructor
    public LoggedInView(LoggedInViewModel viewModel) {
        this.loggedInViewModel = viewModel;
        this.loggedInViewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());

        // ==== TOP / CENTER CONTENT (put whatever you want here) ====
        JLabel title = new JLabel("Spotify Lyric Sentiment Explorer");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        // You can later add playlist panel to CENTER
        JPanel centerPanel = new JPanel();
        centerPanel.add(new JLabel("Main page content goes here"));
        add(centerPanel, BorderLayout.CENTER);

        // ==== BOTTOM BAR WITH LOGOUT BUTTON ====
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutButton = new JButton("Log Out");

        logoutButton.addActionListener(e -> {
            if (logoutController != null) {
                // maybe show confirm dialog
                int choice = JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to log out?",
                        "Confirm Log Out",
                        JOptionPane.YES_NO_OPTION
                );
                if (choice == JOptionPane.YES_OPTION) {
                    logoutController.execute();
                }
            } else {
                // for debugging if something wired wrong
                System.err.println("LogoutController is null, logout not work.");
            }
        });

        bottomBar.add(logoutButton);
        add(bottomBar, BorderLayout.SOUTH);
    }

    // AppBuilder will call this
    public void setLogoutController(LogoutController controller) {
        this.logoutController = controller;
    }

    public String getViewName() {
        return VIEW_NAME;
    }

    public static String getStaticViewName() {
        return VIEW_NAME;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // If your LoggedInViewModel has fields to show
        // like username, playlists etc, update the UI here.
    }

    // For LoginPresenter static call
    public static String getViewNameStatic() {
        return VIEW_NAME;
    }
}

