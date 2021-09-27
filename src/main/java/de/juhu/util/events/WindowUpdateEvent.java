package de.juhu.util.events;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class WindowUpdateEvent extends WindowEvent {

    public WindowUpdateEvent(Stage stage, Scene scene) {
        super("UpdateWindowEvent", stage, scene);
    }
}
