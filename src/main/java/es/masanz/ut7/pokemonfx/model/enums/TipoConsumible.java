package es.masanz.ut7.pokemonfx.model.enums;

public enum TipoConsumible {
    CARAMELORARO("/img/carameloraro.png",true,0,false,0,0),POCION("/img/pocion.png",false,0,false,0,15),SUPERPOCION("/img/superpocion.png",false,0,false,0,30),HIPERPOCION("/img/hiperpocion.png",false,0,false,0,50),POCIONMAXIMA("/img/pocionmaxima.png",false,0,false,0,99999),REVIVIR("/img/revivir.png",false,0,true,0.25,0),REVIVIRMAXIMO("/img/revivirmaximo.png",false,0,true,1.0,0),ETER("/img/eter.png",false,10,false,0,0);
    public String img;
    public boolean subirNivel;
    public int pps;
    public boolean revivir;
    public double vidaDRevivir;
    public int incrementoVida;

    TipoConsumible(String img, boolean subirNivel, int pps, boolean revivir, double vidaDRevivir, int incrementoVida) {
        this.img = img;
        this.subirNivel = subirNivel;
        this.pps = pps;
        this.revivir = revivir;
        this.vidaDRevivir = vidaDRevivir;
        this.incrementoVida = incrementoVida;
    }
}
