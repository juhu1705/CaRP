package de.juhu.guiFX;

import de.juhu.distributor.events.ProgressUpdateEvent;
import de.noisruker.event.EventManager;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * Behandelt alle Aktionen der Prozessleiste.
 *
 * @author Juhu1705
 * @category GUI
 */
public class FullProgress {

    private static FullProgress instance;
    private DoubleProperty progress;

    public static FullProgress getInstance() {
        return instance == null ? instance = new FullProgress() : instance;
    }

    private FullProgress() {
        EventManager.getInstance().registerEventListener(ProgressUpdateEvent.class, event -> this.setProgress(event.getProgress()));
    }

    public final double getProgress() {
        return progress == null ? 0 : progress.get();
    }

    public final void setProgress(double progress) {
        this.progress.set(progress);
        if(GUIManager.getInstance() != null) {
            Platform.runLater(() -> GUIManager.getInstance().progressPercent.setText(String.valueOf(Math.round(progress * 100))));
        }
    }

    public final DoubleProperty progressProperty() {
        return this.progress == null ? this.progress = new SimpleDoubleProperty(0) : this.progress;
    }

}
