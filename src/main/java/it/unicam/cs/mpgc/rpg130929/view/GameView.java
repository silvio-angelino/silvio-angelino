package it.unicam.cs.mpgc.rpg130929.view;

import it.unicam.cs.mpgc.rpg130929.controller.GameController;
import it.unicam.cs.mpgc.rpg130929.model.*;
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

public class GameView {

    private final GameController controller;
    private final Stage stage;
    private Label locationLabel;
    private Label descriptionLabel;
    private ListView<String> cluesList;
    private Label reputationLabel;
    private Label cluesCountLabel;
    private VBox npcPanel;
    private MapView mapView;
    private Font pixelFont;
    private Font titleFont;
    private Label messageLabel;
    private Timeline messageTimeline;

    public GameView(GameController controller, Stage stage) {
        this.controller = controller;
        this.stage = stage;
        this.pixelFont = Font.loadFont(
                getClass().getClassLoader()
                        .getResourceAsStream("PressStart2P-Regular.ttf"), 9);
        this.titleFont = Font.loadFont(
                getClass().getClassLoader()
                        .getResourceAsStream("PressStart2P-Regular.ttf"), 12);
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #000000;");
        root.setPadding(new Insets(0));

        root.setTop(buildTopPanel());
        root.setLeft(buildLeftPanel());
        root.setCenter(buildCenterPanel());
        root.setRight(buildRightPanel());
        root.setBottom(buildBottomPanel());

        Scene scene = new Scene(root, 1280, 800);
        scene.setFill(Color.BLACK);

        stage.setTitle("IL CRONISTA - OPERAZIONE OMBRA");
        stage.setScene(scene);
        stage.show();

        mapView.getCanvas().requestFocus();
        updateView();
    }

    private HBox buildTopPanel() {
        HBox top = new HBox();
        top.setPadding(new Insets(12, 20, 12, 20));
        top.setAlignment(Pos.CENTER_LEFT);
        top.setSpacing(40);
        top.setStyle(
                "-fx-background-color: #000000;" +
                        "-fx-border-color: #FFD700;" +
                        "-fx-border-width: 0 0 2 0;");

        Label title = new Label("// OPERAZIONE OMBRA //");
        title.setStyle("-fx-text-fill: #FFD700;");
        if (titleFont != null) title.setFont(titleFont);

        locationLabel = new Label("POSIZIONE: ---");
        locationLabel.setStyle("-fx-text-fill: #00ff41;");
        if (pixelFont != null) locationLabel.setFont(pixelFont);

        reputationLabel = new Label("CREDIBILITA': 0");
        reputationLabel.setStyle("-fx-text-fill: #FFD700;");
        if (pixelFont != null) reputationLabel.setFont(pixelFont);

        cluesCountLabel = new Label("PROVE: 0/0");
        cluesCountLabel.setStyle("-fx-text-fill: #00ff41;");
        if (pixelFont != null) cluesCountLabel.setFont(pixelFont);

        Label year = new Label("ANNO 1935");
        year.setStyle("-fx-text-fill: #444444;");
        if (pixelFont != null) year.setFont(pixelFont);

        top.getChildren().addAll(title, locationLabel,
                reputationLabel, cluesCountLabel, year);
        return top;
    }

