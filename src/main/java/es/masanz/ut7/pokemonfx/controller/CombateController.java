package es.masanz.ut7.pokemonfx.controller;

import es.masanz.ut7.pokemonfx.app.GameApp;
import es.masanz.ut7.pokemonfx.manager.PokemonManager;
import es.masanz.ut7.pokemonfx.model.base.Ataque;
import es.masanz.ut7.pokemonfx.model.base.Pokemon;
import es.masanz.ut7.pokemonfx.model.fx.NPC;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static es.masanz.ut7.pokemonfx.util.Configuration.*;

public class CombateController {

    private ImageView playerPokemonIV;
    private ImageView npcPokemonIV;
    private VBox playerInfo;
    private VBox npcInfo;
    private Pane root;
    private Stage primaryStage;
    private Scene previousStage;

    private Pokemon selectedPokemon;
    private NPC oponentNPC;
    private Pokemon npcPokemon;
    private Pokemon[] availablePokemons;
    private String ruta;

    public void load(Stage primaryStage, Scene previousStage, NPC npc, String ruta) {
        this.primaryStage = primaryStage;
        this.previousStage = previousStage;
        this.oponentNPC = npc;
        this.ruta = ruta;
        loadAvailablePokemons();
        loadBattleScene(false);
    }

    private void loadBattleScene(boolean pokemonEnemigoAtaca) {
        root = new Pane();

        if(selectedPokemon==null){
            mostrarPantallaDerrota();
            return;
        }

        URL resource = getClass().getResource(POKEMONS_BACK_PATH + selectedPokemon.getClass().getSimpleName() + "_espalda_G1.png");
        playerPokemonIV = new ImageView(new Image(resource.toString()));
        playerPokemonIV.setFitWidth(80);
        playerPokemonIV.setFitHeight(80);
        playerPokemonIV.setLayoutX(60);
        playerPokemonIV.setLayoutY(220 - (playerPokemonIV.getFitHeight() + 2));
        playerPokemonIV.setSmooth(false);
        playerPokemonIV.setPreserveRatio(true);

        npcPokemonIV = new ImageView(new Image(getClass().getResource(POKEMONS_FRONT_PATH+ npcPokemon.getClass().getSimpleName()+"_RA.png").toExternalForm()));
        npcPokemonIV.setFitWidth(80);
        npcPokemonIV.setFitHeight(80);
        npcPokemonIV.setLayoutX(340);
        npcPokemonIV.setLayoutY(20);
        npcPokemonIV.setSmooth(false);
        npcPokemonIV.setPreserveRatio(true);

        npcInfo = crearInfoBox(
                npcPokemon.getClass().getSimpleName(),
                npcPokemon.getNivel(),
                npcPokemon.getHpActual(),
                npcPokemon.getMaxHP(),
                false
        );
        npcInfo.setLayoutX(20);
        npcInfo.setLayoutY(20);

        playerInfo = crearInfoBox(
                selectedPokemon.getApodo()!=null?selectedPokemon.getApodo():selectedPokemon.getClass().getSimpleName(),
                selectedPokemon.getNivel(),
                selectedPokemon.getHpActual(),
                selectedPokemon.getMaxHP(),
                true
        );
        playerInfo.setLayoutX(245);
        playerInfo.setLayoutY(220 - 80);

        VBox supercommandBox = new VBox();
        supercommandBox.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 2px; -fx-padding: 10px;");
        supercommandBox.setLayoutX(20);
        supercommandBox.setLayoutY(220);
        supercommandBox.setPrefSize(440, 80);

        VBox commandBox = new VBox();
        commandBox.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 3px; -fx-padding: 10px;");
        commandBox.setLayoutX(25);
        commandBox.setLayoutY(225);
        commandBox.setPrefSize(430, 70);

        Label battleText = new Label();
        battleText.setText("¿Qué quieres hacer?");
        battleText.setFont(Font.font("Arial", 14));
        battleText.setTextFill(Color.BLACK);
        battleText.setWrapText(true);

        HBox buttonLayout = new HBox();
        buttonLayout.setSpacing(10);

        cargarBotonesDeAcciones(battleText, buttonLayout);

        commandBox.getChildren().addAll(battleText, buttonLayout);

        root.getChildren().addAll(playerPokemonIV, npcPokemonIV, npcInfo, playerInfo, supercommandBox, commandBox);

        Scene scene = new Scene(root, 480, 320);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Combate Pokémon");
        primaryStage.setResizable(false);
        primaryStage.show();

        if(pokemonEnemigoAtaca){
            seleccionarAtaqueOponente();
            selectedPokemon.setAtaqueSeleccionado(null);
            ejecutarRondaAtaque(battleText, buttonLayout);
        }
    }

    private void cargarBotonesDeAcciones(Label battleText, HBox buttonLayout){
        buttonLayout.getChildren().clear();
        battleText.setText("¿Qué quieres hacer?");

        Button attackButton = new Button("Atacar");
        Button itemButton = new Button("Mochila");
        Button pokemonButton = new Button("Pokémon");
        Button runButton = new Button("Huir");

        if(oponentNPC!=null){
            runButton.setDisable(true);
            itemButton.setDisable(true);
        }

        attackButton.setOnAction(e -> {
            cargarBotonesDeCombate(battleText, buttonLayout);
        });

        itemButton.setOnAction(e -> {
            cargarBotonesDeMochila(battleText, buttonLayout);
        });

        pokemonButton.setOnAction(e -> {
            createPokemonSelector();
        });

        runButton.setOnAction(e -> {
            primaryStage.setScene(previousStage);
        });

        buttonLayout.getChildren().addAll(attackButton, itemButton, pokemonButton, runButton);
    }

