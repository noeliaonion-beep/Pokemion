package es.masanz.ut7.pokemonfx.controller;

import es.masanz.ut7.pokemonfx.app.GameApp;
import es.masanz.ut7.pokemonfx.model.base.Consumible;
import es.masanz.ut7.pokemonfx.model.base.Item;
import es.masanz.ut7.pokemonfx.model.base.Pokebolas;
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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import static es.masanz.ut7.pokemonfx.util.Configuration.*;

public class BolsaController {

    private Stage primaryStage;
    private Scene previousScene;
    private Consumer<Pokebolas> onPokeballUsed;

    public void load(Stage stage, Scene scene, Consumer<Pokebolas> onPokeballUsed) {
        this.primaryStage = stage;
        this.previousScene = scene;
        this.onPokeballUsed = onPokeballUsed;
        mostrarBolsa();
    }

    private void mostrarBolsa() {
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setStyle("-fx-padding: 15px; -fx-background-color: #f4f4f4;");

        Label title = new Label("MOCHILA DEL JUGADOR");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
        layout.getChildren().add(title);

        VBox itemsContainer = new VBox(8);
        itemsContainer.setPadding(new Insets(10));
        ScrollPane scroll = new ScrollPane(itemsContainer);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(250);

        List<Item> bolsa = GameApp.jugador.getBolsa();
        Iterator<Item> iterator = bolsa.iterator();
        while (iterator.hasNext()) {
            Item item = iterator.next();
            if (item.getCantidad() <= 0) {
                iterator.remove();
                continue;
            }

            HBox row = new HBox(15);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle("-fx-background-color: white; -fx-border-color: #ccc; -fx-border-radius: 5; -fx-padding: 8;");

            ImageView iv = new ImageView();
            try {
                URL res = getClass().getResource(item.getImg());
                if (res != null) iv.setImage(new Image(res.toExternalForm()));
            } catch (Exception e) {}
            iv.setFitWidth(32); iv.setFitHeight(32); iv.setPreserveRatio(true);

            VBox info = new VBox(2);
            String name = "";
            if (item instanceof Consumible) name = ((Consumible) item).getTipoConsumible().name();
            else if (item instanceof Pokebolas) name = ((Pokebolas) item).getTipo().name();
            
            Label nameL = new Label(name);
            nameL.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            Label qtyL = new Label("Cantidad: " + item.getCantidad());
            info.getChildren().addAll(nameL, qtyL);

            Button useBtn = new Button("USAR");
            useBtn.setMinWidth(70);
            
            if (item instanceof Pokebolas) {
                if (onPokeballUsed == null) {
                    useBtn.setDisable(true);
                    useBtn.setText("EN COMBATE");
                } else {
                    useBtn.setOnAction(e -> onPokeballUsed.accept((Pokebolas) item));
                }
            } else {
                useBtn.setOnAction(e -> seleccionarPokemonParaItem(item));
            }

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            row.getChildren().addAll(iv, info, spacer, useBtn);
            itemsContainer.getChildren().add(row);
        }

        Button closeBtn = new Button("CERRAR");
        closeBtn.setPrefWidth(100);
        closeBtn.setOnAction(e -> primaryStage.setScene(previousScene));
        layout.getChildren().addAll(scroll, closeBtn);

        primaryStage.setScene(new Scene(layout, VIEW_WIDTH, VIEW_HEIGHT));
    }

    private void seleccionarPokemonParaItem(Item item) {
        VBox selector = new VBox(10);
        selector.setAlignment(Pos.CENTER);
        selector.setStyle("-fx-padding: 20; -fx-background-color: #eee;");

        String nombreItem = item instanceof Consumible ? ((Consumible) item).getTipoConsumible().name() : "objeto";
        Label label = new Label("¿En qué Pokémon quieres usar " + nombreItem + "?");
        label.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        selector.getChildren().add(label);

        Pokemon[] equipo = GameApp.jugador.getPokemonesCombate();
        for (Pokemon p : equipo) {
            if (p == null) continue;

            Button pBtn = new Button(p.getNombre() + " (HP: " + p.getHpActual() + "/" + p.getMaxHP() + ")");
            pBtn.setMaxWidth(Double.MAX_VALUE);
            
            if (item instanceof Consumible) {
                Consumible c = (Consumible) item;
                if (!c.getTipoConsumible().revivir && !c.getTipoConsumible().subirNivel) {
                    if (p.getHpActual() >= p.getMaxHP()) {
                        pBtn.setDisable(true);
                        pBtn.setText(p.getNombre() + " (AL MÁXIMO)");
                    }
                }
            }

            pBtn.setOnAction(e -> {
                if (item.consumirItem(p)) mostrarBolsa();
            });
            selector.getChildren().add(pBtn);
        }

        Button cancel = new Button("CANCELAR");
        cancel.setOnAction(e -> mostrarBolsa());
        selector.getChildren().add(cancel);

        primaryStage.setScene(new Scene(selector, VIEW_WIDTH, VIEW_HEIGHT));
    }
}
