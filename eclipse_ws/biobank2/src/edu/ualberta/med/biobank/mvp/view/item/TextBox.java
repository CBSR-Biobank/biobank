package edu.ualberta.med.biobank.mvp.view.item;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.common.util.StringUtil;

public class TextBox extends AbstractValueField<String> {
    private final static String EMPTY_STRING = StringUtil.EMPTY_STRING;
    private final ModifyListener modifyListener = new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent e) {
            String value = !text.getText().isEmpty() ? text.getText() : null;
            setValueInternal(value);
        }
    };
    private Text text;

    public synchronized void setText(Text text) {
        unbindOldText();

        this.text = text;
        updateGui();

        forwardEnabled(text);
        forwardVisible(text);

        text.addModifyListener(modifyListener);
        // TODO: listen for disposal?
    }

    @Override
    protected void updateGui() {
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

            unforwardEnabled(text);
            unforwardVisible(text);
        }
    }
}
