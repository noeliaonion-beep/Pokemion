package es.masanz.ut7.pokemonfx.model.pokemons;

import es.masanz.ut7.pokemonfx.model.base.Ataque;
import es.masanz.ut7.pokemonfx.model.base.Pokemon;
import es.masanz.ut7.pokemonfx.model.enums.Tipo;
import es.masanz.ut7.pokemonfx.model.type.Agua;

public class Squirtle extends Pokemon implements Agua {

    public Squirtle(int nivel) {
        super(nivel);
    }

    @Override
    public int nivelEvolucion() {
        return -1;
    }

    @Override
    public Pokemon pokemonAEvolucionar() {
        return null;
    }

    @Override
    protected void asignarAtaques() {
        // Nivel 1: Placaje
        Ataque placaje = new Ataque("placaje", 35, 95, Tipo.NORMAL, false, 35);
        asignarAtaque(placaje.getNombre(), placaje);

        // Nivel 7: Burbuja
        Ataque burbuja = new Ataque("burbuja", 20, 100, Tipo.AGUA, true, 30);
        asignarAtaque(burbuja.getNombre(), burbuja);

        // Nivel 15: Pistola Agua
        Ataque pistolaAgua = new Ataque("pistola agua", 40, 90, Tipo.AGUA, true, 20);
        asignarAtaque(pistolaAgua.getNombre(), pistolaAgua);

        // Nivel 20: Mordisco
        Ataque mordisco = new Ataque("mordisco", 60, 90, Tipo.SINIESTRO, false, 25);
        asignarAtaque(mordisco.getNombre(), mordisco);

        // Nivel 31: Hidropulso
        Ataque hidropulso = new Ataque("hidropulso", 60, 85, Tipo.AGUA, true, 20);
        asignarAtaque(hidropulso.getNombre(), hidropulso);

        // Nivel 37: Cabezazo
        Ataque cabezazo = new Ataque("cabezazo", 70, 90, Tipo.NORMAL, false, 15);
        asignarAtaque(cabezazo.getNombre(), cabezazo);

        // Nivel 43: Hidrobomba
        Ataque hidrobomba = new Ataque("hidrobomba", 110, 80, Tipo.AGUA, true, 5);
        asignarAtaque(hidrobomba.getNombre(), hidrobomba);

    }

}
