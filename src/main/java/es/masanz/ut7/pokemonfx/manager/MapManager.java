package es.masanz.ut7.pokemonfx.manager;

import es.masanz.ut7.pokemonfx.model.base.Evento;
import es.masanz.ut7.pokemonfx.model.base.Mapa;
import es.masanz.ut7.pokemonfx.model.pokemons.Bulbasaur;
import es.masanz.ut7.pokemonfx.model.base.Entrenador;
import es.masanz.ut7.pokemonfx.model.enums.CollisionType;
import es.masanz.ut7.pokemonfx.model.enums.TileType;
import es.masanz.ut7.pokemonfx.model.fx.NPC;
import es.masanz.ut7.pokemonfx.model.map.Ruta1;
import es.masanz.ut7.pokemonfx.model.map.Ruta2;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static es.masanz.ut7.pokemonfx.util.Configuration.TILE_SIZE;

public class MapManager {

    public static int mapHeight = 20;
    public static int mapWidth = 20;
    public static HashMap<String, Mapa> mapas;
    public static String rutaSeleccionada;
    public static int inicioX = mapWidth / 2 * TILE_SIZE;
    public static int inicioY = mapHeight / 2 * TILE_SIZE;

    // TODO 04: Incluir todas las rutas que se hayan generado previamente
    public static void initMapas(){
        Mapa ruta1 = new Ruta1();
        Mapa ruta2 = new Ruta2();
        mapas = new HashMap<>();
        mapas.put(ruta1.getNombre(), ruta1);
        mapas.put(ruta2.getNombre(), ruta2);
    }

    // Settea las variables necesarias en funcion de la ruta seleccionada
    public static void cargarRuta(String ruta){
        rutaSeleccionada = ruta;
        Mapa mapa = mapas.get(rutaSeleccionada);
        if(mapa!=null){
            mapHeight = mapa.getAltura();
            mapWidth = mapa.getAnchura();
            inicioX = mapa.getInicioX() * TILE_SIZE;
            inicioY = mapa.getInicioY() * TILE_SIZE;
        }
    }

    // Este metodo asigna el mapa que se cargara en el juego
    // Previamente se habra invocado a cargarRuta con la ruta indicada para settear las variables
    public static void generarMapa(int[][] mapData, int[][] collisionMap, String[][] teleportMap, Evento[][] eventsMap, List<NPC> npcs) {
        if(mapas==null){
            initMapas();
        }
        Mapa mapa = mapas.get(rutaSeleccionada);
        if(mapa==null) {
            generarMapaAleatorio(mapData, collisionMap, teleportMap, eventsMap, npcs);
        } else {
            copiarMatriz(mapa.getMapData(), mapData);
            copiarMatriz(mapa.getCollisionMap(), collisionMap);
            copiarMatrizString(mapa.getTeleportMap(), teleportMap);
            copiarMatrizEventos(mapa.getEventsMap(), eventsMap);
            npcs.clear();
            npcs.addAll(mapa.getNpcs());
        }
    }

    private static void copiarMatriz(int[][] origen, int[][] destino) {
        for (int i = 0; i < origen.length; i++) {
            System.arraycopy(origen[i], 0, destino[i], 0, origen[i].length);
        }
    }

    private static void copiarMatrizString(String[][] origen, String[][] destino) {
        for (int i = 0; i < origen.length; i++) {
            System.arraycopy(origen[i], 0, destino[i], 0, origen[i].length);
        }
    }

    private static void copiarMatrizEventos(Evento[][] origen, Evento[][] destino) {
        for (int i = 0; i < origen.length; i++) {
            System.arraycopy(origen[i], 0, destino[i], 0, origen[i].length);
        }
    }

    public static void generarMapaAleatorio(int[][] mapData, int[][] collisionMap, String[][] teleportMap, Evento[][] eventsMap, List<NPC> npcs) {
        Random random = new Random();
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                double rand = Math.random();
                if (rand > 0.95) {
                    mapData[y][x] = TileType.TELEPORT_RED.ordinal();
                    collisionMap[y][x] = CollisionType.SUELO.ordinal();
                    teleportMap[y][x] = "Ruta 1";
                } else if (rand > 0.8) {
                    mapData[y][x] = TileType.CESPED_ARBUSTO.ordinal();
                    collisionMap[y][x] = CollisionType.PARED.ordinal();
                } else if (rand > 0.7) {
                    mapData[y][x] = TileType.CESPED_HIERBA.ordinal();
                    collisionMap[y][x] = CollisionType.SUELO.ordinal();
                } else {
                    mapData[y][x] = TileType.CESPED.ordinal();
                    collisionMap[y][x] = CollisionType.SUELO.ordinal();
                }
                if (collisionMap[y][x] == CollisionType.SUELO.ordinal() && random.nextDouble() < 0.01) {
                    Entrenador entrenador = new Entrenador();
                    entrenador.incluirPokemonParaCombatir(0, new Bulbasaur(4));
                    /*
                    entrenador.incluirPokemonParaCombatir(1, new Bulbasaur(5));
                    entrenador.incluirPokemonParaCombatir(2, new Bulbasaur(6));
                    entrenador.incluirPokemonParaCombatir(3, new Bulbasaur(7));
                    entrenador.incluirPokemonParaCombatir(4, new Bulbasaur(8));
                    entrenador.incluirPokemonParaCombatir(5, new Bulbasaur(9));
                    */
                    npcs.add(new NPC(x, y, entrenador));
                }
            }
        }
    }
}
