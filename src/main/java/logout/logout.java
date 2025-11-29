public final class SessionManager {
    private static User currentUser;

    private SessionManager() { }

    public static void login(User user) {
        currentUser = user;
    }

    public static void logout() {
        currentUser = null;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }
}

public class MainPagePanel extends JPanel {
    private final MainFrame parent;

    public MainPagePanel(MainFrame parent) {
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

            // 3. go back to login page
            parent.showLoginPage();

            // 4. show success message
            JOptionPane.showMessageDialog(
                    parent,
                    "You have been logged out successfully.",
                    "Logged Out",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
}