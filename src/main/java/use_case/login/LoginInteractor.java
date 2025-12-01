package use_case.login;

import entity.User;

/**
 * LoginInteractor coordinates the login use case.
 *
 * It does NOT know about Spotify details; it only calls the
 * LoginUserDataAccessInterface boundary.
 */
public class LoginInteractor implements LoginInputBoundary {

    private final LoginUserDataAccessInterface userDataAccess;
    private final LoginOutputBoundary presenter;

    public LoginInteractor(LoginUserDataAccessInterface userDataAccess,
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
            // Delegate "Spotify + DB" work to the DAO through the boundary.
            User user = userDataAccess.createOrUpdateUserFromSpotifyCode(value);

            presenter.prepareSuccessView(
                    new LoginOutputData(user.getDisplayName(), user.getSpotifyId())
            );
        } catch (Exception e) {
            presenter.prepareFailView("Spotify login failed: " + e.getMessage());
        }
    }
}
