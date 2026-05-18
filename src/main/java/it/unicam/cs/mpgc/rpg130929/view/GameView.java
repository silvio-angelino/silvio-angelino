package it.unicam.cs.mpgc.rpg130929.view;

import it.unicam.cs.mpgc.rpg130929.controller.GameController;
import it.unicam.cs.mpgc.rpg130929.model.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
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

    public GameView(GameController controller, Stage stage) {
        this.controller = controller;
        this.stage = stage;
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        root.setTop(buildTopPanel());
        root.setLeft(buildLeftPanel());
        root.setCenter(buildCenterPanel());
        root.setRight(buildRightPanel());

        Scene scene = new Scene(root, 1024, 768);
        stage.setTitle("Il Cronista - Gioco di Ruolo");
        stage.setScene(scene);
        stage.show();

        updateView();
    }

    private HBox buildTopPanel() {
        HBox top = new HBox(20);
        top.setPadding(new Insets(10));
        top.setStyle("-fx-background-color: #2c2c2c;");

        locationLabel = new Label("Posizione: ");
        locationLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        reputationLabel = new Label("Reputazione: 0");
        reputationLabel.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 14px;");

        cluesCountLabel = new Label("Indizi: 0/0");
        cluesCountLabel.setStyle("-fx-text-fill: #90EE90; -fx-font-size: 14px;");

        top.getChildren().addAll(locationLabel, reputationLabel, cluesCountLabel);
        return top;
    }

    private VBox buildLeftPanel() {
        VBox left = new VBox(10);
        left.setPadding(new Insets(10));
        left.setPrefWidth(200);
        left.setStyle("-fx-background-color: #1e1e1e;");

        Label title = new Label("LUOGHI");
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: white; -fx-font-size: 14px;");
        left.getChildren().add(title);

        for (Location location : controller.getAllLocations()) {
            Button btn = new Button(location.getName());
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setStyle("-fx-background-color: #3c3c3c; -fx-text-fill: white;");
            btn.setOnAction(e -> {
                controller.moveToLocation(location.getId());
                updateView();
            });
            left.getChildren().add(btn);
        }

        return left;
    }

    private VBox buildCenterPanel() {
        VBox center = new VBox(10);
        center.setPadding(new Insets(10));

        Label title = new Label("DESCRIZIONE LUOGO");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        descriptionArea = new TextArea();
        descriptionArea.setEditable(false);
        descriptionArea.setWrapText(true);
        descriptionArea.setPrefHeight(150);

        npcPanel = new VBox(5);
        npcPanel.setPadding(new Insets(5));

        Button collectBtn = new Button("🔍 Raccogli Indizi");
        collectBtn.setStyle("-fx-font-size: 13px; -fx-padding: 8px 20px;");
        collectBtn.setMaxWidth(Double.MAX_VALUE);
        collectBtn.setOnAction(e -> {
            controller.collectAllCluesInCurrentLocation();
            updateView();
            showAlert("Hai raccolto tutti gli indizi disponibili in questo luogo!");
        });

        center.getChildren().addAll(title, descriptionArea,
                new Label("PERSONAGGI PRESENTI:"), npcPanel, collectBtn);
        return center;
    }

    private VBox buildRightPanel() {
        VBox right = new VBox(10);
        right.setPadding(new Insets(10));
        right.setPrefWidth(280);
        right.setStyle("-fx-background-color: #1e1e1e;");

        Label title = new Label("TACCUINO");
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: white; -fx-font-size: 14px;");

        cluesList = new ListView<>();
        cluesList.setPrefHeight(400);

        Button writeBtn = new Button("✏️ Scrivi Articolo");
        writeBtn.setMaxWidth(Double.MAX_VALUE);
        writeBtn.setStyle("-fx-font-size: 13px; -fx-padding: 8px 20px;");
        writeBtn.setOnAction(e -> writeArticle());

        right.getChildren().addAll(title, cluesList, writeBtn);
        return right;
    }

    private void updateView() {
        Location current = controller.getCurrentLocation();
        locationLabel.setText("📍 " + current.getName());
        descriptionArea.setText(current.getDescription());
        reputationLabel.setText("⭐ Reputazione: " +
                controller.getJournalist().getReputation());
        cluesCountLabel.setText("🔍 Indizi: " +
                controller.getDiscoveredCluesCount() + "/" +
                controller.getTotalClues());

        cluesList.getItems().clear();
        controller.getJournalist().getNotebook()
                .forEach(clue -> cluesList.getItems().add("• " + clue.getDescription()));

        npcPanel.getChildren().clear();
        controller.getNpcsInCurrentLocation().forEach(npc -> {
            Button npcBtn = new Button("💬 " + npc.getName() + " - " + npc.getRole());
            npcBtn.setMaxWidth(Double.MAX_VALUE);
            npcBtn.setOnAction(e -> showNpcDialogue(npc));
            npcPanel.getChildren().add(npcBtn);
        });

        if (controller.getNpcsInCurrentLocation().isEmpty()) {
            npcPanel.getChildren().add(new Label("Nessun personaggio presente"));
        }
    }

    private void showNpcDialogue(NPC npc) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Dialogo con " + npc.getName());
        alert.setHeaderText(npc.getName() + " - " + npc.getRole());

        String dialogue = npc.getDialogues().stream()
                .reduce("", (a, b) -> a + "\n\n" + b);

        alert.setContentText(dialogue);
        alert.showAndWait();

        npc.getCluesProvided().forEach(clue -> {
            if (!clue.isDiscovered()) {
                controller.collectClue(clue);
                showAlert("Nuovo indizio raccolto:\n" + clue.getDescription());
            }
        });

        updateView();
    }

    private void writeArticle() {
        if (controller.getJournalist().getNotebook().isEmpty()) {
            showAlert("Non hai ancora raccolto nessun indizio!\nEsplora i luoghi e parla con i personaggi.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nuovo Articolo");
        dialog.setHeaderText("Scrivi un nuovo articolo");
        dialog.setContentText("Titolo dell'articolo:");
        dialog.showAndWait().ifPresent(title -> {
            if (!title.isEmpty()) {
                Article article = controller.createArticle(title);
                controller.getJournalist().getNotebook()
                        .forEach(article::addClue);
                controller.publishArticle(article);
                updateView();
                showAlert("Articolo pubblicato!\n\"" + title + "\"\n\n" +
                        "Indizi usati: " + article.getCluesUsed().size() + "\n" +
                        "Reputazione guadagnata: +" + article.getReputationValue() + "\n" +
                        "Reputazione totale: " + controller.getJournalist().getReputation());
            }
        });
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }
}