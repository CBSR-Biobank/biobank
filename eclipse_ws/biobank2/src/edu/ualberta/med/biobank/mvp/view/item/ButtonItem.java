package edu.ualberta.med.biobank.mvp.view.item;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Button;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

import edu.ualberta.med.biobank.mvp.event.EclipseClickEvent;

public class ButtonItem implements HasClickHandlers {
    private final HandlerManager handlerManager = new HandlerManager(this);
    private final MouseListener mouseListener = new MouseListener() {
        @Override
        public void mouseDoubleClick(MouseEvent e) {
            handlerManager.fireEvent(new EclipseClickEvent());
        }

        @Override
        public void mouseDown(MouseEvent e) {
        }

        @Override
        public void mouseUp(MouseEvent e) {
            handlerManager.fireEvent(new EclipseClickEvent());
        }
    };
    private Button button;

    public synchronized void setButtonItem(Button button) {
        unbindOldButton();
        this.button = button;
        button.addMouseListener(mouseListener);
    }

    public Button getButton() {
        return button;
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler clickHandler) {
        return handlerManager.addHandler(ClickEvent.getType(), clickHandler);
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        handlerManager.fireEvent(event);
    }

    private void unbindOldButton() {
        if (button != null) {
            button.removeMouseListener(mouseListener);
        }
    }
}
