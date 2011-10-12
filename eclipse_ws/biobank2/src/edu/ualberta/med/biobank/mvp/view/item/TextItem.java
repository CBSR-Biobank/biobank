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
    private boolean fireEvents = true;

    public void setText(Text text) {
        removeText();
        this.text = text;
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

    private void removeText() {
        if (text != null) {
            this.text.removeModifyListener(modifyListener);
        }
    }
}
