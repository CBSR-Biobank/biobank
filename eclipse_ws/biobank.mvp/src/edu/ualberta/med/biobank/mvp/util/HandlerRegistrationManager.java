package edu.ualberta.med.biobank.mvp.util;

import java.util.ArrayList;
import java.util.List;

import com.google.web.bindery.event.shared.HandlerRegistration;

public class HandlerRegistrationManager {
    private final List<HandlerRegistration> handlerRegistrations =
        new ArrayList<HandlerRegistration>();

    public void add(HandlerRegistration handlerRegistration) {
        handlerRegistrations.add(handlerRegistration);
    }

    public void clear() {
        for (HandlerRegistration handlerRegistration : handlerRegistrations) {
            handlerRegistration.removeHandler();
        }
        handlerRegistrations.clear();
    }
}
