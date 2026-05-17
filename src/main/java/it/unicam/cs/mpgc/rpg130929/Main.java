package it.unicam.cs.mpgc.rpg130929;

import it.unicam.cs.mpgc.rpg130929.controller.GameController;
import it.unicam.cs.mpgc.rpg130929.repository.JsonGameRepository;
import it.unicam.cs.mpgc.rpg130929.view.GameView;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        JsonGameRepository repository = new JsonGameRepository();
        GameController controller = new GameController(repository);
        GameView view = new GameView(controller, stage);
        view.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
