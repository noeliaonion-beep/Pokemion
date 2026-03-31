package es.masanz.ut7.pokemonfx.model.event;

import es.masanz.ut7.pokemonfx.app.GameApp;
import es.masanz.ut7.pokemonfx.model.base.Evento;
import es.masanz.ut7.pokemonfx.model.base.Item;
import es.masanz.ut7.pokemonfx.model.base.Pokebolas;
import es.masanz.ut7.pokemonfx.model.base.Pokemon;
import es.masanz.ut7.pokemonfx.model.enums.Pokebols;

import java.util.List;

public class EventoEjemplo implements Evento {

    private String imagenEvento = "/pruebas/pokeball_transparente.png";

    @Override
    public void aplicarEfecto() {
        GameApp.jugador.anadirItem(new Pokebolas(1, false, Pokebols.MASTERBOL));
        List<Pokemon> pokemonCapturados = GameApp.jugador.getPokemonesCapturados();
        for (Pokemon pokemon : pokemonCapturados) {
            pokemon.setHpActual(pokemon.getMaxHP());
        }
        imagenEvento = null;
    }

    @Override
    public String imagenDelEvento() {
        return imagenEvento;
    }

}
