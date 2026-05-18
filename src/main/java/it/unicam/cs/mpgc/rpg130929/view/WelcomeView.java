package it.unicam.cs.mpgc.rpg130929.view;

import it.unicam.cs.mpgc.rpg130929.controller.GameController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class WelcomeView {

    private final GameController controller;
    private final Stage stage;

    public WelcomeView(GameController controller, Stage stage) {
        this.controller = controller;
        this.stage = stage;
    }

    public void show() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);

        Label title = new Label("IL CRONISTA");
        title.setStyle("-fx-font-size: 36px; -fx-font-weight: bold;");

        Label subtitle = new Label("Un gioco di ruolo negli anni '30");
        subtitle.setStyle("-fx-font-size: 18px;");

        Label instructions = new Label(
                "Sei un giornalista investigativo.\n\n" +
                        "Il tuo obiettivo è scoprire la verità!\n\n" +
                        "Come si gioca:\n" +
                        "1. Clicca sui LUOGHI a sinistra per spostarti\n" +
                        "2. Clicca RACCOGLI INDIZI per trovare prove\n" +
                        "3. Quando hai abbastanza indizi, scrivi un ARTICOLO\n" +
                        "4. Pubblica l'articolo per aumentare la tua REPUTAZIONE\n\n" +
                        "Buona fortuna!"
        );
        instructions.setStyle("-fx-font-size: 14px;");
        instructions.setTextAlignment(TextAlignment.CENTER);
        instructions.setWrapText(true);

        Button startBtn = new Button("INIZIA IL GIOCO");
        startBtn.setStyle("-fx-font-size: 16px; -fx-padding: 10px 30px;");
        startBtn.setOnAction(e -> {
            GameView gameView = new GameView(controller, stage);
            gameView.show();
        });

        root.getChildren().addAll(title, subtitle,
                new Separator(), instructions, new Separator(), startBtn);

        Scene scene = new Scene(root, 1024, 768);
        stage.setTitle("Il Cronista");
        stage.setScene(scene);
        stage.show();
    }
}
