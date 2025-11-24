package use_case.logout;

public interface LogoutOutputBoundary {
    void prepareSuccessView(LogoutOutputData outputData);
    // (optionally) void prepareFailView(String error);
}


