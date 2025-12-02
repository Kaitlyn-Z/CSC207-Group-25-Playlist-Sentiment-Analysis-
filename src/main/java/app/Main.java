package app;

import javax.swing.JFrame;

public class Main {
    /**
     * Main method.
     * @param args program arguments
     */
    public static void main(String[] args) {
        final AppBuilder appBuilder = new AppBuilder();
        final JFrame application = appBuilder
                .addLoginView()
                .addLoggedInView()
                .addSelectPlaylistUseCase()
                .addLoginUseCase()
                .addAnalysisUseCase()
                .addLogoutUseCase()
                .build();
                
        application.pack();
        application.setLocationRelativeTo(null);
        application.setVisible(true);
    }
}
