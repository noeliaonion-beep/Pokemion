package es.masanz.ut7.pokemonfx.controller;

import es.masanz.ut7.pokemonfx.app.GameApp;
import es.masanz.ut7.pokemonfx.model.base.Ataque;
import es.masanz.ut7.pokemonfx.model.base.Pokemon;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static es.masanz.ut7.pokemonfx.util.Configuration.POKEMONS_FRONT_PATH;

public class PokePCController {

    private Stage primaryStage;
    private Scene previousScene;

    public void load(Stage primaryStage, Scene previousScene) {
        this.primaryStage = primaryStage;
        this.previousScene = previousScene;
        showPokemonPC();
    }

    private void showPokemonPC() {
        VBox root = new VBox(15);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f0f0f0;");

        Label title = new Label("SISTEMA DE ALMACENAMIENTO POKÉMON");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        root.getChildren().add(title);

        // --- SECCIÓN EQUIPO ---
        VBox equipoSection = new VBox(5);
        equipoSection.getChildren().add(new Label("TU EQUIPO (MÁX. 6)"));
        HBox equipoSlots = new HBox(10);
        equipoSlots.setAlignment(Pos.CENTER);
        
        Pokemon[] equipo = GameApp.jugador.getPokemonesCombate();
        for (int i = 0; i < 6; i++) {
            equipoSlots.getChildren().add(createEquipSlot(equipo[i], i));
        }
        equipoSection.getChildren().add(equipoSlots);
        root.getChildren().add(equipoSection);

        // --- SECCIÓN ALMACÉN ---
        VBox almacenSection = new VBox(5);
        almacenSection.getChildren().add(new Label("POKÉMON CAPTURADOS (PC)"));
        
        FlowPane almacenGrid = new FlowPane();
        almacenGrid.setHgap(10);
        almacenGrid.setVgap(10);
        almacenGrid.setPadding(new Insets(10));
        almacenGrid.setAlignment(Pos.TOP_LEFT);

        List<Pokemon> capturados = GameApp.jugador.getPokemonesCapturados();
        for (Pokemon p : capturados) {
            almacenGrid.getChildren().add(createAlmacenSlot(p));
        }

        ScrollPane scroll = new ScrollPane(almacenGrid);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(250);
        almacenSection.getChildren().add(scroll);
        root.getChildren().add(almacenSection);

        Button backButton = new Button("SALIR DEL PC");
        backButton.setPrefWidth(200);
        backButton.setOnAction(e -> primaryStage.setScene(previousScene));
        root.getChildren().add(backButton);

        primaryStage.setScene(new Scene(root, 850, 650));
    }

    private VBox createEquipSlot(Pokemon p, int index) {
        VBox slot = new VBox(5);
        slot.setAlignment(Pos.CENTER);
        slot.setPrefSize(110, 130);
        slot.setStyle("-fx-border-color: #333; -fx-background-color: #fff; -fx-border-radius: 5;");

        if (p != null) {
            slot.getChildren().add(getSmallImageView(p));
            slot.getChildren().add(new Label(p.getNombre()));
            
            Button detailsBtn = new Button("Ver");
            detailsBtn.setOnAction(e -> showPokemonDetails(p));
            
            Button storeBtn = new Button("Guardar");
            storeBtn.setOnAction(e -> {
                GameApp.jugador.getPokemonesCapturados().add(p);
                GameApp.jugador.getPokemonesCombate()[index] = null;
                showPokemonPC();
            });
            
            HBox btns = new HBox(2, detailsBtn, storeBtn);
            btns.setAlignment(Pos.CENTER);
            slot.getChildren().add(btns);
        } else {
            Label empty = new Label("VACÍO");
            empty.setTextFill(Color.GRAY);
            slot.getChildren().add(empty);
        }
        return slot;
    }

    private VBox createAlmacenSlot(Pokemon p) {
        VBox slot = new VBox(5);
        slot.setAlignment(Pos.CENTER);
        slot.setPrefSize(100, 120);
        slot.setStyle("-fx-border-color: #999; -fx-background-color: #eee; -fx-border-radius: 5;");

        slot.getChildren().add(getSmallImageView(p));
        slot.getChildren().add(new Label(p.getNombre()));

        Button takeBtn = new Button("Sacar");
        takeBtn.setOnAction(e -> {
            Pokemon[] equipo = GameApp.jugador.getPokemonesCombate();
            for (int i = 0; i < 6; i++) {
                if (equipo[i] == null) {
                    equipo[i] = p;
                    GameApp.jugador.getPokemonesCapturados().remove(p);
                    showPokemonPC();
                    return;
                }
            }
            System.out.println("Equipo lleno. Libera o guarda uno primero.");
        });

        slot.getChildren().add(takeBtn);
        return slot;
    }

