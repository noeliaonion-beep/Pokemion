package es.masanz.ut7.pokemonfx.model.base;

public class EstadoDormido extends Estado{
    public EstadoDormido() {
        super("Dormido", 5, 0);
    }

    @Override
    public void aplicarEfecto(Pokemon pokemon) {
        pokemon.getEstadoList().add(new EstadoDormido());
    }

    public boolean despertarRepentino(){
        if (Math.random()*10 == 2){
            return true;
        }
        return false;
    }
}
