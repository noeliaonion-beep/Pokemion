package es.masanz.ut7.pokemonfx.app;

import es.masanz.ut7.pokemonfx.controller.MapController;
import es.masanz.ut7.pokemonfx.model.base.Entrenador;
import es.masanz.ut7.pokemonfx.model.pokemons.Mimikyu;
import javafx.application.Application;
import javafx.stage.Stage;

public class GameApp extends Application {

    // SPRITES OBTENIDOS DE (ENTRE OTROS) https://www.spriters-resource.com/game_boy_gbc/pokemongoldsilver/

    public static Entrenador jugador;

    @Override
    public void start(Stage primaryStage) {
        MapController.load(primaryStage);
    }

    public static void main(String[] args) {
        // TODO 00: AQUI DEFINIR VUESTRO ENTRENADOR INICIAL
        jugador = new Entrenador();
        jugador.incluirPokemonParaCombatir(0,new Mimikyu(100));
        launch(args);
    }
}
