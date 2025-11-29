package app;

import data_access.DBPlaylistDataAccessObject;
import data_access.DBUserDataAccessObject;
import data_access.DBSentimentResultDataAccessObject;

import entity.SentimentResultFactory;
import entity.UserFactory;
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

import use_case.analyze_playlist.*;
import use_case.login.LoginInputBoundary;
import use_case.login.LoginInteractor;
import use_case.login.LoginOutputBoundary;
import use_case.logout.LogoutInputBoundary;
import use_case.logout.LogoutInteractor;
import use_case.logout.LogoutOutputBoundary;


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
    final SentimentResultFactory sentimentResultFactory = new SentimentResultFactory();
    final ViewManagerModel viewManagerModel = new ViewManagerModel();
    ViewManager viewManager = new ViewManager(cardPanel, cardLayout, viewManagerModel);

    final DBUserDataAccessObject userDataAccessObject = new DBUserDataAccessObject(userFactory);
    final DBSentimentResultDataAccessObject sentimentDataAccessObject = new DBSentimentResultDataAccessObject(sentimentResultFactory);
    final DBPlaylistDataAccessObject spotifyPlaylistDataAccessObject = new DBPlaylistDataAccessObject(playlistFactory);


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
        final AnalyzePlaylistOutputBoundary analyzePlaylistOutputBoundary = new AnalysisPresenter(analysisViewModel);

        final AnalyzePlaylistInputBoundary analyzePlaylistInteractor = new AnalyzePlaylistInteractor(playlistFactory,
                sentimentResultFactory, sentimentDataAccessObject, analyzePlaylistOutputBoundary, spotifyPlaylistDataAccessObject);

        AnalysisController analysisController = new AnalysisController(analyzePlaylistInteractor);
        analysisView.setAnalysisController(analysisController);
        return this;
    }
     //TODO: Some of them shouldn't be put here, I have moved Factory and DAO to the front
/*
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
*/
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