package it.unicam.cs.mpgc.rpg130929.view;

import it.unicam.cs.mpgc.rpg130929.controller.GameController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.util.Duration;

// schermata di benvenuto con tutorial a più step
public class WelcomeView {

    private final GameController controller;
    private final Stage stage;
    private Label typewriterLabel;
    private Timeline typewriterTimeline;

    // colori tema sepia
    private static final String GOLD = "#c8a96e";
    private static final String DARK_GOLD = "#8B6914";
    private static final String BG_DARK = "#050200";
    private static final String TEXT_DIM = "#6b5530";

    // testi del tutorial
    private final String[] titles = {
            "IL CRONISTA",
            "ANNO 1935",
            "LA TUA MISSIONE",
            "COME GIOCARE",
            "PERICOLO",
            "SEI PRONTO?"
    };

    private final String[] contents = {
            "Una citta' avvolta\nnell'ombra della guerra.\n\nSpie, traditori\ne segreti oscuri\nsi nascondono\ndietro ogni angolo.\n\nTu sei l'unico\nche puo' scoprire\nla verita'...",
            "L'Europa trema.\nLa guerra si avvicina.\n\nReti di spionaggio\ninfiltrano ogni\nistituzione della\ncitta'.\n\nIl tuo giornale\nti ha incaricato\ndi scoprire tutto.",
            "Infiltra la rete\ndi spie straniere.\n\nRaccogli le prove,\nparla con i contatti,\nsvela la cospirazione\ne scrivi il rapporto\nche cambiera'\nla storia!",
            "WASD / FRECCE\n= Muovi l'agente\n\nRAGGIUNGI un luogo\nper esplorarlo\n\nPARLA con i\nCONTATTI locali\n\nSCRIVI il RAPPORTO\nfinale",
            "Le spie sono\novunque.\n\nIl tuo carisma\ndetermina le\ninformazioni\nche ottieni.\n\nSali di livello\nper diventare\npiu' potente.",
            "La citta' aspetta.\n\nLe spie operano\nnell'ombra.\n\nIl tuo giornale\nti aspetta.\n\nBuona fortuna,\nAgente..."
    };

    public WelcomeView(GameController controller, Stage stage) {
        this.controller = controller;
        this.stage = stage;
    }

    public void show() {
        showStep(0);
    }

    private void showStep(int step) {
        if (typewriterTimeline != null) typewriterTimeline.stop();

        Font pixelFont = Font.loadFont(
                getClass().getClassLoader()
                        .getResourceAsStream("PressStart2P-Regular.ttf"), 11);
        Font titleFont = Font.loadFont(
                getClass().getClassLoader()
                        .getResourceAsStream("PressStart2P-Regular.ttf"), 22);

        // disegno lo sfondo con canvas
        Canvas bgCanvas = new Canvas(1280, 800);
        drawBackground(bgCanvas.getGraphicsContext2D());

        VBox content = new VBox(20);
        content.setPadding(new Insets(40));
        content.setAlignment(Pos.CENTER);
        content.setMaxWidth(680);
        content.setStyle(
                "-fx-background-color: rgba(5,2,0,0.90);" +
                        "-fx-border-color: " + DARK_GOLD + ";" +
                        "-fx-border-width: 3px;");

        Label topDeco = new Label("✦ ─────────── ✦");
        topDeco.setStyle("-fx-text-fill: " + DARK_GOLD + ";");
        if (pixelFont != null)
            topDeco.setFont(Font.font(pixelFont.getFamily(), 9));

        Label stepLabel = new Label(
                "[ " + (step + 1) + " di " + titles.length + " ]");
        stepLabel.setStyle("-fx-text-fill: " + TEXT_DIM + ";");
        if (pixelFont != null)
            stepLabel.setFont(Font.font(pixelFont.getFamily(), 8));

        Label titleLabel = new Label(titles[step]);
        titleLabel.setStyle(
                "-fx-text-fill: " + GOLD + ";" +
                        "-fx-effect: dropshadow(gaussian," +
                        DARK_GOLD + ",15,0.7,0,0);");
        if (titleFont != null)
            titleLabel.setFont(Font.font(titleFont.getFamily(), 22));

        Label sep1 = new Label("── ✦ ──");
        sep1.setStyle("-fx-text-fill: " + DARK_GOLD + ";");
        if (pixelFont != null)
            sep1.setFont(Font.font(pixelFont.getFamily(), 10));

        // label con effetto macchina da scrivere
        typewriterLabel = new Label("");
        typewriterLabel.setStyle("-fx-text-fill: " + GOLD + ";");
        typewriterLabel.setWrapText(true);
        typewriterLabel.setTextAlignment(TextAlignment.CENTER);
        typewriterLabel.setAlignment(Pos.CENTER);
        typewriterLabel.setMaxWidth(580);
        if (pixelFont != null)
            typewriterLabel.setFont(
                    Font.font(pixelFont.getFamily(), 11));
        startTypewriterEffect(contents[step]);

        Label sep2 = new Label("── ✦ ──");
        sep2.setStyle("-fx-text-fill: " + DARK_GOLD + ";");
        if (pixelFont != null)
            sep2.setFont(Font.font(pixelFont.getFamily(), 10));

        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER);

