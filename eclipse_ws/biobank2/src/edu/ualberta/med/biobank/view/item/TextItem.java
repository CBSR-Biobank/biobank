package edu.ualberta.med.biobank.view.item;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.event.HandlerRegistration;
import edu.ualberta.med.biobank.event.HasValue;
import edu.ualberta.med.biobank.event.ValueChangeEvent;
import edu.ualberta.med.biobank.event.ValueChangeHandler;

public class TextItem implements HasValue<String> {
    private final Text text;
    private final List<ValueChangeHandler<String>> valueChangeHandlers = new ArrayList<ValueChangeHandler<String>>();
    private final ModifyListener modifyListener = new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent e) {
            if (fireEvents) {
                // TODO: send actual event object
                notifyValueChangeHandlers(null);
            }
        }
    };
    private boolean fireEvents = true;

    public TextItem(Text text) {
        this.text = text;

        text.addModifyListener(modifyListener);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(
        ValueChangeHandler<String> handler) {
        valueChangeHandlers.add(handler);
        return new HandlerRegistrationImpl(this, handler);
    }

    @Override
    public String getValue() {
        return text.getText();
    }

    @Override
    public void setValue(String value) {
        text.setText(value);
    }

    @Override
    public void setValue(String value, boolean fireEvents) {
        this.fireEvents = fireEvents;
        setValue(value);
        this.fireEvents = true;
    }

    private void notifyValueChangeHandlers(ValueChangeEvent<String> event) {
        for (ValueChangeHandler<String> valueChangeHandler : valueChangeHandlers) {
            valueChangeHandler.onValueChange(event);
        }
    }

    private static class HandlerRegistrationImpl implements HandlerRegistration {
        private final TextItem textItem;
        private final ValueChangeHandler<String> handler;

        public HandlerRegistrationImpl(TextItem textItem,
            ValueChangeHandler<String> handler) {
            this.textItem = textItem;
            this.handler = handler;
        }

        @Override
        public void removeHandler() {
            textItem.valueChangeHandlers.remove(handler);
        }
    }
}
