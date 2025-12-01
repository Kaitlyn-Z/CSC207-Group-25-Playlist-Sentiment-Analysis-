package use_case.login;

import data_access.UserDataAccessInterface;
import entity.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for LoginInteractor.
 *
 * These tests run the use case in isolation using fake implementations
 * of UserDataAccessInterface and LoginOutputBoundary, so we only test
 * the interactor logic and not the UI or real DB/Spotify code.
 */
class LoginInteractorTest {

    // ====== Fake DAO ======

    private static class FakeUserDAO implements UserDataAccessInterface {

        User storedUser;     // “database” user
        User currentUser;    // currently logged-in user

        int existsCalls = 0;
        int getCalls = 0;
        int saveCalls = 0;
        int setCurrentUserCalls = 0;
        int clearCurrentUserCalls = 0;
        int createOrUpdateCalls = 0; // for the Spotify-code method

        @Override
        public boolean existsBySpotifyId(String spotifyId) {
            existsCalls++;
            return storedUser != null
                    && storedUser.getSpotifyId().equals(spotifyId);
        }

        @Override
        public User getBySpotifyId(String spotifyId) {
            getCalls++;
            return storedUser;
        }

        @Override
        public void save(User user) {
            saveCalls++;
            storedUser = user;
        }

        @Override
        public void setCurrentUser(User user) {
            setCurrentUserCalls++;
            currentUser = user;
        }

        @Override
        public User getCurrentUser() {
            return currentUser;
        }

        @Override
        public void clearCurrentUser() {
            clearCurrentUserCalls++;
            currentUser = null;
        }

        /**
         * New method that your production UserDataAccessInterface now requires.
         * We don't use it in these tests, so a simple stub implementation is enough.
         */
        @Override
        public User createOrUpdateUserFromSpotifyCode(String code) {
            createOrUpdateCalls++;

            // Minimal "reasonable" behaviour: treat the code as a spotifyId
            // and create a new User, similar to what LoginInteractor does.
            User user = new User(
                    code,
                    "User_" + code,
                    "access_token_" + code,
                    "refresh_token_" + code,
                    LocalDateTime.now().plusHours(1)
            );
            this.storedUser = user;
            return user;
        }
    }

    // ====== Fake Presenter ======

    private static class FakeLoginPresenter implements LoginOutputBoundary {
        LoginOutputData lastSuccess = null;
        String lastError = null;
        int successCalls = 0;
        int failCalls = 0;

        @Override
        public void prepareSuccessView(LoginOutputData data) {
            successCalls++;
            lastSuccess = data;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            failCalls++;
            lastError = errorMessage;
        }
    }

    // ====== Tests ======

    @Test
    void emptyInput_triggersFailAndDoesNotCallDAO() {
        FakeUserDAO dao = new FakeUserDAO();
        FakeLoginPresenter presenter = new FakeLoginPresenter();
        LoginInteractor interactor = new LoginInteractor(dao, presenter);

        // input is only spaces
        LoginInputData inputData = new LoginInputData("   ");
        interactor.execute(inputData);

        // Presenter: should get exactly one fail call
        assertEquals(0, presenter.successCalls, "No success view for empty input");
        assertEquals(1, presenter.failCalls, "Fail view should be called once");
        assertEquals("Login input cannot be empty.",
                presenter.lastError,
                "Error message should match spec");

        // DAO: should not be touched at all
        assertEquals(0, dao.existsCalls, "DAO.existsBySpotifyId should not be called on empty input");
        assertEquals(0, dao.getCalls, "DAO.getBySpotifyId should not be called on empty input");
        assertEquals(0, dao.saveCalls, "DAO.save should not be called on empty input");
        assertEquals(0, dao.setCurrentUserCalls, "DAO.setCurrentUser should not be called on empty input");
        assertNull(dao.currentUser, "There should be no current user on empty input");
    }

    @Test
    void nullInput_triggersFailSameAsEmpty() {
        FakeUserDAO dao = new FakeUserDAO();
        FakeLoginPresenter presenter = new FakeLoginPresenter();
        LoginInteractor interactor = new LoginInteractor(dao, presenter);

        LoginInputData inputData = new LoginInputData(null);
        interactor.execute(inputData);

        assertEquals(0, presenter.successCalls);
        assertEquals(1, presenter.failCalls);
        assertEquals("Login input cannot be empty.", presenter.lastError);

        assertEquals(0, dao.existsCalls);
        assertEquals(0, dao.getCalls);
        assertEquals(0, dao.saveCalls);
        assertEquals(0, dao.setCurrentUserCalls);
        assertNull(dao.currentUser);
    }

    @Test
    void existingUser_loginUsesExistingUserAndDoesNotSaveNew() {
        FakeUserDAO dao = new FakeUserDAO();
        FakeLoginPresenter presenter = new FakeLoginPresenter();
        LoginInteractor interactor = new LoginInteractor(dao, presenter);

        // Arrange: pre-existing user in the “database”
        String spotifyId = "existing-user";
        User existing = new User(
                spotifyId,
                "ExistingUser",
                "access_existing",
                "refresh_existing",
                LocalDateTime.now().plusHours(1)
        );
        dao.storedUser = existing;

        // Act
        LoginInputData inputData = new LoginInputData(spotifyId);
        interactor.execute(inputData);

        // Presenter: success, no fail
        assertEquals(1, presenter.successCalls, "Success view should be called once");
        assertEquals(0, presenter.failCalls, "Fail view should not be called");
        assertNotNull(presenter.lastSuccess, "Success data should be present");
        assertEquals("ExistingUser", presenter.lastSuccess.getDisplayName());
        assertEquals(spotifyId, presenter.lastSuccess.getSpotifyId());

        // DAO calls: exists + get, no save
        assertEquals(1, dao.existsCalls, "existsBySpotifyId should be called once");
        assertEquals(1, dao.getCalls, "getBySpotifyId should be called once");
        assertEquals(0, dao.saveCalls, "save should not be called for existing user");

        // Current user should be the existing instance
        assertEquals(1, dao.setCurrentUserCalls, "setCurrentUser should be called once");
        assertSame(existing, dao.currentUser, "Current user should be the existing user instance");
    }

    @Test
    void newUser_loginCreatesAndSavesUserAndSetsCurrentUser() {
        FakeUserDAO dao = new FakeUserDAO();
        FakeLoginPresenter presenter = new FakeLoginPresenter();
        LoginInteractor interactor = new LoginInteractor(dao, presenter);

        String spotifyId = "new-user";

        LoginInputData inputData = new LoginInputData(spotifyId);
        interactor.execute(inputData);

        // Presenter: success, no fail
        assertEquals(1, presenter.successCalls);
        assertEquals(0, presenter.failCalls);
        assertNotNull(presenter.lastSuccess);
        assertEquals(spotifyId, presenter.lastSuccess.getSpotifyId());
        assertEquals("User_" + spotifyId, presenter.lastSuccess.getDisplayName());

        // DAO: exists called once, then save + setCurrentUser
        assertEquals(1, dao.existsCalls, "existsBySpotifyId should be called once");
        assertEquals(0, dao.getCalls, "getBySpotifyId should not be called for new user");
        assertEquals(1, dao.saveCalls, "save should be called once for new user");
        assertNotNull(dao.storedUser, "New user should be stored in DAO");
        assertEquals(spotifyId, dao.storedUser.getSpotifyId());
        assertEquals("User_" + spotifyId, dao.storedUser.getDisplayName());

        assertEquals(1, dao.setCurrentUserCalls, "setCurrentUser should be called once");
        assertSame(dao.storedUser, dao.currentUser, "Current user should be the same as stored user");
    }
}
