package es.masanz.ut7.pokemonfx.controller;

import es.masanz.ut7.pokemonfx.manager.MapManager;
import es.masanz.ut7.pokemonfx.model.base.Evento;
import es.masanz.ut7.pokemonfx.model.enums.CollisionType;
import es.masanz.ut7.pokemonfx.model.enums.TileType;
import es.masanz.ut7.pokemonfx.model.enums.TrainerType;
import es.masanz.ut7.pokemonfx.model.fx.NPC;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;

import static es.masanz.ut7.pokemonfx.util.Configuration.*;

public class MapController {

    public static Image jugadorImage;

    private static GraphicsContext gc;
    private static int[][] mapData;
    private static int[][] collisionMap;
    private static String[][] teleportMap;
    private static Evento[][] eventsMap;
    private static WritableImage preRenderedMap;

    private static double playerX;
    private static double playerY;
    private static double targetX;
    private static double targetY;
    private static double offsetX, offsetY;
    private static AnimationTimer gameLoop;

    private static int animationFrame;
    private static int spriteTransitionCount;
    private static boolean moving;
    private static boolean blockGame;
    private static boolean iniciandoTransicion;
    private static KeyCode lastDirection;
    private static Set<KeyCode> pressedKeys;
    private static StackPane root;
    private static Stage mainStage;
    private static List<NPC> npcs;

    public static void init(){
        mapData = new int[MapManager.mapHeight][MapManager.mapWidth];
        collisionMap = new int[MapManager.mapHeight][MapManager.mapWidth];
        teleportMap = new String[MapManager.mapHeight][MapManager.mapWidth];
        eventsMap = new Evento[MapManager.mapHeight][MapManager.mapWidth];

        //playerX = MapManager.mapWidth / 2 * TILE_SIZE;
        //playerY = MapManager.mapHeight / 2 * TILE_SIZE;

        //playerX = MapManager.inicioX;
        //playerY = MapManager.inicioY;

        playerX = MapManager.inicioX;
        playerY = MapManager.inicioY;

        targetX = playerX;
        targetY = playerY;
        animationFrame = 0;
        spriteTransitionCount = 0;
        moving = false;
        blockGame = false;
        iniciandoTransicion = false;
        lastDirection = null;
        pressedKeys = new HashSet<>();
        npcs = new ArrayList<>();
        jugadorImage = TrainerType.JUGADOR.imagen;
        if(gameLoop!=null){
            gameLoop.stop();
            gameLoop = null;
        }
    }

    private static void reload(String ruta) {
        blockGame = true;
        iniciandoTransicion = true;
        Rectangle overlay = new Rectangle(VIEW_WIDTH, VIEW_HEIGHT, Color.BLACK);
        overlay.setOpacity(0);
        root.getChildren().add(overlay);
        Timeline fadeAnimation = new Timeline(
                new KeyFrame(Duration.seconds(0.1), new KeyValue(overlay.opacityProperty(), 1.0))
        );
        fadeAnimation.setCycleCount(1);
        fadeAnimation.setOnFinished(event -> {
            MapManager.cargarRuta(ruta);
            load(mainStage);
        });
        fadeAnimation.play();
    }

