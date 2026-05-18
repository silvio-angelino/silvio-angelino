package it.unicam.cs.mpgc.rpg130929.view;

import it.unicam.cs.mpgc.rpg130929.controller.GameController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class WelcomeView {

    private final GameController controller;
    private final Stage stage;
    private int currentStep = 0;
    private Label typewriterLabel;
    private Timeline typewriterTimeline;

    private final String[] titles = {
            "IL CRONISTA",
            "ANNO 1935",
            "LA TUA MISSIONE",
            "COME GIOCARE",
            "ATTENZIONE!",
            "SEI PRONTO?"
    };

    private final String[] contents = {
            "Una citta' avvolta\nnell'ombra.\n\nSpie, traditori\ne segreti oscuri\nnascoste dietro\nogni angolo.\n\nTu sei l'unico\nche puo' scoprire\nla verita'...",
            "L'Europa e' sull'orlo\ndella guerra.\n\nReti di spionaggio\ninfiltrano ogni\nistituzione.\n\nIl tuo giornale\nti ha incaricato\ndi scoprire tutto.",
            "Infiltra la rete\ndi spie straniere\nche opera in citta'.\n\nRaccogli le prove,\nparla con i contatti,\nsvela la cospirazione\ne scrivi il rapporto\nche cambiera'\nla storia!",
            "WASD = Muovi il\n        personaggio\n\nEntra nei LUOGHI\nper esplorarli\n\nParla con i\nCONTATTI locali\n\nRaccogli le PROVE\n\nScrivi il RAPPORTO",
            "Le spie sono\novunque.\n\nNon fidarti\ndi nessuno.\n\nOgni prova\nche raccogli\npotrebbe costarti\ncara...\n\nMa la verita'\ndeve emergere!",
            "La citta' aspetta.\n\nLe spie operano\nnell'ombra.\n\nIl tuo giornale\nascolta.\n\nSolo tu puoi\nsvelare tutto.\n\nBuona fortuna,\nAgente..."
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

        if (typewriterTimeline != null) typewriterTimeline.stop();

        StackPane root = new StackPane();

        VBox background = new VBox();
        background.setStyle("-fx-background-color: #000000;");
        background.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        VBox content = new VBox(30);
        content.setPadding(new Insets(60));
        content.setAlignment(Pos.CENTER);

        Font pixelFont = Font.loadFont(
                getClass().getClassLoader()
                        .getResourceAsStream("PressStart2P-Regular.ttf"), 13);

        Font titleFont = Font.loadFont(
                getClass().getClassLoader()
                        .getResourceAsStream("PressStart2P-Regular.ttf"), 22);

        Label stepIndicator = new Label(
                "[ " + (step + 1) + " / " + titles.length + " ]");
        stepIndicator.setStyle(
                "-fx-text-fill: #333333; -fx-font-size: 9px;");
        if (pixelFont != null)
            stepIndicator.setFont(Font.font(pixelFont.getFamily(), 9));

        Label titleLabel = new Label(titles[step]);
        titleLabel.setStyle(
                "-fx-text-fill: #FFD700; -fx-font-size: 24px;");
        if (titleFont != null)
            titleLabel.setFont(Font.font(titleFont.getFamily(), 24));

        Separator sep1 = new Separator();
        sep1.setStyle("-fx-background-color: #00ff41;");
        sep1.setMaxWidth(700);

        typewriterLabel = new Label("");
        typewriterLabel.setStyle(
                "-fx-text-fill: #00ff41; -fx-font-size: 13px;");
        typewriterLabel.setWrapText(true);
        typewriterLabel.setTextAlignment(TextAlignment.CENTER);
        typewriterLabel.setAlignment(Pos.CENTER);
        typewriterLabel.setMaxWidth(700);
        if (pixelFont != null)
            typewriterLabel.setFont(Font.font(pixelFont.getFamily(), 13));

        startTypewriterEffect(contents[step]);

        Separator sep2 = new Separator();
        sep2.setStyle("-fx-background-color: #00ff41;");
        sep2.setMaxWidth(700);

        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER);

        if (step > 0) {
            Button prevBtn = new Button("< INDIETRO");
            prevBtn.setStyle(
                    "-fx-background-color: #000000; " +
                            "-fx-text-fill: #00ff41; " +
                            "-fx-border-color: #00ff41; " +
                            "-fx-border-width: 2px; " +
                            "-fx-font-size: 9px; " +
                            "-fx-padding: 10px 20px;");
            if (pixelFont != null)
                prevBtn.setFont(Font.font(pixelFont.getFamily(), 9));
            prevBtn.setOnAction(e -> showStep(step - 1));
            prevBtn.setOnMouseEntered(e -> prevBtn.setStyle(
                    "-fx-background-color: #00ff41; " +
                            "-fx-text-fill: #000000; " +
                            "-fx-border-color: #00ff41; " +
                            "-fx-border-width: 2px; " +
                            "-fx-font-size: 9px; " +
                            "-fx-padding: 10px 20px;"));
            prevBtn.setOnMouseExited(e -> prevBtn.setStyle(
                    "-fx-background-color: #000000; " +
                            "-fx-text-fill: #00ff41; " +
                            "-fx-border-color: #00ff41; " +
                            "-fx-border-width: 2px; " +
                            "-fx-font-size: 9px; " +
                            "-fx-padding: 10px 20px;"));
            buttons.getChildren().add(prevBtn);
        }

        if (step < titles.length - 1) {
            Button nextBtn = new Button("AVANTI >");
            nextBtn.setStyle(
                    "-fx-background-color: #000000; " +
                            "-fx-text-fill: #00ff41; " +
                            "-fx-border-color: #00ff41; " +
                            "-fx-border-width: 2px; " +
                            "-fx-font-size: 9px; " +
                            "-fx-padding: 10px 20px;");
            if (pixelFont != null)
                nextBtn.setFont(Font.font(pixelFont.getFamily(), 9));
            nextBtn.setOnAction(e -> showStep(step + 1));
            nextBtn.setOnMouseEntered(e -> nextBtn.setStyle(
                    "-fx-background-color: #00ff41; " +
                            "-fx-text-fill: #000000; " +
                            "-fx-border-color: #00ff41; " +
                            "-fx-border-width: 2px; " +
                            "-fx-font-size: 9px; " +
                            "-fx-padding: 10px 20px;"));
            nextBtn.setOnMouseExited(e -> nextBtn.setStyle(
                    "-fx-background-color: #000000; " +
                            "-fx-text-fill: #00ff41; " +
                            "-fx-border-color: #00ff41; " +
                            "-fx-border-width: 2px; " +
                            "-fx-font-size: 9px; " +
                            "-fx-padding: 10px 20px;"));
            buttons.getChildren().add(nextBtn);
        } else {
            Button startBtn = new Button(">> INIZIA MISSIONE <<");
            startBtn.setStyle(
                    "-fx-background-color: #000000; " +
                            "-fx-text-fill: #FFD700; " +
                            "-fx-border-color: #FFD700; " +
                            "-fx-border-width: 3px; " +
                            "-fx-font-size: 12px; " +
                            "-fx-padding: 15px 30px;");
            if (titleFont != null)
                startBtn.setFont(Font.font(titleFont.getFamily(), 12));
            startBtn.setOnAction(e -> {
                if (typewriterTimeline != null) typewriterTimeline.stop();
                GameView gameView = new GameView(controller, stage);
                gameView.show();
            });
            startBtn.setOnMouseEntered(e -> startBtn.setStyle(
                    "-fx-background-color: #FFD700; " +
                            "-fx-text-fill: #000000; " +
                            "-fx-border-color: #FFD700; " +
                            "-fx-border-width: 3px; " +
                            "-fx-font-size: 12px; " +
                            "-fx-padding: 15px 30px;"));
            startBtn.setOnMouseExited(e -> startBtn.setStyle(
                    "-fx-background-color: #000000; " +
                            "-fx-text-fill: #FFD700; " +
                            "-fx-border-color: #FFD700; " +
                            "-fx-border-width: 3px; " +
                            "-fx-font-size: 12px; " +
                            "-fx-padding: 15px 30px;"));
            buttons.getChildren().add(startBtn);
        }

        content.getChildren().addAll(
                stepIndicator, titleLabel, sep1,
                typewriterLabel, sep2, buttons);

        root.getChildren().addAll(background, content);

        Scene scene = new Scene(root, 1024, 768);
        scene.setFill(Color.BLACK);
        stage.setTitle("IL CRONISTA - OPERAZIONE OMBRA");
        stage.setScene(scene);
        stage.show();
    }

    private void startTypewriterEffect(String text) {
        typewriterLabel.setText("");
        final int[] index = {0};
        typewriterTimeline = new Timeline(
                new KeyFrame(Duration.millis(30), e -> {
                    if (index[0] < text.length()) {
                        typewriterLabel.setText(
                                typewriterLabel.getText() +
                                        text.charAt(index[0]));
                        index[0]++;
                    }
                })
        );
        typewriterTimeline.setCycleCount(text.length());
        typewriterTimeline.play();
    }
}