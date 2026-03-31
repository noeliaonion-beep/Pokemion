package es.masanz.ut7.pokemonfx.model.enums;

public enum Tipo {

    // TODO 10: Si se incluyen nuevos tipos de pokemon, se deberan reflejar aqui
    AGUA(new String[]{"PLANTA"}, new String[]{"FUEGO"}, new String[]{}),
    FUEGO(new String[]{"AGUA"}, new String[]{"PLANTA"}, new String[]{}),
    PLANTA(new String[]{"FUEGO"}, new String[]{"AGUA"}, new String[]{}),
    NORMAL(new String[]{"LUCHA"}, new String[]{}, new String[]{"FANTASMA"}),
    SINIESTRO(new String[]{"LUCHA"}, new String[]{"PSIQUICO"}, new String[]{}),
    DRAGON(new String[]{"HIELO", "DRAGON"}, new String[]{"DRAGON"}, new String[]{});

    public final String[] debilidades;
    public final String[] resistencias;
    public final String[] inmunidades;

    Tipo(String[] debilidades, String[] resistencias, String[] inmunidades) {
        this.debilidades = debilidades;
        this.resistencias = resistencias;
        this.inmunidades = inmunidades;
    }

    public boolean esDebilContra(String tipo) {
        if(debilidades!=null && debilidades.length>0){
            for (String debilidad : debilidades) {
                if(debilidad.equalsIgnoreCase(tipo)){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean esFuerteContra(String tipo) {
        if(resistencias!=null && resistencias.length>0){
            for (String resistencia : resistencias) {
                if(resistencia.equalsIgnoreCase(tipo)){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean esInmuneContra(String tipo) {
        if(inmunidades!=null && inmunidades.length>0){
            for (String inmunidad : inmunidades) {
                if(inmunidad.equalsIgnoreCase(tipo)){
                    return true;
                }
            }
        }
        return false;
    }
}
