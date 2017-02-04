package com.shivampaw.cem.java.util;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class JavaFXUtils {

    /**
     * Create a stage with a new progress dialog
     * @param text Label text
     * @return Stage progressDialogStage
     */
    public static Stage showProgressDialog(String text) {
        Label updateLabel = new Label(text);
        updateLabel.setPrefWidth(400);
        ProgressBar progress = new ProgressBar();
        progress.setPrefWidth(400);

        VBox updatePane = new VBox();
        updatePane.setPadding(new Insets(10));
        updatePane.setSpacing(5.0d);
        updatePane.getChildren().addAll(updateLabel, progress);

        Stage progressDialogStage = new Stage(StageStyle.UTILITY);
        progressDialogStage.setScene(new Scene(updatePane));
        progressDialogStage.show();

        return progressDialogStage;
    }
}
