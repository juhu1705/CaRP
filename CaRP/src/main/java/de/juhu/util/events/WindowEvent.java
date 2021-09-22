package de.juhu.util.events;

import de.noisruker.event.events.Event;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class WindowEvent extends Event {

    private final Scene scene;
    private final Stage stage;

    public WindowEvent(String name, Stage stage, Scene scene) {
        super(name);
        this.scene = scene;
        this.stage = stage;
    }

    public Scene getScene() {
        return this.scene;
    }

    public Stage getStage() {
        return this.stage;
    }
}
