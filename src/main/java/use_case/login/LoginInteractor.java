package use_case.login;

import data_access.UserDataAccessInterface;
import entity.User;

public class LoginInteractor implements LoginInputBoundary {

    private final UserDataAccessInterface userDataAccess;
    private final LoginOutputBoundary presenter;

    public LoginInteractor(UserDataAccessInterface userDataAccess,
                           LoginOutputBoundary presenter) {
        this.userDataAccess = userDataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(LoginInputData inputData) {
        String value = inputData.getSpotifyIdOrCode();

        if (value == null || value.trim().isEmpty()) {
            presenter.prepareFailView("Login input cannot be empty.");
            return;
        }

        try {
            // Delegate the real work (Spotify + DB) to the DAO.
            User user = userDataAccess.createOrUpdateUserFromSpotifyCode(value);

            presenter.prepareSuccessView(
                    new LoginOutputData(user.getDisplayName(), user.getSpotifyId())
            );
        } catch (Exception e) {
            presenter.prepareFailView("Spotify login failed: " + e.getMessage());
        }
    }
}