    private void cargarBotonesDeCombate(Label battleText, HBox buttonLayout){
        buttonLayout.getChildren().clear();
        battleText.setText("");

        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(10);
        statsGrid.setVgap(5);
        int auxContador = 0;
        HashMap<String, Ataque> ataques = selectedPokemon.getAtaques();
        for (Ataque ataque : ataques.values()) {
            Button btnAtaque = new Button(ataque.getNombre()+" ("+ataque.getCantidad()+"/"+ataque.getPp()+")");
            btnAtaque.setOnAction(e -> {
                seleccionarAtaqueOponente();
                selectedPokemon.setAtaqueSeleccionado(ataque);
                ejecutarRondaAtaque(battleText, buttonLayout);
            });
            if(ataque.getCantidad()<=0){
                buttonLayout.setDisable(true);
            }
            statsGrid.add(btnAtaque, (auxContador%2), (auxContador/2));

            auxContador++;
            if(auxContador>3){
                break;
            }
        }
        statsGrid.setMaxHeight(10);
        statsGrid.setMinHeight(10);
        statsGrid.setPrefHeight(10);
        statsGrid.setTranslateY(statsGrid.getTranslateY()-20);
        Button returnButton = new Button("Volver");
        returnButton.setOnAction(e -> {
            cargarBotonesDeAcciones(battleText, buttonLayout);
        });
        buttonLayout.getChildren().add(returnButton);



        buttonLayout.getChildren().add(statsGrid);
    }

    private void cargarBotonesDeMochila(Label battleText, HBox buttonLayout){
        buttonLayout.getChildren().clear();
        battleText.setText("¿Qué quieres hacer?");

        Button pokeballButton = new Button("Usar POKEBALL");
        Button returnButton = new Button("Volver");

        pokeballButton.setOnAction(e -> {
            intentarCaputarPokemonSalvaje(battleText, buttonLayout);
        });

        returnButton.setOnAction(e -> {
            cargarBotonesDeAcciones(battleText, buttonLayout);
        });

        buttonLayout.getChildren().addAll(pokeballButton, returnButton);
    }

    private void intentarCaputarPokemonSalvaje(Label battleText, HBox buttonLayout) {
        battleText.setText("Has lanzado una POKEBALL");
        buttonLayout.getChildren().clear();

        ImageView pokeballIV = new ImageView(new Image(getClass().getResource("/pruebas/pokeball_transparente.png").toExternalForm()));

        pokeballIV.setScaleX(2);
        pokeballIV.setScaleY(2);
        pokeballIV.setLayoutX(50);
        pokeballIV.setLayoutY(300);
        pokeballIV.setSmooth(false);
        pokeballIV.setPreserveRatio(true);

        root.getChildren().addAll(pokeballIV);

        double finalPokeballX = npcPokemonIV.getLayoutX() - pokeballIV.getLayoutX() + 25;

        TranslateTransition movePokeball = new TranslateTransition(Duration.seconds(0.5), pokeballIV);
        movePokeball.setToX(finalPokeballX);
        movePokeball.setToY(npcPokemonIV.getLayoutY() - pokeballIV.getLayoutY() + 25);

        ScaleTransition shrinkPokemon = new ScaleTransition(Duration.seconds(0.2), npcPokemonIV);
        shrinkPokemon.setToX(0);
        shrinkPokemon.setToY(0);

        FadeTransition fadePokemon = new FadeTransition(Duration.seconds(0.2), npcPokemonIV);
        fadePokemon.setToValue(0);

        movePokeball.setOnFinished(event -> {
            ParallelTransition captureAnimation = new ParallelTransition(shrinkPokemon, fadePokemon);
            captureAnimation.setOnFinished(event2 -> {
                double suelo = npcPokemonIV.getLayoutY() - pokeballIV.getLayoutY() + 75;
                botarPokeball(suelo, finalPokeballX, pokeballIV, battleText, buttonLayout);
            });
            captureAnimation.play();
        });

        movePokeball.play();
    }

    private void botarPokeball(double suelo, double finalPokeballX, ImageView pokeballIV, Label battleText, HBox buttonLayout){
        double primerRebote = suelo - 30;
        double segundoRebote = suelo - 15;

        TranslateTransition caida1 = new TranslateTransition(Duration.seconds(0.25), pokeballIV);
        caida1.setToY(suelo);
        caida1.setInterpolator(Interpolator.EASE_IN);

        TranslateTransition rebote1 = new TranslateTransition(Duration.seconds(0.2), pokeballIV);
        rebote1.setToY(primerRebote);
        rebote1.setInterpolator(Interpolator.EASE_OUT);

        TranslateTransition caida2 = new TranslateTransition(Duration.seconds(0.2), pokeballIV);
        caida2.setToY(suelo);
        caida2.setInterpolator(Interpolator.EASE_IN);

        TranslateTransition rebote2 = new TranslateTransition(Duration.seconds(0.1), pokeballIV);
        rebote2.setToY(segundoRebote);
        rebote2.setInterpolator(Interpolator.EASE_OUT);

        TranslateTransition caidaFinal = new TranslateTransition(Duration.seconds(0.1), pokeballIV);
        caidaFinal.setToY(suelo);
        caidaFinal.setInterpolator(Interpolator.EASE_IN);
        caida1.setOnFinished(e -> rebote1.play());
        rebote1.setOnFinished(e -> caida2.play());
        caida2.setOnFinished(e -> rebote2.play());
        rebote2.setOnFinished(e -> caidaFinal.play());
        caidaFinal.setOnFinished(e -> rotarPokeball(finalPokeballX, pokeballIV, 3, battleText, buttonLayout));

        caida1.play();
    }