    private ImageView getSmallImageView(Pokemon p) {
        URL resource = getClass().getResource(POKEMONS_FRONT_PATH + p.getClass().getSimpleName() + "_RA.png");
        ImageView iv = new ImageView();
        if (resource != null) {
            iv.setImage(new Image(resource.toExternalForm()));
            iv.setFitWidth(50);
            iv.setFitHeight(50);
            iv.setPreserveRatio(true);
        }
        return iv;
    }

    private void showPokemonDetails(Pokemon pokemon) {
        VBox detailsLayout = new VBox(10);
        detailsLayout.setAlignment(Pos.CENTER);
        detailsLayout.setPadding(new Insets(20));
        detailsLayout.setStyle("-fx-background-color: #e0e0e0;");

        Label nameLabel = new Label(pokemon.getNombre() + " (Lv." + pokemon.getNivel() + ")");
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        detailsLayout.getChildren().add(nameLabel);

        URL resource = getClass().getResource(POKEMONS_FRONT_PATH + pokemon.getClass().getSimpleName() + "_RA.png");
        ImageView pokemonImage = new ImageView();
        if (resource != null) {
            pokemonImage.setImage(new Image(resource.toExternalForm()));
            pokemonImage.setFitWidth(100);
            pokemonImage.setFitHeight(100);
            pokemonImage.setPreserveRatio(true);
        }
        detailsLayout.getChildren().add(pokemonImage);

        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(10);
        statsGrid.setVgap(5);
        statsGrid.setAlignment(Pos.CENTER);

        statsGrid.add(new Label("HP:"), 0, 0);
        statsGrid.add(new Label(pokemon.getHpActual() + "/" + pokemon.getMaxHP()), 1, 0);
        statsGrid.add(new Label("Ataque:"), 0, 1);
        statsGrid.add(new Label(String.valueOf(pokemon.getAtaque())), 1, 1);
        statsGrid.add(new Label("Defensa:"), 0, 2);
        statsGrid.add(new Label(String.valueOf(pokemon.getDefensa())), 1, 2);
        statsGrid.add(new Label("Velocidad:"), 0, 3);
        statsGrid.add(new Label(String.valueOf(pokemon.getVelocidad())), 1, 3);
        statsGrid.add(new Label("Nº Pokedex:"), 0, 4);
        statsGrid.add(new Label(pokemon.getNumPokedex()), 1, 4);
        statsGrid.add(new Label("Experiencia:"), 0, 5);
        statsGrid.add(new Label(pokemon.getPuntosExp() + "/" + pokemon.experienciaNecesariaParaSubirNivel()), 1, 5);

        detailsLayout.getChildren().add(statsGrid);

        Label attacksTitle = new Label("Ataques:");
        attacksTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        detailsLayout.getChildren().add(attacksTitle);

        VBox attacksBox = new VBox(5);
        attacksBox.setAlignment(Pos.CENTER);
        for (Map.Entry<String, Ataque> entry : pokemon.getAtaques().entrySet()) {
            Ataque ataque = entry.getValue();
            HBox attackRow = new HBox(10);
            attackRow.setAlignment(Pos.CENTER_LEFT);
            attackRow.setPadding(new Insets(5));
            attackRow.setStyle("-fx-border-color: lightgray; -fx-border-width: 1px; -fx-background-color: #f9f9f9;");

            Label attackName = new Label(ataque.getNombre());
            attackName.setPrefWidth(100);
            Label attackType = new Label("Tipo: " + ataque.getTipo().name());
            Label attackDmg = new Label("Daño: " + ataque.getDmgBase());
            Label attackAcc = new Label("Precisión: " + ataque.getPrecision());
            Label attackSpecial = new Label("Especial: " + (ataque.isEsEspecial() ? "Sí" : "No"));
            Label attackPP = new Label("PP: " + ataque.getCantidad() + "/" + ataque.getPp());

            attackRow.getChildren().addAll(attackName, attackType, attackDmg, attackAcc, attackSpecial, attackPP);
            attacksBox.getChildren().add(attackRow);
        }
        detailsLayout.getChildren().add(attacksBox);

        Button backButton = new Button("Volver");
        backButton.setOnAction(e -> showPokemonPC());
        detailsLayout.getChildren().add(backButton);

        primaryStage.setScene(new Scene(detailsLayout, 850, 650));
    }

    private void releasePokemon(Pokemon pokemon, int index) {
        if (Arrays.stream(GameApp.jugador.getPokemonesCombate()).filter(p -> p != null).count() > 1) {
            GameApp.jugador.getPokemonesCombate()[index] = null;
            showPokemonPC();
        } else {
            System.out.println("No puedes liberar a tu último Pokémon!");
        }
    }
}
