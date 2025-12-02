package view;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import entity.Playlist;
import interface_adapter.analysis.AnalysisController;
import interface_adapter.analysis.AnalysisState;
import interface_adapter.analysis.AnalysisViewModel;
import interface_adapter.ViewManagerModel; // Added import
import interface_adapter.logged_in.LoggedInViewModel; // Added import
import view.LoggedInView; // Added import

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Map;


/**
 * The main view for viewing the Lyric Sentiment Analysis Summary.
 * It observes the AnalysisViewModel for state changes and updates the UI accordingly.
 */
public class AnalysisView extends JPanel implements ActionListener, PropertyChangeListener {

    // --- 1. Song Data Structure and Hardcoded Playlist ---
    // TODO: replace with info from user's spotify account
    private final String PLAYLIST_NAME = "Sample Playlist";
    private final List<Map<String, String>> playlistSongs = List.of(
            Map.of("title", "Become the Warm Jets", "artist", "Green Day (Mock)", "lyrics", "I walk the lonely road, the only one that I have ever known."),
            Map.of("title", "Sunshine", "artist", "Artist X", "lyrics", "The sun shines bright, making everything feel right. A smile on my face, winning the race."),
            Map.of("title", "Midnight Rain", "artist", "Artist Y", "lyrics", "Silent streets, a hidden tear. Waiting for the light to appear. Every night, the same old fear."),
            Map.of("title", "Upbeat Track 4", "artist", "The Band", "lyrics", "Dancing all night, feeling so good, everything is alright."),
            Map.of("title", "Mellow Tune 5", "artist", "Solo Singer", "lyrics", "Softly falling snow, watching the garden grow, a peaceful day to know."),
            Map.of("title", "Rock Anthem 6", "artist", "The Noise Makers", "lyrics", "Loud guitars scream, living out the impossible dream, freedom is the theme.")
    );
    private final Playlist playlist;

    private static final String VIEW_NAME = "analysis";

    // Components
    private final JLabel playlistNameLabel;
    private final JList<Map<String, String>> songList;

    // Dependencies
    private AnalysisController analysisController;
    private final AnalysisViewModel analysisViewModel;
    private final ViewManagerModel viewManagerModel;
    private final LoggedInViewModel loggedInViewModel;

    private final JButton analyzeButton;
    private final JButton backButton;
    private final SentimentPanel sentimentPanel = new SentimentPanel();

    // 🔹 AppBuilder: loginView.getViewName() and LoginView.getViewName()
    public static String getViewName() {
        return VIEW_NAME;
    }

