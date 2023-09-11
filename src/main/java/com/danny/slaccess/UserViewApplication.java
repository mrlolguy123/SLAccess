package com.danny.slaccess;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class UserViewApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("user-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Sun Lab Admin Portal");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}