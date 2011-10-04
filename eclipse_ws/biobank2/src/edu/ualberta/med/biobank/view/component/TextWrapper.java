package edu.ualberta.med.biobank.view.component;

import java.awt.TextField;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.event.HandlerRegistration;
import edu.ualberta.med.biobank.event.HasValue;
import edu.ualberta.med.biobank.event.ValueChangeEvent;
import edu.ualberta.med.biobank.event.ValueChangeHandler;

public class TextWrapper implements HasValue<String> {
    private final TextField text;
    private final List<ValueChangeHandler<String>> valueChangeHandlers = new ArrayList<ValueChangeHandler<String>>();
    private final TextListener textListener = new TextListener() {
        @Override
        public void textValueChanged(TextEvent event) {
            if (fireEvents) {
                // TODO: send actual event object
                notifyValueChangeHandlers(null);
            }
        }
    };
    private boolean fireEvents = true;

    public TextWrapper(TextField text) {
        this.text = text;

        text.addTextListener(textListener);
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
        private final TextWrapper textWrapper;
        private final ValueChangeHandler<String> handler;

        public HandlerRegistrationImpl(TextWrapper textWrapper,
            ValueChangeHandler<String> handler) {
            this.textWrapper = textWrapper;
            this.handler = handler;
        }

        @Override
        public void removeHandler() {
            textWrapper.valueChangeHandlers.remove(handler);
        }
    }
}
