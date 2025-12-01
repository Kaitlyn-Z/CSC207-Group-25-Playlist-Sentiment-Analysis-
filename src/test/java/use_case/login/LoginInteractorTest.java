package use_case.login;

import entity.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for LoginInteractor with the new Spotify-based DAO boundary.
 */
class LoginInteractorTest {

    // ====== Fake DAO that never hits Spotify ======
    private static class FakeUserDAO implements LoginUserDataAccessInterface {

        User currentUser;
        int createOrUpdateCalls = 0;

        @Override
        public boolean existsBySpotifyId(String spotifyId) {
            return false; // not used in these tests
        }

        @Override
        public User getBySpotifyId(String spotifyId) {
            return null; // not used
        }

        @Override
        public void save(User user) {
            // no-op for tests
        }

        @Override
        public void setCurrentUser(User user) {
            this.currentUser = user;
        }

        @Override
        public User getCurrentUser() {
            return currentUser;
        }

        @Override
        public void clearCurrentUser() {
            this.currentUser = null;
        }

        @Override
        public User createOrUpdateUserFromSpotifyCode(String code) throws Exception {
            createOrUpdateCalls++;
            // Fabricate a user without calling Spotify
            return new User(
                    code,
                    "User_" + code,
                    "access_" + code,
                    "refresh_" + code,
                    LocalDateTime.now().plusHours(1)
            );
        }
    }

    // ====== Fake Presenter ======
    private static class FakePresenter implements LoginOutputBoundary {
        LoginOutputData lastSuccess;
        String lastError;
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
        FakePresenter presenter = new FakePresenter();
        LoginInteractor interactor = new LoginInteractor(dao, presenter);

        interactor.execute(new LoginInputData("   ")); // spaces only

        assertEquals(0, presenter.successCalls);
        assertEquals(1, presenter.failCalls);
        assertEquals("Login input cannot be empty.", presenter.lastError);

        assertEquals(0, dao.createOrUpdateCalls,
                "DAO should NOT be called when input is empty.");
    }

    @Test
    void nullInput_triggersFailAndDoesNotCallDAO() {
        FakeUserDAO dao = new FakeUserDAO();
        FakePresenter presenter = new FakePresenter();
        LoginInteractor interactor = new LoginInteractor(dao, presenter);

        interactor.execute(new LoginInputData(null));

        assertEquals(0, presenter.successCalls);
        assertEquals(1, presenter.failCalls);
        assertEquals("Login input cannot be empty.", presenter.lastError);
        assertEquals(0, dao.createOrUpdateCalls);
    }

    @Test
    void validToken_callsDAOAndReturnsSuccess() {
        FakeUserDAO dao = new FakeUserDAO();
        FakePresenter presenter = new FakePresenter();
        LoginInteractor interactor = new LoginInteractor(dao, presenter);

        interactor.execute(new LoginInputData("valid-token"));

        assertEquals(1, dao.createOrUpdateCalls);
        assertEquals(1, presenter.successCalls);
        assertEquals(0, presenter.failCalls);
        assertNotNull(presenter.lastSuccess);
        assertEquals("valid-token", presenter.lastSuccess.getSpotifyId());
        assertEquals("User_valid-token", presenter.lastSuccess.getDisplayName());
    }

    @Test
    void daoThrowsException_resultsInFailView() throws Exception {
        // DAO that always throws
        LoginUserDataAccessInterface failingDAO = new LoginUserDataAccessInterface() {
            @Override public boolean existsBySpotifyId(String spotifyId) { return false; }
            @Override public User getBySpotifyId(String spotifyId) { return null; }
            @Override public void save(User user) {}
            @Override public void setCurrentUser(User user) {}
            @Override public User getCurrentUser() { return null; }
            @Override public void clearCurrentUser() {}

            @Override
            public User createOrUpdateUserFromSpotifyCode(String code) throws Exception {
                throw new Exception("boom");
            }
        };

        FakePresenter presenter = new FakePresenter();
        LoginInteractor interactor = new LoginInteractor(failingDAO, presenter);

        interactor.execute(new LoginInputData("whatever"));

        assertEquals(0, presenter.successCalls);
        assertEquals(1, presenter.failCalls);
        assertTrue(presenter.lastError.startsWith("Spotify login failed:"),
                "Error message should start with 'Spotify login failed:'");
    }
}
