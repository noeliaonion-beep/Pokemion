package es.masanz.ut7.pokemonfx.controller;

import es.masanz.ut7.pokemonfx.app.GameApp;
import es.masanz.ut7.pokemonfx.model.base.Ataque;
import es.masanz.ut7.pokemonfx.model.base.Pokemon;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static es.masanz.ut7.pokemonfx.util.Configuration.*;

public class PokePCController {

    private Stage primaryStage;
    private Scene previousStage;
    private Pokemon[] equipoPokemon;
    private List<Pokemon> storagePokemons;

    public void load(Stage primaryStage, Scene previousStage) {
        this.primaryStage = primaryStage;
        this.previousStage = previousStage;
        this.equipoPokemon = GameApp.jugador.getPokemonesCombate();
        this.storagePokemons = GameApp.jugador.getPokemonesCapturados();
        createPokemonSelector();
    }

    public void volver() {
        primaryStage.setScene(previousStage);
    }

    private void createPokemonSelector() {
        VBox mainContainer = new VBox(10);
        mainContainer.setPadding(new Insets(10));
        mainContainer.setAlignment(Pos.CENTER);

        Button volverButton = new Button("Volver");
        volverButton.setOnAction(e -> volver());

        HBox mainLayout = new HBox(10);
        mainLayout.setAlignment(Pos.CENTER);

        VBox equipoBox = createPokemonList(Arrays.asList(equipoPokemon), true);
        VBox storageBox = createPokemonList(storagePokemons, false);

        ScrollPane storageScrollPane = new ScrollPane(storageBox);
        storageScrollPane.setFitToWidth(true);
        storageScrollPane.setPrefHeight(380);
        storageScrollPane.setMinHeight(380);
        storageScrollPane.setMaxHeight(380);

        mainLayout.getChildren().addAll(equipoBox, storageScrollPane);

        mainContainer.getChildren().addAll(volverButton, mainLayout);

        Scene selectorScene = new Scene(mainContainer, VIEW_WIDTH, VIEW_HEIGHT);
        primaryStage.setScene(selectorScene);
        primaryStage.setTitle("PC Pokemon");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private VBox createPokemonList(List<Pokemon> pokemons, boolean isEquipo) {
        VBox listBox = new VBox(5);
        listBox.setPadding(new Insets(5));
        listBox.setPrefWidth(320);
        listBox.setMinWidth(320);
        listBox.setMaxWidth(320);
        listBox.setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-padding: 5px;");

        Label title = new Label(isEquipo ? "Mi Equipo" : "Caja de Pokémon");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        listBox.getChildren().add(title);

        for (Pokemon pokemon : pokemons) {
            HBox row = createPokemonRow(pokemon, isEquipo);
            listBox.getChildren().add(row);
        }

        return listBox;
    }

    private HBox createPokemonRow(Pokemon pokemon, boolean isEquipo) {
        HBox row = new HBox(5);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPrefHeight(52);
        row.setMinHeight(52);
        row.setMaxHeight(52);
        row.setStyle("-fx-border-color: gray; -fx-border-width: 1px; -fx-padding: 5px;");

        if(pokemon!=null){
            URL resource = getClass().getResource(POKEMONS_BACK_PATH + pokemon.getClass().getSimpleName() + "_espalda_G1.png");
            ImageView pokemonImage = new ImageView(new Image(resource.toString()));
            pokemonImage.setFitWidth(40);
            pokemonImage.setFitHeight(40);

            Label nameLabel = new Label((pokemon.getApodo() != null ? pokemon.getApodo() : pokemon.getClass().getSimpleName())+" Lv."+pokemon.getNivel());
            nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));

            VBox nameAndHealth = new VBox(5, nameLabel);
            addHealthBar(nameAndHealth, pokemon.getHpActual(), pokemon.getMaxHP(), 130);

            HBox buttons = createButtons(pokemon, isEquipo);

            row.getChildren().addAll(pokemonImage, nameAndHealth, buttons);
        }

