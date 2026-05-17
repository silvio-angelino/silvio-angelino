package it.unicam.cs.mpgc.rpg130929;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Il Cronista");
        stage.setWidth(1024);
        stage.setHeight(768);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
