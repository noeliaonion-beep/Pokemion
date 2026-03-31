package es.masanz.ut7.pokemonfx.model.enums;

import es.masanz.ut7.pokemonfx.controller.MapController;
import javafx.scene.image.Image;

public enum TileType {

    // TODO 09: Incluir nuevos tipos de sprites. Las dimensiones deben ser 16x16px
    CAMINO_BLANCO("/pruebas/camino_blanco.png"),
    PARED_BLANCA("/pruebas/pared_blanca.png"),
    CESPED("/pruebas/cesped.png"),
    CESPED_ARBUSTO("/pruebas/cesped_arbusto.png"),
    CESPED_HIERBA("/pruebas/cesped_hierba.png"),
    MONTE_CENTRO("/pruebas/monte_centro.png"),
    MONTE_BORDE_SUPERIOR("/pruebas/monte_borde_superior.png"),
    MONTE_BORDE_INFERIOR("/pruebas/monte_borde_inferior.png"),
    MONTE_BORDE_IZQUIERDA("/pruebas/monte_borde_izquierda.png"),
    MONTE_BORDE_DERECHA("/pruebas/monte_borde_derecha.png"),
    MONTE_ESQUINA_SUPERIOR_IZQUIERDA("/pruebas/monte_esquina_superior_izquierda.png"),
    MONTE_ESQUINA_SUPERIOR_DERECHA("/pruebas/monte_esquina_superior_derecha.png"),
    MONTE_ESQUINA_INFERIOR_IZQUIERDA("/pruebas/monte_esquina_inferior_izquierda.png"),
    MONTE_ESQUINA_INFERIOR_DERECHA("/pruebas/monte_esquina_inferior_derecha.png"),
    TELEPORT_RED("/pruebas/teleport_red.png");

    public final Image imagen;

    TileType(String rutaImagen){
        imagen = new Image(MapController.class.getResource(rutaImagen).toExternalForm());
    }

}
