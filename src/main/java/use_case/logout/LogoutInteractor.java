package use_case.logout;

public class LogoutInteractor implements LogoutInputBoundary {

    private final LogoutUserDataAccessInterface userDataAccess;
    private final LogoutOutputBoundary presenter;

    public LogoutInteractor(LogoutUserDataAccessInterface userDataAccess,
                            LogoutOutputBoundary presenter) {
        this.userDataAccess = userDataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute() {
        // 1. clear "current user"
        userDataAccess.clearCurrentUser();

        // 2. create output data
        LogoutOutputData outputData = new LogoutOutputData(true);

        // 3. send to presenter
        presenter.prepareSuccessView(outputData);
    }
}


