package es.masanz.ut7.pokemonfx.model.base;

import es.masanz.ut7.pokemonfx.model.enums.Stats;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public abstract class Pokemon {

    protected String id;
    protected String apodo;
    protected int hpActual;
    protected int hpBase, ataqueBase, defensaBase, velocidadBase, ataqueEspecialBase, defensaEspecialBase;
    protected int hpIV, ataqueIV, defensaIV, velocidadIV, ataqueEspecialIV, defensaEspecialIV;
    protected int expBase;
    protected int nivel;
    protected int puntosExp;
    protected String numPokedex;

    protected LinkedHashMap<String, Ataque> ataques;
    protected Ataque ataqueSeleccionado;
    protected List<Estado> estadoList;

    public Pokemon(int nivel){
        this.id = UUID.randomUUID().toString();
        Stats stats = Stats.valueOf(this.getClass().getSimpleName().toUpperCase());
        this.numPokedex = stats.numPokedex;
        this.hpBase = stats.hp;
        this.ataqueBase = stats.ataque;
        this.defensaBase = stats.defensa;
        this.velocidadBase = stats.velocidad;
        this.ataqueEspecialBase = stats.ataqueEspecial;
        this.defensaEspecialBase = stats.defensaEspecial;
        this.expBase = stats.expBase;
        this.hpIV = (int) (Math.random() * 32);
        this.ataqueIV = (int) (Math.random() * 32);
        this.defensaIV = (int) (Math.random() * 32);
        this.velocidadIV = (int) (Math.random() * 32);
        this.ataqueEspecialIV = (int) (Math.random() * 32);
        this.defensaEspecialIV = (int) (Math.random() * 32);
        this.nivel = nivel;
        this.puntosExp = 0;
        this.apodo = null;
        this.hpActual = getMaxHP();
        this.ataqueSeleccionado = null;
        this.ataques = new LinkedHashMap<>();
        this.estadoList = new ArrayList<>();
        asignarAtaques();
    }

    protected abstract void asignarAtaques();
    public abstract int nivelEvolucion();
    public abstract Pokemon pokemonAEvolucionar();

    public void recibirAtaque(Pokemon agresor, int cantidadDano) {
        int vida = this.hpActual - cantidadDano;
        if(vida < 0) vida = 0;
        this.hpActual = vida;
    }

    public int calcularHP() {
        return ((2 * hpBase + hpIV + 31) * nivel / 100) + nivel + 10;
    }

    private int calcularStat(int statBase, int statIV) {
        return ((2 * statBase + statIV + 31) * nivel / 100) + 5;
    }

    public String atacar(Pokemon enemigo) {
        StringBuilder msg = new StringBuilder();
        boolean puedeAtacar = true;
        
        if (!estadoList.isEmpty()) {
            Iterator<Estado> iterator = estadoList.iterator();
            while (iterator.hasNext()) {
                Estado estado = iterator.next();
                // Primero el veneno (daño al inicio del turno)
                if (estado instanceof EstadoEnvenenado veneno) {
                    if (veneno.turnos <= 0) {
                        iterator.remove();
                        msg.append("[").append(getNombre()).append("] ya no está envenenado. ");
                        veneno.turnos=5;
                    } else {
                        veneno.turnos--;
                        this.hpActual -= veneno.danoRecibido;
                        if (this.hpActual < 0) this.hpActual = 0;
                        msg.append("[").append(getNombre()).append("] sufre por el veneno. ");
                    }
                }
                
                // Segundo el sueño (comprobación de si puede actuar)
                if (estado instanceof EstadoDormido dormido) {
                    if (dormido.despertarRepentino() || dormido.turnos <= 0) {
                        iterator.remove();
                        msg.append("[").append(getNombre()).append("] se ha despertado. ");
                        dormido.turnos=5;
                    } else {
                        dormido.turnos--;
                        msg.append("[").append(getNombre()).append("] sigue dormido... ");
                        puedeAtacar = false;
                    }
                }
            }
        }

        // Si ha muerto por el veneno, ya no ataca
        if (this.hpActual <= 0) {
            return msg.append("[").append(getNombre()).append("] se ha debilitado por el veneno.").toString();
        }

        // Ejecutar ataque si no está impedido
        if (puedeAtacar && ataqueSeleccionado != null) {
            String ataqueMsg = ataqueSeleccionado.ejecutarAtaque(this, enemigo);
            msg.append(ataqueMsg);
        }
        
        return msg.length() > 0 ? msg.toString() : "¡" + getNombre() + " no puede atacar!";
    }

    public void subirNivel(){
        int vidaMaxAnterior = getMaxHP();
        nivel++;
        int gananciaVida = getMaxHP() - vidaMaxAnterior;
        this.hpActual += gananciaVida;
        if(this.hpActual > getMaxHP()) this.hpActual = getMaxHP();
    }

    public boolean sumarExperiencia(int puntosExp){
        this.puntosExp += puntosExp;
        boolean subio = false;
        while(this.puntosExp >= experienciaNecesariaParaSubirNivel()){
            this.puntosExp -= experienciaNecesariaParaSubirNivel();
            subirNivel();
            subio = true;
        }
        return subio;
    }

    public int experienciaNecesariaParaSubirNivel() {
        return (int) Math.pow(this.nivel, 3);
    }

    public void asignarAtaque(String clave, Ataque ataque){
        ataques.put(clave, ataque);
    }

    public String getNombre() { return apodo != null ? apodo : getClass().getSimpleName(); }
    public String getApodo() { return apodo; }
    public void setApodo(String apodo) { this.apodo = apodo; }
    public int getNivel() { return nivel; }
    public int getMaxHP() { return calcularHP(); }
    public int getHpActual() { return hpActual; }
    public void setHpActual(int hpActual) { this.hpActual = hpActual; }
    public int getAtaque() { return calcularStat(ataqueBase, ataqueIV); }
    public int getDefensa() { return calcularStat(defensaBase, defensaIV); }
    public int getVelocidad() { return calcularStat(velocidadBase, velocidadIV); }
    public int getAtaqueEspecial() { return calcularStat(ataqueEspecialBase, ataqueEspecialIV); }
    public int getDefensaEspecial() { return calcularStat(defensaEspecialBase, defensaEspecialIV); }
    public LinkedHashMap<String, Ataque> getAtaques() { return ataques; }
    public Ataque getAtaqueSeleccionado() { return ataqueSeleccionado; }
    public void setAtaqueSeleccionado(Ataque a) { this.ataqueSeleccionado = a; }
    public int getExpBase() { return expBase; }
    public List<Estado> getEstadoList() { return estadoList; }
    public String getNumPokedex() { return numPokedex; }
    public int getPuntosExp() { return puntosExp; }
}
