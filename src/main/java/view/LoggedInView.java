package view;

import interface_adapter.logged_in.LoggedInViewModel;

import javax.swing.*;

public class LoggedInView extends JPanel {

    private static final String VIEW_NAME = "loggedInView";

    private final LoggedInViewModel viewModel;

    public LoggedInView(LoggedInViewModel viewModel) {
        this.viewModel = viewModel;
        // ... your existing UI setup ...
    }

    // For AppBuilder instance call
    public String getViewName() {
        return VIEW_NAME;
    }

    // For LoginPresenter static call
    public static String getViewNameStatic() {
        return VIEW_NAME;
    }
}

