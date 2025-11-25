package app;

import data_access.DBPlaylistDataAccessObject;
import data_access.DBSongDataAccessObject;
import data_access.DBUserDataAccessObject;
import data_access.DBSentimentResult;

import entity.SentimentResultFactory;
import entity.UserFactory;
import entity.SongFactory;
import entity.PlaylistFactory;


import interface_adapter.ViewManagerModel;
import interface_adapter.analysis.AnalysisViewModel;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginPresenter;
import interface_adapter.login.LoginViewModel;
import interface_adapter.logout.LogoutController;
import interface_adapter.logout.LogoutPresenter;
import interface_adapter.analysis.AnalysisController;
import interface_adapter.analysis.AnalysisPresenter;

import use_case.login.LoginInputBoundary;
import use_case.login.LoginInteractor;
import use_case.login.LoginOutputBoundary;
import use_case.logout.LogoutInputBoundary;
import use_case.logout.LogoutInteractor;
import use_case.logout.LogoutOutputBoundary;
import use_case.analyze_playlist.AnalyzePlaylistInputBoundary;
import use_case.analyze_playlist.AnalyzePlaylistInteractor;
import use_case.analyze_playlist.AnalyzePlaylistOutputBoundary;
import use_case.analyze_playlist.SentimentDataAccessInterface;




import view.AnalysisView;
import view.ViewManager;
import view.LoggedInView;
import view.LoginView;


import javax.swing.*;  //JFrame...
import java.awt.*;  //Color...



public class AppBuilder {
    private final JPanel cardPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();
    final UserFactory userFactory = new UserFactory();
    final PlaylistFactory playlistFactory = new PlaylistFactory();
    final SongFactory songFactory = new SongFactory();
    final ViewManagerModel viewManagerModel = new ViewManagerModel();
    ViewManager viewManager = new ViewManager(cardPanel, cardLayout, viewManagerModel);

    final DBUserDataAccessObject userDataAccessObject = new DBUserDataAccessObject(userFactory);
    final DBPlaylistDataAccessObject playlistDataAccessObject = new DBPlaylistDataAccessObject(playlistFactory);
    final DBSongDataAccessObject songDataAccessObject = new DBSongDataAccessObject(songFactory);

    private LoginViewModel loginViewModel;
    private LoggedInViewModel loggedInViewModel;
    private AnalysisViewModel analysisViewModel;
    private LoggedInView loggedInView;
    private LoginView loginView;
    private AnalysisView analysisView;

    public AppBuilder() {cardPanel.setLayout(cardLayout);}

    //Add View to Panel
    //These are just templates, everyone can change them if u need
    public AppBuilder addLoginView(){
        loginViewModel = new LoginViewModel();
        loginView = new LoginView(loginViewModel);
        cardPanel.add(loginView, loginView.getViewName());
        return this;
    }

    public AppBuilder addLoggedInView() {
        loggedInViewModel = new LoggedInViewModel();
        loggedInView = new LoggedInView(loggedInViewModel);
        cardPanel.add(loggedInView, loggedInView.getViewName());
        return this;
    }

    public AppBuilder addAnalysisView() {
        analysisViewModel = new AnalysisViewModel();
        analysisView = new AnalysisView(analysisViewModel);
        cardPanel.add(analysisView, analysisView.getViewName());
        return this;
    }

    public AppBuilder addAnalysisUseCase() {
        SentimentResultFactory sentimentResultFactory = new SentimentResultFactory();

        // 1. Create the Data Access Object (Using the renamed class with Java 11 HttpClient)
        SentimentDataAccessInterface dao = new DBSentimentResult(sentimentResultFactory);

        // 2. Create the Presenter (Updates the ViewModel)
        AnalyzePlaylistOutputBoundary presenter = new AnalysisPresenter(this.analysisViewModel);

        // 3. Create the Interactor (The business logic)
        AnalyzePlaylistInputBoundary interactor = new AnalyzePlaylistInteractor(dao, presenter);

        // 4. Create the Controller (The component the View calls)
        AnalysisController controller = new AnalysisController(interactor, this.analysisViewModel);

        // 5. Inject the Controller into the View (Completing the cycle)
        // This setter call is crucial now that the controller is no longer passed in the constructor.
        if (this.analysisView != null) {
            this.analysisView.setAnalysisController(controller); // <-- Controller is added here
        } else {
            System.err.println("Error: AnalysisView must be added before its use case is wired.");
        }

        return this;
    }

    // Connect UseCase to interface_adapter
    //These are just templates, everyone can change them if u need
    public AppBuilder addLoginUseCase() {
        final LoginOutputBoundary loginOutputBoundary = new LoginPresenter(viewManagerModel,
                loggedInViewModel, loginViewModel);
        final LoginInputBoundary loginInteractor = new LoginInteractor(
                userDataAccessObject, loginOutputBoundary);

        LoginController loginController = new LoginController(loginInteractor);
        loginView.setLoginController(loginController);
        return this;
    }

    public AppBuilder addLogoutUseCase() {
        LogoutOutputBoundary logoutOutputBoundary =
                new LogoutPresenter(viewManagerModel, loggedInViewModel, loginViewModel);

        LogoutInputBoundary logoutInteractor =
                new LogoutInteractor(userDataAccessObject, logoutOutputBoundary);
        //  ^ userDataAccessObject now implements LogoutUserDataAccessInterface

        LogoutController logoutController = new LogoutController(logoutInteractor);
        loggedInView.setLogoutController(logoutController);
        return this;
    }


    /*
    public AppBuilder addAnalysisUseCase(){}
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