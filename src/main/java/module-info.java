module de.juhu.carp {
    requires de.noisruker.logger;
    requires de.noisruker.config;
    requires de.noisruker.event;
    requires de.noisruker.tablefilemanager;
    requires org.controlsfx.controls;
    requires java.logging;
    requires javafx.fxml;
    requires java.desktop;
    requires org.jfxtras.styles.jmetro;
    requires java.sql;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires org.kordamp.ikonli.fontawesome;

    opens assets.layouts to javafx.fxml;
    opens assets.styles to javafx.base;
    opens assets.textures.logo to javafx.fxml;
    opens de.juhu.guiFX to javafx.fxml;

    exports de.juhu.guiFX to javafx.graphics, javafx.fxml;
    exports de.juhu.util to de.noisruker.config;
}