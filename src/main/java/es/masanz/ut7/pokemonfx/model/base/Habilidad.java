package es.masanz.ut7.pokemonfx.model.base;

import es.masanz.ut7.pokemonfx.model.enums.Tipo;

public class Habilidad extends Ataque {
    protected Estado estadoAplicado;

    public Habilidad(String nombre, int precision, Tipo tipo, boolean esEspecial, int cantidad,Estado estadoAplicado) {
        super(nombre, 0, precision, tipo, esEspecial, cantidad);
        this.estadoAplicado=estadoAplicado;
    }

    @Override
    public String ejecutarAtaque(Pokemon ejecutor, Pokemon destinatario) {
        StringBuilder sb = new StringBuilder();
        String nombrePokemonE = ejecutor.getApodo()!=null ? ejecutor.getApodo() : ejecutor.getClass().getSimpleName();
        sb.append("["+nombrePokemonE+"] usó ["+nombre+"].");
        if(cantidad > 0){
            cantidad--;
            if (Math.random() * 100 < precision) {
                if (estadoAplicado!=null) {
                    estadoAplicado.aplicarEfecto(destinatario);
                }
            } else {
                sb.append(" ¡Pero [FALLO] el ataque!");
            }
        } else {
            sb.append(" ¡No quedan PP!");
        }
        return sb.toString();
    }
}
