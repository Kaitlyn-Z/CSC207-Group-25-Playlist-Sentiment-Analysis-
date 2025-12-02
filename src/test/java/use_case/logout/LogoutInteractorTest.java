package use_case.logout;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link LogoutInteractor}.
 */
public class LogoutInteractorTest {

    /**
     * A simple in-memory implementation of LogoutUserDataAccessInterface
     * that just records whether clearCurrentUser() was called.
     */
    private static class InMemoryLogoutUserDataAccess implements LogoutUserDataAccessInterface {

        private boolean cleared = false;

        @Override
        public void clearCurrentUser() {
            cleared = true;
        }

        public boolean isCleared() {
            return cleared;
        }
    }

    /**
     * A test presenter that records whether prepareSuccessView() was called
     * and what LogoutOutputData it received.
     */
    private static class TestLogoutPresenter implements LogoutOutputBoundary {

        private boolean successCalled = false;
        private LogoutOutputData receivedData;

        @Override
        public void prepareSuccessView(LogoutOutputData outputData) {
            successCalled = true;
            receivedData = outputData;
        }

        public boolean isSuccessCalled() {
            return successCalled;
        }

        public LogoutOutputData getReceivedData() {
            return receivedData;
        }
    }

    @Test
    public void testExecuteClearsUserAndCallsPresenter() {
        // Arrange: create stubs and the interactor
        InMemoryLogoutUserDataAccess userDataAccess = new InMemoryLogoutUserDataAccess();
        TestLogoutPresenter presenter = new TestLogoutPresenter();
        LogoutInteractor interactor = new LogoutInteractor(userDataAccess, presenter);

        // Act: run the use case
        interactor.execute();

        // Assert: user cleared and presenter called with success
        assertTrue(userDataAccess.isCleared(),
                "clearCurrentUser() should be called on logout");

        assertTrue(presenter.isSuccessCalled(),
                "Presenter's prepareSuccessView() should be called");

        assertTrue(presenter.getReceivedData().isSuccess(),
                "LogoutOutputData should indicate success");
    }
}

