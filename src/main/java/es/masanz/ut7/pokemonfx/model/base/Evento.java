package es.masanz.ut7.pokemonfx.model.base;

// TODO 05: Crear vuestros propios eventos que implementen esta interfaz
public interface Evento {

    void aplicarEfecto();

    // Si la imagen es null, no mostrara nada
    String imagenDelEvento();

}
