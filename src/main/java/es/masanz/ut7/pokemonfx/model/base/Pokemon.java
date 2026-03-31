package es.masanz.ut7.pokemonfx.model.base;

import es.masanz.ut7.pokemonfx.model.enums.Stats;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

// TODO 06: Crear vuestros propios pokemon que extiendan esta clase
public abstract class Pokemon {

    protected String id;
    protected String apodo;
    protected int hpActual;
    // estos factores se pueden consultar en https://pokemon.fandom.com/es/wiki/Lista_de_Pok%C3%A9mon_con_sus_estad%C3%ADsticas_base
    protected int hpBase, ataqueBase, defensaBase, velocidadBase, ataqueEspecialBase, defensaEspecialBase;
    // estos factores se generan al hacer en un numero random entre el 0 y el 31
    protected int hpIV, ataqueIV, defensaIV, velocidadIV, ataqueEspecialIV, defensaEspecialIV;
    // este factor se puede consultar en https://pokemon.fandom.com/es/wiki/Experiencia_base
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
        asignarAtaques();
        estadoList=new ArrayList<>();
    }

    protected abstract void asignarAtaques();

    public abstract int nivelEvolucion();

    public abstract Pokemon pokemonAEvolucionar();

    public String atacar(Pokemon enemigo) {
        if (estadoList.isEmpty()) {
            String msg = ataqueSeleccionado.ejecutarAtaque(this, enemigo);
            return msg;
        }else{
            for (Estado estado : estadoList) {
                if (estado instanceof EstadoDormido){
                    if (((EstadoDormido) estado).despertarRepentino() || estado.turnos==0){
                        estadoList.remove(estado);
                        String msg="Tu pokemon ya no esta dormido";
                        return msg;
                    }else{
                        String msg="Tu pokemon sigue dormido";
                        estado.turnos--;
                        return msg;
                    }
                }
                if (estado instanceof EstadoEnvenenado){
                    if (estado.turnos==0){
                        estadoList.remove(estado);
                        String msg="Tu pokemon ya no esta envenenado";
                        return msg;
                    }
                    else {
                        estado.turnos--;
                        this.hpActual= hpActual-estado.danoRecibido;
                        String msg="Tu pokemon esta envenenado pierde "+ estado.danoRecibido;
                        return msg;
                    }
                }

            }
        }
        return null;
    }

    public void recibirAtaque(Pokemon agresor, int cantidadDano) {
        int vida = this.getHpActual() - cantidadDano;
        if(vida<0) vida = 0;
        this.setHpActual(vida);
    }

    public int calcularHP() {
        return ((2 * hpBase + hpIV + 31) * nivel / 100) + nivel + 10;
    }

    private int calcularStat(int statBase, int statIV) {
        return ((2 * statBase + statIV + 31) * nivel / 100) + 5;
    }

    public void asignarAtaque(String clave, Ataque ataque){
        if(ataques==null){
            ataques = new LinkedHashMap<>();
        }
        ataques.put(clave, ataque);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Info. de Pokemon "+getClass().getSimpleName()+":\n"));
        sb.append(String.format("   %-20s\n", "Nivel actual: "+getNivel()));
        if(apodo!=null){
            sb.append(String.format("   %-20s\n", "apodo: "+apodo));
        }
        sb.append(String.format("   %-20s\n", "vida max.: "+getMaxHP()));
        sb.append(String.format("   %-20s\n", "vida act.: "+getHpActual()));
        sb.append(String.format("   %-20s\n", "ataque: "+getAtaque()));
        sb.append(String.format("   %-20s\n", "defensa: "+getDefensa()));
        sb.append(String.format("   %-20s\n", "velocidad: "+getVelocidad()));
        sb.append(String.format("   %-20s\n", "ata. especial: "+getAtaqueEspecial()));
        sb.append(String.format("   %-20s\n", "def. especial: "+getAtaqueEspecial()));

        return sb.toString();
    }

    public String getApodo() {
        return apodo;
    }

    public void setApodo(String apodo) {
        this.apodo = apodo;
    }

    public String getNumPokedex() {
        return numPokedex;
    }

    public int getNivel(){
        return nivel;
    }

    public void subirNivel(){
        int vidaMaxAnterior = getMaxHP();
        nivel++;
        this.hpActual += (getMaxHP() - vidaMaxAnterior);
        if(this.hpActual>getMaxHP()) this.hpActual = getMaxHP();
    }

    public boolean sumarExperiencia(int puntosExp){
        boolean aumentaNivel = false;
        this.puntosExp += puntosExp;
        while(this.puntosExp>=experienciaNecesariaParaSubirNivel()){
            this.puntosExp = this.puntosExp - experienciaNecesariaParaSubirNivel();
            aumentaNivel = true;
            subirNivel();
        }
        return aumentaNivel;
    }

    public int experienciaNecesariaParaSubirNivel() {
        return (int) Math.pow(this.nivel, 3);
    }

    public int getMaxHP() {
        return calcularHP();
    }

    public int getHpActual() {
        return hpActual;
    }

    public void setHpActual(int hpActual) {
        this.hpActual = hpActual;
    }

    public int getAtaque() {
        return calcularStat(ataqueBase, ataqueIV);
    }

    public int getDefensa() {
        return calcularStat(defensaBase, defensaIV);
    }

    public int getVelocidad() {
        return calcularStat(velocidadBase, velocidadIV);
    }

    public int getAtaqueEspecial(){
        return calcularStat(ataqueEspecialBase, ataqueEspecialIV);
    }

    public int getDefensaEspecial(){
        return calcularStat(defensaEspecialBase, defensaEspecialIV);
    }

    public LinkedHashMap<String, Ataque> getAtaques() {
        return ataques;
    }

    public Ataque getAtaqueSeleccionado() {
        return ataqueSeleccionado;
    }

    public void setAtaqueSeleccionado(Ataque ataqueSeleccionado) {
        this.ataqueSeleccionado = ataqueSeleccionado;
    }

    public int getExpBase() {
        return expBase;
    }

    public int getPuntosExp() {
        return puntosExp;
    }

    public void setAtaques(LinkedHashMap<String, Ataque> ataques) {
        this.ataques = ataques;
    }

    public List<Estado> getEstadoList() {
        return estadoList;
    }

    public void setEstadoList(List<Estado> estadoList) {
        this.estadoList = estadoList;
    }
// TODO 01: Implementar clone. El clonado deberá generar un pokemon con nuevos IV.
}
