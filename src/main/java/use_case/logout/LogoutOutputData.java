package use_case.logout;

public class LogoutOutputData {
    private final boolean success;

    public LogoutOutputData(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}

