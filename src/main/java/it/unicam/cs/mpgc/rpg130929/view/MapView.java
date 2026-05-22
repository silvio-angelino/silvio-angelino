package it.unicam.cs.mpgc.rpg130929.view;

import it.unicam.cs.mpgc.rpg130929.controller.GameController;
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

// gestisce la mappa del gioco con il personaggio animato
public class MapView {

    private static final int TILE_SIZE = 52;
    private static final int MAP_WIDTH = 13;
    private static final int MAP_HEIGHT = 11;

    private final GameController controller;
    private final Canvas canvas;
    private final Runnable onLocationChange;

    // posizione del giocatore sulla mappa
    private double playerX = 5;
    private double playerY = 5;
    private int animFrame = 0;
    private long lastMoveTime = 0;
    private long lastAnimTime = 0;
    private boolean moving = false;
    private String direction = "down";

    private final Set<KeyCode> pressedKeys = new HashSet<>();
    private final Map<String, int[]> locationPositions;
    private Font pixelFont;

    public MapView(GameController controller, Runnable onLocationChange) {
        this.controller = controller;
        this.onLocationChange = onLocationChange;
        this.canvas = new Canvas(MAP_WIDTH * TILE_SIZE,
                MAP_HEIGHT * TILE_SIZE);
        this.locationPositions = new HashMap<>();

        pixelFont = Font.loadFont(
                getClass().getClassLoader()
                        .getResourceAsStream("PressStart2P-Regular.ttf"), 7);

        initLocationPositions();
        setupKeyHandlers();
        startGameLoop();
        draw();
    }

    // coordinate fisse per ogni luogo sulla mappa
    private void initLocationPositions() {
        locationPositions.put("redazione", new int[]{5, 5});
        locationPositions.put("porto", new int[]{2, 8});
        locationPositions.put("ambasciata", new int[]{9, 2});
        locationPositions.put("mercato", new int[]{3, 3});
        locationPositions.put("osteria", new int[]{8, 7});
        locationPositions.put("stazione", new int[]{10, 5});
    }

    private void setupKeyHandlers() {
        canvas.setFocusTraversable(true);
        canvas.setOnKeyPressed(e -> pressedKeys.add(e.getCode()));
        canvas.setOnKeyReleased(e -> pressedKeys.remove(e.getCode()));
    }