    private void rotarPokeball(double posX, ImageView pokeballIV, int contador, Label battleText, HBox buttonLayout){
        double tiempoAnimacionRotacion = 0.2;
        int gradosRotacionPokeball = 60;
        int distanciaRotacionPokeball = 12;

        RotateTransition rotateLeft = new RotateTransition(Duration.seconds(tiempoAnimacionRotacion), pokeballIV);
        rotateLeft.setByAngle(-gradosRotacionPokeball);
        TranslateTransition rotateLeftT = new TranslateTransition(Duration.seconds(tiempoAnimacionRotacion), pokeballIV);
        rotateLeftT.setToX(posX-distanciaRotacionPokeball);
        ParallelTransition rotateLeftAnimation = new ParallelTransition(rotateLeft, rotateLeftT);

        RotateTransition rotateBack1 = new RotateTransition(Duration.seconds(tiempoAnimacionRotacion), pokeballIV);
        rotateBack1.setByAngle(gradosRotacionPokeball);
        TranslateTransition rotateBack1T = new TranslateTransition(Duration.seconds(tiempoAnimacionRotacion), pokeballIV);
        rotateBack1T.setToX(posX);
        ParallelTransition rotateBack1Animation = new ParallelTransition(rotateBack1, rotateBack1T);

        RotateTransition rotateRight = new RotateTransition(Duration.seconds(tiempoAnimacionRotacion), pokeballIV);
        rotateRight.setByAngle(gradosRotacionPokeball);
        TranslateTransition rotateRightT = new TranslateTransition(Duration.seconds(tiempoAnimacionRotacion), pokeballIV);
        rotateRightT.setToX(posX+distanciaRotacionPokeball);
        rotateRightT.setInterpolator(Interpolator.EASE_IN);
        ParallelTransition rotateRightAnimation = new ParallelTransition(rotateRight, rotateRightT);

        RotateTransition rotateBack2 = new RotateTransition(Duration.seconds(tiempoAnimacionRotacion), pokeballIV);
        rotateBack2.setByAngle(-gradosRotacionPokeball);
        TranslateTransition rotateBack2T = new TranslateTransition(Duration.seconds(tiempoAnimacionRotacion), pokeballIV);
        rotateBack2T.setToX(posX);
        rotateBack2T.setInterpolator(Interpolator.EASE_IN);
        ParallelTransition rotateBack2Animation = new ParallelTransition(rotateBack2, rotateBack2T);

        rotateLeftAnimation.setOnFinished(e -> rotateBack1Animation.play());
        rotateBack1Animation.setOnFinished(e -> rotateRightAnimation.play());
        rotateRightAnimation.setOnFinished(e -> rotateBack2Animation.play());
        rotateBack2Animation.setOnFinished(e -> {
            boolean pokemonAtrapado = true;
            int porcentajeVidaPokemon = 100 - (npcPokemon.getHpActual() * 100) / npcPokemon.getMaxHP();
            int numRandom = (int) (Math.random()*100);
            if(numRandom > porcentajeVidaPokemon){
                pokemonAtrapado = false;
            }
            if(!pokemonAtrapado){
                liberarPokeball(pokeballIV, battleText, buttonLayout);
            } else {
                if(contador>1) {
                    rotarPokeball(posX, pokeballIV, contador-1, battleText, buttonLayout);
                } else {
                    atraparPokemon(battleText, buttonLayout);
                }
            }
        });

        rotateLeftAnimation.play();
    }

    private void liberarPokeball(ImageView pokeballIV, Label battleText, HBox buttonLayout){
        battleText.setText("Oh... No has logrado atraparlo [PAQUETE]");
        ScaleTransition growPokemon = new ScaleTransition(Duration.seconds(0.2), npcPokemonIV);
        growPokemon.setToX(1);
        growPokemon.setToY(1);

        FadeTransition showPokemon = new FadeTransition(Duration.seconds(0.2), npcPokemonIV);
        showPokemon.setToValue(1);

        ScaleTransition shrinkPokeball = new ScaleTransition(Duration.seconds(0.2), pokeballIV);
        shrinkPokeball.setToX(0);
        shrinkPokeball.setToY(0);

        FadeTransition fadePokeball = new FadeTransition(Duration.seconds(0.2), pokeballIV);
        fadePokeball.setToValue(0);

        ParallelTransition reappearPokemon = new ParallelTransition(growPokemon, showPokemon, shrinkPokeball, fadePokeball);

        reappearPokemon.setOnFinished(e -> {
            new Timeline(new KeyFrame(Duration.seconds(1), e2 -> {
                seleccionarAtaqueOponente();
                selectedPokemon.setAtaqueSeleccionado(null);
                ejecutarRondaAtaque(battleText, buttonLayout);
            })).play();
        });

        reappearPokemon.play();
    }

    private void atraparPokemon(Label battleText, HBox buttonLayout){
        battleText.setText("¡Enhorabuena! Has logrado atrapar a ["+npcPokemon.getClass().getSimpleName()+"]");
        GameApp.jugador.getPokemonesCapturados().add(npcPokemon);
        new Timeline(new KeyFrame(Duration.millis(1500), e2 -> mostrarPantallaPokemonCapturado())).play();
    }

    private Ataque seleccionarAtaqueOponente(){
        HashMap<String, Ataque> ataquesMap = npcPokemon.getAtaques();
        List<Ataque> ataques = new ArrayList<>(ataquesMap.values());
        Ataque ataqueRandom = ataques.get((int) (Math.random() * ataques.size()));
        npcPokemon.setAtaqueSeleccionado(ataqueRandom);
        return ataqueRandom;
    }

