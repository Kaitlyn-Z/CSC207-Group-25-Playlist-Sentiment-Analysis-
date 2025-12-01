package use_case.login;

import entity.User;
import entity.UserFactory;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for LoginInteractor, using the LoginUserDataAccessInterface boundary.
 */
class LoginInteractorTest {

    // ====== Fake DAO for tests ======
    private static class FakeUserDAO implements LoginUserDataAccessInterface {

        private final UserFactory userFactory = new UserFactory();

        int createOrUpdateCalls = 0;
        String lastCode = null;
        User userToReturn = null;
        boolean shouldThrow = false;
        String exceptionMessage = "boom";

        @Override
        public User createOrUpdateUserFromSpotifyCode(String code) throws Exception {
            createOrUpdateCalls++;
            lastCode = code;

            if (shouldThrow) {
                throw new Exception(exceptionMessage);
            }

            if (userToReturn != null) {
                return userToReturn;
            }

            return userFactory.create(
                    code,
                    "User_" + code,
                    "access_" + code,
                    "refresh_" + code,
                    LocalDateTime.now().plusHours(1)
            );
        }

        // The rest of the methods aren't used in LoginInteractor now,
        // so we just stub them out.

        @Override
        public boolean existsBySpotifyId(String spotifyId) {
            return false;
        }

        @Override
        public User getBySpotifyId(String spotifyId) {
            return null;
        }

        @Override
        public void save(User user) {}

        @Override
        public void setCurrentUser(User user) {}

        @Override
        public User getCurrentUser() {
            return null;
        }

        @Override
        public void clearCurrentUser() {}
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

        LoginInputData inputData = new LoginInputData("   ");
        interactor.execute(inputData);

        assertEquals(0, presenter.successCalls);
        assertEquals(1, presenter.failCalls);
        assertEquals("Login input cannot be empty.", presenter.lastError);

        assertEquals(0, dao.createOrUpdateCalls);
    }

    @Test
    void nullInput_triggersFailAndDoesNotCallDAO() {
        FakeUserDAO dao = new FakeUserDAO();
        FakeLoginPresenter presenter = new FakeLoginPresenter();
        LoginInteractor interactor = new LoginInteractor(dao, presenter);

        LoginInputData inputData = new LoginInputData(null);
        interactor.execute(inputData);

        assertEquals(0, presenter.successCalls);
        assertEquals(1, presenter.failCalls);
        assertEquals("Login input cannot be empty.", presenter.lastError);

        assertEquals(0, dao.createOrUpdateCalls);
    }

    @Test
    void nonEmptyInput_callsDAOAndReturnsSuccess() {
        FakeUserDAO dao = new FakeUserDAO();
        FakeLoginPresenter presenter = new FakeLoginPresenter();
        LoginInteractor interactor = new LoginInteractor(dao, presenter);

        String code = "abc123";

        // preconfigure a specific user
        UserFactory factory = new UserFactory();
        dao.userToReturn = factory.create(
                "spotify_abc123",
                "Existing Spotify User",
                "access_token_abc123",
                "refresh_token_abc123",
                LocalDateTime.now().plusHours(2)
        );

        LoginInputData inputData = new LoginInputData(code);
        interactor.execute(inputData);

        assertEquals(1, dao.createOrUpdateCalls);
        assertEquals(code, dao.lastCode);

        assertEquals(1, presenter.successCalls);
        assertEquals(0, presenter.failCalls);
        assertNotNull(presenter.lastSuccess);
        assertEquals("Existing Spotify User", presenter.lastSuccess.getDisplayName());
        assertEquals("spotify_abc123", presenter.lastSuccess.getSpotifyId());
    }

    @Test
    void daoThrowsException_triggersFailView() {
        FakeUserDAO dao = new FakeUserDAO();
        dao.shouldThrow = true;
        dao.exceptionMessage = "Spotify service down";

        FakeLoginPresenter presenter = new FakeLoginPresenter();
        LoginInteractor interactor = new LoginInteractor(dao, presenter);

        LoginInputData inputData = new LoginInputData("someCode");
        interactor.execute(inputData);

        assertEquals(1, dao.createOrUpdateCalls);

        assertEquals(0, presenter.successCalls);
        assertEquals(1, presenter.failCalls);
        assertNotNull(presenter.lastError);
        assertTrue(presenter.lastError.startsWith("Spotify login failed:"));
        assertTrue(presenter.lastError.contains("Spotify service down"));
    }
}
