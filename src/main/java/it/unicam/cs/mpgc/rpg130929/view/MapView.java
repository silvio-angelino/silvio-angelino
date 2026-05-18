package it.unicam.cs.mpgc.rpg130929.view;

import it.unicam.cs.mpgc.rpg130929.controller.GameController;
import it.unicam.cs.mpgc.rpg130929.model.Location;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MapView {

    private static final int TILE_SIZE = 48;
    private static final int MAP_WIDTH = 12;
    private static final int MAP_HEIGHT = 12;

    private final GameController controller;
    private final Canvas canvas;
    private final Runnable onLocationChange;

    private double playerX = 5;
    private double playerY = 5;
    private int animFrame = 0;
    private long lastFrameTime = 0;
    private boolean moving = false;
    private String direction = "down";

    private final Set<KeyCode> pressedKeys = new HashSet<>();
    private final Map<String, int[]> locationPositions;
    private Font pixelFont;

    public MapView(GameController controller, Runnable onLocationChange) {
        this.controller = controller;
        this.onLocationChange = onLocationChange;
        this.canvas = new Canvas(MAP_WIDTH * TILE_SIZE, MAP_HEIGHT * TILE_SIZE);
        this.locationPositions = new HashMap<>();

        pixelFont = Font.loadFont(
                getClass().getClassLoader()
                        .getResourceAsStream("PressStart2P-Regular.ttf"), 7);

        initLocationPositions();
        setupKeyHandlers();
        startGameLoop();
        draw();
    }

    private void initLocationPositions() {
        locationPositions.put("redazione", new int[]{5, 5});
        locationPositions.put("porto", new int[]{2, 8});
        locationPositions.put("ambasciata", new int[]{8, 2});
        locationPositions.put("mercato", new int[]{3, 3});
        locationPositions.put("osteria", new int[]{7, 7});
        locationPositions.put("stazione", new int[]{9, 5});
    }

    private void setupKeyHandlers() {
        canvas.setFocusTraversable(true);
        canvas.setOnKeyPressed(e -> pressedKeys.add(e.getCode()));
        canvas.setOnKeyReleased(e -> pressedKeys.remove(e.getCode()));
    }

    private void startGameLoop() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastFrameTime > 150_000_000) {
                    updatePlayer();
                    lastFrameTime = now;
                }
                if (now - lastFrameTime > 200_000_000) {
                    if (moving) animFrame = (animFrame + 1) % 2;
                }
            }
        };
        timer.start();
    }

    private void updatePlayer() {
        double newX = playerX;
        double newY = playerY;
        moving = false;

        if (pressedKeys.contains(KeyCode.W)) {
            newY--;
            direction = "up";
            moving = true;
        } else if (pressedKeys.contains(KeyCode.S)) {
            newY++;
            direction = "down";
            moving = true;
        } else if (pressedKeys.contains(KeyCode.A)) {
            newX--;
            direction = "left";
            moving = true;
        } else if (pressedKeys.contains(KeyCode.D)) {
            newX++;
            direction = "right";
            moving = true;
        }

        if (moving && newX >= 0 && newX < MAP_WIDTH
                && newY >= 0 && newY < MAP_HEIGHT) {
            playerX = newX;
            playerY = newY;
            checkLocationReached();
            draw();
        }
    }

    private void checkLocationReached() {
        for (Map.Entry<String, int[]> entry : locationPositions.entrySet()) {
            int[] pos = entry.getValue();
            if ((int) playerX == pos[0] && (int) playerY == pos[1]) {
                controller.moveToLocation(entry.getKey());
                onLocationChange.run();
                break;
            }
        }
    }

    private void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        drawBackground(gc);
        drawStreets(gc);
        drawGrid(gc);
        drawLocations(gc);
        drawPlayer(gc);
    }

    private void drawBackground(GraphicsContext gc) {
        gc.setFill(Color.web("#1a2a1a"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void drawStreets(GraphicsContext gc) {
        gc.setFill(Color.web("#2a2a2a"));
        for (int x = 0; x < MAP_WIDTH; x++) {
            gc.fillRect(x * TILE_SIZE, 0, TILE_SIZE / 3.0,
                    MAP_HEIGHT * TILE_SIZE);
        }
        for (int y = 0; y < MAP_HEIGHT; y++) {
            gc.fillRect(0, y * TILE_SIZE,
                    MAP_WIDTH * TILE_SIZE, TILE_SIZE / 3.0);
        }
        gc.setFill(Color.web("#3a3a2a"));
        gc.fillRect(0, 4 * TILE_SIZE, MAP_WIDTH * TILE_SIZE, TILE_SIZE);
        gc.fillRect(0, 8 * TILE_SIZE, MAP_WIDTH * TILE_SIZE, TILE_SIZE);
        gc.fillRect(4 * TILE_SIZE, 0, TILE_SIZE, MAP_HEIGHT * TILE_SIZE);
        gc.fillRect(8 * TILE_SIZE, 0, TILE_SIZE, MAP_HEIGHT * TILE_SIZE);
    }

    private void drawGrid(GraphicsContext gc) {
        gc.setStroke(Color.web("#2a2a2a"));
        gc.setLineWidth(0.5);
        for (int x = 0; x <= MAP_WIDTH; x++) {
            gc.strokeLine(x * TILE_SIZE, 0,
                    x * TILE_SIZE, MAP_HEIGHT * TILE_SIZE);
        }
        for (int y = 0; y <= MAP_HEIGHT; y++) {
            gc.strokeLine(0, y * TILE_SIZE,
                    MAP_WIDTH * TILE_SIZE, y * TILE_SIZE);
        }
    }

    private void drawLocations(GraphicsContext gc) {
        for (Map.Entry<String, int[]> entry : locationPositions.entrySet()) {
            int[] pos = entry.getValue();
            String locationId = entry.getKey();
            boolean isCurrent = controller.getCurrentLocation()
                    .getId().equals(locationId);

            drawBuilding(gc, pos[0], pos[1], locationId, isCurrent);
        }
    }

    private void drawBuilding(GraphicsContext gc, int x, int y,
                              String id, boolean isCurrent) {
        double px = x * TILE_SIZE;
        double py = y * TILE_SIZE;

        if (isCurrent) {
            gc.setFill(Color.web("#FFD700"));
        } else {
            gc.setFill(Color.web("#00aa33"));
        }
        gc.fillRect(px + 4, py + 4, TILE_SIZE - 8, TILE_SIZE - 8);

        gc.setFill(Color.web("#005520"));
        gc.fillRect(px + 6, py + 6, TILE_SIZE - 12, 8);

        gc.setFill(Color.web("#000000"));
        gc.fillRect(px + 10, py + 14, 8, 6);
        gc.fillRect(px + 26, py + 14, 8, 6);

        gc.setFill(Color.web("#8B4513"));
        gc.fillRect(px + 18, py + 28, 8, TILE_SIZE - 32);

        gc.setFill(Color.web("#ffffff"));
        if (pixelFont != null) gc.setFont(pixelFont);
        gc.fillText(getLocationSymbol(id), px + 16, py + 44);

        gc.setFill(isCurrent ? Color.web("#FFD700") : Color.web("#00ff41"));
        gc.fillText(getLocationShortName(id), px + 2, py - 4);
    }

    private void drawPlayer(GraphicsContext gc) {
        double px = playerX * TILE_SIZE + TILE_SIZE / 2.0;
        double py = playerY * TILE_SIZE + TILE_SIZE / 2.0;

        gc.setFill(Color.web("#8B4513"));
        gc.fillOval(px - 6, py - 20, 12, 12);

        gc.setFill(Color.web("#2c2c2c"));
        gc.fillRect(px - 5, py - 9, 10, 2);

        gc.setFill(Color.web("#F5DEB3"));
        gc.fillOval(px - 5, py - 18, 10, 10);

        gc.setFill(Color.web("#333366"));
        gc.fillRect(px - 6, py - 8, 12, 12);

        if (moving && animFrame == 0) {
            gc.setFill(Color.web("#F5DEB3"));
            gc.fillRect(px - 10, py - 6, 4, 8);
            gc.fillRect(px + 6, py - 2, 4, 8);
            gc.fillRect(px - 5, py + 4, 4, 10);
            gc.fillRect(px + 1, py + 4, 4, 10);
        } else {
            gc.setFill(Color.web("#F5DEB3"));
            gc.fillRect(px - 10, py - 2, 4, 8);
            gc.fillRect(px + 6, py - 6, 4, 8);
            gc.fillRect(px - 5, py + 4, 4, 10);
            gc.fillRect(px + 1, py + 4, 4, 10);
        }

        gc.setFill(Color.web("#ff4444"));
        gc.fillOval(px - 3, py - 22, 6, 6);
    }

    private String getLocationSymbol(String id) {
        return switch (id) {
            case "redazione" -> "R";
            case "porto" -> "P";
            case "ambasciata" -> "A";
            case "mercato" -> "M";
            case "osteria" -> "O";
            case "stazione" -> "S";
            default -> "?";
        };
    }

    private String getLocationShortName(String id) {
        return switch (id) {
            case "redazione" -> "REDAZIONE";
            case "porto" -> "PORTO";
            case "ambasciata" -> "AMBASCIATA";
            case "mercato" -> "MERCATO";
            case "osteria" -> "OSTERIA";
            case "stazione" -> "STAZIONE";
            default -> "???";
        };
    }

    public Canvas getCanvas() { return canvas; }

    public void refresh() { draw(); }

    public void setPlayerPosition(double x, double y) {
        this.playerX = x;
        this.playerY = y;
        draw();
    }
}