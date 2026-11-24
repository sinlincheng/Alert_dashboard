package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.ui.DashboardView;

import java.net.URL;

public class DashboardApp extends Application {

    private static final String CSS_PATH = "/css/dashboard.css";

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(new DashboardView(), 900, 580);

        URL css = DashboardApp.class.getResource(CSS_PATH);
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        } else {
            System.err.println("Stylesheet not found: " + CSS_PATH
                    + " (check it is in src/main/resources/css/)");
        }

        stage.setTitle("Alert Triage Dashboard");
        stage.setMinWidth(820);
        stage.setMinHeight(520);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}