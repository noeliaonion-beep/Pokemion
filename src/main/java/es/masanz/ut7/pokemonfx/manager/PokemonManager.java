package es.masanz.ut7.pokemonfx.manager;

import es.masanz.ut7.pokemonfx.model.base.Pokemon;
import es.masanz.ut7.pokemonfx.model.pokemons.*;
import java.util.Random;

public class PokemonManager {

    private static final Random random = new Random();

    public static Pokemon generarPokemonSalvaje(String ruta) {
        int r = random.nextInt(4);
        int nivel = random.nextInt(6) + 2;
        return switch (r) {
            case 0 -> new Bulbasaur(nivel);
            case 1 -> new Charmander(nivel);
            case 2 -> new Squirtle(nivel);
            default -> new Mimikyu(nivel);
        };
    }
}
