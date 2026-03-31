package es.masanz.ut7.pokemonfx.controller;

import es.masanz.ut7.pokemonfx.app.GameApp;
import es.masanz.ut7.pokemonfx.manager.PokemonManager;
import es.masanz.ut7.pokemonfx.model.base.*;
import es.masanz.ut7.pokemonfx.model.fx.NPC;

import es.masanz.ut7.pokemonfx.model.pokemons.Bulbasaur;
import es.masanz.ut7.pokemonfx.model.pokemons.Charmander;
import es.masanz.ut7.pokemonfx.model.pokemons.Mimikyu;
import es.masanz.ut7.pokemonfx.model.pokemons.Squirtle;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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

    private Label battleText;
    private HBox buttonLayout;

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

        npcInfo = crearInfoBox(npcPokemon.getNombre(), npcPokemon.getNivel(), npcPokemon.getHpActual(), npcPokemon.getMaxHP());
        npcInfo.setLayoutX(20);
        npcInfo.setLayoutY(20);

        playerInfo = crearInfoBox(selectedPokemon.getNombre(), selectedPokemon.getNivel(), selectedPokemon.getHpActual(), selectedPokemon.getMaxHP());
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

        battleText = new Label();
        battleText.setText("¿Qué quieres hacer?");
        battleText.setFont(Font.font("Arial", 14));
        battleText.setTextFill(Color.BLACK);
        battleText.setWrapText(true);

        buttonLayout = new HBox();
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
        Button itemButton = new Button("Bolsa");
        Button pokemonButton = new Button("Pokémon");
        Button runButton = new Button("Huir");

        if(oponentNPC!=null) runButton.setDisable(true);

        attackButton.setOnAction(e -> cargarBotonesDeCombate(battleText, buttonLayout));
        itemButton.setOnAction(e -> {
            BolsaController bc = new BolsaController();
            bc.load(primaryStage, root.getScene(), bola -> {
                primaryStage.setScene(root.getScene());
                intentarCapturarConBola(bola);
            });
        });
        pokemonButton.setOnAction(e -> createPokemonSelector());
        runButton.setOnAction(e -> primaryStage.setScene(previousStage));

        buttonLayout.getChildren().addAll(attackButton, itemButton, pokemonButton, runButton);
    }

    private void cargarBotonesDeCombate(Label battleText, HBox buttonLayout){
        buttonLayout.getChildren().clear();
        battleText.setText("");
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(10); statsGrid.setVgap(5);
        int auxContador = 0;
        HashMap<String, Ataque> ataques = selectedPokemon.getAtaques();
        for (Ataque ataque : ataques.values()) {
            Button btnAtaque = new Button(ataque.getNombre()+" ("+ataque.getCantidad()+"/"+ataque.getPp()+")");
            btnAtaque.setOnAction(e -> {
                seleccionarAtaqueOponente();
                selectedPokemon.setAtaqueSeleccionado(ataque);
                ejecutarRondaAtaque(battleText, buttonLayout);
            });
            if(ataque.getCantidad()<=0) btnAtaque.setDisable(true);
            statsGrid.add(btnAtaque, (auxContador%2), (auxContador/2));
            auxContador++;
            if(auxContador>3) break;
        }
        statsGrid.setTranslateY(-10);
        Button returnButton = new Button("Volver");
        returnButton.setOnAction(e -> cargarBotonesDeAcciones(battleText, buttonLayout));
        buttonLayout.getChildren().addAll(returnButton, statsGrid);
    }

    public void intentarCapturarConBola(Pokebolas bola) {
        bola.setCantidad(bola.getCantidad() - 1);
        battleText.setText("Has lanzado una " + bola.getTipo().name() + "...");
        buttonLayout.getChildren().clear();

        ImageView pokeballIV = new ImageView();
        URL resBola = getClass().getResource(bola.getImg());
        if(resBola != null) pokeballIV.setImage(new Image(resBola.toExternalForm()));
        
        pokeballIV.setFitWidth(24); pokeballIV.setFitHeight(24);
        pokeballIV.setPreserveRatio(true);
        pokeballIV.setLayoutX(50); pokeballIV.setLayoutY(300);

        root.getChildren().add(pokeballIV);

        double finalX = npcPokemonIV.getLayoutX() - pokeballIV.getLayoutX() + 25;
        double finalY = npcPokemonIV.getLayoutY() - pokeballIV.getLayoutY() + 25;

        TranslateTransition move = new TranslateTransition(Duration.seconds(0.6), pokeballIV);
        move.setToX(finalX); move.setToY(finalY);

        ScaleTransition shrink = new ScaleTransition(Duration.seconds(0.2), npcPokemonIV);
        shrink.setToX(0); shrink.setToY(0);
        FadeTransition fade = new FadeTransition(Duration.seconds(0.2), npcPokemonIV);
        fade.setToValue(0);

        move.setOnFinished(e -> {
            ParallelTransition captureEffect = new ParallelTransition(shrink, fade);
            captureEffect.setOnFinished(e2 -> {
                double suelo = npcPokemonIV.getLayoutY() - pokeballIV.getLayoutY() + 75;
                botarPokeballConRatio(suelo, finalX, pokeballIV, bola.getTipo().ratioCaptura);
            });
            captureEffect.play();
        });
        move.play();
    }

    private void botarPokeballConRatio(double suelo, double finalX, ImageView pokeballIV, double ratio){
        TranslateTransition caida1 = new TranslateTransition(Duration.seconds(0.25), pokeballIV);
        caida1.setToY(suelo); caida1.setInterpolator(Interpolator.EASE_IN);
        caida1.setOnFinished(e -> rotarPokeball(finalX, pokeballIV, 3, ratio));
        caida1.play();
    }

    private void rotarPokeball(double posX, ImageView pokeballIV, int contador, double ratio){
        RotateTransition rot = new RotateTransition(Duration.seconds(0.3), pokeballIV);
        rot.setByAngle(30); rot.setAutoReverse(true); rot.setCycleCount(2);
        rot.setOnFinished(e -> {
            boolean pokemonAtrapado = true;
            int lifePercentage = 100 - (npcPokemon.getHpActual() * 100) / npcPokemon.getMaxHP();
            double prob = (lifePercentage / 100.0) + ratio;
            if(Math.random() > prob && ratio < 1.0) pokemonAtrapado = false;
            
            if(!pokemonAtrapado) liberarPokeball(pokeballIV);
            else {
                if(contador > 1) rotarPokeball(posX, pokeballIV, contador - 1, ratio);
                else atraparPokemon();
            }
        });
        rot.play();
    }

    private void liberarPokeball(ImageView pokeballIV){
        battleText.setText("Oh... No has logrado atraparlo [PAQUETE]");
        ScaleTransition grow = new ScaleTransition(Duration.seconds(0.2), npcPokemonIV);
        grow.setToX(1); grow.setToY(1);
        FadeTransition show = new FadeTransition(Duration.seconds(0.2), npcPokemonIV);
        show.setToValue(1);
        FadeTransition fadeBall = new FadeTransition(Duration.seconds(0.2), pokeballIV);
        fadeBall.setToValue(0);
        ParallelTransition anim = new ParallelTransition(grow, show, fadeBall);
        anim.setOnFinished(e -> {
            root.getChildren().remove(pokeballIV);
            new Timeline(new KeyFrame(Duration.seconds(1), e2 -> {
                seleccionarAtaqueOponente();
                selectedPokemon.setAtaqueSeleccionado(null);
                ejecutarRondaAtaque(battleText, buttonLayout);
            })).play();
        });
        anim.play();
    }

    private void atraparPokemon(){
        battleText.setText("¡Enhorabuena! Has logrado atrapar a ["+npcPokemon.getClass().getSimpleName()+"]");
        GameApp.jugador.getPokemonesCapturados().add(npcPokemon);
        new Timeline(new KeyFrame(Duration.millis(1500), e2 -> mostrarPantallaPokemonCapturado())).play();
    }

    private void seleccionarAtaqueOponente(){
        HashMap<String, Ataque> ataquesMap = npcPokemon.getAtaques();
        List<Ataque> ataques = new ArrayList<>(ataquesMap.values());
        npcPokemon.setAtaqueSeleccionado(ataques.get((int) (Math.random() * ataques.size())));
    }

    private void ejecutarRondaAtaque(Label battleText, HBox buttonLayout) {
        Ataque atJug = selectedPokemon.getAtaqueSeleccionado();
        Ataque atOp = npcPokemon.getAtaqueSeleccionado();
        if(atOp == null) return;

        final Pokemon[] p1 = new Pokemon[1];
        final Pokemon[] p2 = new Pokemon[1];
        final VBox[] b1 = new VBox[1];
        final VBox[] b2 = new VBox[1];
        final ImageView[] v1 = new ImageView[1];
        final ImageView[] v2 = new ImageView[1];

        if(atJug == null || (atOp.getPrioridad() > atJug.getPrioridad())){
            p1[0] = npcPokemon; p2[0] = selectedPokemon;
            b1[0] = npcInfo; b2[0] = playerInfo;
            v1[0] = npcPokemonIV; v2[0] = playerPokemonIV;
        } else {
            p1[0] = selectedPokemon; p2[0] = npcPokemon;
            b1[0] = playerInfo; b2[0] = npcInfo;
            v1[0] = playerPokemonIV; v2[0] = npcPokemonIV;
        }

        buttonLayout.getChildren().clear();
        String msg = p1[0].atacar(p2[0]);
        battleText.setText(msg);

        // ACTUALIZACIÓN DE VIDA POR ESTADO (VENENO)
        actualizarBarrasVida();

        TranslateTransition shake = new TranslateTransition(Duration.millis(150), v2[0]);
        shake.setByX(10); shake.setAutoReverse(true); shake.setCycleCount(4);
        
        shake.setOnFinished(e -> {
            actualizarBarrasVida();
            if(p2[0].getHpActual() > 0){
                new Timeline(new KeyFrame(Duration.seconds(1), e2 -> {
                    String msg2 = p2[0].atacar(p1[0]);
                    battleText.setText(msg2);
                    actualizarBarrasVida();
                    new Timeline(new KeyFrame(Duration.seconds(1), e3 -> finalizarTurno())).play();
                })).play();
            } else {
                finalizarTurno();
            }
        });
        shake.play();
    }

    private void actualizarBarrasVida() {
        actualizarInfoBox(playerInfo, selectedPokemon);
        actualizarInfoBox(npcInfo, npcPokemon);
    }

    private void actualizarInfoBox(VBox box, Pokemon p) {
        Rectangle barra = buscarBarraVida(box);
        Label hpL = buscarHPLabel(box);
        if(barra != null) {
            double targetWidth = 196 * ((double)p.getHpActual()/p.getMaxHP());
            Timeline t = new Timeline(new KeyFrame(Duration.millis(500), new KeyValue(barra.widthProperty(), targetWidth)));
            t.play();
        }
        if(hpL != null) hpL.setText("HP: " + p.getHpActual() + " / " + p.getMaxHP());
    }

    private void finalizarTurno() {
        if(selectedPokemon.getHpActual() <= 0 || npcPokemon.getHpActual() <= 0) analizarSituacion();
        else cargarBotonesDeAcciones(battleText, buttonLayout);
    }

    private void analizarSituacion() {
        if(npcPokemon.getHpActual() <= 0) mostrarPantallaVictoria();
        else if(selectedPokemon.getHpActual() <= 0){
            boolean algunoVivo = false;
            for(Pokemon p : availablePokemons) if(p != null && p.getHpActual() > 0) { algunoVivo = true; break; }
            if(algunoVivo) createPokemonSelector();
            else mostrarPantallaDerrota();
        }
    }

    private void mostrarPantallaVictoria() {
        StackPane pane = new StackPane(); pane.setStyle("-fx-background-color: rgba(0,0,0,0.8);");
        Label l = new Label("¡Victoria! " + npcPokemon.getNombre() + " debilitado.");
        l.setTextFill(Color.WHITE); l.setFont(new Font(20));
        Button b = new Button("Continuar"); b.setOnAction(e -> analizarEvolucionPokemon());
        VBox v = new VBox(20, l, b); v.setAlignment(Pos.CENTER);
        pane.getChildren().add(v); primaryStage.setScene(new Scene(pane, 480, 320));
    }

    private void mostrarPantallaPokemonCapturado() {
        StackPane pane = new StackPane(); pane.setStyle("-fx-background-color: black;");
        Label l = new Label("¡Has capturado a " + npcPokemon.getNombre() + "!");
        l.setTextFill(Color.WHITE);
        TextField tf = new TextField(); tf.setPromptText("Apodo..."); tf.setMaxWidth(200);
        Button b = new Button("Listo");
        b.setOnAction(e -> {
            if(!tf.getText().isEmpty()) npcPokemon.setApodo(tf.getText());
            primaryStage.setScene(previousStage);
        });
        VBox v = new VBox(15, l, tf, b); v.setAlignment(Pos.CENTER);
        pane.getChildren().add(v); primaryStage.setScene(new Scene(pane, 480, 320));
    }

    private void mostrarPantallaDerrota() { primaryStage.setScene(previousStage); }

    private void analizarEvolucionPokemon() {
        if(selectedPokemon.getNivel() >= selectedPokemon.nivelEvolucion() && selectedPokemon.nivelEvolucion() != -1){
            Pokemon evo = selectedPokemon.pokemonAEvolucionar();
            battleText.setText("¡Vaya! " + selectedPokemon.getNombre() + " está evolucionando...");
            new Timeline(new KeyFrame(Duration.seconds(2), e -> {
                for(int i=0; i<6; i++) if(availablePokemons[i] == selectedPokemon) availablePokemons[i] = evo;
                primaryStage.setScene(previousStage);
            })).play();
        } else {
            primaryStage.setScene(previousStage);
        }
    }

    public Rectangle buscarBarraVida(Parent padre) {
        for (Node node : padre.getChildrenUnmodifiable()) {
            if (node instanceof Rectangle) return (Rectangle) node;
            else if (node instanceof Parent) {
                Rectangle found = buscarBarraVida((Parent) node);
                if (found != null) return found;
            }
        }
        return null;
    }

    public Label buscarHPLabel(Parent padre) {
        for (Node node : padre.getChildrenUnmodifiable()) {
            if (node instanceof Label label) {
                if (label.getText().startsWith("HP:")) return label;
            } else if (node instanceof Parent) {
                Label found = buscarHPLabel((Parent) node);
                if (found != null) return found;
            }
        }
        return null;
    }

    private void loadAvailablePokemons() {
        availablePokemons = GameApp.jugador.getPokemonesCombate();
        for (Pokemon p : availablePokemons) if(p != null && p.getHpActual() > 0) { selectedPokemon = p; break; }
        if(npcPokemon == null) npcPokemon = generarPokemonRandom();
    }

    private Pokemon generarPokemonRandom(){
        switch ((int) (Math.random() * 4)){
            case 0:
                return new Charmander((int) (Math.random()*100));
            case 1:
                return new Squirtle((int) (Math.random()*100));
            case 2:
                return new Bulbasaur((int) (Math.random()*100));
            case 3:
                return new Mimikyu((int) (Math.random()*100));
        }
        return null;
    }

    private void createPokemonSelector() {
        VBox layout = new VBox(5); layout.setAlignment(Pos.CENTER); layout.setPadding(new Insets(10));
        for (Pokemon p : availablePokemons) {
            if(p == null) continue;
            Button b = new Button(p.getNombre() + " Lv." + p.getNivel() + " (HP: " + p.getHpActual() + ")");
            b.setMaxWidth(Double.MAX_VALUE);
            b.setOnAction(e -> { selectedPokemon = p; loadBattleScene(true); });
            layout.getChildren().add(b);
        }
        Button back = new Button("Volver");
        back.setOnAction(e -> loadBattleScene(false));
        layout.getChildren().add(back);
        primaryStage.setScene(new Scene(layout, 480, 320));
    }

    private VBox crearInfoBox(String nombre, int nivel, int vidaA, int vidaM) {
        VBox info = new VBox(5);
        info.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-padding: 5px;");
        info.setPrefWidth(210);
        Label n = new Label(nombre + " Lv." + nivel);
        n.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        Rectangle barra = new Rectangle(196, 10, Color.GREEN);
        barra.setWidth(196 * ((double)vidaA/vidaM));
        Label hp = new Label("HP: " + vidaA + " / " + vidaM);
        info.getChildren().addAll(n, barra, hp);
        return info;
    }
}
