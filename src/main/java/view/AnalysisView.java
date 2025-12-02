package view;

import com.google.gson.JsonObject;
import interface_adapter.analysis.AnalysisState;
import interface_adapter.analysis.AnalysisViewModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class AnalysisView extends JPanel implements PropertyChangeListener {

    public static final String VIEW_NAME = "analysis";

    private final AnalysisViewModel analysisViewModel;
    private final JLabel playlistNameLabel;
    private final JList<JsonObject> songList;
    private final JButton closeButton;
    private final SentimentPanel sentimentPanel;

    public AnalysisView(AnalysisViewModel analysisViewModel) {
        this.analysisViewModel = analysisViewModel;
        this.analysisViewModel.addPropertyChangeListener(this);

        this.sentimentPanel = new SentimentPanel();
        this.playlistNameLabel = new JLabel();
        this.closeButton = new JButton("Close");
        this.songList = new JList<>(new DefaultListModel<>());

        setLayout(new BorderLayout(10, 10));
        buildUI();
    }

    private void buildUI() {
        // --- Top Header Panel (Playlist Name and Close Button) ---
        JPanel topHeaderPanel = new JPanel(new BorderLayout());
        playlistNameLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        playlistNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topHeaderPanel.add(playlistNameLabel, BorderLayout.CENTER);

        JPanel closeButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        closeButtonPanel.add(closeButton);
        topHeaderPanel.add(closeButtonPanel, BorderLayout.EAST);

        closeButton.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) {
                window.dispose();
            }
        });

        // --- Center Panel for Song List ---
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.add(new JLabel("Songs in Playlist:"));
        listContainer.add(Box.createVerticalStrut(5));

        JPanel headerPanel = new JPanel(new GridLayout(1, 2));
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        JLabel titleHeader = new JLabel("TITLE");
        titleHeader.setFont(new Font("SansSerif", Font.BOLD, 12));
        JLabel artistHeader = new JLabel("ARTIST");
        artistHeader.setFont(new Font("SansSerif", Font.BOLD, 12));
        headerPanel.add(titleHeader);
        headerPanel.add(artistHeader);
        listContainer.add(headerPanel);

        songList.setCellRenderer(new SongListCellRenderer());
        JScrollPane scrollPane = new JScrollPane(songList);
        listContainer.add(scrollPane);
        centerPanel.add(listContainer, BorderLayout.CENTER);

        // --- Assemble View ---
        this.add(topHeaderPanel, BorderLayout.NORTH);
        this.add(centerPanel, BorderLayout.CENTER);
        this.add(sentimentPanel, BorderLayout.SOUTH);

        // Set initial state
        updateViewFromState(this.analysisViewModel.getState());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName())) {
            AnalysisState state = (AnalysisState) evt.getNewValue();
            updateViewFromState(state);
        }
    }

    private void updateViewFromState(AnalysisState state) {
        sentimentPanel.setLoading(state.isLoading());

        if (state.isLoading()) {
            playlistNameLabel.setText("Analyzing...");
            DefaultListModel<JsonObject> model = (DefaultListModel<JsonObject>) songList.getModel();
            model.clear();
            sentimentPanel.setResult(null); // Clear previous results
        } else {
            playlistNameLabel.setText(state.getPlaylistName());

            DefaultListModel<JsonObject> model = (DefaultListModel<JsonObject>) songList.getModel();
            model.clear();
            if (state.getSongs() != null) {
                for (int i = 0; i < state.getSongs().size(); i++) {
                    model.addElement(state.getSongs().get(i).getAsJsonObject());
                }
            }

            if (state.getResult() != null) {
                sentimentPanel.setResult(state.getResult());
            }

            if (state.getErrorMessage() != null) {
                JOptionPane.showMessageDialog(this, state.getErrorMessage(), "Analysis Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static class SongListCellRenderer extends JPanel implements ListCellRenderer<JsonObject> {
        private final JLabel titleLabel = new JLabel();
        private final JLabel artistLabel = new JLabel();

        public SongListCellRenderer() {
            setLayout(new BorderLayout());
            add(titleLabel, BorderLayout.WEST);
            add(artistLabel, BorderLayout.EAST);
            setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        }

        @Override
        public Component getListCellRendererComponent(
                JList<? extends JsonObject> list, JsonObject song, int index,
                boolean isSelected, boolean cellHasFocus) {
            
            if (song != null) {
                titleLabel.setText(song.has("title") ? song.get("title").getAsString() : "No Title");
                artistLabel.setText(song.has("artist") ? song.get("artist").getAsString() : "No Artist");
            }

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            return this;
        }
    }

    public static String getViewName() {
        return VIEW_NAME;
    }
}