package es.masanz.ut7.pokemonfx.model.map;

import es.masanz.ut7.pokemonfx.model.base.Evento;
import es.masanz.ut7.pokemonfx.model.base.Mapa;
import es.masanz.ut7.pokemonfx.model.pokemons.Bulbasaur;
import es.masanz.ut7.pokemonfx.model.base.Entrenador;
import es.masanz.ut7.pokemonfx.model.enums.CollisionType;
import es.masanz.ut7.pokemonfx.model.enums.TileType;
import es.masanz.ut7.pokemonfx.model.fx.NPC;
import es.masanz.ut7.pokemonfx.model.pokemons.Charmander;
import es.masanz.ut7.pokemonfx.model.pokemons.Squirtle;

import java.util.ArrayList;

import static es.masanz.ut7.pokemonfx.util.Configuration.*;

public class Ruta2 extends Mapa {

    @Override
    protected void cargarPokemonSalvajes(){
        pokemonSalvajes = new ArrayList<>();
        pokemonSalvajes.add(new Bulbasaur(80));
        pokemonSalvajes.add(new Squirtle(100));
        pokemonSalvajes.add(new Charmander(9));
    }

    @Override
    protected void cargarMapa() {

        int[][] mapaRuta = {
                {1, 1, 1, 1,  1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 , 1, 1, 1},
                {1, 2, 2, 1,  2, 2, 2, 3, 2, 2, 2, 3, 2, 2, 2, 1 , 2, 2, 1},
                {1, 2, 2, 2,  2, 2, 2, 3, 2, 2, 2, 3, 2, 2, 2, 2 , 2, 2, 1},
                {1, 2, 2, 11, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 12, 2, 2, 1},
                {1, 2, 2, 9,  6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 10, 2, 2, 1},
                {1, 2, 2, 9,  6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 10, 2, 2, 1},
                {1, 2, 2, 9,  6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 10, 2, 2, 1},
                {1, 2, 2, 9,  6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 10, 2, 2, 1},
                {1, 2, 2, 9,  6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 10, 2, 2, 1},
                {1, 2, 2, 9,  6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 10, 2, 2, 1},
                {1, 2, 2, 9,  6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 10, 2, 2, 1},
                {1, 2, 2, 13, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 14, 2, 2, 1},
                {1, 2, 2, 2,  2, 2, 2, 3, 2, 2, 2, 3, 2, 2, 2, 2 , 2, 2, 1},
                {1, 2, 2, 1,  2, 2, 2, 3, 2, 2, 2, 3, 2, 2, 2, 1 , 2, 2, 1},
                {1, 1, 1, 1,  1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 , 1, 1, 1}
        };

        this.inicioX = 9;
        this.inicioY = 2;

        this.altura = mapaRuta.length;
        this.anchura = mapaRuta[0].length;
        this.nombre = "Ruta 2";

        this.mapData = new int[altura][anchura];
        this.collisionMap = new int[altura][anchura];
        this.teleportMap = new String[altura][anchura];
        this.eventsMap = new Evento[altura][anchura];
        this.npcs = new ArrayList<>();

        teleportMap[3][9] = "Ruta 1";

        Entrenador entrenador1 = new Entrenador();
        entrenador1.incluirPokemonParaCombatir(0, new Bulbasaur(50));
        entrenador1.incluirPokemonParaCombatir(1, new Bulbasaur(40));
        entrenador1.incluirPokemonParaCombatir(2, new Bulbasaur(30));
        entrenador1.incluirPokemonParaCombatir(3, new Bulbasaur(20));
        entrenador1.incluirPokemonParaCombatir(4, new Bulbasaur(10));
        entrenador1.incluirPokemonParaCombatir(5, new Bulbasaur(5));

        Entrenador entrenador2 = new Entrenador();
        entrenador2.incluirPokemonParaCombatir(0, new Charmander(50));
        entrenador2.incluirPokemonParaCombatir(1, new Charmander(40));
        entrenador2.incluirPokemonParaCombatir(2, new Charmander(30));
        entrenador2.incluirPokemonParaCombatir(3, new Charmander(20));
        entrenador2.incluirPokemonParaCombatir(4, new Charmander(10));
        entrenador2.incluirPokemonParaCombatir(5, new Charmander(5));

        // ENTRENADOR CON APARIENCIA RANDOM
        npcs.add(new NPC(2, 1, ABAJO, entrenador1));
        // ENTRENADOR CON APARIENCIA QUE YO INDICO
        npcs.add(new NPC(2, 3, ARRIBA, 0, entrenador2));

        for (int y = 0; y < altura; y++) {
            for (int x = 0; x < anchura; x++) {
                switch (mapaRuta[y][x]) {
                    case 1:
                        mapData[y][x] = TileType.PARED_BLANCA.ordinal();
                        collisionMap[y][x] = CollisionType.PARED.ordinal();
                        break;
                    case 2:
                        mapData[y][x] = TileType.CESPED.ordinal();
                        collisionMap[y][x] = CollisionType.SUELO.ordinal();
                        break;
                    case 3:
                        mapData[y][x] = TileType.CESPED_HIERBA.ordinal();
                        collisionMap[y][x] = CollisionType.SUELO.ordinal();
                        break;
                    case 4:
                        mapData[y][x] = TileType.CESPED_ARBUSTO.ordinal();
                        collisionMap[y][x] = CollisionType.PARED.ordinal();
                        break;
                    case 5:
                        mapData[y][x] = TileType.TELEPORT_RED.ordinal();
                        collisionMap[y][x] = CollisionType.SUELO.ordinal();
                        teleportMap[y][x] = "Ruta 1";
                        break;
                    case 6:
                        mapData[y][x] = TileType.MONTE_CENTRO.ordinal();
                        collisionMap[y][x] = CollisionType.PARED.ordinal();
                        break;
                    case 7:
                        mapData[y][x] = TileType.MONTE_BORDE_SUPERIOR.ordinal();
                        collisionMap[y][x] = CollisionType.PARED.ordinal();
                        break;
                    case 8:
                        mapData[y][x] = TileType.MONTE_BORDE_INFERIOR.ordinal();
                        collisionMap[y][x] = CollisionType.PARED.ordinal();
                        break;
                    case 9:
                        mapData[y][x] = TileType.MONTE_BORDE_IZQUIERDA.ordinal();
                        collisionMap[y][x] = CollisionType.PARED.ordinal();
                        break;
                    case 10:
                        mapData[y][x] = TileType.MONTE_BORDE_DERECHA.ordinal();
                        collisionMap[y][x] = CollisionType.PARED.ordinal();
                        break;
                    case 11:
                        mapData[y][x] = TileType.MONTE_ESQUINA_SUPERIOR_IZQUIERDA.ordinal();
                        collisionMap[y][x] = CollisionType.PARED.ordinal();
                        break;
                    case 12:
                        mapData[y][x] = TileType.MONTE_ESQUINA_SUPERIOR_DERECHA.ordinal();
                        collisionMap[y][x] = CollisionType.PARED.ordinal();
                        break;
                    case 13:
                        mapData[y][x] = TileType.MONTE_ESQUINA_INFERIOR_IZQUIERDA.ordinal();
                        collisionMap[y][x] = CollisionType.PARED.ordinal();
                        break;
                    case 14:
                        mapData[y][x] = TileType.MONTE_ESQUINA_INFERIOR_DERECHA.ordinal();
                        collisionMap[y][x] = CollisionType.PARED.ordinal();
                        break;
                    default:
                        mapData[y][x] = TileType.CAMINO_BLANCO.ordinal();
                        collisionMap[y][x] = CollisionType.SUELO.ordinal();
                        break;
                }
            }
        }
    }

}
