package es.masanz.ut7.pokemonfx.model.enums;

public enum Pokebols {

    POKEBOL("/pokebol/pokebol.png",0.1),SUPERBOL("/pokebol/superbol.png",0.2),ULTRABOL("/pokebol/ultraball.png",0.3),MASTERBOL("/pokebol/masterbol.png",1.0);
    public String imagen;
    public double ratioCaptura;

    Pokebols(String imagen, double ratioCaptura) {
        this.imagen = imagen;
        this.ratioCaptura = ratioCaptura;
    }
}
