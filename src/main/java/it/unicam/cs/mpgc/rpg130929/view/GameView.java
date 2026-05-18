package it.unicam.cs.mpgc.rpg130929.view;

import it.unicam.cs.mpgc.rpg130929.controller.GameController;
import it.unicam.cs.mpgc.rpg130929.model.*;
import it.unicam.cs.mpgc.rpg130929.repository.GameDataLoader;
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

import java.util.List;

public class GameView {

    private final GameController controller;
    private final Stage stage;
    private Label locationLabel;
    private Label descriptionLabel;
    private ListView<String> cluesList;
    private Label reputationLabel;
    private Label cluesCountLabel;
    private Label statsLabel;
    private Label levelLabel;
    private Label xpLabel;
    private VBox npcPanel;
    private VBox questPanel;
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
                        .getResourceAsStream("PressStart2P-Regular.ttf"), 8);
        this.titleFont = Font.loadFont(
                getClass().getClassLoader()
                        .getResourceAsStream("PressStart2P-Regular.ttf"), 11);
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #000000;");

        root.setTop(buildTopPanel());
        root.setLeft(buildLeftPanel());
        root.setCenter(buildCenterPanel());
        root.setRight(buildRightPanel());
        root.setBottom(buildBottomPanel());

        Scene scene = new Scene(root, 1400, 850);
        scene.setFill(Color.BLACK);

        stage.setTitle("IL CRONISTA - OPERAZIONE OMBRA");
        stage.setScene(scene);
        stage.show();

        mapView.getCanvas().requestFocus();
        updateView();
    }

    private HBox buildTopPanel() {
        HBox top = new HBox(30);
        top.setPadding(new Insets(10, 20, 10, 20));
        top.setAlignment(Pos.CENTER_LEFT);
        top.setStyle(
                "-fx-background-color: #0a0000;" +
                        "-fx-border-color: #FFD700;" +
                        "-fx-border-width: 0 0 2 0;");

        Label title = new Label("// OPERAZIONE OMBRA - 1935 //");
        title.setStyle("-fx-text-fill: #FFD700;");
        if (titleFont != null) title.setFont(titleFont);

        locationLabel = new Label("POSIZIONE: ---");
        locationLabel.setStyle("-fx-text-fill: #00ff41;");
        if (pixelFont != null) locationLabel.setFont(pixelFont);

        reputationLabel = new Label("CREDIBILITA': 0");
        reputationLabel.setStyle("-fx-text-fill: #FFD700;");
        if (pixelFont != null) reputationLabel.setFont(pixelFont);

        levelLabel = new Label("LV.1");
        levelLabel.setStyle("-fx-text-fill: #ff4444;");
        if (pixelFont != null) levelLabel.setFont(pixelFont);

        xpLabel = new Label("XP: 0/100");
        xpLabel.setStyle("-fx-text-fill: #4444ff;");
        if (pixelFont != null) xpLabel.setFont(pixelFont);

        cluesCountLabel = new Label("PROVE: 0/0");
        cluesCountLabel.setStyle("-fx-text-fill: #00ff41;");
        if (pixelFont != null) cluesCountLabel.setFont(pixelFont);

        top.getChildren().addAll(title, locationLabel,
                reputationLabel, levelLabel, xpLabel, cluesCountLabel);
        return top;
    }

    private VBox buildLeftPanel() {
        VBox left = new VBox(10);
        left.setPadding(new Insets(15));
        left.setPrefWidth(270);
        left.setStyle(
                "-fx-background-color: #050505;" +
                        "-fx-border-color: #333333;" +
                        "-fx-border-width: 0 1 0 0;");

        Label scenarioTitle = buildSectionTitle("[ SCENARIO ]");
        descriptionLabel = new Label("");
        descriptionLabel.setStyle("-fx-text-fill: #aaaaaa;");
        descriptionLabel.setWrapText(true);
        if (pixelFont != null)
            descriptionLabel.setFont(
                    Font.font(pixelFont.getFamily(), 8));

        Separator sep1 = buildSeparator();

        Label statsTitle = buildSectionTitle("[ STATISTICHE ]");
        statsLabel = new Label("");
        statsLabel.setStyle("-fx-text-fill: #00ff41;");
        statsLabel.setWrapText(true);
        if (pixelFont != null)
            statsLabel.setFont(Font.font(pixelFont.getFamily(), 8));

        Separator sep2 = buildSeparator();

        Label contactsTitle = buildSectionTitle("[ CONTATTI ]");
        npcPanel = new VBox(6);

        Separator sep3 = buildSeparator();

        Button collectBtn = buildButton(
                "[ CERCA PROVE ]", "#00ff41");
        collectBtn.setOnAction(e -> {
            controller.collectAllCluesInCurrentLocation();
            updateView();
            mapView.getCanvas().requestFocus();
            showMessage("PROVE RACCOLTE NEL LUOGO CORRENTE!");
        });

        left.getChildren().addAll(
                scenarioTitle, descriptionLabel, sep1,
                statsTitle, statsLabel, sep2,
                contactsTitle, npcPanel, sep3, collectBtn);
        return left;
    }

    private VBox buildCenterPanel() {
        VBox center = new VBox(8);
        center.setPadding(new Insets(15));
        center.setAlignment(Pos.TOP_CENTER);
        center.setStyle("-fx-background-color: #000000;");

        Label mapTitle = buildSectionTitle("[ MAPPA CITTA' - 1935 ]");

        mapView = new MapView(controller, this::updateView);

        HBox controls = new HBox(10);
        controls.setAlignment(Pos.CENTER);
        controls.getChildren().addAll(
                buildKeyLabel("W"), buildKeyLabel("A"),
                buildKeyLabel("S"), buildKeyLabel("D"),
                buildKeyLabel("↑"), buildKeyLabel("↓"),
                buildKeyLabel("←"), buildKeyLabel("→")
        );

        Label moveLabel = new Label("= MUOVI AGENTE");
        moveLabel.setStyle("-fx-text-fill: #333333;");
        if (pixelFont != null)
            moveLabel.setFont(Font.font(pixelFont.getFamily(), 7));

        center.getChildren().addAll(
                mapTitle, mapView.getCanvas(), controls, moveLabel);
        return center;
    }

    private VBox buildRightPanel() {
        VBox right = new VBox(10);
        right.setPadding(new Insets(15));
        right.setPrefWidth(280);
        right.setStyle(
                "-fx-background-color: #050505;" +
                        "-fx-border-color: #333333;" +
                        "-fx-border-width: 0 0 0 1;");

        Label dossierTitle = buildSectionTitle("[ DOSSIER ]");

        cluesList = new ListView<>();
        cluesList.setPrefHeight(250);
        cluesList.setStyle(
                "-fx-background-color: #050505;" +
                        "-fx-border-color: #1a1a1a;");

        Separator sep1 = buildSeparator();

        Label questTitle = buildSectionTitle("[ MISSIONI ]");
        questPanel = new VBox(6);

        Separator sep2 = buildSeparator();

        Button writeBtn = buildButton(
                "[ SCRIVI RAPPORTO ]", "#FFD700");
        writeBtn.setOnAction(e -> {
            writeArticle();
            mapView.getCanvas().requestFocus();
        });

        right.getChildren().addAll(
                dossierTitle, cluesList, sep1,
                questTitle, questPanel, sep2, writeBtn);
        return right;
    }

    private HBox buildBottomPanel() {
        HBox bottom = new HBox();
        bottom.setPadding(new Insets(8, 20, 8, 20));
        bottom.setAlignment(Pos.CENTER_LEFT);
        bottom.setStyle(
                "-fx-background-color: #0a0000;" +
                        "-fx-border-color: #FFD700;" +
                        "-fx-border-width: 2 0 0 0;");

        messageLabel = new Label(
                "> Operazione Ombra iniziata. Buona fortuna, Agente.");
        messageLabel.setStyle("-fx-text-fill: #00ff41;");
        if (pixelFont != null)
            messageLabel.setFont(
                    Font.font(pixelFont.getFamily(), 8));

        bottom.getChildren().add(messageLabel);
        return bottom;
    }

    private Label buildSectionTitle(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #FFD700;");
        if (titleFont != null) label.setFont(titleFont);
        return label;
    }

    private Separator buildSeparator() {
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #1a1a1a;");
        return sep;
    }

    private Label buildKeyLabel(String key) {
        Label label = new Label(key);
        label.setStyle(
                "-fx-text-fill: #FFD700;" +
                        "-fx-border-color: #333333;" +
                        "-fx-border-width: 1px;" +
                        "-fx-padding: 3px 6px;" +
                        "-fx-background-color: #111111;");
        if (pixelFont != null)
            label.setFont(Font.font(pixelFont.getFamily(), 8));
        return label;
    }

    private Button buildButton(String text, String color) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle(
                "-fx-background-color: #000000;" +
                        "-fx-text-fill: " + color + ";" +
                        "-fx-border-color: " + color + ";" +
                        "-fx-border-width: 2px;" +
                        "-fx-padding: 10px;");
        if (pixelFont != null) btn.setFont(pixelFont);
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: #000000;" +
                        "-fx-border-color: " + color + ";" +
                        "-fx-border-width: 2px;" +
                        "-fx-padding: 10px;"));
        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: #000000;" +
                        "-fx-text-fill: " + color + ";" +
                        "-fx-border-color: " + color + ";" +
                        "-fx-border-width: 2px;" +
                        "-fx-padding: 10px;"));
        return btn;
    }

    private void showMessage(String message) {
        if (messageTimeline != null) messageTimeline.stop();
        messageLabel.setText("> " + message);
        messageLabel.setStyle("-fx-text-fill: #FFD700;");
        if (pixelFont != null)
            messageLabel.setFont(
                    Font.font(pixelFont.getFamily(), 8));
        messageTimeline = new Timeline(
                new KeyFrame(Duration.seconds(4), e -> {
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

        PlayerStats stats = controller.getJournalist().getStats();
        levelLabel.setText("LV." + stats.getLevel());
        xpLabel.setText("XP: " + stats.getExperience() +
                "/" + (stats.getLevel() * 100));
        statsLabel.setText(
                "INT: " + stats.getIntelligence() +
                        "  CAR: " + stats.getCharisma() +
                        "\nFUR: " + stats.getStealth());

        cluesCountLabel.setText("PROVE: " +
                controller.getDiscoveredCluesCount() +
                "/" + controller.getTotalClues());

        cluesList.getItems().clear();
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
                npcBtn.setFont(
                        Font.font(pixelFont.getFamily(), 7));
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
                showDialogueChoices(npc);
                mapView.getCanvas().requestFocus();
            });
            npcPanel.getChildren().add(npcBtn);
        });

        if (controller.getNpcsInCurrentLocation().isEmpty()) {
            Label noNpc = new Label("// nessun contatto //");
            noNpc.setStyle("-fx-text-fill: #333333;");
            if (pixelFont != null)
                noNpc.setFont(Font.font(pixelFont.getFamily(), 7));
            npcPanel.getChildren().add(noNpc);
        }

        updateQuestPanel();
        if (mapView != null) mapView.refresh();
    }

    private void updateQuestPanel() {
        questPanel.getChildren().clear();
        controller.getActiveQuests().forEach(quest -> {
            Label questLabel = new Label(
                    "▶ " + quest.getTitle() + "\n" +
                            quest.getCompletedObjectivesCount() +
                            "/" + quest.getTotalObjectivesCount() +
                            " obiettivi");
            questLabel.setStyle(
                    "-fx-text-fill: #FFD700;" +
                            "-fx-font-size: 7px;");
            questLabel.setWrapText(true);
            if (pixelFont != null)
                questLabel.setFont(
                        Font.font(pixelFont.getFamily(), 7));
            questPanel.getChildren().add(questLabel);
        });

        controller.getCompletedQuests().forEach(quest -> {
            Label questLabel = new Label(
                    "✓ " + quest.getTitle());
            questLabel.setStyle(
                    "-fx-text-fill: #00ff41;" +
                            "-fx-font-size: 7px;");
            if (pixelFont != null)
                questLabel.setFont(
                        Font.font(pixelFont.getFamily(), 7));
            questPanel.getChildren().add(questLabel);
        });
    }

    private void showDialogueChoices(NPC npc) {
        List<GameDataLoader.ChoiceData> choices =
                controller.getChoicesForNpc(npc.getId());

        if (choices.isEmpty()) {
            showSimpleDialogue(npc);
            return;
        }

        Stage dialogStage = new Stage();
        dialogStage.setTitle("CONTATTO: " + npc.getName());

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #000000;");

        Label nameLabel = new Label(
                npc.getName().toUpperCase() +
                        " | " + npc.getRole());
        nameLabel.setStyle("-fx-text-fill: #FFD700;");
        if (titleFont != null) nameLabel.setFont(titleFont);

        Label introLabel = new Label(
                npc.getDialogues().isEmpty() ? "" :
                        npc.getDialogues().get(0));
        introLabel.setStyle("-fx-text-fill: #aaaaaa;");
        introLabel.setWrapText(true);
        if (pixelFont != null)
            introLabel.setFont(
                    Font.font(pixelFont.getFamily(), 8));

        Label charismaLabel = new Label(
                "Il tuo carisma: " +
                        controller.getJournalist().getStats().getCharisma());
        charismaLabel.setStyle("-fx-text-fill: #444444;");
        if (pixelFont != null)
            charismaLabel.setFont(
                    Font.font(pixelFont.getFamily(), 7));

        VBox choicesBox = new VBox(8);
        Label responseLabel = new Label("");
        responseLabel.setStyle("-fx-text-fill: #00ff41;");
        responseLabel.setWrapText(true);
        if (pixelFont != null)
            responseLabel.setFont(
                    Font.font(pixelFont.getFamily(), 8));

        for (GameDataLoader.ChoiceData choice : choices) {
            boolean canUse = controller.getJournalist()
                    .getStats()
                    .canUseDialogueOption(choice.requiredCharisma);

            Button choiceBtn = new Button(
                    (canUse ? "> " : "[CAR." +
                            choice.requiredCharisma + "] ") +
                            choice.text);
            choiceBtn.setMaxWidth(Double.MAX_VALUE);
            choiceBtn.setWrapText(true);
            choiceBtn.setTextAlignment(TextAlignment.LEFT);

            String btnColor = canUse ? "#00ff41" : "#333333";
            choiceBtn.setStyle(
                    "-fx-background-color: #0a0a0a;" +
                            "-fx-text-fill: " + btnColor + ";" +
                            "-fx-border-color: " + btnColor + ";" +
                            "-fx-border-width: 1px;" +
                            "-fx-padding: 8px;");
            if (pixelFont != null)
                choiceBtn.setFont(
                        Font.font(pixelFont.getFamily(), 7));

            choiceBtn.setOnAction(e -> {
                GameController.DialogueResult result =
                        controller.processChoice(npc.getId(), choice);
                responseLabel.setText(result.response);
                if (result.success) {
                    responseLabel.setStyle(
                            "-fx-text-fill: #00ff41;");
                    if (result.discoveredClue != null) {
                        showMessage("NUOVA PROVA: " +
                                result.discoveredClue.getDescription());
                    }
                    if (result.experienceGained > 0) {
                        showMessage("+" +
                                result.experienceGained + " XP!");
                    }
                } else {
                    responseLabel.setStyle(
                            "-fx-text-fill: #ff4444;");
                }
                updateView();
            });

            choicesBox.getChildren().add(choiceBtn);
        }

        Button closeBtn = buildButton("[ CHIUDI ]", "#FFD700");
        closeBtn.setOnAction(e -> dialogStage.close());

        root.getChildren().addAll(
                nameLabel, introLabel, charismaLabel,
                new Separator(), choicesBox,
                new Separator(), responseLabel, closeBtn);

        Scene dialogScene = new Scene(root, 500, 600);
        dialogScene.setFill(Color.BLACK);
        dialogStage.setScene(dialogScene);
        dialogStage.show();
    }

    private void showSimpleDialogue(NPC npc) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("CONTATTO");
        alert.setHeaderText(npc.getName().toUpperCase() +
                " | " + npc.getRole());
        String dialogue = String.join("\n\n", npc.getDialogues());
        alert.setContentText(dialogue);
        alert.showAndWait();
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
            alert.setHeaderText(
                    "OPERAZIONE OMBRA - COMPLETATA");
            alert.setContentText(
                    "Hai smascherato la rete di spie!\n\n" +
                            "Il tuo rapporto ha raggiunto\n" +
                            "i vertici del governo.\n\n" +
                            "AGENTE " +
                            controller.getJournalist()
                                    .getName().toUpperCase() +
                            " - MISSIONE COMPLETATA!\n\n" +
                            "LIVELLO: " +
                            controller.getJournalist()
                                    .getStats().getLevel() + "\n" +
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