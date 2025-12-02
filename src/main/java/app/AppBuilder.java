package app;

import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import data_access.DBPlaylistDataAccessObject;
import data_access.DBSentimentResultDataAccessObject;
import data_access.DBUserDataAccessObject;
import entity.PlaylistFactory;
import entity.SentimentResultFactory;
import entity.UserFactory;
import interface_adapter.ViewManagerModel;
import interface_adapter.analysis.AnalysisController;
import interface_adapter.analysis.AnalysisPresenter;
import interface_adapter.analysis.AnalysisViewModel;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.logged_in.SelectPlaylistController;
import interface_adapter.logged_in.SelectPlaylistPresenter;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginPresenter;
import interface_adapter.login.LoginViewModel;
import interface_adapter.logout.LogoutController;
import interface_adapter.logout.LogoutPresenter;
import use_case.analyze_playlist.AnalyzePlaylistInputBoundary;
import use_case.analyze_playlist.AnalyzePlaylistInteractor;
import use_case.analyze_playlist.AnalyzePlaylistOutputBoundary;
import use_case.login.LoginInputBoundary;
import use_case.login.LoginInteractor;
import use_case.login.LoginOutputBoundary;
import use_case.logout.LogoutInputBoundary;
import use_case.logout.LogoutInteractor;
import use_case.logout.LogoutOutputBoundary;
import use_case.select_playlist.SelectPlaylistInteractor;
import view.AnalysisView;
import view.LoggedInView;
import view.LoginView;
import view.ViewManager;

public class AppBuilder {
    private final JPanel cardPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();
    private final UserFactory userFactory = new UserFactory();
    private final PlaylistFactory playlistFactory = new PlaylistFactory();
    private final SentimentResultFactory sentimentResultFactory = new SentimentResultFactory();
    private final ViewManagerModel viewManagerModel = new ViewManagerModel();
    private ViewManager viewManager = new ViewManager(cardPanel, cardLayout, viewManagerModel);

    private final DBUserDataAccessObject userDataAccessObject = new DBUserDataAccessObject(userFactory);
    private final DBSentimentResultDataAccessObject sentimentDataAccessObject =
            new DBSentimentResultDataAccessObject(sentimentResultFactory);
    private final DBPlaylistDataAccessObject spotifyPlaylistDataAccessObject =
            new DBPlaylistDataAccessObject(playlistFactory);
    private final data_access.AnalysisStatsDataAccessObject analysisStatsDataAccessObject =
            new data_access.AnalysisStatsDataAccessObject("analysis_stats.json");
    // New DAO

    private LoginViewModel loginViewModel;
    private LoggedInViewModel loggedInViewModel;
    private AnalysisViewModel analysisViewModel;
    private LoggedInView loggedInView;
    private LoginView loginView;

    public AppBuilder() {
        cardPanel.setLayout(cardLayout);

        // ViewModels are created upfront
        loginViewModel = new LoginViewModel();
        loggedInViewModel = new LoggedInViewModel();
        analysisViewModel = new AnalysisViewModel();
    }

    /**
     * Add login view to panel.
     * @return this
     */
    public AppBuilder addLoginView() {
        loginView = new LoginView(loginViewModel);
        cardPanel.add(loginView, loginView.getViewName());
        return this;
    }

    /**
     * Add logged in view to panel.
     * @return this
     */
    public AppBuilder addLoggedInView() {
        loggedInView = new LoggedInView(loggedInViewModel, analysisViewModel);
        cardPanel.add(loggedInView, loggedInView.getViewName());
        return this;
    }

    /**
     * Add analysis use case to panel.
     * @return this
     */
    public AppBuilder addAnalysisUseCase() {
        // The presenter no longer needs the ViewManagerModel because it doesn't switch
        // the main view. It just updates the AnalysisViewModel, which the pop-up listens to.
        final AnalyzePlaylistOutputBoundary analyzePlaylistOutputBoundary = new AnalysisPresenter(analysisViewModel);

        final AnalyzePlaylistInputBoundary analyzePlaylistInteractor = new AnalyzePlaylistInteractor(playlistFactory,
                sentimentResultFactory, sentimentDataAccessObject,
                analyzePlaylistOutputBoundary, spotifyPlaylistDataAccessObject,
                analysisStatsDataAccessObject);

        final AnalysisController analysisController = new AnalysisController(analyzePlaylistInteractor);
        loggedInView.setAnalysisController(analysisController);
        return this;
    }

    /**
     * Add login use case.
     * @return this
     */
    public AppBuilder addLoginUseCase() {
        final LoginOutputBoundary loginOutputBoundary = new LoginPresenter(viewManagerModel,
                loggedInViewModel, loginViewModel);
        final LoginInputBoundary loginInteractor = new LoginInteractor(
                userDataAccessObject, loginOutputBoundary);

        final LoginController loginController = new LoginController(loginInteractor);
        loginView.setLoginController(loginController);
        return this;
    }

    /**
     * Add logout use case.
     * @return this
     */
    public AppBuilder addLogoutUseCase() {
        final LogoutOutputBoundary logoutOutputBoundary =
                new LogoutPresenter(viewManagerModel, loggedInViewModel, loginViewModel);

        final LogoutInputBoundary logoutInteractor =
                new LogoutInteractor(userDataAccessObject, logoutOutputBoundary);

        final LogoutController logoutController = new LogoutController(logoutInteractor);
        loggedInView.setLogoutController(logoutController);
        return this;
    }

    public AppBuilder addSelectPlaylistUseCase() {

        var presenter = new SelectPlaylistPresenter(loggedInViewModel);

        var interactor = new SelectPlaylistInteractor(
                loggedInViewModel,
                presenter
        );

        var controller = new SelectPlaylistController(interactor);

        loggedInView.setSelectPlaylistController(controller);

        return this;
    }


    /**
     * Build method.
     * @return User Login JFrame
     */
    public JFrame build() {
        final JFrame application = new JFrame("User Login");
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        application.add(cardPanel);

        viewManagerModel.setState(LoginView.getViewName());
        viewManagerModel.firePropertyChange();

        return application;
    }
}
