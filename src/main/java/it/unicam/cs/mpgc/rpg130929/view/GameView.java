package it.unicam.cs.mpgc.rpg130929.view;

import it.unicam.cs.mpgc.rpg130929.controller.GameController;
import it.unicam.cs.mpgc.rpg130929.model.*;
import it.unicam.cs.mpgc.rpg130929.repository.GameDataLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;

// vista principale del gioco, gestisce tutta la GUI
public class GameView {

    private final GameController controller;
    private final Stage stage;

    // label per le info in alto
    private Label locationLabel;
    private Label cluesCountLabel;
    private Label levelLabel;
    private Label daysLabel;

    // pannello sinistro
    private Label objectiveLabel;
    private Label statsLabel;
    private VBox npcPanel;

    // barre canvas
    private Canvas suspicionBar;
    private Canvas xpBar;
    private Canvas repBar;
    private Label xpLabel;
    private Label repLabel;

    // pannello destro
    private VBox questPanel;
    private VBox messageLog;
    private ListView<String> cluesList;

    // mappa e pulsante prove
    private MapView mapView;
    private Button collectBtn;

    // font pixel art
    private Font pixelFont;
    private Font titleFont;
    private Font bigFont;

    // percorso del foglio di stile, applicato ai dialoghi nativi
    // (Alert, TextInputDialog) per uniformarli al tema del gioco
    private final String styleSheetPath;

    // colori tema sepia
    private static final String GOLD = "#c8a96e";
    private static final String DARK_GOLD = "#8B6914";
    private static final String BG_DARK = "#050200";
    private static final String BG_PANEL = "#0a0500";
    private static final String BG_PANEL2 = "#080400";
    private static final String TEXT_DIM = "#6b5530";
    private static final String RED = "#cc3300";
    private static final String GREEN = "#336600";

    public GameView(GameController controller, Stage stage) {
        this.controller = controller;
        this.stage = stage;

        // carico il font pixel art dalle risorse
        this.pixelFont = Font.loadFont(
                getClass().getClassLoader()
                        .getResourceAsStream("PressStart2P-Regular.ttf"), 8);
        this.titleFont = Font.loadFont(
                getClass().getClassLoader()
                        .getResourceAsStream("PressStart2P-Regular.ttf"), 10);
        this.bigFont = Font.loadFont(
                getClass().getClassLoader()
                        .getResourceAsStream("PressStart2P-Regular.ttf"), 13);

        // carico il foglio di stile una sola volta, se presente
        URL cssUrl = getClass().getClassLoader().getResource("style.css");
        this.styleSheetPath = cssUrl != null ? cssUrl.toExternalForm() : null;
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG_DARK + ";");

        root.setTop(buildTopPanel());
        root.setLeft(buildLeftPanel());
        root.setCenter(buildCenterPanel());
        root.setRight(buildRightPanel());
        root.setBottom(buildBottomPanel());

        Scene scene = new Scene(root, 1400, 860);
        scene.setFill(Color.web(BG_DARK));

        stage.setTitle("IL CRONISTA - OPERAZIONE OMBRA");
        stage.setScene(scene);
        stage.show();

