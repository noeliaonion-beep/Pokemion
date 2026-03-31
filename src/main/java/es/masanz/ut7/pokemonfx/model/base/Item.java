package es.masanz.ut7.pokemonfx.model.base;

public abstract class Item {
    protected int cantidad;
    protected boolean keyItem;
    protected String img;

    public Item(int cantidad, boolean keyItem) {
        this.cantidad = cantidad;
        this.keyItem = keyItem;
    }

    public abstract boolean consumirItem(Pokemon pokemon);

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getImg() {
        return img;
    }
}
