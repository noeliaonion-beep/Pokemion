package es.masanz.ut7.pokemonfx.model.fx;

import es.masanz.ut7.pokemonfx.controller.MapController;
import es.masanz.ut7.pokemonfx.model.base.Entrenador;
import es.masanz.ut7.pokemonfx.model.enums.TrainerType;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import static es.masanz.ut7.pokemonfx.util.Configuration.*;

public class NPC {

    private int contadorCambioSprite;
    private int x, y;
    private int posX, posY;
    private boolean isActive = false;
    private WritableImage sprite;
    private int mirandoA;
    private int elegirEntrenador;
    private boolean eliminarNPC = false;
    private int numSprite;
    private Image imagen;
    private Entrenador entrenador;

    // TODO 12: MODIFICAR ESTE CONSTRUCTOR PARA LLAMAR AL CONSTRUCTOR COMPLETO
    public NPC(int x, int y, Entrenador entrenador) {
        this.x = x;
        this.y = y;
        this.mirandoA = (int) (Math.random() * 4);
        this.elegirEntrenador = (int) (Math.random() * 4)+1;
        this.entrenador = entrenador;
        this.posX = x * TILE_SIZE;
        this.posY = y * TILE_SIZE;
        this.contadorCambioSprite = 0;
        generateSprite();
    }

    public NPC(int x, int y, int mirandoA, Entrenador entrenador) {
        this.x = x;
        this.y = y;
        this.mirandoA = mirandoA;
        this.elegirEntrenador = (int) (Math.random() * 4)+1;
        this.entrenador = entrenador;
        this.posX = x * TILE_SIZE;
        this.posY = y * TILE_SIZE;
        this.contadorCambioSprite = 0;
        generateSprite();
    }

    public NPC(int x, int y, int mirandoA, int elegirEntrenador, Entrenador entrenador) {
        this.x = x;
        this.y = y;
        this.mirandoA = mirandoA;
        this.elegirEntrenador = elegirEntrenador;
        this.entrenador = entrenador;
        this.posX = x * TILE_SIZE;
        this.posY = y * TILE_SIZE;
        this.contadorCambioSprite = 0;
        generateSprite();
    }

    public void generateSprite(){
        numSprite = 1;
        if(mirandoA==1) numSprite = 4;
        if(mirandoA==2) numSprite = 6;
        if(mirandoA==3) numSprite = 8;

        imagen = TrainerType.values()[elegirEntrenador].imagen;

        sprite = new WritableImage(imagen.getPixelReader(), numSprite * TILE_SIZE / SCALE_FACTOR, 0, TILE_SIZE / SCALE_FACTOR, TILE_SIZE / SCALE_FACTOR);
        PixelReader pixelReader = sprite.getPixelReader();
        PixelWriter pixelWriter = sprite.getPixelWriter();
        for (int i = 0; i < TILE_SIZE / SCALE_FACTOR; i++) {
            for (int j = 0; j < TILE_SIZE / SCALE_FACTOR; j++) {
                Color color = pixelReader.getColor(i, j);
                if (Math.round(color.getRed()*255) == 255 && Math.round(color.getGreen()*255) == 127 && Math.round(color.getBlue()*255) == 39) {
                    pixelWriter.setColor(i, j, Color.TRANSPARENT);
                } else {
                    pixelWriter.setColor(i, j, color);
                }
            }
        }
    }

    private void actualizarSprite(){
        if(mirandoA==0) numSprite = (numSprite + 1) % 3;
        if(mirandoA==1) numSprite = ((numSprite + 1) % 3) + 3;
        if(mirandoA==2) numSprite = ((numSprite + 1) % 2) + 6;
        if(mirandoA==3) numSprite = ((numSprite + 1) % 2) + 8;
        sprite = new WritableImage(imagen.getPixelReader(), numSprite * TILE_SIZE / SCALE_FACTOR, 0, TILE_SIZE / SCALE_FACTOR, TILE_SIZE / SCALE_FACTOR);
        PixelReader pixelReader = sprite.getPixelReader();
        PixelWriter pixelWriter = sprite.getPixelWriter();
        for (int i = 0; i < TILE_SIZE / SCALE_FACTOR; i++) {
            for (int j = 0; j < TILE_SIZE / SCALE_FACTOR; j++) {
                Color color = pixelReader.getColor(i, j);
                if (Math.round(color.getRed()*255) == 255 && Math.round(color.getGreen()*255) == 127 && Math.round(color.getBlue()*255) == 39) {
                    pixelWriter.setColor(i, j, Color.TRANSPARENT);
                } else {
                    pixelWriter.setColor(i, j, color);
                }
            }
        }
    }

    public WritableImage getSprite() {
        return sprite;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public int getMirandoA() {
        return mirandoA;
    }

    public boolean isEliminarNPC() {
        return eliminarNPC;
    }

    public Entrenador getEntrenador() {
        return entrenador;
    }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }

    public void moveTowards(double targetX, double targetY) {
        int aux = contadorCambioSprite;
        contadorCambioSprite = (contadorCambioSprite + 1) % VELOCIDAD_CAMBIO_SPRITES;
        if(contadorCambioSprite<aux){
            actualizarSprite();
        }
        if ((posX + TILE_SIZE) < targetX) posX += MOVE_SPEED;
        else if (posX - TILE_SIZE > targetX) posX -= MOVE_SPEED;
        else if (posY + TILE_SIZE < targetY) posY += MOVE_SPEED;
        else if (posY - TILE_SIZE > targetY) posY -= MOVE_SPEED;
        else {
            isActive = false;
            MapController.cambiarAEscenario(this);
            eliminarNPC = true;
        }
    }

}