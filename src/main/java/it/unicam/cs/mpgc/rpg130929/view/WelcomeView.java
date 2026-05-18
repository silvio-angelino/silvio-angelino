package it.unicam.cs.mpgc.rpg130929.view;

import it.unicam.cs.mpgc.rpg130929.controller.GameController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class WelcomeView {

    private final GameController controller;
    private final Stage stage;
    private int currentStep = 0;

    private final String[] titles = {
            "IL CRONISTA",
            "LA TUA MISSIONE",
            "COME GIOCARE",
            "CONSIGLI",
            "PRONTO?"
    };

    private final String[] contents = {
            "Anno 1935.\n\nSei Silvio Angelino,\nun giornalista\ninvestigativo.\n\nLa città nasconde\nsegreti oscuri...",
            "Il sindaco è accusato\ndi corruzione.\n\nDevi raccogliere\nle prove e scrivere\nun articolo\nche scuota la città!",
            "1. Spostati tra\n   i LUOGHI\n\n2. Raccogli INDIZI\n   esplorando\n\n3. Parla con i\n   PERSONAGGI\n\n4. Scrivi e pubblica\n   un ARTICOLO",
            "- Visita tutti\n  i luoghi\n\n- Parla con ogni\n  personaggio\n\n- Più indizi usi,\n  più reputazione\n  guadagni!\n\n- Raggiungi 100\n  di reputazione!",
            "La città aspetta\nla verità.\n\nIl tuo giornale\naspetta l'articolo.\n\nSei pronto\na scoprire\nla verità?"
    };

    public WelcomeView(GameController controller, Stage stage) {
        this.controller = controller;
        this.stage = stage;
    }

    public void show() {
        showStep(0);
    }

    private void showStep(int step) {
        currentStep = step;

        VBox root = new VBox(30);
        root.setPadding(new Insets(50));
        root.setAlignment(Pos.CENTER);

        Font pixelFont = Font.loadFont(
                getClass().getClassLoader().getResourceAsStream("PressStart2P-Regular.ttf"),
                12
        );

        Label titleLabel = new Label(titles[step]);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #FFD700;");
        if (pixelFont != null) titleLabel.setFont(Font.font(pixelFont.getFamily(), 16));

        Label contentLabel = new Label(contents[step]);
        contentLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #00ff41;");
        contentLabel.setWrapText(true);
        contentLabel.setAlignment(Pos.CENTER);
        if (pixelFont != null) contentLabel.setFont(Font.font(pixelFont.getFamily(), 11));

        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER);

        if (step > 0) {
            Button prevBtn = new Button("< INDIETRO");
            prevBtn.setOnAction(e -> showStep(step - 1));
            buttons.getChildren().add(prevBtn);
        }

        if (step < titles.length - 1) {
            Button nextBtn = new Button("AVANTI >");
            nextBtn.setOnAction(e -> showStep(step + 1));
            buttons.getChildren().add(nextBtn);
        } else {
            Button startBtn = new Button(">> INIZIA <<");
            startBtn.setStyle("-fx-font-size: 14px; -fx-padding: 12px 24px;");
            startBtn.setOnAction(e -> {
                GameView gameView = new GameView(controller, stage);
                gameView.show();
            });
            buttons.getChildren().add(startBtn);
        }

        Label stepLabel = new Label((step + 1) + " / " + titles.length);
        stepLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 9px;");

        root.getChildren().addAll(titleLabel, new Separator(),
                contentLabel, new Separator(), buttons, stepLabel);

        Scene scene = new Scene(root, 1024, 768);
        scene.getStylesheets().add(
                getClass().getClassLoader().getResource("style.css").toExternalForm()
        );
        stage.setScene(scene);
        stage.show();
    }
}