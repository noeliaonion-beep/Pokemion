package es.masanz.ut7.pokemonfx.model.base;

import java.util.ArrayList;
import java.util.List;

public class Entrenador {

    private List<Pokemon> pokemonesCapturados;
    private Pokemon[] pokemonesCombate;

    public Entrenador(){
        pokemonesCapturados = new ArrayList<>();
        pokemonesCombate = new Pokemon[6];
    }

    public void incluirPokemonParaCombatir(int pos, Pokemon pokemon){
        pokemonesCombate[pos] = pokemon;
    }

    public List<Pokemon> getPokemonesCapturados() {
        return pokemonesCapturados;
    }

    public Pokemon[] getPokemonesCombate() {
        return pokemonesCombate;
    }

}
