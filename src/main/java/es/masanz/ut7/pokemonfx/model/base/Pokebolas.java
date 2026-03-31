package es.masanz.ut7.pokemonfx.model.base;

import es.masanz.ut7.pokemonfx.model.enums.Pokebols;

public class Pokebolas extends Item {
    private Pokebols tipo;

    public Pokebolas(int cantidad, boolean keyItem, Pokebols tipo) {
        super(cantidad, keyItem);
        this.tipo = tipo;
        this.img = tipo.imagen;
    }

    @Override
    public boolean consumirItem(Pokemon pokemon) {
        // La lógica de consumo para capturar se gestiona en CombateController
        // porque requiere animaciones y acceso al estado del combate
        return false; 
    }

    public Pokebols getTipo() {
        return tipo;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Pokebolas){
            if (((Pokebolas) obj).getTipo()==this.getTipo()){
                return true;
            }
        }
        return false;
    }
}
