package use_case.login;

import data_access.UserDataAccessInterface;
import entity.User;

import java.time.LocalDateTime;

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

        // For now: treat value as spotifyId. Later: treat as auth code and delegate to DAO.
        User user;
        if (userDataAccess.existsBySpotifyId(value)) {
            user = userDataAccess.getBySpotifyId(value);
        } else {
            user = new User(
                    value,
                    "User_" + value,
                    "access_token_" + value,
                    "refresh_token_" + value,
                    LocalDateTime.now().plusHours(1)
            );
            userDataAccess.save(user);
        }

        userDataAccess.setCurrentUser(user);

        presenter.prepareSuccessView(
                new LoginOutputData(user.getDisplayName(), user.getSpotifyId())
        );
    }
}