        mapView.getCanvas().requestFocus();
        updateView();
        addMessage("Operazione Ombra iniziata. Hai " +
                controller.getDaysRemaining() +
                " giorni per smascherare la rete di spie.");
    }

    // pannello in alto con le info principali
    private HBox buildTopPanel() {
        HBox top = new HBox(25);
        top.setPadding(new Insets(10, 20, 10, 20));
        top.setAlignment(Pos.CENTER_LEFT);
        top.setStyle(
                "-fx-background-color: " + BG_PANEL + ";" +
                        "-fx-border-color: " + DARK_GOLD + ";" +
                        "-fx-border-width: 0 0 2 0;");

        Label title = new Label("// OPERAZIONE OMBRA //");
        title.setStyle("-fx-text-fill: " + GOLD + ";");
        if (titleFont != null) title.setFont(titleFont);

        locationLabel = new Label("---");
        locationLabel.setStyle("-fx-text-fill: " + GOLD + ";");
        if (pixelFont != null) locationLabel.setFont(pixelFont);

        daysLabel = new Label("GIORNI: 10");
        daysLabel.setStyle("-fx-text-fill: " + GOLD + ";");
        if (pixelFont != null) daysLabel.setFont(pixelFont);

        levelLabel = new Label("LV.1");
        levelLabel.setStyle("-fx-text-fill: " + GOLD + ";");
        if (pixelFont != null) levelLabel.setFont(pixelFont);

        cluesCountLabel = new Label("PROVE: 0/0");
        cluesCountLabel.setStyle("-fx-text-fill: " + GOLD + ";");
        if (pixelFont != null) cluesCountLabel.setFont(pixelFont);

        // barra del sospetto
        BarBox suspicionBox = buildBarBox("SOSPETTO", 150, RED);
        suspicionBar = suspicionBox.canvas;

        top.getChildren().addAll(title, locationLabel,
                daysLabel, levelLabel, cluesCountLabel, suspicionBox.container);
        return top;
    }

    // pannello sinistro con agente, obiettivo e contatti
    private VBox buildLeftPanel() {
        VBox left = new VBox(12);
        left.setPadding(new Insets(15));
        left.setPrefWidth(265);
        left.setStyle(
                "-fx-background-color: " + BG_PANEL + ";" +
                        "-fx-border-color: " + DARK_GOLD + ";" +
                        "-fx-border-width: 0 1 0 0;");

        Label charTitle = buildTitle("[ AGENTE ]");
        statsLabel = new Label("");
        statsLabel.setStyle("-fx-text-fill: " + GOLD + ";");
        statsLabel.setWrapText(true);
        if (pixelFont != null)
            statsLabel.setFont(Font.font(pixelFont.getFamily(), 8));

        Separator sep1 = buildSeparator();

        // obiettivo in grande così il giocatore sa sempre cosa fare
        Label objTitle = buildTitle("[ OBIETTIVO ]");
        objectiveLabel = new Label("");
        objectiveLabel.setStyle(
                "-fx-text-fill: #ffcc44;" +
                        "-fx-background-color: #1a0800;" +
                        "-fx-border-color: #ffcc44;" +
                        "-fx-border-width: 2px;" +
                        "-fx-padding: 12px;");
        objectiveLabel.setWrapText(true);
        objectiveLabel.setMaxWidth(230);
        if (bigFont != null)
            objectiveLabel.setFont(bigFont);

        Separator sep2 = buildSeparator();

        Label npcTitle = buildTitle("[ CONTATTI ]");
        npcPanel = new VBox(6);

        left.getChildren().addAll(charTitle, statsLabel, sep1,
                objTitle, objectiveLabel, sep2, npcTitle, npcPanel);
        return left;
    }

    // pannello centrale con mappa e barre
    private VBox buildCenterPanel() {
        VBox center = new VBox(8);
        center.setPadding(new Insets(12));
        center.setAlignment(Pos.TOP_CENTER);
        center.setStyle("-fx-background-color: " + BG_DARK + ";");

        Label mapTitle = buildTitle("[ MAPPA - CITTA' 1935 ]");

        mapView = new MapView(controller, () -> {
            updateView();
            addMessage("Ti sei spostato in " +
                    controller.getCurrentLocation().getName() + ".");
            checkGameState();
        });

        // barre xp e credibilita sotto la mappa
        HBox barsBox = new HBox(20);
        barsBox.setAlignment(Pos.CENTER);
        barsBox.setPadding(new Insets(5, 0, 5, 0));

        BarBox xpBox = buildBarBox("ESPERIENZA", 250, "#4466cc");
        xpBar = xpBox.canvas;
        xpLabel = new Label("0 XP");
        xpLabel.setStyle("-fx-text-fill: #4466cc;");
        if (pixelFont != null)
            xpLabel.setFont(Font.font(pixelFont.getFamily(), 7));
        xpBox.container.getChildren().add(xpLabel);

        BarBox repBox = buildBarBox("CREDIBILITA'", 250, DARK_GOLD);
        repBar = repBox.canvas;
        repLabel = new Label("0 / 100");
        repLabel.setStyle("-fx-text-fill: " + DARK_GOLD + ";");
        if (pixelFont != null)
            repLabel.setFont(Font.font(pixelFont.getFamily(), 7));
        repBox.container.getChildren().add(repLabel);

        barsBox.getChildren().addAll(xpBox.container, repBox.container);

        // questo pulsante appare solo quando ci sono prove da raccogliere
        collectBtn = buildButton("[ OTTIENI PROVE ]", GOLD);
        collectBtn.setVisible(false);
        collectBtn.setOnAction(e -> {
            int found = controller.collectAllCluesInCurrentLocation();
            if (found > 0) {
                addMessage("Trovate " + found + " prove in " +
                        controller.getCurrentLocation().getName() + "!");
            } else {
                addMessage("Nessuna nuova prova qui.");
            }
            updateView();
            mapView.getCanvas().requestFocus();
            checkGameState();
        });

        HBox controls = new HBox(8);
        controls.setAlignment(Pos.CENTER);
        controls.getChildren().addAll(
                buildKeyLabel("W"), buildKeyLabel("A"),
                buildKeyLabel("S"), buildKeyLabel("D"),
                buildKeyLabel("↑"), buildKeyLabel("↓"),
                buildKeyLabel("←"), buildKeyLabel("→"));

        center.getChildren().addAll(mapTitle,
                mapView.getCanvas(), controls, barsBox, collectBtn);
        return center;
    }

    // pannello destro con dossier e missioni
    private VBox buildRightPanel() {
        VBox right = new VBox(10);
        right.setPadding(new Insets(15));
        right.setPrefWidth(275);
        right.setStyle(
                "-fx-background-color: " + BG_PANEL + ";" +
                        "-fx-border-color: " + DARK_GOLD + ";" +
                        "-fx-border-width: 0 0 0 1;");

        Label dossierTitle = buildTitle("[ DOSSIER ]");

        Label dossierHint = new Label("Prove raccolte:");
        dossierHint.setStyle("-fx-text-fill: " + TEXT_DIM + ";");
        if (pixelFont != null)
            dossierHint.setFont(Font.font(pixelFont.getFamily(), 7));

        cluesList = new ListView<>();
        cluesList.setPrefHeight(220);
        cluesList.setStyle(
                "-fx-background-color: " + BG_PANEL2 + ";" +
                        "-fx-border-color: " + DARK_GOLD + ";");

        Separator sep1 = buildSeparator();

        Label questTitle = buildTitle("[ MISSIONI ]");
        questPanel = new VBox(6);
        questPanel.setPrefHeight(180);

        Separator sep2 = buildSeparator();

        Button writeBtn = buildButton("[ SCRIVI RAPPORTO ]", GOLD);
        writeBtn.setOnAction(e -> {
            writeArticle();
            mapView.getCanvas().requestFocus();
        });

        right.getChildren().addAll(dossierTitle, dossierHint,
                cluesList, sep1, questTitle, questPanel, sep2, writeBtn);
        return right;
    }

    // log messaggi in basso
    private VBox buildBottomPanel() {
        VBox bottom = new VBox(5);
        bottom.setPadding(new Insets(8, 15, 8, 15));
        bottom.setStyle(
                "-fx-background-color: " + BG_PANEL + ";" +
                        "-fx-border-color: " + DARK_GOLD + ";" +
                        "-fx-border-width: 2 0 0 0;");

        Label logTitle = new Label("[ LOG OPERAZIONE ]");
        logTitle.setStyle("-fx-text-fill: " + TEXT_DIM + ";");
        if (pixelFont != null)
            logTitle.setFont(Font.font(pixelFont.getFamily(), 7));

        messageLog = new VBox(3);
        messageLog.setStyle("-fx-background-color: " + BG_PANEL + ";");

        ScrollPane messageScroll = new ScrollPane(messageLog);
        messageScroll.setPrefHeight(70);
        messageScroll.setStyle(
                "-fx-background-color: " + BG_PANEL + ";" +
                        "-fx-background: " + BG_PANEL + ";");
        messageScroll.setFitToWidth(true);
        messageScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        messageScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        bottom.getChildren().addAll(logTitle, messageScroll);
        return bottom;
    }

    // piccola struttura di supporto: tiene insieme il container e il canvas
    // della barra, evitando di doverli ripescare con cast fragili
    private static class BarBox {
        final VBox container;
        final Canvas canvas;

        BarBox(VBox container, Canvas canvas) {
            this.container = container;
            this.canvas = canvas;
        }
    }

    // crea una barra di progresso con canvas
    private BarBox buildBarBox(String label, int width, String color) {
        VBox box = new VBox(3);
        box.setAlignment(Pos.CENTER_LEFT);

        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: " + TEXT_DIM + ";");
        if (pixelFont != null)
            lbl.setFont(Font.font(pixelFont.getFamily(), 7));

        Canvas bar = new Canvas(width, 10);
        drawBar(bar.getGraphicsContext2D(), 0, width, color);

        VBox barContainer = new VBox(bar);
        barContainer.setStyle(
                "-fx-border-color: " + color + ";" +
                        "-fx-border-width: 1px;");

        box.getChildren().addAll(lbl, barContainer);
        return new BarBox(box, bar);
    }

    // disegna la barra colorata sul canvas
    private void drawBar(GraphicsContext gc,
                         double value, double max, String color) {
        double width = gc.getCanvas().getWidth();
        double height = gc.getCanvas().getHeight();
        double ratio = Math.min(value / max, 1.0);

        gc.setFill(Color.web("#050200"));
        gc.fillRect(0, 0, width, height);

        if (ratio > 0) {
            gc.setFill(Color.web(color));
            gc.fillRect(0, 0, width * ratio, height);
        }
    }

    // aggiunge un messaggio al log (massimo 5)
    private void addMessage(String text) {
        Label msg = new Label("> " + text);
        msg.setStyle("-fx-text-fill: " + GOLD + ";");
        if (pixelFont != null)
            msg.setFont(Font.font(pixelFont.getFamily(), 8));
        msg.setWrapText(true);
        messageLog.getChildren().add(0, msg);
        while (messageLog.getChildren().size() > 5) {
            messageLog.getChildren().remove(
                    messageLog.getChildren().size() - 1);
        }
    }

    private Label buildTitle(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: " + GOLD + ";");
        if (titleFont != null) label.setFont(titleFont);
        return label;
    }

    private Separator buildSeparator() {
        Separator sep = new Separator();
        sep.setStyle(
                "-fx-background-color: " + DARK_GOLD + ";" +
                        "-fx-opacity: 0.4;");
        return sep;
    }

    private Label buildKeyLabel(String key) {
        Label label = new Label(key);
        label.setStyle(
                "-fx-text-fill: " + GOLD + ";" +
                        "-fx-border-color: " + DARK_GOLD + ";" +
                        "-fx-border-width: 1px;" +
                        "-fx-padding: 3px 6px;" +
                        "-fx-background-color: " + BG_PANEL + ";");
        if (pixelFont != null)
            label.setFont(Font.font(pixelFont.getFamily(), 7));
        return label;
    }

    private Button buildButton(String text, String color) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle(
                "-fx-background-color: " + BG_DARK + ";" +
                        "-fx-text-fill: " + color + ";" +
                        "-fx-border-color: " + color + ";" +
                        "-fx-border-width: 2px;" +
                        "-fx-padding: 10px;");
        if (pixelFont != null) btn.setFont(pixelFont);
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: " + BG_DARK + ";" +
                        "-fx-border-color: " + color + ";" +
                        "-fx-border-width: 2px;" +
                        "-fx-padding: 10px;"));
        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: " + BG_DARK + ";" +
                        "-fx-text-fill: " + color + ";" +
                        "-fx-border-color: " + color + ";" +
                        "-fx-border-width: 2px;" +
                        "-fx-padding: 10px;"));
        return btn;
    }

    // applica il foglio di stile del gioco a un dialogo nativo,
    // se il file e' stato trovato in resources
    private void applyStylesheet(DialogPane pane) {
        if (styleSheetPath != null) {
            pane.getStylesheets().add(styleSheetPath);
        }
    }

    // aggiorna tutti i componenti della vista
    private void updateView() {
        Location current = controller.getCurrentLocation();
        locationLabel.setText(current.getName().toUpperCase());

        PlayerStats stats = controller.getJournalist().getStats();
        levelLabel.setText("LV." + stats.getLevel());
        statsLabel.setText(
                "INT: " + stats.getIntelligence() +
                        "   CAR: " + stats.getCharisma() +
                        "\nFUR: " + stats.getStealth() +
                        "   XP: " + stats.getExperience());

        int rep = controller.getJournalist().getReputation();

        daysLabel.setText("GIORNI: " + controller.getDaysRemaining());

        // cambio colore giorni quando sono pochi
        if (controller.getDaysRemaining() <= 3) {
            daysLabel.setStyle("-fx-text-fill: #cc3300;");
        } else if (controller.getDaysRemaining() <= 5) {
            daysLabel.setStyle("-fx-text-fill: #cc8800;");
        } else {
            daysLabel.setStyle("-fx-text-fill: " + GOLD + ";");
        }

        cluesCountLabel.setText("PROVE: " +
                controller.getDiscoveredCluesCount() +
                "/" + controller.getTotalClues());

        objectiveLabel.setText(controller.getCurrentObjective());

        drawBar(suspicionBar.getGraphicsContext2D(),
                controller.getSuspicionLevel(),
                controller.getMaxSuspicion(), RED);

        drawBar(xpBar.getGraphicsContext2D(),
                stats.getExperience(),
                stats.getLevel() * 100, "#4466cc");
        xpLabel.setText(stats.getExperience() +
                " / " + (stats.getLevel() * 100) + " XP");

        drawBar(repBar.getGraphicsContext2D(), rep, 100, DARK_GOLD);
        repLabel.setText(rep + " / 100");

        // mostro il pulsante solo se ci sono prove disponibili
        boolean hasUndiscoveredClues = current.getClues()
                .stream()
                .anyMatch(clue -> !clue.isDiscovered());
        collectBtn.setVisible(hasUndiscoveredClues);
        if (hasUndiscoveredClues) {
            collectBtn.setText("[ OTTIENI PROVE IN " +
                    current.getName().toUpperCase() + " ]");
        }

        cluesList.getItems().clear();
        controller.getJournalist().getNotebook()
                .forEach(clue -> cluesList.getItems()
                        .add("> " + clue.getDescription()));

        // aggiorno i bottoni dei contatti
        npcPanel.getChildren().clear();
        controller.getNpcsInCurrentLocation().forEach(npc -> {
            Button npcBtn = new Button(
                    npc.getName() + "\n[" + npc.getRole() + "]");
            npcBtn.setMaxWidth(Double.MAX_VALUE);
            npcBtn.setStyle(
                    "-fx-background-color: " + BG_PANEL2 + ";" +
                            "-fx-text-fill: " + GOLD + ";" +
                            "-fx-border-color: " + DARK_GOLD + ";" +
                            "-fx-border-width: 1px;" +
                            "-fx-padding: 6px;");
            if (pixelFont != null)
                npcBtn.setFont(Font.font(pixelFont.getFamily(), 7));
            npcBtn.setOnMouseEntered(e -> npcBtn.setStyle(
                    "-fx-background-color: " + DARK_GOLD + ";" +
                            "-fx-text-fill: " + BG_DARK + ";" +
                            "-fx-border-color: " + DARK_GOLD + ";" +
                            "-fx-border-width: 1px;" +
                            "-fx-padding: 6px;"));
            npcBtn.setOnMouseExited(e -> npcBtn.setStyle(
                    "-fx-background-color: " + BG_PANEL2 + ";" +
                            "-fx-text-fill: " + GOLD + ";" +
                            "-fx-border-color: " + DARK_GOLD + ";" +
                            "-fx-border-width: 1px;" +
                            "-fx-padding: 6px;"));
            npcBtn.setOnAction(e -> {
                showDialogueChoices(npc);
                mapView.getCanvas().requestFocus();
            });
            npcPanel.getChildren().add(npcBtn);
        });

        if (controller.getNpcsInCurrentLocation().isEmpty()) {
            Label noNpc = new Label("// nessun contatto qui //");
            noNpc.setStyle("-fx-text-fill: " + TEXT_DIM + ";");
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
            VBox questBox = new VBox(3);
            questBox.setStyle(
                    "-fx-background-color: " + BG_PANEL2 + ";" +
                            "-fx-border-color: " + DARK_GOLD + ";" +
                            "-fx-border-width: 1px;" +
                            "-fx-padding: 5px;");

            Label questTitle = new Label("▶ " + quest.getTitle());
            questTitle.setStyle("-fx-text-fill: " + GOLD + ";");
            questTitle.setWrapText(true);
            if (pixelFont != null)
                questTitle.setFont(
                        Font.font(pixelFont.getFamily(), 7));

            Canvas questBar = new Canvas(200, 6);
            drawBar(questBar.getGraphicsContext2D(),
                    quest.getCompletedObjectivesCount(),
                    quest.getTotalObjectivesCount(), DARK_GOLD);

            Label questProgress = new Label(
                    quest.getCompletedObjectivesCount() +
                            "/" + quest.getTotalObjectivesCount() +
                            " obiettivi");
            questProgress.setStyle("-fx-text-fill: " + TEXT_DIM + ";");
            if (pixelFont != null)
                questProgress.setFont(
                        Font.font(pixelFont.getFamily(), 6));

            questBox.getChildren().addAll(
                    questTitle, questBar, questProgress);
            questPanel.getChildren().add(questBox);
        });

        controller.getCompletedQuests().forEach(quest -> {
            Label questLabel = new Label("✓ " + quest.getTitle());
            questLabel.setStyle("-fx-text-fill: " + GREEN + ";");
            if (pixelFont != null)
                questLabel.setFont(
                        Font.font(pixelFont.getFamily(), 7));
            questPanel.getChildren().add(questLabel);
        });
    }

    // apre la finestra dei dialoghi con l'NPC
    private void showDialogueChoices(NPC npc) {
        List<GameDataLoader.ChoiceData> choices =
                controller.getChoicesForNpc(npc.getId());

        if (choices.isEmpty()) {
            showSimpleDialogue(npc);
            return;
        }

        Stage dialogStage = new Stage();
        dialogStage.setTitle(npc.getName());

        VBox root = new VBox(12);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: " + BG_PANEL + ";");

        Label nameLabel = new Label(
                npc.getName().toUpperCase() + "  |  " + npc.getRole());
        nameLabel.setStyle("-fx-text-fill: " + GOLD + ";");
        if (titleFont != null) nameLabel.setFont(titleFont);

        Label introLabel = new Label(
                npc.getDialogues().isEmpty() ? "" :
                        "\"" + npc.getDialogues().get(0) + "\"");
        introLabel.setStyle(
                "-fx-text-fill: #a09060;" +
                        "-fx-font-style: italic;");
        introLabel.setWrapText(true);
        if (pixelFont != null)
            introLabel.setFont(Font.font(pixelFont.getFamily(), 8));

        Label charismaLabel = new Label(
                "Il tuo carisma: " +
                        controller.getJournalist().getStats().getCharisma());
        charismaLabel.setStyle("-fx-text-fill: " + TEXT_DIM + ";");
        if (pixelFont != null)
            charismaLabel.setFont(Font.font(pixelFont.getFamily(), 7));

        Separator sep = buildSeparator();

        Label responseLabel = new Label("");
        responseLabel.setStyle("-fx-text-fill: " + GOLD + ";");
        responseLabel.setWrapText(true);
        if (pixelFont != null)
            responseLabel.setFont(Font.font(pixelFont.getFamily(), 8));

        VBox choicesBox = new VBox(8);
        for (GameDataLoader.ChoiceData choice : choices) {
            boolean canUse = controller.getJournalist()
                    .getStats().canUseDialogueOption(choice.requiredCharisma);

            String btnText = canUse ? "> " + choice.text :
                    "[CAR." + choice.requiredCharisma + " richiesto] " +
                            choice.text;

            Button choiceBtn = new Button(btnText);
            choiceBtn.setMaxWidth(Double.MAX_VALUE);
            choiceBtn.setWrapText(true);
            choiceBtn.setTextAlignment(TextAlignment.LEFT);

            String color = canUse ? GOLD : TEXT_DIM;
            choiceBtn.setStyle(
                    "-fx-background-color: " + BG_PANEL2 + ";" +
                            "-fx-text-fill: " + color + ";" +
                            "-fx-border-color: " + color + ";" +
                            "-fx-border-width: 1px;" +
                            "-fx-padding: 8px;");
            if (pixelFont != null)
                choiceBtn.setFont(Font.font(pixelFont.getFamily(), 7));

            if (canUse) {
                choiceBtn.setOnMouseEntered(e -> choiceBtn.setStyle(
                        "-fx-background-color: " + DARK_GOLD + ";" +
                                "-fx-text-fill: " + BG_DARK + ";" +
                                "-fx-border-color: " + DARK_GOLD + ";" +
                                "-fx-border-width: 1px;" +
                                "-fx-padding: 8px;"));
                choiceBtn.setOnMouseExited(e -> choiceBtn.setStyle(
                        "-fx-background-color: " + BG_PANEL2 + ";" +
                                "-fx-text-fill: " + color + ";" +
                                "-fx-border-color: " + color + ";" +
                                "-fx-border-width: 1px;" +
                                "-fx-padding: 8px;"));
            }

            choiceBtn.setOnAction(e -> {
                GameController.DialogueResult result =
                        controller.processChoice(npc.getId(), choice);
                responseLabel.setText("\"" + result.response + "\"");
                if (result.success) {
                    responseLabel.setStyle(
                            "-fx-text-fill: " + GOLD +
                                    "; -fx-font-style: italic;");
                    if (result.discoveredClue != null) {
                        addMessage("NUOVA PROVA: " +
                                result.discoveredClue.getDescription());
                    }
                    if (result.experienceGained > 0) {
                        addMessage("+" + result.experienceGained +
                                " XP guadagnati!");
                    }
                } else {
                    responseLabel.setStyle(
                            "-fx-text-fill: #cc3300;" +
                                    "-fx-font-style: italic;");
                    addMessage("Carisma insufficiente! Sospetto aumentato.");
                }
                updateView();
                checkGameState();
            });

            choicesBox.getChildren().add(choiceBtn);
        }

        Button closeBtn = buildButton("[ CHIUDI ]", GOLD);
        closeBtn.setOnAction(e -> dialogStage.close());

        root.getChildren().addAll(nameLabel, introLabel,
                charismaLabel, sep, choicesBox, responseLabel, closeBtn);

        Scene dialogScene = new Scene(root, 520, 580);
        dialogScene.setFill(Color.web(BG_PANEL));
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
        applyStylesheet(alert.getDialogPane());
        alert.showAndWait();
        updateView();
    }

    private void writeArticle() {
        if (controller.getJournalist().getNotebook().isEmpty()) {
            addMessage("ERRORE: Nessuna prova raccolta!");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("RAPPORTO SEGRETO");
        dialog.setHeaderText(
                "Invia il rapporto all'agenzia\n" +
                        "Prove disponibili: " +
                        controller.getJournalist().getNotebook().size());
        dialog.setContentText("Titolo del rapporto:");
        applyStylesheet(dialog.getDialogPane());
        dialog.showAndWait().ifPresent(title -> {
            if (!title.isEmpty()) {
                Article article = controller.writeArticleFromNotebook(title);
                addMessage("Rapporto \"" + title +
                        "\" pubblicato! +" +
                        article.getReputationValue() + " credibilita'.");
                updateView();
                checkGameState();
            }
        });
    }

    // controlla se il gioco è finito
    private void checkGameState() {
        if (controller.isGameOver()) {
            showGameOver();
        } else if (controller.isVictory()) {
            showVictory();
        } else if (controller.getSuspicionLevel() >= 70) {
            addMessage("ATTENZIONE: Le spie ti stanno tenendo d'occhio!");
        } else if (controller.getDaysRemaining() <= 3) {
            addMessage("URGENTE: Solo " +
                    controller.getDaysRemaining() + " giorni rimasti!");
        }
    }

    private void showGameOver() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("MISSIONE FALLITA");
        alert.setHeaderText("OPERAZIONE OMBRA - FALLITA");
        alert.setContentText(
                controller.getGameOverReason() + "\n\n" +
                        "Prove raccolte: " + controller.getDiscoveredCluesCount() +
                        "\nCredibilita' finale: " +
                        controller.getJournalist().getReputation() +
                        "\nLivello raggiunto: " +
                        controller.getJournalist().getStats().getLevel());
        applyStylesheet(alert.getDialogPane());
        alert.showAndWait();
    }

    private void showVictory() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("MISSIONE COMPIUTA!");
        alert.setHeaderText("OPERAZIONE OMBRA - COMPLETATA!");
        alert.setContentText(
                "Hai smascherato la rete di spie!\n\n" +
                        "AGENTE " +
                        controller.getJournalist().getName().toUpperCase() +
                        " - MISSIONE COMPLETATA!\n\n" +
                        "Livello finale: " +
                        controller.getJournalist().getStats().getLevel() + "\n" +
                        "Credibilita': " +
                        controller.getJournalist().getReputation() + "\n" +
                        "Prove raccolte: " +
                        controller.getDiscoveredCluesCount() +
                        "/" + controller.getTotalClues());
        applyStylesheet(alert.getDialogPane());
        alert.showAndWait();
    }
}