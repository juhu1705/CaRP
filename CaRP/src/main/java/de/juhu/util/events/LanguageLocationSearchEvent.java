package de.juhu.util.events;

import de.noisruker.event.events.Event;

public class LanguageLocationSearchEvent extends Event {

    private final String name;

    public LanguageLocationSearchEvent(String name) {
        super("search language");
        this.name = name;
    }

    public String getLanguageName() {
        return this.name;
    }

    /**
     * @param location The location to search the language file.
     */
    public void setLocation(String location) {
        super.setResult(location);
    }
}
