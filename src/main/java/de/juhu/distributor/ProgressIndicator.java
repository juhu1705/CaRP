package de.juhu.distributor;

import de.juhu.distributor.events.ProgressUpdateEvent;
import de.juhu.guiFX.FullProgress;
import de.juhu.guiFX.PartProgress;
import de.noisruker.event.EventManager;

/**
 * Verwaltet die Fortschrittsanzeige des Prozesses.
 *
 * @author Juhu1705
 * @version 1.0
 * @category Distribution, GUI
 * @since 0.0.1
 */
public class ProgressIndicator {

    private static ProgressIndicator instance;
    private int fProgressMax = 0, fProgressValue = 0, aProgressMax = 0, aProgressValue = 0;

    protected ProgressIndicator() {
        // References.LOGGER.info(p0 + "|" + p1);
        instance = this;
    }

    public static ProgressIndicator getInstance() {
        return instance == null ? new ProgressIndicator() : instance;
    }

    public ProgressIndicator addfProgressValue(int value) {
        return this.setfProgressValue(this.fProgressValue + value);
    }

    public ProgressIndicator addaProgressValue(int value) {
        return this.setaProgressValue(this.aProgressValue + value);
    }

    /**
     * @return the fProgressMax
     */
    public int getfProgressMax() {
        return fProgressMax;
    }

    public ProgressIndicator setfProgressMax(int value) {
        this.fProgressMax = value;
        return this;
    }

    /**
     * @return the fProgressValue
     */
    public int getfProgressValue() {
        return fProgressValue;
    }

    public ProgressIndicator setfProgressValue(int value) {
        this.fProgressValue = value;

        EventManager.getInstance().triggerEvent(new ProgressUpdateEvent(this.fProgressValue, this.fProgressMax));

        return this;
    }

    /**
     * @return the aProgressMax
     */
    public int getaProgressMax() {
        return aProgressMax;
    }

    public ProgressIndicator setaProgressMax(int value) {
        this.aProgressMax = value;
        return this;
    }

    /**
     * @return the aProgressValue
     */
    public int getaProgressValue() {
        return aProgressValue;
    }

    public ProgressIndicator setaProgressValue(int value) {
        this.aProgressValue = value;

        PartProgress.getInstance().setProgress((double) this.aProgressValue / (double) this.aProgressMax);

        return this;
    }

}
