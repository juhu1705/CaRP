package de.juhu.util.events;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class WindowCreatedEvent extends WindowEvent {
    public WindowCreatedEvent(Stage stage, Scene scene) {
        super("CreateWindowEvent", stage, scene);

    }
}
