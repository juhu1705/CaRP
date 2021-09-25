package de.juhu.util.events;

import de.noisruker.event.events.Event;

import java.util.ArrayList;
import java.util.List;

public class LanguageRegisterEvent extends Event {

    public LanguageRegisterEvent() {
        super("LanguageRegistrationEvent");
        super.setResult(new ArrayList<String>());
    }

    public void registerNewLanguage(String name) {
        if (super.getResult() instanceof List) {
            ((List) super.getResult()).add(name);
        }
    }
}
