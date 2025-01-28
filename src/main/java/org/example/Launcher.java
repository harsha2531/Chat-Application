package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(FXMLLoader.load(this.getClass().getResource("/view/server_form.fxml"))));
        Stage stage1 = new Stage();
        stage1.setScene(new Scene(FXMLLoader.load(this.getClass().getResource("/view/client_form.fxml"))));
        Stage stage2 = new Stage();
        stage2.setScene(new Scene(FXMLLoader.load(this.getClass().getResource("/view/client_form_2.fxml"))));

        stage.setTitle("Server Form");
        stage1.setTitle("Client Form 1");
        stage2.setTitle("Client Form 2");
        stage.centerOnScreen();
        stage1.centerOnScreen();
        stage2.centerOnScreen();

        stage.show();
        stage1.show();
        stage2.show();

    }
}
