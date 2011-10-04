package edu.ualberta.med.biobank.view.component;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import edu.ualberta.med.biobank.event.ClickEvent;
import edu.ualberta.med.biobank.event.ClickHandler;
import edu.ualberta.med.biobank.event.HasClickHandlers;

public class ButtonWrapper implements HasClickHandlers {
    private final List<ClickHandler> clickHandlers = new ArrayList<ClickHandler>();
    private final Listener listener = new Listener() {
        @Override
        public void handleEvent(Event event) {
            switch (event.type) {
            case SWT.Selection:
                // TODO: use actual event
                notifyClickHandlers(null);
                break;
            }
        }
    };

    public ButtonWrapper(Button button) {
        button.addListener(SWT.Selection, listener);
    }

    @Override
    public void addClickHandler(ClickHandler clickHandler) {
        clickHandlers.add(clickHandler);
    }

    private void notifyClickHandlers(ClickEvent event) {
        for (ClickHandler clickHandler : clickHandlers) {
            clickHandler.onClick(event);
        }
    }
}
