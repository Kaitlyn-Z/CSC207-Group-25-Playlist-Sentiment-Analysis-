package logout;

import entity.User;
import javax.swing.*;
import java.awt.*;

/*
 * This file contained two public classes in one file which caused compile errors
 * on Windows due to filename mismatches. To keep the change minimal and safe,
 * both types are made package-private and required imports / package declaration
 * are added. Behavior that referenced a non-existing MainFrame type is replaced
 * with neutral calls so the project can compile. Review these changes if you
 * expect UI navigation to use a different type.
 */

final class SessionManager {
    private static User currentUser;

    private SessionManager() { }

    static void login(User user) {
        currentUser = user;
    }

    static void logout() {
        currentUser = null;
    }

    static boolean isLoggedIn() {
        return currentUser != null;
    }

    static User getCurrentUser() {
        return currentUser;
    }
}

class MainPagePanel extends JPanel {
    private final Object parent;

    public MainPagePanel(Object parent) {
        this.parent = parent;

        setLayout(new BorderLayout());


        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutButton = new JButton("Log Out");
        bottomBar.add(logoutButton);

        add(bottomBar, BorderLayout.SOUTH);


        logoutButton.addActionListener(e -> handleLogout());
    }

    private void handleLogout() {
        // 1. confirmation
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out?",
                "Confirm Log Out",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            // 2. clear current user/session
            SessionManager.logout();

            // 3. go back to login page (original code called parent.showLoginPage();
            //    there is no MainFrame type in the repo; to avoid breaking the build
            //    we leave a neutral placeholder here. If you have a specific
            //    application frame type, restore that call accordingly.)
            System.out.println("Requested navigation to login page");

            // 4. show success message
            JOptionPane.showMessageDialog(
                    null,
                    "You have been logged out successfully.",
                    "Logged Out",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
}