    private VBox buildLeftPanel() {
        VBox left = new VBox(12);
        left.setPadding(new Insets(20));
        left.setPrefWidth(260);
        left.setStyle(
                "-fx-background-color: #050505;" +
                        "-fx-border-color: #00ff41;" +
                        "-fx-border-width: 0 1 0 0;");

        Label scenarioTitle = new Label("[ SCENARIO ]");
        scenarioTitle.setStyle("-fx-text-fill: #FFD700;");
        if (titleFont != null) scenarioTitle.setFont(titleFont);

        descriptionLabel = new Label("");
        descriptionLabel.setStyle(
                "-fx-text-fill: #00cc33;" +
                        "-fx-font-size: 9px;");
        descriptionLabel.setWrapText(true);
        descriptionLabel.setTextAlignment(TextAlignment.LEFT);
        if (pixelFont != null)
            descriptionLabel.setFont(Font.font(pixelFont.getFamily(), 9));

        Separator sep1 = new Separator();
        sep1.setStyle("-fx-background-color: #1a1a1a;");

        Label contactsTitle = new Label("[ CONTATTI ]");
        contactsTitle.setStyle("-fx-text-fill: #FFD700;");
        if (titleFont != null) contactsTitle.setFont(titleFont);

        npcPanel = new VBox(8);

        Separator sep2 = new Separator();
        sep2.setStyle("-fx-background-color: #1a1a1a;");

        Button collectBtn = new Button("[ CERCA PROVE ]");
        collectBtn.setMaxWidth(Double.MAX_VALUE);
        collectBtn.setStyle(
                "-fx-background-color: #000000;" +
                        "-fx-text-fill: #00ff41;" +
                        "-fx-border-color: #00ff41;" +
                        "-fx-border-width: 2px;" +
                        "-fx-padding: 10px;");
        if (pixelFont != null) collectBtn.setFont(pixelFont);
        collectBtn.setOnMouseEntered(e -> collectBtn.setStyle(
                "-fx-background-color: #00ff41;" +
                        "-fx-text-fill: #000000;" +
                        "-fx-border-color: #00ff41;" +
                        "-fx-border-width: 2px;" +
                        "-fx-padding: 10px;"));
        collectBtn.setOnMouseExited(e -> collectBtn.setStyle(
                "-fx-background-color: #000000;" +
                        "-fx-text-fill: #00ff41;" +
                        "-fx-border-color: #00ff41;" +
                        "-fx-border-width: 2px;" +
                        "-fx-padding: 10px;"));
        collectBtn.setOnAction(e -> {
            controller.collectAllCluesInCurrentLocation();
            updateView();
            mapView.getCanvas().requestFocus();
            showMessage("PROVE RACCOLTE NEL LUOGO CORRENTE!");
        });

        left.getChildren().addAll(scenarioTitle, descriptionLabel,
                sep1, contactsTitle, npcPanel, sep2, collectBtn);
        return left;
    }

    private VBox buildCenterPanel() {
        VBox center = new VBox(10);
        center.setPadding(new Insets(15));
        center.setAlignment(Pos.TOP_CENTER);
        center.setStyle("-fx-background-color: #000000;");

        Label mapTitle = new Label("[ MAPPA CITTA' - 1935 ]");
        mapTitle.setStyle("-fx-text-fill: #FFD700;");
        if (titleFont != null) mapTitle.setFont(titleFont);

        mapView = new MapView(controller, () -> {
            updateView();
        });

        HBox controls = new HBox(20);
        controls.setAlignment(Pos.CENTER);

        Label wKey = buildKeyLabel("W");
        Label aKey = buildKeyLabel("A");
        Label sKey = buildKeyLabel("S");
        Label dKey = buildKeyLabel("D");
        Label moveLabel = new Label("= MUOVI");
        moveLabel.setStyle("-fx-text-fill: #444444;");
        if (pixelFont != null) moveLabel.setFont(
                Font.font(pixelFont.getFamily(), 8));

        controls.getChildren().addAll(wKey, aKey, sKey, dKey, moveLabel);

        center.getChildren().addAll(mapTitle, mapView.getCanvas(), controls);
        return center;
    }

    private Label buildKeyLabel(String key) {
        Label label = new Label(key);
        label.setStyle(
                "-fx-text-fill: #FFD700;" +
                        "-fx-border-color: #FFD700;" +
                        "-fx-border-width: 1px;" +
                        "-fx-padding: 4px 8px;" +
                        "-fx-background-color: #111111;");
        if (pixelFont != null)
            label.setFont(Font.font(pixelFont.getFamily(), 9));
        return label;
    }

