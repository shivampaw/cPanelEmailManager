package com.shivampaw.cpanelemailmanager.utils;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class JavaFXUtils {

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
        progressDialogStage.initModality(Modality.APPLICATION_MODAL);
        progressDialogStage.setScene(new Scene(updatePane));
        progressDialogStage.show();

        return progressDialogStage;
    }

    public static void showErrorAlert(String text) {
        showErrorAlert("Error", text);
    }

    public static void showErrorAlert(String headerText, String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(headerText);
        alert.setContentText(text);
        alert.show();
    }
}