        if (step > 0) {
            Button prevBtn = buildButton("< INDIETRO",
                    pixelFont, false);
            prevBtn.setOnAction(e -> showStep(step - 1));
            buttons.getChildren().add(prevBtn);
        }
        if (step < titles.length - 1) {
            Button nextBtn = buildButton("AVANTI >",
                    pixelFont, false);
            nextBtn.setOnAction(e -> showStep(step + 1));
            buttons.getChildren().add(nextBtn);
        } else {
            // ultimo step — pulsante per iniziare
            Button startBtn = buildButton(
                    ">> INIZIA MISSIONE <<", pixelFont, true);
            startBtn.setOnAction(e -> {
                if (typewriterTimeline != null)
                    typewriterTimeline.stop();
                new GameView(controller, stage).show();
            });
            buttons.getChildren().add(startBtn);
        }

        Label bottomDeco = new Label("✦ ─────────── ✦");
        bottomDeco.setStyle("-fx-text-fill: " + DARK_GOLD + ";");
        if (pixelFont != null)
            bottomDeco.setFont(Font.font(pixelFont.getFamily(), 9));

        content.getChildren().addAll(topDeco, stepLabel,
                titleLabel, sep1, typewriterLabel, sep2,
                buttons, bottomDeco);

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: " + BG_DARK + ";");
        root.getChildren().addAll(bgCanvas, content);