        public AnalysisView(AnalysisViewModel analysisViewModel,

                            ViewManagerModel viewManagerModel,

                            LoggedInViewModel loggedInViewModel) {

            this.analysisViewModel = analysisViewModel;

            this.analysisViewModel.addPropertyChangeListener(this);

            this.viewManagerModel = viewManagerModel;

            this.loggedInViewModel = loggedInViewModel;

    

            // Create JsonArray for Playlist entity

            JsonArray songsJsonArray = new JsonArray();

            for (Map<String, String> song : playlistSongs) {

                JsonObject songJson = new JsonObject();

                songJson.addProperty("title", song.get("title"));

                songJson.addProperty("artist", song.get("artist"));

                songsJsonArray.add(songJson);

            }

            this.playlist = new Playlist("sample-id", PLAYLIST_NAME, songsJsonArray);

    

    

            // Initialize new components

            this.playlistNameLabel = new JLabel(PLAYLIST_NAME);

            this.backButton = new JButton("Back");

    

            // Setup JList and its model

            DefaultListModel<Map<String, String>> songListModel = new DefaultListModel<>();

            for (Map<String, String> song : playlistSongs) {

                songListModel.addElement(song);

            }

        this.songList = new JList<>(songListModel);
        this.songList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.songList.setLayoutOrientation(JList.VERTICAL);
        this.songList.setVisibleRowCount(5);

        // NEW: Set the custom renderer for column formatting and separators
        this.songList.setCellRenderer(new SongListCellRenderer());

        // Initialize the "Analyze Sentiment" button
        this.analyzeButton = new JButton(AnalysisViewModel.ANALYZE_BUTTON_LABEL);
        this.setLayout(new BorderLayout(10, 10));

        // --- Top Header Panel (Playlist Name and Back Button) ---
        JPanel topHeaderPanel = new JPanel(new BorderLayout());
        playlistNameLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        playlistNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topHeaderPanel.add(playlistNameLabel, BorderLayout.CENTER);

        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        backButtonPanel.add(backButton);
        topHeaderPanel.add(backButtonPanel, BorderLayout.EAST);

        // --- 2. Input Panel
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Container for the song list, its title, and the new headers
        JPanel listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.add(new JLabel("Songs in Playlist:"));
        listContainer.add(Box.createVerticalStrut(5));

        // Header Panel for Title and Artist columns
        JPanel headerPanel = new JPanel(new GridLayout(1, 2));
        headerPanel.setMaximumSize(new Dimension(400, 20));

        JLabel titleHeader = new JLabel("TITLE");
        titleHeader.setFont(new Font("SansSerif", Font.BOLD, 12));
        titleHeader.setHorizontalAlignment(SwingConstants.LEFT);
        titleHeader.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

        JLabel artistHeader = new JLabel("ARTIST");
        artistHeader.setFont(new Font("SansSerif", Font.BOLD, 12));
        artistHeader.setHorizontalAlignment(SwingConstants.RIGHT);
        artistHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));

        headerPanel.add(titleHeader);
        headerPanel.add(artistHeader);
        headerPanel.setBackground(new Color(230, 230, 230));
        listContainer.add(headerPanel);

        // Wrap JList in JScrollPane for scrolling
        JScrollPane scrollPane = new JScrollPane(songList);
        scrollPane.setPreferredSize(new Dimension(400, 120));
        listContainer.add(scrollPane);

        inputPanel.add(listContainer, BorderLayout.CENTER);

        // Button setup
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(analyzeButton);
        inputPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Attach action listener to this class instance (analyzeButton)
        analyzeButton.addActionListener(this);
        
        // Attach action listener to the back button
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource().equals(backButton)) {
                    viewManagerModel.setState(LoggedInView.VIEW_NAME);
                    viewManagerModel.firePropertyChange();
                }
            }
        });

        // --- 3. Visualization Panel ---
        // SentimentPanel is already initialized

        // --- Assemble View ---
        this.add(topHeaderPanel, BorderLayout.NORTH);
        this.add(inputPanel, BorderLayout.CENTER);
        this.add(sentimentPanel, BorderLayout.SOUTH);

        // Initial update
        updateViewFromState(analysisViewModel.getState());
    }

    /**
     * Custom List Cell Renderer to display Title and Artist in two columns
     * and add a line separator below each item.
     */
    private static class SongListCellRenderer extends JPanel implements ListCellRenderer<Map<String, String>> {

        private final JLabel titleLabel;
        private final JLabel artistLabel;
        private final JPanel contentPanel;
        private final JPanel separator;

        public SongListCellRenderer() {
            // Use BorderLayout for left/right alignment (Title/Artist)
            setLayout(new BorderLayout());

            // Sub-panel to hold the content (Title/Artist)
            contentPanel = new JPanel(new GridLayout(1, 2));

            titleLabel = new JLabel();
            artistLabel = new JLabel();

            titleLabel.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
            artistLabel.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
            artistLabel.setHorizontalAlignment(SwingConstants.RIGHT);

            contentPanel.add(titleLabel);
            contentPanel.add(artistLabel);
            contentPanel.setOpaque(true);

            add(contentPanel, BorderLayout.CENTER);

            // Separator line at the bottom
            separator = new JPanel();
            separator.setBackground(Color.LIGHT_GRAY);
            separator.setPreferredSize(new Dimension(1, 1));
            add(separator, BorderLayout.SOUTH);
        }

        @Override
        public Component getListCellRendererComponent(
                JList<? extends Map<String, String>> list,
                Map<String, String> song,
                int index,
                boolean isSelected,
                boolean cellHasFocus)
        {
            titleLabel.setText(song.get("title"));
            artistLabel.setText(song.get("artist"));

            // Determine colors
            Color background = isSelected ? list.getSelectionBackground() : list.getBackground();
            Color foreground = isSelected ? list.getSelectionForeground() : list.getForeground();

            // Apply colors
            this.setBackground(background);
            contentPanel.setBackground(background);
            titleLabel.setBackground(background);
            artistLabel.setBackground(background);

            titleLabel.setForeground(foreground);
            artistLabel.setForeground(foreground);

            setEnabled(list.isEnabled());
            setFont(list.getFont());

            return this;
        }
    }


    /**
     * Setter required to inject the controller after the view is constructed.
     * @param analysisController The controller instance.
     */
    public void setAnalysisController(AnalysisController analysisController) {
        this.analysisController = analysisController;
    }

    /**
     * Handles button clicks.
     * @param e the ActionEvent to react to
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!e.getSource().equals(analyzeButton)) {
            return;
        }

        // Make sure the controller is wired
        if (this.analysisController == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Application is not fully initialized. Analysis controller is missing.",
                    "System Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Call the controller with the playlist info and songs JsonArray (without lyrics)
        // The backend interactor is responsible for fetching the lyrics based on song title and artist.
        analysisController.execute(playlist.getPlaylistId(), playlist.getPlaylistName(), playlist.getSongs());
    }


    /**
     * Listener for changes in the AnalysisViewModel's state.
     * @param evt The property change event.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        AnalysisState state = (AnalysisState) evt.getNewValue();
        updateViewFromState(state);
    }

    /**
     * Updates the UI components based on the current state of the ViewModel.
     * @param state The current analysis state.
     */
    private void updateViewFromState(AnalysisState state) {
        // Update button and loading state
        analyzeButton.setEnabled(!state.isLoading());
        if (state.isLoading()) {
            analyzeButton.setText("Analyzing...");
            sentimentPanel.setLoading(true);
        } else {
            analyzeButton.setText(AnalysisViewModel.ANALYZE_BUTTON_LABEL);
            sentimentPanel.setLoading(false);
        }

        // Update the visualization panel with the result
        sentimentPanel.setResult(state.getResult());

        // Handle errors
        if (state.getErrorMessage() != null && !state.isLoading()) {
            // Note: The Presenter already shows a JOptionPane, so we primarily focus on clearing state here.
            // If the Presenter wasn't showing the dialog, we would place the JOptionPane.showMessageDialog call here.
            System.err.println("Analysis Error displayed: " + state.getErrorMessage());
            // Clear the error message after handling it
            state.setErrorMessage(null);
        }
    }

}