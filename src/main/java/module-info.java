module es.masanz.ut7.pokemonfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jsoup;
    requires jdk.xml.dom;

    // Abrimos los controladores para que FXML pueda acceder a ellos
    opens es.masanz.ut7.pokemonfx.controller to javafx.fxml;

    // Exportamos los paquetes que deben ser accesibles desde otros módulos
    exports es.masanz.ut7.pokemonfx.app;
    exports es.masanz.ut7.pokemonfx.controller;
    exports es.masanz.ut7.pokemonfx.model.base;
    exports es.masanz.ut7.pokemonfx.model.pokemons;
    exports es.masanz.ut7.pokemonfx.model.enums;
    exports es.masanz.ut7.pokemonfx.model.fx;
    exports es.masanz.ut7.pokemonfx.model.type;
    exports es.masanz.ut7.pokemonfx.util;
}