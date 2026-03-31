package es.masanz.ut7.pokemonfx.model.pokemons;

import es.masanz.ut7.pokemonfx.model.base.Ataque;
import es.masanz.ut7.pokemonfx.model.base.Pokemon;
import es.masanz.ut7.pokemonfx.model.enums.Tipo;
import es.masanz.ut7.pokemonfx.model.type.Planta;

public class Bulbasaur extends Pokemon implements Planta {

    public Bulbasaur(int nivel){
        super(nivel);
    }

    @Override
    public int nivelEvolucion() {
        return 6;
    }

    @Override
    public Pokemon pokemonAEvolucionar() {
        // Esto NO MOLA. Tener en cuenta que el apodo y los IV los perdemos si hacemos esto
        Pokemon pokemon = new Charmander(this.nivel);
        return pokemon;
    }

    @Override
    protected void asignarAtaques() {
        // Nivel 1: Placaje
        Ataque placaje = new Ataque("placaje", 35, 95, Tipo.NORMAL, false, 35);
        asignarAtaque(placaje.getNombre(), placaje);

        // Nivel 10: Látigo Cepa
        Ataque latigoCepa = new Ataque("látigo cepa", 35, 90, Tipo.PLANTA, false, 15);
        asignarAtaque(latigoCepa.getNombre(), latigoCepa);

        // Nivel 20: Hoja Afilada
        Ataque hojaAfilada = new Ataque("hoja afilada", 55, 95, Tipo.PLANTA, false, 25);
        asignarAtaque(hojaAfilada.getNombre(), hojaAfilada);

        // Nivel 25: Doble Filo
        Ataque dobleFilo = new Ataque("doble filo", 120, 80, Tipo.NORMAL, false, 10);
        asignarAtaque(dobleFilo.getNombre(), dobleFilo);

        // Nivel 32: Rayo Solar
        Ataque rayoSolar = new Ataque("rayo solar", 120, 80, Tipo.PLANTA, true, 10);
        asignarAtaque(rayoSolar.getNombre(), rayoSolar);

    }

}