    private void ejecutarRondaAtaque(Label battleText, HBox buttonLayout) {

        Ataque ataqueJugador = selectedPokemon.getAtaqueSeleccionado();
        Ataque ataqueOponente = npcPokemon.getAtaqueSeleccionado();
        if(ataqueOponente==null){
            return;
        }
        // Si, lo se, se puede hacer mejor...
        Pokemon primeroEnAtacar = null;
        Pokemon segundoEnAtacar = null;
        VBox primeraInfoBox = null;
        VBox segundaInfoBox = null;
        ImageView primeraIV = null;
        ImageView segundaIV = null;
        if(ataqueJugador==null){
            primeroEnAtacar = npcPokemon;
            segundoEnAtacar = selectedPokemon;
            primeraInfoBox = npcInfo;
            segundaInfoBox = playerInfo;
            primeraIV = npcPokemonIV;
            segundaIV = playerPokemonIV;
        } else if(ataqueJugador.getPrioridad()>ataqueOponente.getPrioridad()){
            primeroEnAtacar = selectedPokemon;
            segundoEnAtacar = npcPokemon;
            primeraInfoBox = playerInfo;
            segundaInfoBox = npcInfo;
            primeraIV = playerPokemonIV;
            segundaIV = npcPokemonIV;
        } else if(ataqueOponente.getPrioridad()>ataqueJugador.getPrioridad()) {
            primeroEnAtacar = npcPokemon;
            segundoEnAtacar = selectedPokemon;
            primeraInfoBox = npcInfo;
            segundaInfoBox = playerInfo;
            primeraIV = npcPokemonIV;
            segundaIV = playerPokemonIV;
        } else {
            if(selectedPokemon.getVelocidad()>npcPokemon.getVelocidad()){
                primeroEnAtacar = selectedPokemon;
                segundoEnAtacar = npcPokemon;
                primeraInfoBox = playerInfo;
                segundaInfoBox = npcInfo;
                primeraIV = playerPokemonIV;
                segundaIV = npcPokemonIV;
            } else if(npcPokemon.getVelocidad()>selectedPokemon.getVelocidad()){
                primeroEnAtacar = npcPokemon;
                segundoEnAtacar = selectedPokemon;
                primeraInfoBox = npcInfo;
                segundaInfoBox = playerInfo;
                primeraIV = npcPokemonIV;
                segundaIV = playerPokemonIV;
            } else {
                int random = (int) (Math.random()*2);
                if(random==0){
                    primeroEnAtacar = selectedPokemon;
                    segundoEnAtacar = npcPokemon;
                    primeraInfoBox = playerInfo;
                    segundaInfoBox = npcInfo;
                    primeraIV = playerPokemonIV;
                    segundaIV = npcPokemonIV;
                } else {
                    primeroEnAtacar = npcPokemon;
                    segundoEnAtacar = selectedPokemon;
                    primeraInfoBox = npcInfo;
                    segundaInfoBox = playerInfo;
                    primeraIV = npcPokemonIV;
                    segundaIV = playerPokemonIV;
                }
            }
        }

        Pokemon[] primeroEAArray = {primeroEnAtacar};
        Pokemon[] segundoEAArray = {segundoEnAtacar};
        VBox[] primeraIBArray = {primeraInfoBox};
        VBox[] segundaIBArray = {segundaInfoBox};
        ImageView[] primeraIVArray = {primeraIV};
        ImageView[] segundaIVArray = {segundaIV};

        if(primeroEAArray[0].getHpActual()>0 && primeroEAArray[0].getAtaqueSeleccionado()!=null) {
            buttonLayout.getChildren().clear();
            int hpActualPrimero = segundoEAArray[0].getHpActual();
            String msg = primeroEAArray[0].atacar(segundoEAArray[0]);
            battleText.setText(msg);

            TranslateTransition shake = new TranslateTransition(Duration.millis(150), segundaIVArray[0]);
            shake.setByX(10);
            shake.setAutoReverse(true);
            shake.setCycleCount(6);
            shake.play();

            Rectangle barraVida = buscarBarraVida(segundaIBArray[0]);
            Rectangle[] primeraBVArray = {barraVida};
            double healthPercentage = (double) segundoEAArray[0].getHpActual() / segundoEAArray[0].getMaxHP();
            //barraVida.setWidth(196 * healthPercentage);
            //METO ANIMACION
            double newWidth = 196 * healthPercentage;
            KeyValue keyValue = new KeyValue(primeraBVArray[0].widthProperty(), newWidth, Interpolator.EASE_IN);
            KeyFrame keyFrame = new KeyFrame(Duration.millis(VELOCIDAD_ANIMACIONES), keyValue);
            Timeline timeline = new Timeline(keyFrame);

            Label hpLabel = buscarHPLabel(segundaIBArray[0]);
            //hpLabel.setText("HP: "+segundoEAArray[0].getHpActual()+" / "+segundoEAArray[0].getMaxHP());
            //METO ANIMACION
            Label[] hpLArray = {hpLabel};
            String oldText = hpLabel.getText();
            int oldHP = Integer.parseInt(oldText.split("HP: ")[1].split(" /")[0]);
            Timeline timelineLabel = new Timeline();
            int frames = 30;
            double frameDuration = VELOCIDAD_ANIMACIONES / frames;
            int hpChange = segundoEAArray[0].getHpActual() - oldHP;
            for (int i = 0; i <= frames; i++) {
                int interpolatedHP = oldHP + (hpChange * i) / frames;
                KeyFrame keyFrameLabel = new KeyFrame(
                        Duration.millis(i * frameDuration),
                        e -> hpLArray[0].setText("HP: " + interpolatedHP + " / " + segundoEAArray[0].getMaxHP())
                );
                timelineLabel.getKeyFrames().add(keyFrameLabel);
            }

            ParallelTransition parallelTransition = new ParallelTransition(timeline, timelineLabel);
            parallelTransition.setOnFinished(e -> {
                if (segundoEAArray[0].getHpActual() > 0 && segundoEAArray[0].getAtaqueSeleccionado()!=null) {
                    int hpActualSegundo = primeroEAArray[0].getHpActual();
                    String msg2 = segundoEAArray[0].atacar(primeroEAArray[0]);
                    battleText.setText(msg2);
                    Rectangle barraVida2 = buscarBarraVida(primeraIBArray[0]);
                    double healthPercentage2 = (double) primeroEAArray[0].getHpActual() / primeroEAArray[0].getMaxHP();
                    //barraVida2.setWidth(196 * healthPercentage2);
                    //METO ANIMACION
                    double newWidth2 = 196 * healthPercentage2;
                    KeyValue keyValue2 = new KeyValue(barraVida2.widthProperty(), newWidth2, Interpolator.EASE_IN);
                    KeyFrame keyFrame2 = new KeyFrame(Duration.millis(VELOCIDAD_ANIMACIONES), keyValue2);
                    Timeline timeline2 = new Timeline(keyFrame2);

                    Label hpLabel2 = buscarHPLabel(primeraIBArray[0]);
                    //hpLabel2.setText("HP: "+primeroEAArray[0].getHpActual()+" / "+primeroEAArray[0].getMaxHP());
                    //METO ANIMACION
                    Label[] hpLArray2 = {hpLabel2};
                    String oldText2 = hpLabel2.getText();
                    int oldHP2 = Integer.parseInt(oldText2.split("HP: ")[1].split(" /")[0]);
                    Timeline timelineLabel2 = new Timeline();
                    int hpChange2 = primeroEAArray[0].getHpActual() - oldHP2;
                    for (int i = 0; i <= frames; i++) {
                        int interpolatedHP2 = oldHP2 + (hpChange2 * i) / frames;
                        KeyFrame keyFrameLabel2 = new KeyFrame(
                                Duration.millis(i * frameDuration),
                                e2 -> hpLArray2[0].setText("HP: " + interpolatedHP2 + " / " + primeroEAArray[0].getMaxHP())
                        );
                        timelineLabel2.getKeyFrames().add(keyFrameLabel2);
                    }

                    ParallelTransition parallelTransition2 = new ParallelTransition(timeline2, timelineLabel2);
                    parallelTransition2.setOnFinished(e2 -> {

                        if(primeroEAArray[0].getHpActual()<=0 || segundoEAArray[0].getHpActual()<=0){
                            if(selectedPokemon.getHpActual()>0){
                                int expObtenida = calcularExperienciaObtenida(selectedPokemon, npcPokemon);
                                String nombrePokemon = selectedPokemon.getApodo()!=null?selectedPokemon.getApodo():selectedPokemon.getClass().getSimpleName();
                                String msg3 = "["+nombrePokemon+"] obtiene ["+expObtenida+"] puntos de [EXPERIENCIA].";
                                battleText.setText(msg3);
                                boolean aumentaNivel = selectedPokemon.sumarExperiencia(expObtenida);
                                if(aumentaNivel) {
                                    msg3 = msg3 + " Su [NIVEL] ahora es ["+selectedPokemon.getNivel()+"].";
                                }
                                battleText.setText(msg3);
                                Timeline lapsoSubirExp = new Timeline(new KeyFrame(Duration.millis(1500), e3 -> analizarSituacion()));
                                lapsoSubirExp.play();
                            } else {
                                analizarSituacion();
                            }
                        } else {
                            cargarBotonesDeAcciones(battleText, buttonLayout);
                        }
                    });
                    parallelTransition2.play();

                    TranslateTransition shake2 = new TranslateTransition(Duration.millis(150), primeraIVArray[0]);
                    shake2.setByX(10);
                    shake2.setAutoReverse(true);
                    shake2.setCycleCount(6);
                    shake2.play();

                } else {
                    if(primeroEAArray[0].getHpActual()<=0 || segundoEAArray[0].getHpActual()<=0){
                        if(selectedPokemon.getHpActual()>0){
                            int expObtenida = calcularExperienciaObtenida(selectedPokemon, npcPokemon);
                            String nombrePokemon = selectedPokemon.getApodo()!=null?selectedPokemon.getApodo():selectedPokemon.getClass().getSimpleName();
                            String msg3 = "["+nombrePokemon+"] obtiene ["+expObtenida+"] puntos de [EXPERIENCIA].";
                            battleText.setText(msg3);
                            boolean aumentaNivel = selectedPokemon.sumarExperiencia(expObtenida);
                            if(aumentaNivel) {
                                msg3 = msg3 + " Su [NIVEL] ahora es ["+selectedPokemon.getNivel()+"].";
                            }
                            battleText.setText(msg3);
                            Timeline lapsoSubirExp = new Timeline(new KeyFrame(Duration.millis(1500), e3 -> analizarSituacion()));
                            lapsoSubirExp.play();
                        } else {
                            analizarSituacion();
                        }
                    } else {
                        cargarBotonesDeAcciones(battleText, buttonLayout);
                    }
                }
            });
            parallelTransition.play();
        }
    }

