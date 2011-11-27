package edu.ualberta.med.biobank.mvp.util;

import java.util.ArrayList;
import java.util.List;

import com.google.web.bindery.event.shared.HandlerRegistration;
import com.pietschy.gwt.pectin.client.binding.Disposable;

public class HandlerRegManager implements Disposable {
    private final List<HandlerRegistration> handlerRegistrations =
        new ArrayList<HandlerRegistration>();

    public void add(HandlerRegistration handlerRegistration) {
        handlerRegistrations.add(handlerRegistration);
    }

    public void dispose() {
        for (HandlerRegistration handlerRegistration : handlerRegistrations) {
            handlerRegistration.removeHandler();
        }
        handlerRegistrations.clear();
    }
}
