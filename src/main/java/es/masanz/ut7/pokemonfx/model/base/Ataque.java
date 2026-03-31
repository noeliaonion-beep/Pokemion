package es.masanz.ut7.pokemonfx.model.base;

import es.masanz.ut7.pokemonfx.model.enums.Tipo;

public class Ataque {

    protected String nombre;
    protected int dmgBase;
    protected int precision;
    protected Tipo tipo;
    protected boolean esEspecial;
    protected int pp;
    protected int cantidad;
    protected int prioridad;

    public Ataque(String nombre, int dmgBase, int precision, Tipo tipo, boolean esEspecial, int cantidad) {
        this.nombre = nombre;
        this.dmgBase = dmgBase;
        this.precision = precision;
        this.tipo = tipo;
        this.esEspecial = esEspecial;
        this.pp = cantidad;
        this.cantidad = cantidad;
        this.prioridad = 1;
    }

    public String ejecutarAtaque(Pokemon ejecutor, Pokemon destinatario) {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(ejecutor.getNombre()).append("] usó [").append(nombre).append("].");
        if(cantidad > 0){
            cantidad--;
            if (Math.random() * 100 < precision) {
                int dano = calcularDano(ejecutor, destinatario, sb);
                sb.append(" Hace [").append(dano).append("] puntos de daño.");
                destinatario.recibirAtaque(ejecutor, dano);
            } else {
                sb.append(" ¡Pero [FALLO] el ataque!");
            }
        } else {
            sb.append(" ¡No quedan PP!");
        }
        return sb.toString();
    }

    protected int calcularDano(Pokemon ejecutor, Pokemon objetivo, StringBuilder sb) {
        int ataque = esEspecial ? ejecutor.getAtaqueEspecial() : ejecutor.getAtaque();
        int defensa = esEspecial ? objetivo.getDefensaEspecial() : objetivo.getDefensa();

        double danioBase = (((2.0 * ejecutor.getNivel() + 10) / 250.0) * (ataque / (double) defensa) * dmgBase + 2);
        
        Class<?>[] interfaces = ejecutor.getClass().getInterfaces();
        for (Class<?> interfaceType : interfaces) {
            if(interfaceType.getSimpleName().equalsIgnoreCase(tipo.name())){
                danioBase *= 1.5;
            }
        }

        double multiplicador = 1.0;
        Class<?>[] interfacesObj = objetivo.getClass().getInterfaces();
        for (Class<?> interfaceType : interfacesObj) {
            String tipoDef = interfaceType.getSimpleName().toUpperCase();
            if(tipo.esDebilContra(tipoDef)) { sb.append(" No es muy efectivo..."); multiplicador *= 0.5; }
            if(tipo.esFuerteContra(tipoDef)) { sb.append(" ¡Es super efectivo!"); multiplicador *= 2.0; }
            if(tipo.esInmuneContra(tipoDef)) { sb.append(" Es inmune..."); multiplicador *= 0.0; }
        }
        
        danioBase *= multiplicador;
        if (Math.random() < (1.0 / 16.0)) { sb.append(" ¡ATAQUE CRITICO!"); danioBase *= 1.5; }
        danioBase *= (0.85 + (Math.random() * 0.15));

        return (int) Math.max(1, danioBase);
    }

    public String getNombre() { return nombre; }
    public int getDmgBase() { return dmgBase; }
    public int getPrecision() { return precision; }
    public Tipo getTipo() { return tipo; }
    public boolean isEsEspecial() { return esEspecial; }
    public int getPp() { return pp; }
    public int getCantidad() { return cantidad; }
    public int getPrioridad() { return prioridad; }
    public void setPrioridad(int prioridad) { this.prioridad = prioridad;
    }


    public Object clone(Ataque ataque)  {
        Ataque nuevoAtaque= new Ataque(ataque.getNombre(), ataque.getDmgBase(), ataque.getPrecision(), ataque.getTipo(), ataque.isEsEspecial(), ataque.getCantidad());
        return nuevoAtaque;
    }
}