    // Me lo guardo para mas adelante...
    public TextFlow generarMensajeCombate(Pokemon pokemon, int dano){
        Text text1 = new Text("[");
        Text boldText1 = new Text(pokemon.getApodo()!=null ? pokemon.getApodo() : pokemon.getClass().getSimpleName());
        boldText1.setStyle("-fx-font-weight: bold;");
        Text text2 = new Text("] ejecuta [");
        Text boldText2 = new Text(pokemon.getAtaqueSeleccionado().getNombre());
        boldText2.setStyle("-fx-font-weight: bold;");
        Text text3 = new Text("] y hace [");
        Text boldText3 = new Text(String.valueOf(dano));
        boldText3.setStyle("-fx-font-weight: bold;");
        Text text4 = new Text("] puntos de daño.");
        TextFlow textFlow = new TextFlow(text1, boldText1, text2, boldText2, text3, boldText3, text4);
        return textFlow;
    }

    private void analizarSituacion() {
        boolean jugadorDerrotado = true;
        for (int i = 0; i < availablePokemons.length; i++) {
            Pokemon pokemon = availablePokemons[i];
            if(pokemon!=null && pokemon.getHpActual()>0){
                jugadorDerrotado = false;
                break;
            }
        }
        if(oponentNPC != null) {
            boolean terminarCombate = true;
            for (int i = 0; i < oponentNPC.getEntrenador().getPokemonesCombate().length; i++) {
                Pokemon pokemon = oponentNPC.getEntrenador().getPokemonesCombate()[i];
                if(pokemon!=null && pokemon.getHpActual()>0){
                    terminarCombate = false;
                    break;
                }
            }
            if(terminarCombate){
                mostrarPantallaVictoria();
            } else {
                if(jugadorDerrotado){
                    mostrarPantallaDerrota();
                } else {
                    if(selectedPokemon.getHpActual()<=0){
                        TranslateTransition shake = new TranslateTransition(Duration.millis(VELOCIDAD_ANIMACIONES/2), playerPokemonIV);
                        shake.setByX(-1000);
                        shake.setOnFinished(e -> {
                            createPokemonSelector();
                        });
                        shake.play();
                    } else {
                        TranslateTransition shake = new TranslateTransition(Duration.millis(VELOCIDAD_ANIMACIONES/2), npcPokemonIV);
                        shake.setByX(1000);
                        shake.setOnFinished(e -> {
                            load(primaryStage, previousStage, oponentNPC, ruta);
                        });
                        shake.play();
                    }
                }
            }
        } else if (npcPokemon != null && npcPokemon.getHpActual()<=0) {
            mostrarPantallaVictoria();
        } else {
            if(jugadorDerrotado){
                mostrarPantallaDerrota();
            } else {
                createPokemonSelector();
            }
        }
    }