    private VBox buildRightPanel() {
        VBox right = new VBox(12);
        right.setPadding(new Insets(20));
        right.setPrefWidth(280);
        right.setStyle(
                "-fx-background-color: #050505;" +
                        "-fx-border-color: #00ff41;" +
                        "-fx-border-width: 0 0 0 1;");

        Label dossierTitle = new Label("[ DOSSIER ]");
        dossierTitle.setStyle("-fx-text-fill: #FFD700;");
        if (titleFont != null) dossierTitle.setFont(titleFont);

        Label dossierHint = new Label(
                "Prove raccolte durante\nl'operazione:");
        dossierHint.setStyle("-fx-text-fill: #444444;");
        if (pixelFont != null)
            dossierHint.setFont(Font.font(pixelFont.getFamily(), 8));

        cluesList = new ListView<>();
        cluesList.setPrefHeight(350);
        cluesList.setStyle(
                "-fx-background-color: #050505;" +
                        "-fx-border-color: #1a1a1a;" +
                        "-fx-border-width: 1px;");

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #1a1a1a;");

        Label statsTitle = new Label("[ STATISTICHE ]");
        statsTitle.setStyle("-fx-text-fill: #FFD700;");
        if (titleFont != null) statsTitle.setFont(titleFont);

        Label statsHint = new Label(
                "Raccogli tutte le prove\ne scrivi il rapporto\nper completare\nla missione!");
        statsHint.setStyle("-fx-text-fill: #444444;");
        statsHint.setWrapText(true);
        if (pixelFont != null)
            statsHint.setFont(Font.font(pixelFont.getFamily(), 8));

        Button writeBtn = new Button("[ SCRIVI RAPPORTO ]");
        writeBtn.setMaxWidth(Double.MAX_VALUE);
        writeBtn.setStyle(
                "-fx-background-color: #000000;" +
                        "-fx-text-fill: #FFD700;" +
                        "-fx-border-color: #FFD700;" +
                        "-fx-border-width: 2px;" +
                        "-fx-padding: 10px;");
        if (pixelFont != null) writeBtn.setFont(pixelFont);
        writeBtn.setOnMouseEntered(e -> writeBtn.setStyle(
                "-fx-background-color: #FFD700;" +
                        "-fx-text-fill: #000000;" +
                        "-fx-border-color: #FFD700;" +
                        "-fx-border-width: 2px;" +
                        "-fx-padding: 10px;"));
        writeBtn.setOnMouseExited(e -> writeBtn.setStyle(
                "-fx-background-color: #000000;" +
                        "-fx-text-fill: #FFD700;" +
                        "-fx-border-color: #FFD700;" +
                        "-fx-border-width: 2px;" +
                        "-fx-padding: 10px;"));
        writeBtn.setOnAction(e -> {
            writeArticle();
            mapView.getCanvas().requestFocus();
        });

        right.getChildren().addAll(dossierTitle, dossierHint,
                cluesList, sep, statsTitle, statsHint, writeBtn);
        return right;
    }

    private HBox buildBottomPanel() {
        HBox bottom = new HBox();
        bottom.setPadding(new Insets(8, 20, 8, 20));
        bottom.setAlignment(Pos.CENTER_LEFT);
        bottom.setStyle(
                "-fx-background-color: #000000;" +
                        "-fx-border-color: #FFD700;" +
                        "-fx-border-width: 2 0 0 0;");

        messageLabel = new Label(
                "> Benvenuto, Agente. La missione ha inizio...");
        messageLabel.setStyle("-fx-text-fill: #00ff41;");
        if (pixelFont != null)
            messageLabel.setFont(Font.font(pixelFont.getFamily(), 9));

        bottom.getChildren().add(messageLabel);
        return bottom;
    }

    private void showMessage(String message) {
        if (messageTimeline != null) messageTimeline.stop();
        messageLabel.setText("> " + message);
        messageLabel.setStyle("-fx-text-fill: #FFD700;");
        messageTimeline = new Timeline(
                new KeyFrame(Duration.seconds(3), e -> {
                    messageLabel.setText("> In attesa di ordini...");
                    messageLabel.setStyle("-fx-text-fill: #00ff41;");
                })
        );
        messageTimeline.play();
    }

