package edu.ualberta.med.biobank.mvp.view.item;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.mvp.event.SimpleValueChangeEvent;

public class TextItem extends ValidationItem<String> {
    private final static String EMPTY_STRING = ""; //$NON-NLS-1$
    final ModifyListener modifyListener = new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent e) {
            if (fireEvents) {
                String value = getValue();
                fireEvent(new SimpleValueChangeEvent<String>(value));
            }
        }
    };
    private Text text;
    private String value; // track value while Text not set
    private boolean fireEvents = true;

    public synchronized void setText(Text text) {
        unbindOldText();

        this.text = text;
        setValue(value);
        text.addModifyListener(modifyListener);
        // TODO: listen for disposal?
    }

    @Override
    public String getValue() {
        if (text != null) {
            return !text.getText().isEmpty() ? text.getText() : null;
        }
        return value;
    }

    @Override
    public void setValue(String value, boolean fireEvents) {
        this.value = value;

        if (text != null) {
            this.fireEvents = fireEvents;
            text.setText(value != null ? value : EMPTY_STRING);
            this.fireEvents = true;
        }
    }

    private void unbindOldText() {
        if (text != null) {
            text.removeModifyListener(modifyListener);
        }
    }
}
