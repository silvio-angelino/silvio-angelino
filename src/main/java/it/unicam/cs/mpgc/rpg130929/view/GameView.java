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
        locationLabel = new Label("Posizione: ");
        reputationLabel = new Label("Reputazione: 0");
        top.getChildren().addAll(locationLabel, reputationLabel);
        return top;
    }

    private VBox buildLeftPanel() {
        VBox left = new VBox(10);
        left.setPadding(new Insets(10));
        left.setPrefWidth(200);

        Label title = new Label("Luoghi");
        title.setStyle("-fx-font-weight: bold;");

        for (Location location : controller.getAllLocations()) {
            Button btn = new Button(location.getName());
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setOnAction(e -> {
                controller.moveToLocation(location.getId());
                updateView();
            });
            left.getChildren().add(btn);
        }

        left.getChildren().add(0, title);
        return left;
    }

    private VBox buildCenterPanel() {
        VBox center = new VBox(10);
        center.setPadding(new Insets(10));

        Label title = new Label("Descrizione");
        title.setStyle("-fx-font-weight: bold;");

        descriptionArea = new TextArea();
        descriptionArea.setEditable(false);
        descriptionArea.setWrapText(true);
        descriptionArea.setPrefHeight(300);

        Button collectBtn = new Button("Raccogli Indizi");
        collectBtn.setOnAction(e -> collectClues());

        center.getChildren().addAll(title, descriptionArea, collectBtn);
        return center;
    }

    private VBox buildRightPanel() {
        VBox right = new VBox(10);
        right.setPadding(new Insets(10));
        right.setPrefWidth(250);

        Label title = new Label("Taccuino");
        title.setStyle("-fx-font-weight: bold;");

        cluesList = new ListView<>();
        cluesList.setPrefHeight(300);

        Button writeBtn = new Button("Scrivi Articolo");
        writeBtn.setMaxWidth(Double.MAX_VALUE);
        writeBtn.setOnAction(e -> writeArticle());

        right.getChildren().addAll(title, cluesList, writeBtn);
        return right;
    }

    private void collectClues() {
        Location current = controller.getCurrentLocation();
        for (Clue clue : current.getClues()) {
            if (!clue.isDiscovered()) {
                controller.collectClue(clue);
            }
        }
        updateView();
    }

    private void writeArticle() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nuovo Articolo");
        dialog.setHeaderText("Scrivi un nuovo articolo");
        dialog.setContentText("Titolo:");
        dialog.showAndWait().ifPresent(title -> {
            if (!title.isEmpty()) {
                Article article = controller.createArticle(title);
                for (Clue clue : controller.getJournalist().getNotebook()) {
                    article.addClue(clue);
                }
                controller.publishArticle(article);
                updateView();
                showAlert("Articolo pubblicato! Reputazione: " +
                        controller.getJournalist().getReputation());
            }
        });
    }

    private void updateView() {
        Location current = controller.getCurrentLocation();
        locationLabel.setText("Posizione: " + current.getName());
        descriptionArea.setText(current.getDescription());
        reputationLabel.setText("Reputazione: " +
                controller.getJournalist().getReputation());

        cluesList.getItems().clear();
        for (Clue clue : controller.getJournalist().getNotebook()) {
            cluesList.getItems().add(clue.getDescription());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }
}