        return row;
    }

    private HBox createButtons(Pokemon pokemon, boolean isEquipo) {
        Button upButton = new Button("↑");
        Button downButton = new Button("↓");
        Button actionButton = new Button(isEquipo ? "-" : "+");
        Button detailsButton = new Button("\uD83D\uDC41");

        upButton.setOnAction(e -> movePokemon(pokemon, -1, isEquipo));
        downButton.setOnAction(e -> movePokemon(pokemon, 1, isEquipo));
        actionButton.setOnAction(e -> transferPokemon(pokemon, isEquipo));
        detailsButton.setOnAction(e -> viewPokemonDetails(pokemon));

        return new HBox(5, upButton, downButton, actionButton, detailsButton);
    }

    private void movePokemon(Pokemon pokemon, int direction, boolean isEquipo) {
        if (isEquipo) {
            int index = Arrays.asList(equipoPokemon).indexOf(pokemon);
            int newIndex = index + direction;
            if (newIndex >= 0 && newIndex < equipoPokemon.length) {
                Pokemon temp = equipoPokemon[index];
                equipoPokemon[index] = equipoPokemon[newIndex];
                equipoPokemon[newIndex] = temp;
                createPokemonSelector();
            }
        } else {
            int index = storagePokemons.indexOf(pokemon);
            int newIndex = index + direction;
            if (newIndex >= 0 && newIndex < storagePokemons.size()) {
                storagePokemons.remove(index);
                storagePokemons.add(newIndex, pokemon);
                createPokemonSelector();
            }
        }
    }

    private void transferPokemon(Pokemon pokemon, boolean isEquipo) {
        if (isEquipo) {
            storagePokemons.add(pokemon);
            for (int i = 0; i < equipoPokemon.length; i++) {
                if (equipoPokemon[i] == pokemon) {
                    equipoPokemon[i] = null;
                    break;
                }
            }
        } else {
            if (Arrays.stream(equipoPokemon).filter(p -> p != null).count() < 6) {
                storagePokemons.remove(pokemon);
                for (int i = 0; i < equipoPokemon.length; i++) {
                    if (equipoPokemon[i] == null) {
                        equipoPokemon[i] = pokemon;
                        break;
                    }
                }
            }
        }
        createPokemonSelector();
    }

    private void addHealthBar(VBox infoBox, int currentHP, int maxHP, int barWidth) {
        double healthPercentage = (double) currentHP / maxHP;
        Rectangle healthBar = new Rectangle((barWidth-2) * healthPercentage, 8);
        //healthBar.setFill(Color.GREEN);
        healthBar.setFill(healthPercentage > 0.5 ? Color.LIMEGREEN : healthPercentage > 0.2 ? Color.ORANGE : Color.RED);

        HBox healthContainer = new HBox(healthBar);
        healthContainer.setPrefSize(barWidth, 10);
        healthContainer.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1px;");
        healthContainer.setPrefWidth(barWidth);
        healthContainer.setMaxWidth(barWidth);
        healthContainer.setMinWidth(barWidth);
        healthContainer.setPrefHeight(10);
        healthContainer.setMaxHeight(10);
        healthContainer.setMinHeight(10);

        infoBox.getChildren().add(healthContainer);
    }

    private void viewPokemonDetails(Pokemon pokemon) {
        VBox mainContainer = new VBox(10);
        mainContainer.setPadding(new Insets(10));
        mainContainer.setAlignment(Pos.CENTER);

        Button volverButton = new Button("Volver");
        volverButton.setOnAction(e -> createPokemonSelector());

        URL resource = getClass().getResource(POKEMONS_FRONT_PATH + pokemon.getClass().getSimpleName() + "_RA.png");
        ImageView pokemonImage = new ImageView(new Image(resource.toString()));
        pokemonImage.setFitWidth(120);
        pokemonImage.setFitHeight(120);

        Label numPokedexLabel = new Label("Nº "+pokemon.getNumPokedex());
        VBox seccionImagen = new VBox(10, pokemonImage, numPokedexLabel);
        seccionImagen.setAlignment(Pos.BOTTOM_CENTER);

        String nombre = pokemon.getClass().getSimpleName();
        if(pokemon.getApodo()!=null){
            nombre = pokemon.getApodo() + " ("+nombre+")";
        }
        Label nameLabel = new Label(nombre + "  Lv." + pokemon.getNivel());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        VBox healthBarContainer = new VBox();
        addHealthBar(healthBarContainer, pokemon.getHpActual(), pokemon.getMaxHP(), 200);

        Label hpLabel = new Label("HP: "+pokemon.getHpActual() + "/" + pokemon.getMaxHP());

        Label expLabel = new Label("XP: "+pokemon.getPuntosExp() + "/" + pokemon.experienciaNecesariaParaSubirNivel());

        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(10);
        statsGrid.setVgap(5);
        statsGrid.add(new Label("Ataque:"), 0, 0);
        statsGrid.add(new Label(String.valueOf(pokemon.getAtaque())), 1, 0);
        statsGrid.add(new Label("Defensa:"), 2, 0);
        statsGrid.add(new Label(String.valueOf(pokemon.getDefensa())), 3, 0);
        statsGrid.add(new Label("Ataque Esp.:"), 0, 1);
        statsGrid.add(new Label(String.valueOf(pokemon.getAtaqueEspecial())), 1, 1);
        statsGrid.add(new Label("Defensa Esp.:"), 2, 1);
        statsGrid.add(new Label(String.valueOf(pokemon.getDefensaEspecial())), 3, 1);
        statsGrid.add(new Label("Velocidad:"), 0, 2);
        statsGrid.add(new Label(String.valueOf(pokemon.getVelocidad())), 1, 2);

        VBox statsBox = new VBox(5, nameLabel, healthBarContainer, hpLabel, expLabel, statsGrid);
        statsBox.setAlignment(Pos.CENTER_LEFT);
        HBox firstRow = new HBox(20, seccionImagen, new Separator(Orientation.VERTICAL), statsBox);
        firstRow.setAlignment(Pos.CENTER);

        Separator separator = new Separator(Orientation.HORIZONTAL);
        separator.setPrefWidth(300);

        VBox attacksBox = new VBox(5);
        attacksBox.setAlignment(Pos.CENTER);
        Label attacksLabel = new Label("Ataques:");
        attacksLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        GridPane attacksGrid = new GridPane();
        attacksGrid.setAlignment(Pos.CENTER);
        attacksGrid.setHgap(15);
        attacksGrid.setVgap(5);
        int row = 0, col = 0;

        if (pokemon.getAtaques() != null && !pokemon.getAtaques().isEmpty()) {

            for (Ataque ataque : pokemon.getAtaques().values()) {
                VBox ataqueBox = new VBox(3);
                ataqueBox.setStyle("-fx-border-color: black; -fx-padding: 5px;");
                ataqueBox.setAlignment(Pos.CENTER);

                Label nombreAtaque = new Label(ataque.getNombre());
                nombreAtaque.setFont(Font.font("Arial", FontWeight.BOLD, 12));

                GridPane atackGrid = new GridPane();
                atackGrid.setHgap(10);
                atackGrid.setVgap(5);
                atackGrid.add(new Label("Tipo:"), 0, 0);
                atackGrid.add(new Label(String.valueOf(ataque.getTipo())), 1, 0);
                atackGrid.add(new Label("Daño:"), 0, 1);
                atackGrid.add(new Label(String.valueOf(ataque.getDmgBase())), 1, 1);
                atackGrid.add(new Label("Precisión:"), 0, 2);
                atackGrid.add(new Label(String.valueOf(ataque.getPrecision())), 1, 2);
                atackGrid.add(new Label("Es especial:"), 0, 3);
                atackGrid.add(new Label(ataque.isEsEspecial()?"Si":"No"), 1, 3);
                atackGrid.add(new Label("PP:"), 0, 4);
                atackGrid.add(new Label(ataque.getCantidad()+"/"+ataque.getPp()), 1, 4);

                ataqueBox.getChildren().addAll(nombreAtaque, atackGrid);

                attacksGrid.add(ataqueBox, col, row);
                col++;
                if (col == 4) {
                    col = 0;
                    row++;
                }
            }
        } else {
            attacksGrid.add(new Label("Este Pokémon no tiene ataques."), 0, 0);
        }

        attacksBox.getChildren().addAll(attacksLabel, attacksGrid);

        mainContainer.getChildren().addAll(volverButton, firstRow, separator, attacksBox);

        Scene detailsScene = new Scene(mainContainer, VIEW_WIDTH, VIEW_HEIGHT);
        primaryStage.setScene(detailsScene);
        primaryStage.setTitle("Detalles de " + (pokemon.getApodo() != null ? pokemon.getApodo() : pokemon.getClass().getSimpleName()));
        primaryStage.setResizable(false);
        primaryStage.show();
    }



}