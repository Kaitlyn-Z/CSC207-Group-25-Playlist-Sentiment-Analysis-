package view;

import interface_adapter.analysis.AnalysisController;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.logged_in.PlaylistItem;
import interface_adapter.logged_in.SelectPlaylistController;
import interface_adapter.logged_in.SelectPlaylistPresenter;
import interface_adapter.logout.LogoutController;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Main page shown after user logs in.
 * - Shows a list of playlists
 * - Instruction text on the right
 * - Buttons to refresh, analyze, and log out
 */
public class LoggedInView extends JPanel implements PropertyChangeListener {

    public static final String VIEW_NAME = "logged in";

    private final LoggedInViewModel loggedInViewModel;
    private LogoutController logoutController;
    private SelectPlaylistController selectPlaylistController;
    private AnalysisController analysisController;

    // --- Main UI components ---
    private final DefaultListModel<PlaylistItem> playlistListModel = new DefaultListModel<>();
    private final JList<PlaylistItem> playlistList = new JList<>(playlistListModel);

    private final JButton refreshButton = new JButton("Refresh Playlists");
    private final JButton analyzeButton = new JButton("Analyze Selected");
    private final JButton logoutButton = new JButton("Log Out");

    private final JLabel statusLabel = new JLabel("No playlist selected.");

    // Constructor
    public LoggedInView(LoggedInViewModel viewModel) {
        this.loggedInViewModel = viewModel;
        this.loggedInViewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());
        buildUI();
        wireButtonActions();
    }

    // ---------- UI construction ----------

    private void buildUI() {
        // ===== TOP: Title =====
        JLabel title = new JLabel("Spotify Lyric Sentiment Explorer");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        add(title, BorderLayout.NORTH);

        // ===== CENTER: Playlists area (left) + Instructions (right) =====
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Left side: playlist list inside scroll pane
        playlistList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane playlistScroll = new JScrollPane(playlistList);
        playlistScroll.setBorder(BorderFactory.createTitledBorder("Your Playlists"));

        centerPanel.add(playlistScroll, BorderLayout.CENTER);

        // Right side: instructions
        JTextArea infoArea = new JTextArea(
                "How to use:\n\n" +
                        "1. Select one of your playlists from the list.\n" +
                        "2. Click \"Analyze Selected\" to run lyric sentiment analysis.\n\n" +
                        "Notes:\n" +
                        "- \"Refresh Playlists\" will later be connected to Spotify.\n" +
                        "- \"Analyze Selected\" will later call the Analysis Use Case."
        );
        infoArea.setEditable(false);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        infoArea.setOpaque(false);

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
        infoPanel.add(infoArea, BorderLayout.NORTH);

        centerPanel.add(infoPanel, BorderLayout.EAST);

        add(centerPanel, BorderLayout.CENTER);

        // ===== BOTTOM: Status + buttons =====
        JPanel bottomBar = new JPanel(new BorderLayout());

        // Left: status label
        JPanel leftStatusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftStatusPanel.add(statusLabel);
        bottomBar.add(leftStatusPanel, BorderLayout.WEST);

        // Right: buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(refreshButton);
        buttonPanel.add(analyzeButton);
        buttonPanel.add(logoutButton);

        bottomBar.add(buttonPanel, BorderLayout.EAST);

        add(bottomBar, BorderLayout.SOUTH);

        // Dummy playlists for now (remove when you hook real data)
        addDummyPlaylistsForNow();
    }

    /** TODO: remove this once playlists come from your ViewModel / Spotify use case */
    /* private void addDummyPlaylistsForNow() {
        playlistListModel.addElement("🎵 Chill Vibes");// it won't work now cuz I changed the model from string to playlistitem
        playlistListModel.addElement("🔥 Workout Mix");
        playlistListModel.addElement("🎧 Study Lo-fi");
    }*/

    // ---------- Button behaviour ----------

    private void wireButtonActions() {
        // Update status label, call controller to prepare selected playlist's info
        playlistList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && selectPlaylistController != null) {
                PlaylistItem selected = playlistList.getSelectedValue();
                if (selected != null) {
                    selectPlaylistController.execute(selected.getId(), selected.getName());
                }
            }
        });

        // Refresh playlists (placeholder behavior)
        refreshButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Refresh Playlists clicked.\n" +
                            "Later this will call a use case to fetch playlists from Spotify.",
                    "Refresh",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        // Analyze selected playlist (placeholder)
        analyzeButton.addActionListener(e -> {
            var state = loggedInViewModel.getState();

            if (state.selectedPlaylist == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please select a playlist first.",
                        "No Playlist Selected",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            var playlist = state.selectedPlaylist;

            analysisController.execute(
                    playlist.getPlaylistId(),
                    playlist.getPlaylistName(),
                    playlist.getSongs()
            );
        });


        // Log out (fully wired)
        logoutButton.addActionListener(e -> {
            if (logoutController != null) {
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
                System.err.println("LogoutController is null, logout not work.");
            }
        });
    }

    // ---------- Wiring from AppBuilder ----------

    public void setLogoutController(LogoutController controller) {
        this.logoutController = controller;
    }

    public String getViewName() {
        return VIEW_NAME;
    }

    public static String getStaticViewName() {
        return VIEW_NAME;
    }

    // Alias for your existing LoginPresenter code
    public static String getViewNameStatic() {
        return VIEW_NAME;
    }

    public void setSelectPlaylistController(SelectPlaylistController controller) {
        this.selectPlaylistController = controller;
    }

    // ---------- Reacting to ViewModel changes ----------

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        var state = loggedInViewModel.getState();
        statusLabel.setText(state.statusMessage);
        // Later, when LoggedInViewModel has playlist data or displayName,
        // you can update statusLabel and playlistListModel here.

        // Example (pseudo-code, only if your State has these fields):
        //
        // var state = loggedInViewModel.getState();
        // statusLabel.setText("Welcome, " + state.displayName + "!");
        // playlistListModel.clear();
        // for (String name : state.playlistNames) {
        //     playlistListModel.addElement(name);
        // }
    }
}
