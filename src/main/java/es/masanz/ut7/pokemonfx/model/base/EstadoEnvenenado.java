package es.masanz.ut7.pokemonfx.model.base;

public class EstadoEnvenenado extends Estado{
    public EstadoEnvenenado() {
        super("Envenenado", 5, (int) (Math.random()*10)+1);
    }

    @Override
    public void aplicarEfecto(Pokemon pokemon) {
        pokemon.getEstadoList().add(new EstadoEnvenenado());
    }
}
