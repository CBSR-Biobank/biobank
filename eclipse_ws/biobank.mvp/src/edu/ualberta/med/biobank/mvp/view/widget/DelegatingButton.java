package edu.ualberta.med.biobank.mvp.view.widget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

import edu.ualberta.med.biobank.mvp.user.ui.HasButton;

public class DelegatingButton implements HasButton {
    private final HandlerManager handlerManager = new HandlerManager(this);
    private final ClickMonitor clickMonitor = new ClickMonitor();
    private HandlerRegistration handlerRegistration;
    private HasButton button;
    private boolean enabled = true;

    public synchronized void setDelegate(HasButton button) {
        removeOldHandler();
        this.button = button;
        button.setEnabled(enabled);
        handlerRegistration = button.addClickHandler(clickMonitor);
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return handlerManager.addHandler(ClickEvent.getType(), handler);
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        handlerManager.fireEvent(event);
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        if (button != null) {
            button.setEnabled(enabled);
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    private void removeOldHandler() {
        if (handlerRegistration != null) {
            handlerRegistration.removeHandler();
        }
    }

    private class ClickMonitor implements ClickHandler {
        @Override
        public void onClick(ClickEvent event) {
            fireEvent(event);
        }
    }
}
