package es.masanz.ut7.pokemonfx.util;

public class Configuration {

    // CONSTANTES DEL NPC
    public static final int ABAJO = 0;
    public static final int ARRIBA = 1;
    public static final int IZQUIERDA = 2;
    public static final int DERECHA = 3;

    // SI QUEREIS QUE LA APP FUNCIONE BIEN, MEJOR NO TOCAR ESTO
    public static final int SCALE_FACTOR = 2;

    // CONSTANTES DEL MAPCONTROLLER
    public static final int TILE_SIZE = 16 * SCALE_FACTOR;
    public static final int VIEW_WIDTH = 336 * SCALE_FACTOR;
    public static final int VIEW_HEIGHT = 256 * SCALE_FACTOR;
    public static final int MOVE_SPEED = 1 * SCALE_FACTOR;
    public static final int VELOCIDAD_CAMBIO_SPRITES = 4;
    public static final double PROBABILIDAD_POKEMON_SALVAJE = 0.1;

    // CONSTANTES DEL COMBATECONTROLLER
    public static final String IMAGE_PATH = "/pokemons/";
    public static final String POKEMONS_FRONT_PATH = "/pokemonesRAFrente/";
    public static final String POKEMONS_BACK_PATH = "/pokemonesRAEspalda/";
    public static final int VELOCIDAD_ANIMACIONES = 1500;

}