    private void mostrarPantallaVictoria() {
        StackPane pantallaVictoria = new StackPane();
        pantallaVictoria.setStyle("-fx-background-color: black; -fx-opacity: 0.9;");

        Label mensaje = new Label("Has ganado el combate");
        mensaje.setTextFill(Color.WHITE);
        mensaje.setFont(new Font(24));

        Button botonContinuar = new Button("Continuar");
        botonContinuar.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 16px;");
        botonContinuar.setOnAction(e -> analizarEvolucionPokemon());

        VBox contenedor = new VBox(20, mensaje, botonContinuar);
        contenedor.setAlignment(Pos.CENTER);
        pantallaVictoria.getChildren().add(contenedor);
        Scene escenaVictoria = new Scene(pantallaVictoria, primaryStage.getWidth(), primaryStage.getHeight());

        primaryStage.setScene(escenaVictoria);
    }

    private int calcularExperienciaObtenida(Pokemon pokemonGanador, Pokemon pokemonDerrotado) {
        int nivelPokemonGanador = pokemonGanador.getNivel();
        int expBasePokemonDerrotado = pokemonDerrotado.getExpBase();
        int nivelPokemonDerrotado = pokemonDerrotado.getNivel();
        double experiencia = (expBasePokemonDerrotado * Math.pow(nivelPokemonDerrotado, 2)) / (7.0 * nivelPokemonGanador);
        return (int) Math.round(experiencia);
    }

    private void mostrarPantallaPokemonCapturado() {
        StackPane pantallaPokemonCapturado = new StackPane();
        pantallaPokemonCapturado.setStyle("-fx-background-color: black; -fx-opacity: 0.9;");

        Label mensaje = new Label("¡Has capturado a " + npcPokemon.getClass().getSimpleName() + "!");
        mensaje.setTextFill(Color.WHITE);
        mensaje.setFont(new Font(24));

        Label instruccion = new Label("Introduce un apodo (opcional):");
        instruccion.setTextFill(Color.WHITE);
        instruccion.setFont(new Font(16));

        TextField inputApodo = new TextField();
        inputApodo.setMaxWidth(300);
        inputApodo.setMinWidth(300);
        inputApodo.setPrefWidth(300);
        inputApodo.setPromptText("Escribe un apodo...");

        Label nota = new Label("(Si no introduces un apodo, se usará el nombre original)");
        nota.setTextFill(Color.GRAY);
        nota.setFont(new Font(12));

        Button botonContinuar = new Button("Continuar");
        botonContinuar.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 16px;");
        botonContinuar.setOnAction(e -> {
            String apodo = inputApodo.getText().trim();
            if (apodo!=null && !apodo.isEmpty()) {
                npcPokemon.setApodo(apodo);
            }
            analizarEvolucionPokemon();
        });

        VBox contenedor = new VBox(15, mensaje, instruccion, inputApodo, nota, botonContinuar);
        contenedor.setAlignment(Pos.CENTER);
        pantallaPokemonCapturado.getChildren().add(contenedor);
        Scene escenaVictoria = new Scene(pantallaPokemonCapturado, primaryStage.getWidth(), primaryStage.getHeight());

        primaryStage.setScene(escenaVictoria);
    }

    private void mostrarPantallaDerrota() {
        StackPane pantallaDerrota = new StackPane();
        pantallaDerrota.setStyle("-fx-background-color: black; -fx-opacity: 0.9;");

        Label mensaje = new Label("Oh... Has perdido :(");
        mensaje.setTextFill(Color.WHITE);
        mensaje.setFont(new Font(24));

        Button botonContinuar = new Button("Continuar");
        botonContinuar.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 16px;");
        botonContinuar.setOnAction(e -> analizarEvolucionPokemon());

        VBox contenedor = new VBox(20, mensaje, botonContinuar);
        contenedor.setAlignment(Pos.CENTER);
        pantallaDerrota.getChildren().add(contenedor);
        Scene escenaVictoria = new Scene(pantallaDerrota, primaryStage.getWidth(), primaryStage.getHeight());

        primaryStage.setScene(escenaVictoria);
    }

