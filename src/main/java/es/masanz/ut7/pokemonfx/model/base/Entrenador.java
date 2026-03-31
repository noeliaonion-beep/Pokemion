package es.masanz.ut7.pokemonfx.model.base;

import java.util.ArrayList;
import java.util.List;

public class Entrenador {

    private List<Pokemon> pokemonesCapturados;
    private Pokemon[] pokemonesCombate;
    private List<Item> bolsa;

    public Entrenador(){
        pokemonesCapturados = new ArrayList<>();
        pokemonesCombate = new Pokemon[6];
        bolsa=new ArrayList<>();
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

    public List<Item> getBolsa() {
        return bolsa;
    }
    
    public void anadirItem(Item item) {
        if (bolsa.contains(item)) {
            bolsa.get(bolsa.indexOf(item)).setCantidad(bolsa.get(bolsa.indexOf(item)).getCantidad() + 1);
        } else {
            bolsa.add(item);
        }
    }

}