    private void updateView() {
        Location current = controller.getCurrentLocation();
        locationLabel.setText("POSIZIONE: " +
                current.getName().toUpperCase());
        descriptionLabel.setText(current.getDescription());
        reputationLabel.setText("CREDIBILITA': " +
                controller.getJournalist().getReputation());
        cluesCountLabel.setText("PROVE: " +
                controller.getDiscoveredCluesCount() +
                "/" + controller.getTotalClues());

        cluesList.getItems().clear();
        cluesList.setStyle(
                "-fx-background-color: #050505;" +
                        "-fx-border-color: #1a1a1a;");
        controller.getJournalist().getNotebook()
                .forEach(clue -> cluesList.getItems()
                        .add("> " + clue.getDescription()));

        npcPanel.getChildren().clear();
        controller.getNpcsInCurrentLocation().forEach(npc -> {
            Button npcBtn = new Button(
                    npc.getName() + "\n[" + npc.getRole() + "]");
            npcBtn.setMaxWidth(Double.MAX_VALUE);
            npcBtn.setStyle(
                    "-fx-background-color: #0a0a0a;" +
                            "-fx-text-fill: #00ff41;" +
                            "-fx-border-color: #1a3a1a;" +
                            "-fx-border-width: 1px;" +
                            "-fx-padding: 8px;");
            if (pixelFont != null)
                npcBtn.setFont(Font.font(pixelFont.getFamily(), 8));
            npcBtn.setOnMouseEntered(e -> npcBtn.setStyle(
                    "-fx-background-color: #001a00;" +
                            "-fx-text-fill: #00ff41;" +
                            "-fx-border-color: #00ff41;" +
                            "-fx-border-width: 1px;" +
                            "-fx-padding: 8px;"));
            npcBtn.setOnMouseExited(e -> npcBtn.setStyle(
                    "-fx-background-color: #0a0a0a;" +
                            "-fx-text-fill: #00ff41;" +
                            "-fx-border-color: #1a3a1a;" +
                            "-fx-border-width: 1px;" +
                            "-fx-padding: 8px;"));
            npcBtn.setOnAction(e -> {
                showNpcDialogue(npc);
                mapView.getCanvas().requestFocus();
            });
            npcPanel.getChildren().add(npcBtn);
        });

        if (controller.getNpcsInCurrentLocation().isEmpty()) {
            Label noNpc = new Label("// nessun contatto //");
            noNpc.setStyle("-fx-text-fill: #333333;");
            if (pixelFont != null)
                noNpc.setFont(Font.font(pixelFont.getFamily(), 8));
            npcPanel.getChildren().add(noNpc);
        }

        if (mapView != null) mapView.refresh();
    }

    private void showNpcDialogue(NPC npc) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("CONTATTO SEGRETO");
        alert.setHeaderText(
                npc.getName().toUpperCase() + " | " + npc.getRole());
        String dialogue = String.join("\n\n", npc.getDialogues());
        alert.setContentText(dialogue);
        alert.showAndWait();

        npc.getCluesProvided().forEach(clue -> {
            if (!clue.isDiscovered()) {
                controller.collectClue(clue);
                showMessage("NUOVA PROVA: " + clue.getDescription());
            }
        });

        updateView();
    }

    private void writeArticle() {
        if (controller.getJournalist().getNotebook().isEmpty()) {
            showMessage("ERRORE: Nessuna prova raccolta!");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("RAPPORTO SEGRETO");
        dialog.setHeaderText("Invia il rapporto all'agenzia");
        dialog.setContentText("Titolo del rapporto:");
        dialog.showAndWait().ifPresent(title -> {
            if (!title.isEmpty()) {
                Article article = controller.createArticle(title);
                controller.getJournalist().getNotebook()
                        .forEach(article::addClue);
                controller.publishArticle(article);
                updateView();
                checkVictory();
            }
        });
    }

    private void checkVictory() {
        if (controller.getJournalist().getReputation() >= 100) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("MISSIONE COMPIUTA!");
            alert.setHeaderText("OPERAZIONE OMBRA - COMPLETATA");
            alert.setContentText(
                    "Hai smascherato la rete di spie!\n\n" +
                            "Il tuo rapporto ha raggiunto\n" +
                            "i vertici del governo.\n\n" +
                            "La rete di spionaggio e' stata\n" +
                            "neutralizzata!\n\n" +
                            "AGENTE " +
                            controller.getJournalist().getName().toUpperCase() +
                            " - MISSIONE COMPLETATA!\n\n" +
                            "CREDIBILITA' FINALE: " +
                            controller.getJournalist().getReputation());
            alert.showAndWait();
        } else {
            showMessage("RAPPORTO INVIATO! CREDIBILITA': " +
                    controller.getJournalist().getReputation() +
                    " - Continua a raccogliere prove!");
        }
    }
}