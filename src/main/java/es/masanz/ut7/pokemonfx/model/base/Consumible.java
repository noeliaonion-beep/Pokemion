package es.masanz.ut7.pokemonfx.model.base;

import es.masanz.ut7.pokemonfx.model.enums.TipoConsumible;

public class Consumible extends Item{
    protected TipoConsumible tipoConsumible;

    public Consumible(int cantidad, boolean keyItem,TipoConsumible tipoConsumible) {
        super(cantidad, keyItem);
        this.tipoConsumible=tipoConsumible;
        this.img = tipoConsumible.img;
    }

    @Override
    public boolean consumirItem(Pokemon pokemon) {
        boolean exito = false;
        if (tipoConsumible.subirNivel){
            pokemon.subirNivel();
            exito = true;
        } else if (tipoConsumible.revivir && pokemon.getHpActual()==0) {
            pokemon.setHpActual((int) (pokemon.getMaxHP() * (tipoConsumible.vidaDRevivir > 0 ? tipoConsumible.vidaDRevivir : 1.0)));
            exito = true;
        } else if (!tipoConsumible.revivir && pokemon.getHpActual() > 0) {
            int nuevaVida = pokemon.getHpActual() + tipoConsumible.incrementoVida;
            if (nuevaVida > pokemon.getMaxHP()) nuevaVida = pokemon.getMaxHP();
            pokemon.setHpActual(nuevaVida);
            exito = true;
        }
        
        if (exito) {
            this.cantidad--;
        }
        return exito;
    }

    public TipoConsumible getTipoConsumible() {
        return tipoConsumible;
    }
}
