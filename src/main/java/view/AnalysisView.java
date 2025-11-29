package view;

import entity.Playlist;
import interface_adapter.analysis.AnalysisController;
import interface_adapter.analysis.AnalysisState;
import interface_adapter.analysis.AnalysisViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The main view for viewing the Lyric Sentiment Analysis Summary.
 * It observes the AnalysisViewModel for state changes and updates the UI accordingly.
 */
public class AnalysisView extends JPanel implements ActionListener, PropertyChangeListener {

    public static final String VIEW_NAME = "analysis";

    // --- 1. Song Data Structure and Hardcoded Playlist ---
    private static class Song {
        String title;
        String artist;
        String lyrics;

        public Song(String title, String artist, String lyrics) {
            this.title = title;
            this.artist = artist;
            this.lyrics = lyrics;
        }
    }

    // TODO: replace with info from user's spotify account
    private final String PLAYLIST_NAME = "Sample Playlist";
    private final List<Song> playlistSongs = List.of(
            new Song("Become the Warm Jets", "Green Day (Mock)", "I walk the lonely road, the only one that I have ever known. Don't know where it goes, but it's home to me and I walk alone."),
            new Song("Sunshine", "Artist X", "The sun shines bright, making everything feel right. A smile on my face, winning the race."),
            new Song("Midnight Rain", "Artist Y", "Silent streets, a hidden tear. Waiting for the light to appear. Every night, the same old fear."),
            new Song("Upbeat Track 4", "The Band", "Dancing all night, feeling so good, everything is alright."),
            new Song("Mellow Tune 5", "Solo Singer", "Softly falling snow, watching the garden grow, a peaceful day to know."),
            new Song("Rock Anthem 6", "The Noise Makers", "Loud guitars scream, living out the impossible dream, freedom is the theme.")
    );


    // Components
    private final JLabel playlistNameLabel;
    // CHANGED: JList now holds Song objects for better custom rendering
    private final JList<Song> songList;

    // Dependencies
    private AnalysisController analysisController;
    private final AnalysisViewModel analysisViewModel;

    private final JButton analyzeButton;
    private final SentimentPanel sentimentPanel = new SentimentPanel();

    public AnalysisView(AnalysisViewModel analysisViewModel) {
        this.analysisViewModel = analysisViewModel;
        this.analysisViewModel.addPropertyChangeListener(this);

        // Initialize new components
        this.playlistNameLabel = new JLabel(PLAYLIST_NAME);

        // Setup JList and its model
        // CHANGED: Model now holds Song objects
        DefaultListModel<Song> songListModel = new DefaultListModel<>();
        for (Song song : playlistSongs) {
            songListModel.addElement(song);
        }

        this.songList = new JList<>(songListModel);
        this.songList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.songList.setLayoutOrientation(JList.VERTICAL);
        this.songList.setVisibleRowCount(5); // Show a few rows at a time

        // NEW: Set the custom renderer for column formatting and separators
        this.songList.setCellRenderer(new SongListCellRenderer());

        // Initialize the "Analyze Sentiment" button
        this.analyzeButton = new JButton(AnalysisViewModel.ANALYZE_BUTTON_LABEL);
        this.setLayout(new BorderLayout(10, 10));

        // --- 2. Input Panel (Playlist Display) ---
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Display Playlist Name on the top
        playlistNameLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        playlistNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        inputPanel.add(playlistNameLabel, BorderLayout.NORTH);

        // Container for the song list, its title, and the new headers
        JPanel listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.add(new JLabel("Songs in Playlist:"));
        listContainer.add(Box.createVerticalStrut(5));

        // NEW: Header Panel for Title and Artist columns
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
        headerPanel.setBackground(new Color(230, 230, 230)); // Light background for header
        listContainer.add(headerPanel);

        // Wrap JList in JScrollPane for scrolling
        JScrollPane scrollPane = new JScrollPane(songList);
        scrollPane.setPreferredSize(new Dimension(400, 120)); // Fixed height for visibility
        listContainer.add(scrollPane);

        inputPanel.add(listContainer, BorderLayout.CENTER);

        // Button setup
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(analyzeButton);
        inputPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Attach action listener to this class instance
        analyzeButton.addActionListener(this);

        // --- 3. Visualization Panel ---
        // SentimentPanel is already initialized

        // --- Assemble View ---
        this.add(inputPanel, BorderLayout.NORTH);
        this.add(sentimentPanel, BorderLayout.CENTER);

        // Initial update
        updateViewFromState(analysisViewModel.getState());
    }

    /**
     * Custom List Cell Renderer to display Title and Artist in two columns
     * and add a line separator below each item.
     */
    private static class SongListCellRenderer extends JPanel implements ListCellRenderer<Song> {

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

            titleLabel.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5)); // Padding
            artistLabel.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5)); // Padding
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
                JList<? extends Song> list,         // the list
                Song song,                          // the value to render
                int index,                          // list index
                boolean isSelected,                 // selected or not
                boolean cellHasFocus)               // focused or not
        {
            titleLabel.setText(song.title);
            artistLabel.setText(song.artist);

            // Determine colors
            Color background = isSelected ? list.getSelectionBackground() : list.getBackground();
            Color foreground = isSelected ? list.getSelectionForeground() : list.getForeground();

            // Apply colors
            this.setBackground(background);
            contentPanel.setBackground(background); // Internal panel must match
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
        if (e.getSource().equals(analyzeButton)) {
            // Check if the controller has been set by the AppBuilder
            if (this.analysisController != null) {
                // Combine the lyrics of all songs into a single string
                String combinedLyrics = playlistSongs.stream()
                        .map(song -> song.lyrics)
                        .collect(Collectors.joining("\n---\n")); // Use a separator for the API

                // Delegate the combined lyrics to the controller
                analysisController.execute(combinedLyrics);
            } else {
                // Should not happen in a properly wired application, but good for robustness
                JOptionPane.showMessageDialog(this, "Application is not fully initialized. Analysis controller is missing.", "System Error", JOptionPane.ERROR_MESSAGE);
            }
        }
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

    public String getViewName() {
        return VIEW_NAME;
    }
}