    // loop di gioco per gestire movimento e animazione
    private void startGameLoop() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastMoveTime > 180_000_000L) {
                    updatePlayer();
                    lastMoveTime = now;
                }
                if (now - lastAnimTime > 250_000_000L) {
                    if (moving) animFrame = (animFrame + 1) % 4;
                    else animFrame = 0;
                    lastAnimTime = now;
                    draw();
                }
            }
        };
        timer.start();
    }

    private void updatePlayer() {
        double newX = playerX;
        double newY = playerY;
        moving = false;

        if (pressedKeys.contains(KeyCode.W) ||
                pressedKeys.contains(KeyCode.UP)) {
            newY--;
            direction = "up";
            moving = true;
        } else if (pressedKeys.contains(KeyCode.S) ||
                pressedKeys.contains(KeyCode.DOWN)) {
            newY++;
            direction = "down";
            moving = true;
        } else if (pressedKeys.contains(KeyCode.A) ||
                pressedKeys.contains(KeyCode.LEFT)) {
            newX--;
            direction = "left";
            moving = true;
        } else if (pressedKeys.contains(KeyCode.D) ||
                pressedKeys.contains(KeyCode.RIGHT)) {
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

    // controlla se il giocatore è arrivato su un edificio
    private void checkLocationReached() {
        for (Map.Entry<String, int[]> entry :
                locationPositions.entrySet()) {
            int[] pos = entry.getValue();
            if ((int) playerX == pos[0] &&
                    (int) playerY == pos[1]) {
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
        drawBuildings(gc);
        drawPlayer(gc);
        drawLocationNames(gc);
        drawMiniStats(gc);
    }

    private void drawBackground(GraphicsContext gc) {
        // sfondo a scacchiera per dare un po' di texture
        for (int x = 0; x < MAP_WIDTH; x++) {
            for (int y = 0; y < MAP_HEIGHT; y++) {
                if ((x + y) % 2 == 0) {
                    gc.setFill(Color.web("#0d1a0d"));
                } else {
                    gc.setFill(Color.web("#0a150a"));
                }
                gc.fillRect(x * TILE_SIZE, y * TILE_SIZE,
                        TILE_SIZE, TILE_SIZE);
            }
        }
    }

    private void drawStreets(GraphicsContext gc) {
        gc.setFill(Color.web("#1a1a1a"));
        // strade orizzontali
        gc.fillRect(0, 4 * TILE_SIZE,
                MAP_WIDTH * TILE_SIZE, TILE_SIZE);
        gc.fillRect(0, 7 * TILE_SIZE,
                MAP_WIDTH * TILE_SIZE, TILE_SIZE);

        // strade verticali
        gc.fillRect(4 * TILE_SIZE, 0,
                TILE_SIZE, MAP_HEIGHT * TILE_SIZE);
        gc.fillRect(8 * TILE_SIZE, 0,
                TILE_SIZE, MAP_HEIGHT * TILE_SIZE);

        // linee tratteggiate al centro
        gc.setStroke(Color.web("#333300"));
        gc.setLineWidth(1);
        gc.setLineDashes(8, 6);
        gc.strokeLine(0, 4 * TILE_SIZE + TILE_SIZE / 2.0,
                MAP_WIDTH * TILE_SIZE,
                4 * TILE_SIZE + TILE_SIZE / 2.0);
        gc.strokeLine(0, 7 * TILE_SIZE + TILE_SIZE / 2.0,
                MAP_WIDTH * TILE_SIZE,
                7 * TILE_SIZE + TILE_SIZE / 2.0);
        gc.strokeLine(4 * TILE_SIZE + TILE_SIZE / 2.0, 0,
                4 * TILE_SIZE + TILE_SIZE / 2.0,
                MAP_HEIGHT * TILE_SIZE);
        gc.strokeLine(8 * TILE_SIZE + TILE_SIZE / 2.0, 0,
                8 * TILE_SIZE + TILE_SIZE / 2.0,
                MAP_HEIGHT * TILE_SIZE);
        gc.setLineDashes(0);

        gc.setStroke(Color.web("#2a2a2a"));
        gc.setLineWidth(2);
        gc.strokeLine(0, 3 * TILE_SIZE + TILE_SIZE - 2,
                MAP_WIDTH * TILE_SIZE,
                3 * TILE_SIZE + TILE_SIZE - 2);
        gc.strokeLine(0, 5 * TILE_SIZE + 2,
                MAP_WIDTH * TILE_SIZE, 5 * TILE_SIZE + 2);
    }

    private void drawBuildings(GraphicsContext gc) {
        for (Map.Entry<String, int[]> entry :
                locationPositions.entrySet()) {
            int[] pos = entry.getValue();
            String id = entry.getKey();
            boolean isCurrent = controller.getCurrentLocation()
                    .getId().equals(id);
            drawBuilding(gc, pos[0], pos[1], id, isCurrent);
        }
    }

    private void drawBuilding(GraphicsContext gc, int x, int y,
                              String id, boolean isCurrent) {
        double px = x * TILE_SIZE;
        double py = y * TILE_SIZE;

        // ombra
        gc.setFill(Color.web("#000000", 0.5));
        gc.fillRect(px + 4, py + 4,
                TILE_SIZE - 4, TILE_SIZE - 4);

        Color baseColor = getBuildingColor(id, isCurrent);
        Color darkColor = baseColor.darker().darker();
        Color lightColor = baseColor.brighter();

        // corpo edificio
        gc.setFill(baseColor);
        gc.fillRect(px + 2, py + 2, TILE_SIZE - 6, TILE_SIZE - 6);

        // tetto
        gc.setFill(darkColor);
        gc.fillRect(px + 2, py + 2, TILE_SIZE - 6, 10);

        // texture mattoni
        gc.setStroke(darkColor);
        gc.setLineWidth(0.5);
        for (int i = 0; i < 3; i++) {
            gc.strokeLine(px + 2, py + 14 + i * 8,
                    px + TILE_SIZE - 4, py + 14 + i * 8);
        }

        // finestre
        gc.setFill(isCurrent ?
                Color.web("#FFD700") : Color.web("#88aaff", 0.6));
        gc.fillRect(px + 6, py + 14, 7, 7);
        gc.fillRect(px + TILE_SIZE - 15, py + 14, 7, 7);

        // porta
        gc.setFill(darkColor);
        gc.fillRect(px + TILE_SIZE / 2 - 4,
                py + TILE_SIZE - 14, 8, 10);

        gc.setStroke(lightColor);
        gc.setLineWidth(1);
        gc.strokeRect(px + TILE_SIZE / 2 - 4,
                py + TILE_SIZE - 14, 8, 10);

        // lettera identificativa
        gc.setFill(isCurrent ?
                Color.web("#FFD700") : Color.web("#ffffff"));
        if (pixelFont != null) gc.setFont(pixelFont);
        gc.fillText(getLocationSymbol(id),
                px + TILE_SIZE / 2 - 3, py + 12);

        // evidenzia il luogo corrente
        if (isCurrent) {
            gc.setFill(Color.web("#FFD700", 0.15));
            gc.fillRect(px, py, TILE_SIZE, TILE_SIZE);
            gc.setStroke(Color.web("#FFD700"));
            gc.setLineWidth(2);
            gc.strokeRect(px + 1, py + 1,
                    TILE_SIZE - 4, TILE_SIZE - 4);
        }
    }

    private Color getBuildingColor(String id, boolean isCurrent) {
        if (isCurrent) return Color.web("#2a4a2a");
        return switch (id) {
            case "redazione" -> Color.web("#1a2a3a");
            case "porto" -> Color.web("#1a1a2a");
            case "ambasciata" -> Color.web("#2a1a1a");
            case "mercato" -> Color.web("#2a2a1a");
            case "osteria" -> Color.web("#2a1a2a");
            case "stazione" -> Color.web("#1a2a2a");
            default -> Color.web("#1a1a1a");
        };
    }

    // disegna il personaggio animato
    private void drawPlayer(GraphicsContext gc) {
        double px = playerX * TILE_SIZE + TILE_SIZE / 2.0;
        double py = playerY * TILE_SIZE + TILE_SIZE / 2.0;

        gc.setFill(Color.web("#000000", 0.4));
        gc.fillOval(px - 8, py + 8, 16, 6);

        // gambe con animazione camminata
        gc.setFill(Color.web("#1a1a6a"));
        if (moving) {
            switch (animFrame % 4) {
                case 0 -> {
                    gc.fillRect(px - 5, py + 2, 4, 10);
                    gc.fillRect(px + 1, py + 2, 4, 8);
                }
                case 1 -> {
                    gc.fillRect(px - 5, py + 2, 4, 8);
                    gc.fillRect(px + 1, py + 2, 4, 10);
                }
                case 2 -> {
                    gc.fillRect(px - 5, py + 2, 4, 6);
                    gc.fillRect(px + 1, py + 2, 4, 10);
                }
                case 3 -> {
                    gc.fillRect(px - 5, py + 2, 4, 10);
                    gc.fillRect(px + 1, py + 2, 4, 6);
                }
            }
        } else {
            gc.fillRect(px - 5, py + 2, 4, 9);
            gc.fillRect(px + 1, py + 2, 4, 9);
        }

        gc.setFill(Color.web("#3a1a00"));
        gc.fillRect(px - 6, py + 10, 5, 3);
        gc.fillRect(px + 1, py + 10, 5, 3);

        // cappotto
        gc.setFill(Color.web("#2a2a2a"));
        gc.fillRect(px - 6, py - 8, 12, 12);

        gc.setFill(Color.web("#1a1a1a"));
        gc.fillRect(px - 4, py - 8, 3, 7);
        gc.fillRect(px + 1, py - 8, 3, 7);

        // braccia
        gc.setFill(Color.web("#2a2a2a"));
        if (moving && animFrame % 2 == 0) {
            gc.fillRect(px - 10, py - 6, 4, 8);
            gc.fillRect(px + 6, py - 4, 4, 8);
        } else {
            gc.fillRect(px - 10, py - 4, 4, 8);
            gc.fillRect(px + 6, py - 6, 4, 8);
        }

        gc.setFill(Color.web("#F5DEB3"));
        gc.fillOval(px - 11, py + 2, 5, 5);
        gc.fillOval(px + 6, py + 2, 5, 5);

        // testa
        gc.setFill(Color.web("#F5DEB3"));
        gc.fillRect(px - 2, py - 10, 4, 4);
        gc.fillOval(px - 6, py - 20, 12, 12);

        gc.setFill(Color.web("#000000"));
        gc.fillOval(px - 4, py - 17, 2, 2);
        gc.fillOval(px + 2, py - 17, 2, 2);

        // cappello fedora
        gc.setFill(Color.web("#111111"));
        gc.fillRect(px - 7, py - 24, 14, 5);
        gc.fillRect(px - 5, py - 29, 10, 7);

        gc.setFill(Color.web("#8B0000"));
        gc.fillRect(px - 5, py - 25, 10, 2);

        // triangolino indicatore posizione
        gc.setFill(Color.web("#FFD700", 0.8));
        gc.fillPolygon(
                new double[]{px, px - 4, px + 4},
                new double[]{py - 32, py - 26, py - 26},
                3);
    }

    private void drawLocationNames(GraphicsContext gc) {
        if (pixelFont != null) gc.setFont(pixelFont);
        for (Map.Entry<String, int[]> entry :
                locationPositions.entrySet()) {
            int[] pos = entry.getValue();
            String id = entry.getKey();
            boolean isCurrent = controller.getCurrentLocation()
                    .getId().equals(id);

            double px = pos[0] * TILE_SIZE;
            double py = pos[1] * TILE_SIZE;

            gc.setFill(Color.web("#000000", 0.7));
            gc.fillRect(px - 2, py - 14, TILE_SIZE + 4, 12);

            gc.setFill(isCurrent ?
                    Color.web("#FFD700") : Color.web("#aaaaaa"));
            gc.fillText(getLocationShortName(id), px, py - 4);
        }
    }

    // mini pannello in basso con info veloci
    private void drawMiniStats(GraphicsContext gc) {
        gc.setFill(Color.web("#000000", 0.8));
        gc.fillRect(4, MAP_HEIGHT * TILE_SIZE - 40, 200, 36);
        gc.setStroke(Color.web("#333333"));
        gc.setLineWidth(1);
        gc.strokeRect(4, MAP_HEIGHT * TILE_SIZE - 40, 200, 36);

        if (pixelFont != null) gc.setFont(pixelFont);
        gc.setFill(Color.web("#FFD700"));
        gc.fillText("LV." +
                        controller.getJournalist().getStats().getLevel(),
                10, MAP_HEIGHT * TILE_SIZE - 24);

        gc.setFill(Color.web("#c8a96e"));
        gc.fillText("PROVE: " +
                        controller.getDiscoveredCluesCount() +
                        "/" + controller.getTotalClues(),
                50, MAP_HEIGHT * TILE_SIZE - 24);

        gc.setFill(Color.web("#aaaaaa"));
        gc.fillText(controller.getCurrentLocation()
                        .getName().toUpperCase(),
                10, MAP_HEIGHT * TILE_SIZE - 10);
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