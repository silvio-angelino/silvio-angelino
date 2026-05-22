package it.unicam.cs.mpgc.rpg130929;

import it.unicam.cs.mpgc.rpg130929.controller.GameController;
import it.unicam.cs.mpgc.rpg130929.repository.JsonGameRepository;
import it.unicam.cs.mpgc.rpg130929.view.WelcomeView;
import javafx.application.Application;
import javafx.stage.Stage;

// punto di ingresso dell'applicazione
public class Main extends Application {

    @Override
    public void start(Stage stage) {
        // creo il repository e il controller
        JsonGameRepository repository = new JsonGameRepository();
        GameController controller = new GameController(repository);

        // mostro la schermata iniziale
        WelcomeView welcome = new WelcomeView(controller, stage);
        welcome.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}