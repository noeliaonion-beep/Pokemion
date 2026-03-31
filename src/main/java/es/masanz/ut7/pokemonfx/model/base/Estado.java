package es.masanz.ut7.pokemonfx.model.base;

public abstract class Estado {
    protected String nombre;
    protected int turnos;
    protected int danoRecibido;

    public Estado(String nombre, int turnos, int danoRecibido) {
        this.nombre = nombre;
        this.turnos = turnos;
        this.danoRecibido = danoRecibido;
    }

    public abstract void aplicarEfecto(Pokemon pokemon);
}