    private void analizarEvolucionPokemon() {
        boolean hayEvolucion = false;
        for (int i = 0; i < availablePokemons.length; i++) {
            Pokemon pokemon = availablePokemons[i];
            if (pokemon != null && pokemon.getNivel() >= pokemon.nivelEvolucion() && pokemon.pokemonAEvolucionar() != null) {
                hayEvolucion = true;
                Pokemon evolucionPokemon = pokemon.pokemonAEvolucionar();

                StackPane pantallaEvolucion = new StackPane();
                pantallaEvolucion.setStyle("-fx-background-color: black; -fx-opacity: 0.9;");

                String nombre = (pokemon.getApodo() != null) ? pokemon.getApodo() : pokemon.getClass().getSimpleName();
                Label mensaje = new Label("¡Vaya! ¡[" + nombre + "] está EVOLUCIONANDO!");
                mensaje.setTextFill(Color.WHITE);
                mensaje.setFont(new Font(24));

                URL resource = getClass().getResource(POKEMONS_FRONT_PATH + pokemon.getClass().getSimpleName() + "_RA.png");
                ImageView pokemonIV = new ImageView(new Image(resource.toString()));

                URL resource2 = getClass().getResource(POKEMONS_FRONT_PATH + evolucionPokemon.getClass().getSimpleName() + "_RA.png");
                ImageView evolucionPokemonIV = new ImageView(new Image(resource2.toString()));

                StackPane spriteContainer = new StackPane(pokemonIV, evolucionPokemonIV);
                evolucionPokemonIV.setVisible(false);

                Button botonContinuar = new Button("Continuar");
                botonContinuar.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 16px;");
                botonContinuar.setOnAction(e -> analizarEvolucionPokemon());
                botonContinuar.setVisible(false);

                VBox contenedor = new VBox(20, mensaje, spriteContainer, botonContinuar);
                contenedor.setAlignment(Pos.CENTER);
                pantallaEvolucion.getChildren().add(contenedor);
                Scene escenaEvolucion = new Scene(pantallaEvolucion, VIEW_WIDTH, VIEW_HEIGHT);
                primaryStage.setScene(escenaEvolucion);

                Timeline timeline = new Timeline();

                timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(0.5), e -> {
                    pokemonIV.setVisible(!pokemonIV.isVisible());
                    evolucionPokemonIV.setVisible(!evolucionPokemonIV.isVisible());
                }));

                timeline.setCycleCount(5);

                int finalI = i;
                timeline.setOnFinished(e -> {
                    botonContinuar.setVisible(true);
                    availablePokemons[finalI] = evolucionPokemon;
                    mensaje.setText("¡Enhorabuena! ["+nombre+"] evolucionó en ["+evolucionPokemon.getClass().getSimpleName()+"]");
                });

                timeline.play();
                break;
            }
        }
        if(!hayEvolucion){
            primaryStage.setScene(previousStage);
        }
    }

    public Rectangle buscarBarraVida(Parent padre) {
        for (Node node : padre.getChildrenUnmodifiable()) {
            if (node instanceof Rectangle) {
                return (Rectangle) node;
            } else if (node instanceof Parent) {
                Rectangle found = buscarBarraVida((Parent) node);
                if (found != null) return found;
            }
        }
        return null;
    }

    public Label buscarHPLabel(Parent padre) {
        for (Node node : padre.getChildrenUnmodifiable()) {
            if (node instanceof Label) {
                Label label = (Label) node;
                if (label.getText().startsWith("HP:")) {
                    return label;
                }
            } else if (node instanceof Parent) {
                Label found = buscarHPLabel((Parent) node);
                if (found != null) return found;
            }
        }
        return null;
    }

    private void loadAvailablePokemons() {
        if(selectedPokemon==null || selectedPokemon.getHpActual()<=0){
            availablePokemons = GameApp.jugador.getPokemonesCombate();
            for (int i = 0; i < 6; i++) {
                Pokemon pokemon = availablePokemons[i];
                if(pokemon!=null && pokemon.getHpActual()>0){
                    selectedPokemon = pokemon;
                    break;
                }
            }
        }
        if(npcPokemon==null || npcPokemon.getHpActual()<=0){
            if(oponentNPC!=null && oponentNPC.getEntrenador()!=null &&
                    oponentNPC.getEntrenador().getPokemonesCombate()!=null && oponentNPC.getEntrenador().getPokemonesCombate().length>0){
                Pokemon[] oponentPokemons = oponentNPC.getEntrenador().getPokemonesCombate();
                for (int i = 0; i < 6; i++) {
                    Pokemon pokemon = oponentPokemons[i];
                    if(pokemon!=null && pokemon.getHpActual()>0){
                        npcPokemon = pokemon;
                        break;
                    }
                }
            } else {
                npcPokemon = PokemonManager.generarPokemonSalvaje(this.ruta);
            }
        }
    }

    private void createPokemonSelector() {
        VBox selectorLayout = new VBox(5);
        selectorLayout.setAlignment(Pos.CENTER);
        selectorLayout.setStyle("-fx-padding: 10px;");

        for (Pokemon pokemon : availablePokemons) {
            HBox row = new HBox(5);
            row.setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-padding: 2px;");
            row.setPrefHeight(46);
            row.setMinHeight(46);
            row.setMaxHeight(46);
            row.setAlignment(Pos.CENTER_LEFT);

            if(pokemon!=null){
                URL resource = getClass().getResource(POKEMONS_BACK_PATH + pokemon.getClass().getSimpleName() + "_espalda_G1.png");
                playerPokemonIV = new ImageView(new Image(resource.toString()));
                ImageView pokemonImage = new ImageView(new Image(resource.toString()));
                pokemonImage.setFitWidth(40);
                pokemonImage.setFitHeight(40);

                VBox nameAndHealth = new VBox(5);
                Label nameLabel = new Label((pokemon.getApodo()!=null?pokemon.getApodo():pokemon.getClass().getSimpleName()) + " Lv." + pokemon.getNivel());
                nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
                nameLabel.setTextFill(Color.BLACK);

                nameAndHealth.getChildren().addAll(nameLabel);

                addHealthBar(nameAndHealth, pokemon.getHpActual(), pokemon.getMaxHP());

                Label hpLabel = new Label("HP: "+pokemon.getHpActual()+" / "+pokemon.getMaxHP());
                hpLabel.setFont(Font.font("Arial", 12));
                hpLabel.setTextFill(Color.BLACK);

                HBox hpContainer = new HBox(hpLabel);
                hpContainer.setPadding(new Insets(18, 0, 0, 5));

                Button selectButton = new Button("Seleccionar");
                HBox.setHgrow(selectButton, Priority.ALWAYS);
                selectButton.setMaxWidth(Double.MAX_VALUE);
                selectButton.setOnAction(e -> {
                    boolean pokemonEnemigoAtaca = (selectedPokemon.getHpActual()>0) && (selectedPokemon != pokemon);
                    selectedPokemon = pokemon;
                    loadBattleScene(pokemonEnemigoAtaca);
                });

                HBox buttonContainer = new HBox(selectButton);
                buttonContainer.setAlignment(Pos.CENTER_RIGHT);
                buttonContainer.setPrefWidth(100);

                if(pokemon.getHpActual()<=0){
                    buttonContainer.setVisible(false);
                }

                row.getChildren().addAll(pokemonImage, nameAndHealth, hpContainer, buttonContainer);
            }

            selectorLayout.getChildren().add(row);
        }

        Scene selectorScene = new Scene(selectorLayout, 480, 320);
        primaryStage.setScene(selectorScene);
    }

    private VBox crearInfoBox(String nombre, int nivel, int vidaActual, int vidaMaxima, boolean esJugador) {
        VBox superBox = new VBox();

        HBox pokeballsBox = new HBox(5);
        pokeballsBox.setAlignment(Pos.CENTER_RIGHT);

        if(esJugador){
            for (int i = 0; i < availablePokemons.length; i++) {
                Pokemon pokemon = availablePokemons[i];
                if(pokemon!=null){
                    ImageView pokeballIV = new ImageView(new Image(getClass().getResource("/pruebas/pokeball_transparente.png").toExternalForm()));
                    pokeballIV.setFitWidth(16);
                    pokeballIV.setFitHeight(16);
                    pokeballIV.setSmooth(false);
                    if(pokemon.getHpActual()<=0){
                        ColorAdjust grayscale = new ColorAdjust();
                        grayscale.setSaturation(-1);
                        pokeballIV.setEffect(grayscale);
                    }
                    pokeballsBox.getChildren().add(pokeballIV);
                }
            }
        } else {
            if(oponentNPC!=null && oponentNPC.getEntrenador()!=null &&
                    oponentNPC.getEntrenador().getPokemonesCombate()!=null && oponentNPC.getEntrenador().getPokemonesCombate().length>0){
                Pokemon[] oponentPokemons = oponentNPC.getEntrenador().getPokemonesCombate();
                for (int i = 0; i < oponentPokemons.length; i++) {
                    Pokemon pokemon = oponentPokemons[i];
                    if(pokemon!=null){
                        ImageView pokeballIV = new ImageView(new Image(getClass().getResource("/pruebas/pokeball_transparente.png").toExternalForm()));
                        pokeballIV.setFitWidth(16);
                        pokeballIV.setFitHeight(16);
                        pokeballIV.setSmooth(false);
                        if(pokemon.getHpActual()<=0){
                            ColorAdjust grayscale = new ColorAdjust();
                            grayscale.setSaturation(-1);
                            pokeballIV.setEffect(grayscale);
                        }
                        pokeballsBox.getChildren().add(pokeballIV);
                    }
                }
                pokeballsBox.setAlignment(Pos.CENTER_LEFT);
            }
        }

        VBox infoBox = new VBox();
        infoBox.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 2px; -fx-padding: 5px;");
        infoBox.setPrefSize(210, 50);

        Label nameLabel = new Label(nombre + "  Lv." + nivel);
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        nameLabel.setTextFill(Color.BLACK);

        HBox hpContainer = new HBox();
        hpContainer.setAlignment(Pos.CENTER);
        Label hpLabel = new Label("HP: " + vidaActual + " / "+vidaMaxima);
        hpLabel.setFont(Font.font("Arial", 12));
        hpLabel.setTextFill(Color.BLACK);
        hpContainer.getChildren().add(hpLabel);

        infoBox.getChildren().addAll(nameLabel);
        addHealthBar(infoBox, vidaActual, vidaMaxima);
        infoBox.getChildren().addAll(hpContainer);
        superBox.getChildren().addAll(pokeballsBox, infoBox);

        return superBox;
    }

    private void addHealthBar(VBox infoBox, int currentHP, int maxHP) {
        HBox barraVida = new HBox();
        barraVida.setPrefWidth(200);
        barraVida.setMaxWidth(200);
        barraVida.setMinWidth(200);
        barraVida.setPrefHeight(12);
        barraVida.setMaxHeight(12);
        barraVida.setMinHeight(12);
        barraVida.setStyle("-fx-background-color: white; -fx-border-width: 2px; -fx-border-color: black;");
        Rectangle healthBar = new Rectangle(196, 8);
        healthBar.setFill(Color.GREEN);

        healthBar.setStrokeWidth(1);

        double healthPercentage = (double) currentHP / maxHP;
        healthBar.setWidth(196 * healthPercentage);

        barraVida.getChildren().add(healthBar);
        infoBox.getChildren().add(barraVida);
    }
}
