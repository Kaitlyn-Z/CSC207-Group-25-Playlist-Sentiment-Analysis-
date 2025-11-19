package interface_adapter.login;

import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.LoggedInViewModel;
import use_case.login.LoginOutputBoundary;
import use_case.login.LoginOutputData;
import view.LoggedInView;
import view.LoginView;

public class LoginPresenter implements LoginOutputBoundary {

    private final ViewManagerModel viewManagerModel;
    private final LoggedInViewModel loggedInViewModel;
    private final LoginViewModel loginViewModel;

    public LoginPresenter(ViewManagerModel viewManagerModel,
                          LoggedInViewModel loggedInViewModel,
                          LoginViewModel loginViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.loggedInViewModel = loggedInViewModel;
        this.loginViewModel = loginViewModel;
    }

    @Override
    public void prepareSuccessView(LoginOutputData data) {
        // 1) Update "logged in" state
        loggedInViewModel.setDisplayName(data.getDisplayName());
        loggedInViewModel.setSpotifyId(data.getSpotifyId());

        // 2) Optionally reflect logged-in status in the login VM too
        loginViewModel.setLoggedIn(data.getDisplayName());

        // 3) Switch to the logged-in view
        viewManagerModel.setState(LoggedInView.getViewNameStatic());
        viewManagerModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        // 1) Show error on the login screen
        loginViewModel.setError(errorMessage);

        // 2) Stay on login screen
        viewManagerModel.setState(LoginView.getViewName());
        viewManagerModel.firePropertyChange();
    }
}
