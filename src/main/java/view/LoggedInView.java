package view;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import data_access.AnalysisStatsDataAccessObject;
import entity.Playlist;
import interface_adapter.analysis.AnalysisController;
import interface_adapter.analysis.AnalysisViewModel;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.logged_in.PlaylistItem;
import interface_adapter.logged_in.SelectPlaylistController;
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
        private final AnalysisViewModel analysisViewModel; // Added
        private LogoutController logoutController;
        private SelectPlaylistController selectPlaylistController;
        private AnalysisController analysisController;
    
        // --- Main UI components ---
        private final DefaultListModel<PlaylistItem> playlistListModel = new DefaultListModel<>();
        private final JList<PlaylistItem> playlistList = new JList<>(playlistListModel);
    
        private final JButton refreshButton = new JButton("Refresh Playlists");
        private final JButton analyzeButton = new JButton("Analyze Selected");
        private final JButton showStatsButton = new JButton("Show Stats");
        private final JButton logoutButton = new JButton("Log Out");
    
        private final JLabel statusLabel = new JLabel("No playlist selected.");
        private Playlist samplePlaylist;
    
        // Constructor
        public LoggedInView(LoggedInViewModel loggedInViewModel, AnalysisViewModel analysisViewModel) { // Modified
            this.loggedInViewModel = loggedInViewModel;
            this.analysisViewModel = analysisViewModel; // Added
            this.loggedInViewModel.addPropertyChangeListener(this);
    
            setLayout(new BorderLayout());
            buildUI();
            wireButtonActions();
        }
    
        // ... (buildUI method is unchanged) ...
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
            buttonPanel.add(showStatsButton);
            buttonPanel.add(logoutButton);
    
            bottomBar.add(buttonPanel, BorderLayout.EAST);
    
            add(bottomBar, BorderLayout.SOUTH);
    
            addSamplePlaylist();
        }
    
        private void addSamplePlaylist() {
            JsonArray songs = new JsonArray();
            songs.add(createSong("Riptide", "Vance Joy"));
            songs.add(createSong("Let Her Go", "Passenger"));
            songs.add(createSong("Hey There Delilah", "The Plain White T's"));
            this.samplePlaylist = new Playlist("sample-id", "Sample Playlist", songs);
    
            playlistListModel.addElement(new PlaylistItem(samplePlaylist.getPlaylistId(), samplePlaylist.getPlaylistName()));
        }
    
        private JsonObject createSong(String title, String artist) {
            JsonObject song = new JsonObject();
            song.addProperty("title", title);
            song.addProperty("artist", artist);
            return song;
        }
    
        private JsonObject createSong(String title, String artist, String lyrics) {
            JsonObject song = createSong(title, artist);
            song.addProperty("lyrics", lyrics);
            return song;
        }
    
        // ---------- Button behaviour ----------
    
        private void wireButtonActions() {
            // Update status label, call controller to prepare selected playlist's info
            playlistList.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting() && selectPlaylistController != null) {
                    PlaylistItem selected = playlistList.getSelectedValue();
                    if (selected != null) {
                        // Special case for the sample playlist
                        if (samplePlaylist != null && selected.getId().equals(samplePlaylist.getPlaylistId())) {
                            loggedInViewModel.setSelectedPlaylist(samplePlaylist);
                        } else {
                            // For real playlists, use the controller
                            selectPlaylistController.execute(selected.getId(), selected.getName());
                        }
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
    
            // Analyze selected playlist
            analyzeButton.addActionListener(e -> {
                var loggedInState = loggedInViewModel.getState();
                var playlist = loggedInState.selectedPlaylist;

                if (playlist == null) {
                    JOptionPane.showMessageDialog(this, "Please select a playlist first.", "No Playlist Selected", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // --- Part 1: Immediately update UI to "Loading" state and show the dialog ---

                var analysisState = analysisViewModel.getState();
                analysisState.setLoading(true);
                analysisState.setPlaylistName(playlist.getPlaylistName());
                // Pass the songs to the state so the view can display them immediately
                analysisState.setSongs(playlist.getSongs());
                analysisViewModel.firePropertyChanged();

                AnalysisView analysisView = new AnalysisView(analysisViewModel);
                JDialog analysisDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Analysis Results");
                analysisDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                analysisDialog.setContentPane(analysisView);

                analysisDialog.pack();
                analysisDialog.setLocationRelativeTo(this);
                analysisDialog.setVisible(true);

                // --- Part 2: Run the actual analysis in a background thread ---

                SwingWorker<Void, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        if (samplePlaylist != null && playlist.getPlaylistId().equals(samplePlaylist.getPlaylistId())) {
                            JsonArray songsWithLyrics = new JsonArray();
                            songsWithLyrics.add(createSong("Riptide", "Vance Joy", "I was scared of dentists and the dark I was scared of girls and starting conversations Oh, all my friends are turning green You're the magician's assistant in their dream Ah, ooh, ooh, ooh Ah, ooh, ooh, ooh And they come unstuck Lady, running down to the riptide Taken away to the dark side I wanna be your left hand man I love you when you're singing that song and I got a lump in my throat 'Cause you're gonna sing the words wrong Ah, ooh, ooh, ooh Ah, ooh, ooh, ooh I just wanna, I just wanna know If you're gonna stay, I gotta know I can't have it, I can't have it any other way I swear she's destined for the screen Closest thing to a G-d that I have ever seen Ah, ooh, ooh, ooh Ah, ooh, ooh, ooh Lady, running down to the riptide Taken away to the dark side I wanna be your left hand man I love you when you're singing that song and I got a lump in my throat 'Cause you're gonna sing the words wrong Ah, ooh, ooh, ooh Ah, ooh, ooh, ooh I just wanna, I just wanna know If you're gonna stay, I gotta know I can't have it, I can't have it any other way So, become my left hand man We'll become one with the Riptide Ah, ooh, ooh, ooh Ah, ooh, ooh, ooh (Oh, lady, running down to the riptide, taken away to the dark side) Ah, ooh, ooh, ooh Ah, ooh, ooh, ooh (I wanna be your left hand man) Ah, ooh, ooh, ooh Ah, ooh, ooh, ooh (I love you when you're singing that song and I got a lump in my throat) Ah, ooh, ooh, ooh Ah, ooh, ooh, ooh ('Cause you're gonna sing the words wrong) Lady, running down to the riptide Taken away to the dark side I wanna be your left hand man I love you when you're singing that song and I got a lump in my throat 'Cause you're gonna sing the words wrong I just wanna, I just wanna know If you're gonna stay, I gotta know, I can't have it, I can't have it any other way"));
                            songsWithLyrics.add(createSong("Let Her Go", "Passenger", "Well, you only need the light when it's burning low Only miss the sun when it starts to snow Only know you love her when you let her go Only know you've been high when you're feeling low Only hate the road when you're missing home Only know you love her when you let her go And you let her go Staring at the bottom of your glass Hoping one day you'll make a dream last But dreams come slow and they go so fast You see her when you close your eyes Maybe one day you'll understand why Everything you touch, oh, it dies 'Cause you only need the light whenit's burning low Only miss the sun when it starts to snow Only know you love her when you let her go Only know you've been high when you're feeling low Only hate the road when you're missing home Only know you love her when you let her go Staring at the ceiling in the dark Same old empty feeling in your heart 'Cause love comes slow and it goes so fast Well, you see her when you fall asleep But never get to keep all the promises you make Pffft, and you'll lie 'Cause you only need the light when it's burning low Only miss the sun when it starts to snow Only know you love her when you let her go Only know you've been high when you're feeling low Only hate the road when you're missing home Only know you love her when you let her go And you let her go Woah-oh-oh Woah-oh-oh Woah-oh-oh Woah-oh-oh Woah-oh-oh Woah-oh-oh Woah-oh-oh 'Cause you only need the light when it's burning low Only miss the sun when it starts to snow Only know you love her when you let her go Only know you've been high when you're feeling low Only hate the road when you're missing home Only know you love her when you let her go 'Cause you only need the light when it's burning low Only miss the sun when it starts to snow Only know you love her when you let her go Only know you've been high when you're feeling low Only hate the road when you're missing home Only know you love her when you let her go Let her go"));
                            songsWithLyrics.add(createSong("Hey There Delilah", "Plain White T's", "Hey there Delilah What's it like in New York City? I'm a thousand miles away But girl, tonight you look so pretty Yes, you do Time's Square can't shine as bright as your divine I swear it's true Hey there Delilah Don't you worry about the distance I'm right there if you get lonely Give this song another listen Close your eyes Listen to my voice, it's my disguise I'm by your side Oh, it's what you do to me Oh, it's what you do to me Oh, it's what you do to me Oh, it's what you do to me What you do Hey there Delilah I know times are getting hard But just believe me, girl, someday I'll pay the bills with this guitar We'll have it all We'll have what we deserve Delilah's theme song starts with a D and ends with a love Oh, it's what you do to me Oh, it's what you do to me Oh, it's what you do to me Oh, it's what you do to me Ten years ago, I never thought I'd be talking to you right here right now But I just got so much to say, man, I'm trying to play it cool Hey there Delilah I've got so much left to say If every simple song I wrote to you Would take your breath away I'd write it all Even more in love with me you'd fall We'd have it all Oh, it's what you do to me Oh, it's what you do to me Oh, it's what you do to me Oh, it's what you do to me What you do Hey there Delilah What's it like in New York City? I'm a thousand miles away But girl, tonight you look so pretty Yes, you do Time's Square can't shine as bright as your divine I swear it's true"));

                            analysisController.execute(
                                playlist.getPlaylistId(),
                                playlist.getPlaylistName(),
                                songsWithLyrics
                            );
                        } else {
                            analysisController.execute(
                                playlist.getPlaylistId(),
                                playlist.getPlaylistName(),
                                playlist.getSongs()
                            );
                        }
                        return null;
                    }

                    @Override
                    protected void done() {
                        // The presenter will update the view model when the analysis is complete.
                        // We could add error handling here if the worker throws an exception.
                        try {
                            get(); // Call get() to retrieve the result and propagate exceptions
                        } catch (Exception ex) {
                            // The presenter should have already handled this, but as a fallback:
                            var currentState = analysisViewModel.getState();
                            currentState.setLoading(false);
                            currentState.setErrorMessage("An unexpected error occurred during analysis: " + ex.getMessage());
                            analysisViewModel.firePropertyChanged();
                        }
                    }
                };

                worker.execute();
            });

            // Show stats button
            showStatsButton.addActionListener(e -> {
                AnalysisStatsDataAccessObject statsDAO = new AnalysisStatsDataAccessObject("analysis_stats.json");
                int count = statsDAO.getAnalyzedPlaylistsCount();
                JOptionPane.showMessageDialog(this, "Number of playlists analyzed: " + count, "Analysis Statistics", JOptionPane.INFORMATION_MESSAGE);
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
    
            public void setAnalysisController(AnalysisController controller) { // Added
                this.analysisController = controller;
            }
        
            public void setLogoutController(LogoutController controller) {
                this.logoutController = controller;
            }
        
            public String getViewName() {        return VIEW_NAME;
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
