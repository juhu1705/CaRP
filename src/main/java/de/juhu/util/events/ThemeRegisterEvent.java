package de.juhu.util.events;

import de.noisruker.event.events.Event;

import java.util.ArrayList;
import java.util.List;

public class ThemeRegisterEvent extends Event {

    public ThemeRegisterEvent() {
        super("ThemeRegistrationEvent");
        super.setResult(new ArrayList<String>());
    }

    public void registerNewTheme(String name) {
        if (super.getResult() instanceof List) {
            ((List) super.getResult()).add(name);
        }
    }
}
