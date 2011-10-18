package edu.ualberta.med.biobank.mvp.view.item;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;

import edu.ualberta.med.biobank.mvp.event.SimpleValueChangeEvent;

public class TextItem implements HasValue<String> {
    private final static String DEFAULT_VALUE = ""; //$NON-NLS-1$
    private final HandlerManager handlerManager = new HandlerManager(this);
    private final ModifyListener modifyListener = new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent e) {
            if (fireEvents) {
                String value = getValue();
                handlerManager.fireEvent(new SimpleValueChangeEvent<String>(
                    value));
            }
        }
    };
    private Text text;
    private String value = DEFAULT_VALUE; // track value while Widget not bound
    private boolean fireEvents = true;

    public synchronized void setText(Text text) {
        unbindOldText();

        this.text = text;
        setValue(value);
        text.addModifyListener(modifyListener);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(
        ValueChangeHandler<String> handler) {
        return handlerManager.addHandler(ValueChangeEvent.getType(), handler);
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        handlerManager.fireEvent(event);
    }

    @Override
    public String getValue() {
        return text != null ? text.getText() : value;
    }

    @Override
    public void setValue(String value) {
        this.value = value != null ? value : DEFAULT_VALUE;

        if (text != null) {
            text.setText(value);
        }
    }

    @Override
    public void setValue(String value, boolean fireEvents) {
        this.fireEvents = fireEvents;
        setValue(value);
        this.fireEvents = true;
    }

    private void unbindOldText() {
        if (text != null) {
            text.removeModifyListener(modifyListener);
        }
    }
}
