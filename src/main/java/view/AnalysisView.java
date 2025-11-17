package view;

import interface_adapter.analysis.AnalysisViewModel;

import javax.swing.*;

public class AnalysisView extends JPanel {

    private static final String VIEW_NAME = "analysisView";

    private final AnalysisViewModel viewModel;

    public AnalysisView(AnalysisViewModel viewModel) {
        this.viewModel = viewModel;
        // ... your UI setup ...
    }

    public String getViewName() {
        return VIEW_NAME;
    }

    public static String getViewNameStatic() {
        return VIEW_NAME;
    }
}