    public static void load(Stage primaryStage) {
        init();
        mainStage = primaryStage;
        Canvas canvas = new Canvas(VIEW_WIDTH, VIEW_HEIGHT);
        gc = canvas.getGraphicsContext2D();
        gc.setImageSmoothing(false);
        generateMap();
        preRenderMap();
        root = new StackPane(canvas);
        root.setStyle("-fx-background-color: black;");
        Scene scene = new Scene(root);
        scene.setOnKeyPressed(event -> {
            if(!blockGame) pressedKeys.add(event.getCode());
        });
        scene.setOnKeyReleased(event -> pressedKeys.remove(event.getCode()));
        if(gameLoop!=null){
            gameLoop.stop();
            gameLoop = null;
        }
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateMovement();
                updateNPCMovement();
                render();
            }
        };
        gameLoop.start();

        Button actionButton = new Button("Gestionar nuestros Pokemon");
        actionButton.setOnAction(e -> {
            PokePCController pcC = new PokePCController();
            pcC.load(primaryStage, mainStage.getScene());
        });
        actionButton.setTranslateX(-(VIEW_WIDTH/2) + 100);
        actionButton.setTranslateY(-(VIEW_HEIGHT/2) + 25);
        root.getChildren().add(actionButton);

        primaryStage.setTitle("Ruta 1");
        primaryStage.setScene(scene);
        primaryStage.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == scene) {
                System.out.println("¡Hemos vuelto a la escena principal!");
                iniciandoTransicion = false;
                blockGame = false;
                updateNPCList();
            }
        });
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private static void generateMap() {
        MapManager.generarMapa(mapData, collisionMap, teleportMap, eventsMap, npcs);
    }

    private static void preRenderMap() {
        int scaledWidth = MapManager.mapWidth * TILE_SIZE;
        int scaledHeight = MapManager.mapHeight * TILE_SIZE;
        WritableImage tempImage = new WritableImage(scaledWidth, scaledHeight);
        Canvas tempCanvas = new Canvas(scaledWidth, scaledHeight);
        GraphicsContext gcTemp = tempCanvas.getGraphicsContext2D();
        gcTemp.setImageSmoothing(false);
        for (int y = 0; y < MapManager.mapHeight; y++) {
            for (int x = 0; x < MapManager.mapWidth; x++) {
                TileType tileType = TileType.values()[mapData[y][x]];
                Image tileImage = tileType.imagen;
                gcTemp.drawImage(tileImage, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                if(eventsMap[y][x]!=null && eventsMap[y][x].imagenDelEvento()!=null){
                    URL resource = MapController.class.getResource(eventsMap[y][x].imagenDelEvento());
                    if(resource!=null){
                        Image imagen = new Image(MapController.class.getResource(eventsMap[y][x].imagenDelEvento()).toExternalForm());
                        gcTemp.drawImage(imagen, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    }
                }
            }
        }
        tempCanvas.snapshot(null, tempImage);
        preRenderedMap = tempImage;
    }

    private static void updateMovement() {
        if (!moving && !blockGame) {
            checkNPCVision();
            if(!blockGame){
                if (pressedKeys.contains(KeyCode.W) || pressedKeys.contains(KeyCode.UP)) moveTo(playerX, playerY - TILE_SIZE, KeyCode.W);
                if (pressedKeys.contains(KeyCode.S) || pressedKeys.contains(KeyCode.DOWN)) moveTo(playerX, playerY + TILE_SIZE, KeyCode.S);
                if (pressedKeys.contains(KeyCode.A) || pressedKeys.contains(KeyCode.LEFT)) moveTo(playerX - TILE_SIZE, playerY, KeyCode.A);
                if (pressedKeys.contains(KeyCode.D) || pressedKeys.contains(KeyCode.RIGHT)) moveTo(playerX + TILE_SIZE, playerY, KeyCode.D);
            }
        }

        if (moving) {
            double dx = targetX - playerX;
            double dy = targetY - playerY;
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance > MOVE_SPEED) {
                playerX += (dx / distance) * MOVE_SPEED;
                playerY += (dy / distance) * MOVE_SPEED;
                int spriteTransitionCountIni = spriteTransitionCount;
                spriteTransitionCount = (spriteTransitionCount+1) % VELOCIDAD_CAMBIO_SPRITES;
                if(spriteTransitionCountIni>spriteTransitionCount){
                    animationFrame = (animationFrame + 1) % 6;
                }
            } else {
                playerX = targetX;
                playerY = targetY;
                moving = false;

                // Verificar si el jugador pisa HIERBA_ALTA y hay un 10% de probabilidad de cambiar de escenario
                int tileX = (int) (playerX / TILE_SIZE);
                int tileY = (int) (playerY / TILE_SIZE);
                if (mapData[tileY][tileX] == TileType.CESPED_HIERBA.ordinal() && Math.random() < PROBABILIDAD_POKEMON_SALVAJE) {
                    cambiarAEscenario(null);
                }
                if(teleportMap[tileY][tileX]!=null){
                    reload(teleportMap[tileY][tileX]);
                }
                if(eventsMap[tileY][tileX]!=null){
                    eventsMap[tileY][tileX].aplicarEfecto();
                    eventsMap[tileY][tileX] = null;
                    preRenderMap();
                    blockGame = true;
                    pressedKeys = new HashSet<>();
                    Rectangle overlay = new Rectangle(VIEW_WIDTH, VIEW_HEIGHT, Color.BLACK);
                    overlay.setOpacity(0);
                    root.getChildren().add(overlay);
                    Timeline fadeAnimation = new Timeline(
                            new KeyFrame(Duration.seconds(0.1), new KeyValue(overlay.opacityProperty(), 1.0)),
                            new KeyFrame(Duration.seconds(0.3), new KeyValue(overlay.opacityProperty(), 0.0))
                    );
                    fadeAnimation.setCycleCount(1);
                    fadeAnimation.setOnFinished(event -> {
                        blockGame = false;
                        root.getChildren().remove(overlay);
                    });
                    fadeAnimation.play();

                }
            }
        }

        offsetX = playerX - VIEW_WIDTH / 2 + TILE_SIZE / 2;
        offsetY = playerY - VIEW_HEIGHT / 2 + TILE_SIZE / 2;
    }

    public static void cambiarAEscenario(NPC npc) {
        System.out.println("¡Cambio de escenario! Comenzando batalla pokemon...");

        pressedKeys = new HashSet<>();

        blockGame = true;
        iniciandoTransicion = true;

        Scene escenaActual = mainStage.getScene();

        Rectangle overlay = new Rectangle(VIEW_WIDTH, VIEW_HEIGHT, Color.BLACK);
        overlay.setOpacity(0);
        root.getChildren().add(overlay);

        Timeline fadeAnimation = new Timeline(
                new KeyFrame(Duration.seconds(0.1), new KeyValue(overlay.opacityProperty(), 1.0)),
                new KeyFrame(Duration.seconds(0.3), new KeyValue(overlay.opacityProperty(), 0.0))
        );

        fadeAnimation.setCycleCount(3);

        fadeAnimation.setOnFinished(event -> {
            blockGame = false;
            root.getChildren().remove(overlay);
            loadBattle(escenaActual, npc);
        });

        fadeAnimation.play();
    }

    private static void loadBattle(Scene escenaOriginal, NPC npc) {
        Platform.runLater(() -> {
            try {
                CombateController cc = new CombateController();
                cc.load(mainStage, escenaOriginal, npc, MapManager.rutaSeleccionada);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void moveTo(double x, double y, KeyCode direction) {
        int tileX = (int) (x / TILE_SIZE);
        int tileY = (int) (y / TILE_SIZE);

        if (!moving) {
            lastDirection = direction;
        }

        if (tileX >= 0 && tileX < MapManager.mapWidth && tileY >= 0 && tileY < MapManager.mapHeight) {
            // hacer aqui lo del TP?
            /*
            if(teleportMap[tileY][tileX]!=null){
                reload(teleportMap[tileY][tileX]);
            }
            */

            if (collisionMap[tileY][tileX] != CollisionType.PARED.ordinal()) {
                for (NPC npc : npcs) {
                    if(npc.getX()==tileX && npc.getY()==tileY){
                        return;
                    }
                }
                targetX = x;
                targetY = y;
                lastDirection = direction;
                moving = true;
            }
        }
    }

    private static void render() {
        gc.clearRect(0, 0, VIEW_WIDTH, VIEW_HEIGHT);
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, VIEW_WIDTH, VIEW_HEIGHT);
        gc.drawImage(preRenderedMap, -offsetX, -offsetY);

        drawPlayer(gc, VIEW_WIDTH / 2 - TILE_SIZE / 2, VIEW_HEIGHT / 2 - TILE_SIZE / 2);
        drawNPCs(gc);
    }

    private static void drawPlayer(GraphicsContext gc, int x, int y) {
        int frameX = animationFrame;
        int startIn = 0;

        if (lastDirection == KeyCode.W || lastDirection == KeyCode.UP) {
            frameX = frameX % 3;
            startIn = 3;
        }
        if (lastDirection == KeyCode.S || lastDirection == KeyCode.DOWN) {
            frameX = frameX % 3;
            startIn = 0;
        }
        if (lastDirection == KeyCode.A || lastDirection == KeyCode.LEFT) {
            frameX = frameX % 2;
            startIn = 6;
        }
        if (lastDirection == KeyCode.D || lastDirection == KeyCode.RIGHT) {
            frameX = frameX % 2;
            startIn = 8;
        }

        WritableImage playerSprite = new WritableImage(jugadorImage.getPixelReader(), (startIn + frameX) * TILE_SIZE / SCALE_FACTOR, 0, TILE_SIZE / SCALE_FACTOR, TILE_SIZE / SCALE_FACTOR);

        PixelReader pixelReader = playerSprite.getPixelReader();
        PixelWriter pixelWriter = playerSprite.getPixelWriter();

        for (int i = 0; i < TILE_SIZE / SCALE_FACTOR; i++) {
            for (int j = 0; j < TILE_SIZE / SCALE_FACTOR; j++) {
                Color color = pixelReader.getColor(i, j);
                if (Math.round(color.getRed()*255) == 255 && Math.round(color.getGreen()*255) == 127 && Math.round(color.getBlue()*255) == 39) {
                    pixelWriter.setColor(i, j, Color.TRANSPARENT);
                } else {
                    pixelWriter.setColor(i, j, color);
                }
            }
        }
        gc.drawImage(playerSprite, x, y, TILE_SIZE, TILE_SIZE);
    }

    private static void drawNPCs(GraphicsContext gc) {
        for (NPC npc : npcs) {
            WritableImage enemySprite = npc.getSprite();
            gc.drawImage(enemySprite, npc.getPosX() - offsetX, npc.getPosY() - offsetY, TILE_SIZE, TILE_SIZE);
        }
    }

    private static void checkNPCVision() {
        for (NPC npc : npcs) {
            int npcTileX = npc.getX();
            int npcTileY = npc.getY();

            int playerTileX = (int) (playerX / TILE_SIZE);
            int playerTileY = (int) (playerY / TILE_SIZE);

            int distanciaDeVision = 3;

            int npcMirandoA = npc.getMirandoA();

            if(npcTileX == playerTileX && (npcTileY - playerTileY) <= distanciaDeVision &&
                    (npcTileY - playerTileY) >= 0 && npcMirandoA==ARRIBA){
                boolean hayVision = true;
                for (int i = 0; i < npcTileY - playerTileY; i++) {
                    if (collisionMap[playerTileY + i][playerTileX] == CollisionType.PARED.ordinal()) {
                        hayVision = false;
                    }
                }
                if(hayVision) {
                    blockGame = true;
                    npc.setActive(true);
                }
            } else if(npcTileX == playerTileX && (playerTileY - npcTileY) <= distanciaDeVision &&
                    (playerTileY - npcTileY) >= 0 && npcMirandoA==ABAJO){
                boolean hayVision = true;
                for (int i = 0; i < playerTileY - npcTileY; i++) {
                    if (collisionMap[npcTileY + i][npcTileX] == CollisionType.PARED.ordinal()) {
                        hayVision = false;
                    }
                }
                if(hayVision) {
                    blockGame = true;
                    npc.setActive(true);
                }
            } else if(npcTileY == playerTileY && (npcTileX - playerTileX) <= distanciaDeVision &&
                    (npcTileX - playerTileX) >= 0 && npcMirandoA==IZQUIERDA){
                boolean hayVision = true;
                for (int i = 0; i < npcTileX - playerTileX; i++) {
                    if (collisionMap[playerTileY][playerTileX + i] == CollisionType.PARED.ordinal()) {
                        hayVision = false;
                    }
                }
                if(hayVision) {
                    blockGame = true;
                    npc.setActive(true);
                }
            } else if(npcTileY == playerTileY && (playerTileX - npcTileX) <= distanciaDeVision &&
                    (playerTileX - npcTileX) >= 0 && npcMirandoA==DERECHA){
                boolean hayVision = true;
                for (int i = 0; i < playerTileX - npcTileX; i++) {
                    if (collisionMap[npcTileY][npcTileX + i] == CollisionType.PARED.ordinal()) {
                        hayVision = false;
                    }
                }
                if(hayVision) {
                    blockGame = true;
                    npc.setActive(true);
                }
            }
        }
    }

    private static void updateNPCMovement() {
        if(!iniciandoTransicion) {
            for (NPC npc : npcs) {
                if (npc.isActive()) {
                    npc.moveTowards(playerX, playerY);
                    if (Math.abs(npc.getX() - playerX) < TILE_SIZE && Math.abs(npc.getY() - playerY) < TILE_SIZE) {
                        cambiarAEscenario(npc);
                    }
                }
            }
        }
    }

    private static void updateNPCList(){
        Iterator<NPC> iterator = npcs.iterator();
        while(iterator.hasNext()) {
            NPC npc = iterator.next();
            if (npc.isEliminarNPC()) {
                iterator.remove();
            }
        }
    }

}
