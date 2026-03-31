package es.masanz.ut7.pokemonfx.model.pokemons;

import es.masanz.ut7.pokemonfx.model.base.*;
import es.masanz.ut7.pokemonfx.model.enums.Tipo;

public class Mimikyu extends Pokemon {
    public Mimikyu(int nivel) {
        super(nivel);
    }

    @Override
    protected void asignarAtaques() {


                // Placaje
                Ataque placaje = new Ataque("placaje", 35, 95, Tipo.NORMAL, false, 35);
                asignarAtaque(placaje.getNombre(), placaje);
                // Bola Sombra
                Ataque bolaSombra= new Ataque("Bola Sombra",50,89,Tipo.SINIESTRO,true,15);
                asignarAtaque(bolaSombra.getNombre(), bolaSombra);
                // Malicioso
                Ataque malicioso= new Habilidad("Malicioso",20,Tipo.SINIESTRO,true,20, new EstadoDormido());
                asignarAtaque(malicioso.getNombre(), malicioso);
                // Veneno
                Ataque envenenar =new Habilidad("Envenenar",100,Tipo.SINIESTRO,true,10,new EstadoEnvenenado() );
                asignarAtaque(envenenar.getNombre(), envenenar);
        }


    @Override
    public int nivelEvolucion() {
        return -1;
    }

    @Override
    public Pokemon pokemonAEvolucionar() {
        return null;
    }

}
