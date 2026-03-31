package es.masanz.ut7.pokemonfx.app;

import es.masanz.ut7.pokemonfx.controller.MapController;
import es.masanz.ut7.pokemonfx.model.base.Entrenador;
import es.masanz.ut7.pokemonfx.model.base.Consumible;
import es.masanz.ut7.pokemonfx.model.base.Pokebolas;
import es.masanz.ut7.pokemonfx.model.enums.TipoConsumible;
import es.masanz.ut7.pokemonfx.model.enums.Pokebols;
import es.masanz.ut7.pokemonfx.model.pokemons.Mimikyu;
import javafx.application.Application;
import javafx.stage.Stage;

public class GameApp extends Application {

    public static Entrenador jugador;

    @Override
    public void start(Stage primaryStage) {
        MapController.load(primaryStage);
    }

    public static void main(String[] args) {
        jugador = new Entrenador();
        
        // Creamos el equipo inicial usando las clases individuales
        jugador.incluirPokemonParaCombatir(0, new Mimikyu(100));
        
        // Items iniciales
        jugador.anadirItem(new Consumible(5, false, TipoConsumible.POCION));
        jugador.anadirItem(new Pokebolas(10, false, Pokebols.POKEBOL));
        
        launch(args);
    }
}
