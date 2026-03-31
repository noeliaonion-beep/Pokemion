package es.masanz.ut7.pokemonfx.model.enums;

import es.masanz.ut7.pokemonfx.controller.MapController;
import javafx.scene.image.Image;

public enum TrainerType {

    JUGADOR("/pruebas/player.png"),
    ENTRENADOR_1("/pruebas/entrenador1.png"),
    ENTRENADOR_2("/pruebas/entrenador2.png"),
    ENTRENADOR_3("/pruebas/entrenador3.png"),
    ENTRENADOR_4("/pruebas/entrenador4.png");

    public final Image imagen;

    TrainerType(String rutaImagen){
        imagen = new Image(MapController.class.getResource(rutaImagen).toExternalForm());
    }

}
