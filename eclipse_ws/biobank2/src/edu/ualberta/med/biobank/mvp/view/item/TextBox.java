package edu.ualberta.med.biobank.mvp.view.item;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;

public class TextBox extends AbstractValueField<String> {
    private final static String EMPTY_STRING = ""; //$NON-NLS-1$
    private final ModifyListener modifyListener = new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent e) {
            String value = !text.getText().isEmpty() ? text.getText() : null;
            setValue(value, true);
        }
    };
    private Text text;

    public synchronized void setText(Text text) {
        unbindOldText();

        this.text = text;
        update();
        text.addModifyListener(modifyListener);
        // TODO: listen for disposal?
    }

    @Override
    protected void update() {
        if (text != null) {
            text.removeModifyListener(modifyListener);

            String value = getValue();
            text.setText(value != null ? value : EMPTY_STRING);

            text.addModifyListener(modifyListener);
        }
    }

    private void unbindOldText() {
        if (text != null) {
            text.removeModifyListener(modifyListener);
        }
    }
}