        Scene scene = new Scene(root, 1280, 800);
        scene.setFill(Color.web(BG_DARK));
        stage.setTitle("IL CRONISTA - OPERAZIONE OMBRA");
        stage.setScene(scene);
        stage.show();
    }

    private void drawBackground(GraphicsContext gc) {
        gc.setFill(Color.web("#050200"));
        gc.fillRect(0, 0, 1280, 800);

        // sfumatura cielo
        for (int y = 0; y < 600; y++) {
            double ratio = y / 600.0;
            gc.setFill(Color.web("#0a0510", 0.4 * (1 - ratio)));
            gc.fillRect(0, y, 1280, 1);
        }

        drawStars(gc);
        drawMoon(gc, 980, 60);
        drawPixelBuildings(gc);

        drawLampPost(gc, 100, 480);
        drawLampPost(gc, 350, 470);
        drawLampPost(gc, 1130, 470);

        drawInvestigator(gc, 1130, 470);

        // strada e marciapiede
        gc.setFill(Color.web("#100800"));
        gc.fillRect(0, 648, 1280, 152);
        gc.setFill(Color.web("#0d0600"));
        gc.fillRect(0, 638, 1280, 12);
        gc.setStroke(Color.web("#1a0d00"));
        gc.setLineWidth(1);
        gc.strokeLine(0, 638, 1280, 638);

        // riflessi lampioni
        gc.setFill(Color.web("#c8a96e", 0.06));
        gc.fillOval(70, 650, 90, 25);
        gc.fillOval(320, 645, 90, 25);
        gc.fillOval(1100, 645, 90, 25);
    }

    private void drawStars(GraphicsContext gc) {
        // stelle grandi con alone
        int[][] bigStars = {
                {50,25},{150,45},{280,20},{420,55},
                {580,30},{720,60},{860,25},{1100,40},
                {1220,70},{200,80},{650,90},{900,50},
                {1180,30},{70,100},{350,110},{500,70},
                {800,85},{1050,95},{130,130},{450,140}
        };

        for (int[] s : bigStars) {
            gc.setFill(Color.web("#c8a96e", 0.15));
            gc.fillOval(s[0] - 3, s[1] - 3, 10, 10);
            gc.setFill(Color.web("#e8d090", 0.9));
            gc.fillOval(s[0], s[1], 4, 4);
            gc.setFill(Color.web("#ffffff", 0.7));
            gc.fillOval(s[0] + 1, s[1] + 1, 2, 2);
        }

        // stelle medie
        int[][] medStars = {
                {30,60},{110,35},{230,95},{370,25},
                {490,80},{610,45},{740,110},{820,35},
                {950,75},{1050,55},{1160,85},{1240,45},
                {85,145},{310,125},{560,135},{690,100},
                {930,120},{1200,110},{160,160},{410,150}
        };

        for (int[] s : medStars) {
            gc.setFill(Color.web("#c8a96e", 0.7));
            gc.fillOval(s[0], s[1], 3, 3);
        }

        // stelle piccole
        int[][] smallStars = {
                {20,40},{60,15},{90,75},{180,30},{250,65},
                {330,85},{400,10},{470,50},{540,90},{620,20},
                {680,70},{760,40},{840,95},{910,15},{970,65},
                {1020,35},{1090,75},{1130,20},{1250,55},{1270,90},
                {45,115},{115,160},{185,105},{265,170},{345,115},
                {415,165},{505,120},{575,155},{645,125},{715,170},
                {785,115},{855,160},{925,130},{995,165},{1065,120},
                {1135,155},{1205,125},{1275,165},{25,175},{295,145}
        };

        for (int[] s : smallStars) {
            gc.setFill(Color.web("#c8a96e",
                    0.3 + (s[0] % 7) * 0.08));
            gc.fillOval(s[0], s[1], 2, 2);
        }

        // stelle con effetto croce
        int[][] sparkleStars = {
                {200,50},{500,35},{780,55},{1050,40},{320,90}
        };
        for (int[] s : sparkleStars) {
            gc.setFill(Color.web("#ffffff", 0.8));
            gc.fillRect(s[0], s[1] - 4, 2, 10);
            gc.fillRect(s[0] - 4, s[1], 10, 2);
            gc.setFill(Color.web("#c8a96e", 0.4));
            gc.fillOval(s[0] - 5, s[1] - 5, 12, 12);
        }
    }

    private void drawMoon(GraphicsContext gc, int x, int y) {
        int size = 110;

        // alone esterno
        gc.setFill(Color.web("#c8a96e", 0.05));
        gc.fillOval(x - 25, y - 25, size + 50, size + 50);
        gc.setFill(Color.web("#c8a96e", 0.10));
        gc.fillOval(x - 12, y - 12, size + 24, size + 24);

        // corpo luna
        gc.setFill(Color.web("#f0d878"));
        gc.fillOval(x, y, size, size);
        gc.setFill(Color.web("#ffe89a"));
        gc.fillOval(x + 8, y + 6, size - 20, size - 20);
        gc.setFill(Color.web("#c8a050", 0.5));
        gc.fillOval(x + size / 3, y + 5, size / 2, size - 10);

        // crateri
        gc.setFill(Color.web("#b89040", 0.6));
        gc.fillOval(x + 20, y + 20, 28, 22);
        gc.setFill(Color.web("#d4a855", 0.4));
        gc.fillOval(x + 23, y + 23, 18, 14);

        gc.setFill(Color.web("#b89040", 0.5));
        gc.fillOval(x + 60, y + 35, 18, 15);
        gc.setFill(Color.web("#d4a855", 0.3));
        gc.fillOval(x + 63, y + 38, 10, 9);

        gc.setFill(Color.web("#b89040", 0.5));
        gc.fillOval(x + 30, y + 65, 20, 16);
        gc.setFill(Color.web("#d4a855", 0.3));
        gc.fillOval(x + 33, y + 68, 12, 10);

        gc.setFill(Color.web("#b89040", 0.4));
        gc.fillOval(x + 75, y + 20, 10, 8);
        gc.fillOval(x + 15, y + 55, 12, 10);
        gc.fillOval(x + 55, y + 70, 8, 7);
        gc.fillOval(x + 82, y + 60, 10, 8);
        gc.fillOval(x + 40, y + 15, 8, 6);

        gc.setFill(Color.web("#c8a050", 0.15));
        gc.fillOval(x + 5, y + 40, size - 15, 20);
        gc.fillOval(x + 10, y + 70, size - 25, 15);

        gc.setStroke(Color.web("#a87830", 0.4));
        gc.setLineWidth(2);
        gc.strokeOval(x, y, size, size);
    }

    private void drawPixelBuildings(GraphicsContext gc) {
        Color buildingColor = Color.web("#2a1500");
        Color buildingDark = Color.web("#1a0d00");
        Color windowOn = Color.web("#c8a96e");
        Color windowDim = Color.web("#8B6914", 0.6);

        drawPixelBuilding(gc, 0, 400, 90, 250,
                buildingColor, buildingDark, windowOn, 3, 6);
        drawPixelBuilding(gc, 100, 280, 70, 370,
                buildingColor, buildingDark, windowOn, 2, 9);

        // toretta sull'edificio 2
        gc.setFill(buildingDark);
        gc.fillRect(115, 250, 40, 35);
        gc.fillRect(125, 230, 20, 25);
        double[] gx = {135, 128, 142};
        double[] gy = {200, 230, 230};
        gc.setFill(Color.web("#3a1a00"));
        gc.fillPolygon(gx, gy, 3);

        drawPixelBuilding(gc, 180, 350, 110, 300,
                buildingColor, buildingDark, windowDim, 3, 7);
        drawPixelBuilding(gc, 300, 200, 80, 450,
                buildingColor, buildingDark, windowOn, 2, 12);

        gc.setFill(buildingDark);
        gc.fillRect(315, 170, 50, 35);
        gc.fillRect(325, 150, 30, 25);

        drawPixelBuilding(gc, 390, 300, 140, 350,
                buildingColor, buildingDark, windowOn, 4, 8);

        // grattacielo centrale con antenna
        drawPixelBuilding(gc, 540, 150, 90, 500,
                buildingColor, buildingDark, windowOn, 2, 14);
        gc.setFill(buildingDark);
        gc.fillRect(555, 120, 60, 35);
        gc.fillRect(565, 100, 40, 25);
        gc.fillRect(575, 80, 20, 25);
        gc.setFill(Color.web("#4a2500"));
        gc.fillRect(583, 50, 4, 35);
        gc.setFill(Color.web("#ff4444", 0.8));
        gc.fillOval(581, 45, 8, 8);

        drawPixelBuilding(gc, 640, 280, 100, 370,
                buildingColor, buildingDark, windowDim, 3, 9);
        drawPixelBuilding(gc, 750, 220, 85, 430,
                buildingColor, buildingDark, windowOn, 2, 11);
        drawPixelBuilding(gc, 845, 320, 120, 330,
                buildingColor, buildingDark, windowDim, 3, 7);
        drawPixelBuilding(gc, 975, 260, 95, 390,
                buildingColor, buildingDark, windowOn, 2, 10);
        drawPixelBuilding(gc, 1080, 300, 110, 350,
                buildingColor, buildingDark, windowDim, 3, 8);
        drawPixelBuilding(gc, 1200, 350, 80, 300,
                buildingColor, buildingDark, windowOn, 2, 7);
    }

    // disegna un singolo edificio pixel art
    private void drawPixelBuilding(GraphicsContext gc,
                                   int x, int y, int w, int h,
                                   Color base, Color dark, Color windowColor,
                                   int cols, int rows) {
        gc.setFill(base);
        gc.fillRect(x, y, w, h);
        gc.setFill(dark);
        gc.fillRect(x + w - 8, y, 8, h);
        gc.fillRect(x, y, w, 8);

        // fasce orizzontali
        gc.setFill(dark);
        for (int i = 1; i < rows / 3; i++) {
            gc.fillRect(x, y + i * (h / 3), w, 2);
        }

        int winW = 8;
        int winH = 10;
        int marginX = (w - cols * (winW + 6)) / 2;
        int marginY = 15;
        int spacingY = h / (rows + 1);

        // finestre
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int wx = x + marginX + c * (winW + 6);
                int wy = y + marginY + r * spacingY;
                boolean isLit = (wx + wy) % 3 != 0;
                gc.setFill(isLit ? windowColor :
                        Color.web("#050200"));
                gc.fillRect(wx, wy, winW, winH);
                gc.setStroke(dark);
                gc.setLineWidth(1);
                gc.strokeRect(wx - 1, wy - 1,
                        winW + 2, winH + 2);
                if (isLit) {
                    gc.setStroke(dark);
                    gc.strokeLine(wx + winW / 2, wy,
                            wx + winW / 2, wy + winH);
                }
            }
        }
    }

    private void drawLampPost(GraphicsContext gc, int x, int y) {
        gc.setFill(Color.web("#3a2a10"));
        gc.fillRect(x, y, 5, 100);
        gc.setFill(Color.web("#2a1a08"));
        gc.fillRect(x - 5, y + 95, 15, 8);
        gc.setFill(Color.web("#3a2a10"));
        gc.fillRect(x - 20, y, 25, 5);
        gc.fillRect(x - 22, y - 5, 5, 10);
        gc.setFill(Color.web("#c8a96e"));
        gc.fillRect(x - 28, y - 20, 18, 20);
        gc.setFill(Color.web("#8B6914"));
        gc.fillRect(x - 30, y - 22, 22, 4);
        gc.fillRect(x - 30, y, 22, 4);
        // alone luce
        gc.setFill(Color.web("#c8a96e", 0.18));
        gc.fillOval(x - 55, y - 45, 72, 72);
        gc.setFill(Color.web("#c8a96e", 0.08));
        gc.fillOval(x - 75, y - 65, 112, 112);
    }

    // disegna l'investigatore di profilo appoggiato al lampione
    private void drawInvestigator(GraphicsContext gc,
                                  int lampX, int lampY) {
        int x = lampX + 25;
        int y = lampY + 15;

        gc.setFill(Color.web("#000000", 0.5));
        gc.fillOval(x - 5, y + 148, 55, 14);

        // stivali di profilo
        gc.setFill(Color.web("#1a0d00"));
        gc.fillRect(x + 10, y + 118, 14, 22);
        gc.fillRect(x + 8, y + 136, 18, 8);
        gc.setFill(Color.web("#0d0600"));
        gc.fillRect(x + 20, y + 118, 12, 20);
        gc.fillRect(x + 18, y + 134, 16, 7);

        // pantaloni
        gc.setFill(Color.web("#1a1000"));
        gc.fillRect(x + 10, y + 90, 14, 32);
        gc.setFill(Color.web("#100b00"));
        gc.fillRect(x + 20, y + 90, 10, 30);

        // cappotto lungo
        gc.setFill(Color.web("#2a1800"));
        gc.fillRect(x + 5, y + 20, 35, 75);
        gc.setFill(Color.web("#1a0e00"));
        gc.fillRect(x + 28, y + 20, 12, 75);
        gc.setFill(Color.web("#1a0d00"));
        gc.fillRect(x + 5, y + 20, 7, 35);

        // bottoni
        gc.setFill(Color.web("#8B6914"));
        for (int i = 0; i < 3; i++) {
            gc.fillOval(x + 14, y + 30 + i * 14, 4, 4);
        }

        gc.setFill(Color.web("#3a2000"));
        gc.fillRect(x + 5, y + 55, 35, 5);
        gc.setFill(Color.web("#8B6914"));
        gc.fillRect(x + 18, y + 54, 10, 7);

        gc.setFill(Color.web("#2a1800"));
        gc.fillRect(x + 2, y + 20, 8, 18);
        gc.setFill(Color.web("#1a0e00"));
        gc.fillRect(x + 38, y + 20, 6, 16);

        // braccio sinistro appoggiato al lampione
        gc.setFill(Color.web("#2a1800"));
        gc.fillRect(x - 5, y + 22, 12, 45);
        gc.fillRect(x - 10, y + 55, 15, 10);
        gc.fillRect(x - 15, y + 60, 15, 8);
        gc.setFill(Color.web("#b89060"));
        gc.fillOval(x - 18, y + 62, 10, 9);

        // braccio destro con giornale
        gc.setFill(Color.web("#1a0e00"));
        gc.fillRect(x + 36, y + 22, 8, 40);
        gc.setFill(Color.web("#a07840"));
        gc.fillOval(x + 35, y + 60, 9, 9);

        // giornale
        gc.setFill(Color.web("#d4b878"));
        gc.fillRect(x + 40, y + 40, 32, 28);
        gc.setFill(Color.web("#c8a96e"));
        gc.fillRect(x + 56, y + 40, 16, 28);
        gc.setFill(Color.web("#1a0d00"));
        gc.fillRect(x + 42, y + 44, 12, 2);
        gc.fillRect(x + 42, y + 49, 12, 2);
        gc.fillRect(x + 42, y + 54, 8, 2);
        gc.fillRect(x + 58, y + 44, 12, 2);
        gc.fillRect(x + 58, y + 49, 12, 2);
        gc.fillRect(x + 58, y + 54, 10, 2);
        gc.setFill(Color.web("#0a0500"));
        gc.fillRect(x + 42, y + 42, 26, 4);

        // collo
        gc.setFill(Color.web("#b89060"));
        gc.fillRect(x + 10, y + 8, 6, 14);

        // testa di profilo
        gc.setFill(Color.web("#1a0d00"));
        gc.fillOval(x + 5, y - 18, 26, 28);
        gc.setFill(Color.web("#c8a96e"));
        gc.fillOval(x + 8, y - 16, 22, 26);
        gc.fillRect(x + 14, y - 18, 12, 10);

        // occhio
        gc.setFill(Color.web("#1a0d00"));
        gc.fillOval(x + 12, y - 8, 5, 4);
        gc.fillRect(x + 11, y - 11, 8, 2);
        gc.setFill(Color.web("#e8d090"));
        gc.fillOval(x + 13, y - 7, 3, 3);
        gc.setFill(Color.web("#0a0500"));
        gc.fillOval(x + 13, y - 7, 2, 2);

        // naso
        gc.setFill(Color.web("#b89060"));
        gc.fillRect(x + 28, y - 6, 5, 4);
        gc.fillRect(x + 30, y - 2, 4, 4);

        // bocca e baffi
        gc.setFill(Color.web("#a07050"));
        gc.fillRect(x + 24, y + 4, 6, 3);
        gc.setFill(Color.web("#1a0d00"));
        gc.fillRect(x + 18, y + 2, 12, 3);

        // sigaretta con fumo
        gc.setFill(Color.web("#e8d090"));
        gc.fillRect(x + 28, y + 5, 18, 3);
        gc.setFill(Color.web("#ff4400", 0.8));
        gc.fillOval(x + 44, y + 4, 5, 5);
        gc.setFill(Color.web("#c8a96e", 0.25));
        gc.fillOval(x + 46, y - 5, 10, 12);
        gc.setFill(Color.web("#c8a96e", 0.15));
        gc.fillOval(x + 44, y - 18, 14, 15);
        gc.setFill(Color.web("#c8a96e", 0.08));
        gc.fillOval(x + 42, y - 32, 18, 17);

        // cappello fedora
        gc.setFill(Color.web("#111111"));
        gc.fillRect(x + 4, y - 20, 42, 5);
        gc.fillRect(x + 8, y - 42, 24, 24);
        gc.fillRect(x + 5, y - 20, 10, 4);
        gc.setFill(Color.web("#8B6914"));
        gc.fillRect(x + 8, y - 20, 24, 3);
        gc.setFill(Color.web("#111111"));
        gc.fillOval(x + 8, y - 46, 24, 10);
        gc.setFill(Color.web("#0a0500", 0.3));
        gc.fillRect(x + 8, y - 16, 24, 8);

        // luce lampione sul personaggio
        gc.setFill(Color.web("#c8a96e", 0.06));
        gc.fillOval(x - 25, y - 30, 90, 210);
    }

    private Button buildButton(String text,
                               Font pixelFont, boolean isPrimary) {
        Button btn = new Button(text);
        String color = isPrimary ? GOLD : DARK_GOLD;
        String padding = isPrimary ? "12px 28px" : "10px 20px";
        double fontSize = isPrimary ? 12 : 9;

        btn.setStyle(
                "-fx-background-color: " + BG_DARK + ";" +
                        "-fx-text-fill: " + color + ";" +
                        "-fx-border-color: " + color + ";" +
                        "-fx-border-width: 2px;" +
                        "-fx-padding: " + padding + ";");
        if (pixelFont != null)
            btn.setFont(Font.font(pixelFont.getFamily(), fontSize));

        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: " + BG_DARK + ";" +
                        "-fx-border-color: " + color + ";" +
                        "-fx-border-width: 2px;" +
                        "-fx-padding: " + padding + ";"));
        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: " + BG_DARK + ";" +
                        "-fx-text-fill: " + color + ";" +
                        "-fx-border-color: " + color + ";" +
                        "-fx-border-width: 2px;" +
                        "-fx-padding: " + padding + ";"));
        return btn;
    }

    // effetto macchina da scrivere — mostra il testo carattere per carattere
    private void startTypewriterEffect(String text) {
        typewriterLabel.setText("");
        final int[] index = {0};
        typewriterTimeline = new Timeline(
                new KeyFrame(Duration.millis(35), e -> {
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