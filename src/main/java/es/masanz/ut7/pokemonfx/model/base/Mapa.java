package es.masanz.ut7.pokemonfx.model.base;

import es.masanz.ut7.pokemonfx.model.fx.NPC;

import java.util.List;

// TODO 02: Crear vuestras propias rutas que implementen esta clase
public abstract class Mapa {

    protected String nombre;
    protected int altura;
    protected int anchura;

    protected int[][] mapData;
    protected int[][] collisionMap;
    protected String[][] teleportMap;
    protected Evento[][] eventsMap;
    protected List<NPC> npcs;
    protected List<Pokemon> pokemonSalvajes;

    protected int inicioX;
    protected int inicioY;

    public Mapa(){
        cargarPokemonSalvajes();
        cargarMapa();
    }

    protected abstract void cargarPokemonSalvajes();

    protected abstract void cargarMapa();

    public String getNombre() {
        return nombre;
    }

    public int getAltura() {
        return altura;
    }

    public int getAnchura() {
        return anchura;
    }

    public int[][] getMapData() {
        return mapData;
    }

    public int[][] getCollisionMap() {
        return collisionMap;
    }

    public String[][] getTeleportMap() {
        return teleportMap;
    }

    public Evento[][] getEventsMap() {
        return eventsMap;
    }

    public int getInicioX() {
        return inicioX;
    }

    public int getInicioY() {
        return inicioY;
    }

    public List<NPC> getNpcs() {
        return npcs;
    }

    public List<Pokemon> getPokemonSalvajes() {
        return pokemonSalvajes;
    }
}
