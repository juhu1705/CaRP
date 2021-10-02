package de.juhu.distributor.events;

import de.noisruker.event.events.Event;

public class ProgressUpdateEvent extends Event {

    private final int value, max;

    public ProgressUpdateEvent(int value, int max) {
        super("progress_update");
        this.value = value;
        this.max = max;
    }

    public double getProgress() {
        if(this.value == -1)
            return this.value;
        return (double) this.value / (double) this.max;
    }

    public long getProgressPercent() {
        return Math.round(this.getProgress() * 100d);
    }

    public int getValue() {
        return this.value;
    }

    public int getMax() {
        return this.max;
    }

}
