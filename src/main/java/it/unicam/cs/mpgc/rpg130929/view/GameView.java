package it.unicam.cs.mpgc.rpg130929.view;

import it.unicam.cs.mpgc.rpg130929.controller.GameController;
import it.unicam.cs.mpgc.rpg130929.model.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class GameView {

    private final GameController controller;
    private final Stage stage;
    private Label locationLabel;
    private TextArea descriptionArea;
    private ListView<String> cluesList;
    private Label reputationLabel;
    private Label cluesCountLabel;
    private VBox npcPanel;
    private MapView mapView;
    private Font pixelFont;

    public GameView(GameController controller, Stage stage) {
        this.controller = controller;
        this.stage = stage;
        this.pixelFont = Font.loadFont(
                getClass().getClassLoader()
                        .getResourceAsStream("PressStart2P-Regular.ttf"), 10);
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        root.setTop(buildTopPanel());
        root.setLeft(buildLeftPanel());
        root.setCenter(buildCenterPanel());
        root.setRight(buildRightPanel());

        Scene scene = new Scene(root, 1280, 768);
        scene.getStylesheets().add(
                getClass().getClassLoader()
                        .getResource("style.css").toExternalForm()
        );

        stage.setTitle("IL CRONISTA - OPERAZIONE OMBRA");
        stage.setScene(scene);
        stage.show();

        mapView.getCanvas().requestFocus();
        updateView();
    }

    private HBox buildTopPanel() {
        HBox top = new HBox(30);
        top.setPadding(new Insets(15));
        top.setStyle("-fx-background-color: #0a0a0a; " +
                "-fx-border-color: #00ff41; -fx-border-width: 0 0 2 0;");

        Label title = new Label(">> OPERAZIONE OMBRA - 1935 <<");
        title.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 12px;");

        locationLabel = new Label("POSIZIONE: ");
        locationLabel.setStyle("-fx-text-fill: #00ff41; -fx-font-size: 9px;");

        reputationLabel = new Label("REP: 0");
        reputationLabel.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 9px;");

        cluesCountLabel = new Label("INDIZI: 0/0");
        cluesCountLabel.setStyle("-fx-text-fill: #00ff41; -fx-font-size: 9px;");

        Label controls = new Label("[ WASD = MUOVI ]");
        controls.setStyle("-fx-text-fill: #444444; -fx-font-size: 8px;");

        top.getChildren().addAll(title, locationLabel,
                reputationLabel, cluesCountLabel, controls);
        return top;
    }

    private VBox buildLeftPanel() {
        VBox left = new VBox(10);
        left.setPadding(new Insets(15));
        left.setPrefWidth(250);
        left.setStyle("-fx-background-color: #0a0a0a; " +
                "-fx-border-color: #00ff41; -fx-border-width: 0 2 0 0;");

        Label title = new Label("[ SCENARIO ]");
        title.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 10px;");

        descriptionArea = new TextArea();
        descriptionArea.setEditable(false);
        descriptionArea.setWrapText(true);
        descriptionArea.setPrefHeight(200);

        Label npcTitle = new Label("[ CONTATTI ]");
        npcTitle.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 10px;");

        npcPanel = new VBox(5);

        Button collectBtn = new Button("[ CERCA PROVE ]");
        collectBtn.setMaxWidth(Double.MAX_VALUE);
        collectBtn.setOnAction(e -> {
            controller.collectAllCluesInCurrentLocation();
            updateView();
            mapView.getCanvas().requestFocus();
            showAlert("PROVE RACCOLTE",
                    "Hai esaminato il luogo.\nProve aggiunte al dossier!");
        });

        left.getChildren().addAll(title, descriptionArea,
                new Separator(), npcTitle, npcPanel,
                new Separator(), collectBtn);
        return left;
    }

    private VBox buildCenterPanel() {
        VBox center = new VBox(10);
        center.setPadding(new Insets(15));

        Label title = new Label("[ MAPPA CITTA' ]");
        title.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 10px;");

        mapView = new MapView(controller, () -> {
            updateView();
        });

        Label hint = new Label(
                "W = SU  |  S = GIU  |  A = SX  |  D = DX\n" +
                        "Raggiungi un luogo per esplorarlo!");
        hint.setStyle("-fx-text-fill: #444444; -fx-font-size: 7px;");

        center.getChildren().addAll(title, mapView.getCanvas(), hint);
        return center;
    }

    private VBox buildRightPanel() {
        VBox right = new VBox(10);
        right.setPadding(new Insets(15));
        right.setPrefWidth(280);
        right.setStyle("-fx-background-color: #0a0a0a; " +
                "-fx-border-color: #00ff41; -fx-border-width: 0 0 0 2;");

        Label title = new Label("[ DOSSIER ]");
        title.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 10px;");

        cluesList = new ListView<>();
        cluesList.setPrefHeight(400);

        Button writeBtn = new Button("[ SCRIVI RAPPORTO ]");
        writeBtn.setMaxWidth(Double.MAX_VALUE);
        writeBtn.setOnAction(e -> {
            writeArticle();
            mapView.getCanvas().requestFocus();
        });

        right.getChildren().addAll(title, cluesList,
                new Separator(), writeBtn);
        return right;
    }

    private void updateView() {
        Location current = controller.getCurrentLocation();
        locationLabel.setText("POSIZIONE: " +
                current.getName().toUpperCase());
        descriptionArea.setText(current.getDescription());
        reputationLabel.setText("REP: " +
                controller.getJournalist().getReputation());
        cluesCountLabel.setText("PROVE: " +
                controller.getDiscoveredCluesCount() +
                "/" + controller.getTotalClues());

        cluesList.getItems().clear();
        controller.getJournalist().getNotebook()
                .forEach(clue -> cluesList.getItems()
                        .add("> " + clue.getDescription()));

        npcPanel.getChildren().clear();
        controller.getNpcsInCurrentLocation().forEach(npc -> {
            Button npcBtn = new Button(">> " + npc.getName()
                    + " [" + npc.getRole().toUpperCase() + "]");
            npcBtn.setMaxWidth(Double.MAX_VALUE);
            npcBtn.setOnAction(e -> {
                showNpcDialogue(npc);
                mapView.getCanvas().requestFocus();
            });
            npcPanel.getChildren().add(npcBtn);
        });

        if (controller.getNpcsInCurrentLocation().isEmpty()) {
            Label noNpc = new Label("// nessun contatto //");
            noNpc.setStyle("-fx-text-fill: #444444; -fx-font-size: 8px;");
            npcPanel.getChildren().add(noNpc);
        }

        if (mapView != null) mapView.refresh();
    }

    private void showNpcDialogue(NPC npc) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("CONTATTO");
        alert.setHeaderText(npc.getName().toUpperCase() +
                " - " + npc.getRole());
        String dialogue = String.join("\n\n", npc.getDialogues());
        alert.setContentText(dialogue);
        alert.showAndWait();

        npc.getCluesProvided().forEach(clue -> {
            if (!clue.isDiscovered()) {
                controller.collectClue(clue);
                showAlert("NUOVA PROVA!", clue.getDescription());
            }
        });

        updateView();
    }

    private void writeArticle() {
        if (controller.getJournalist().getNotebook().isEmpty()) {
            showAlert("ATTENZIONE",
                    "Non hai ancora raccolto prove!\n\n" +
                            "Esplora la citta' e\nparla con i contatti.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("NUOVO RAPPORTO");
        dialog.setHeaderText("Scrivi il tuo rapporto segreto");
        dialog.setContentText("Titolo:");
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
            showAlert("MISSIONE COMPIUTA!",
                    "Hai smascherato la rete di spie!\n\n" +
                            "Il tuo rapporto ha scosso\n" +
                            "i servizi segreti!\n\n" +
                            "AGENTE " +
                            controller.getJournalist().getName().toUpperCase() +
                            " - MISSIONE COMPLETATA!");
        } else {
            showAlert("RAPPORTO INVIATO",
                    "Prove usate: " +
                            controller.getJournalist().getNotebook().size() +
                            "\nReputazione: " +
                            controller.getJournalist().getReputation() +
                            "\n\nContinua a investigare!");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}