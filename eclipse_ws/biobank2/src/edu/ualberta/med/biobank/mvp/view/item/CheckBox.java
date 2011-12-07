package edu.ualberta.med.biobank.mvp.view.item;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class CheckBox extends AbstractValueField<Boolean> {
    private final SelectionListener selectionListener = new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            boolean value = button.getSelection();
            setValue(value, true);
        }
    };
    private Button button;

    public Button create(Composite parent) {
        Button button = new Button(parent, SWT.CHECK);

        setButton(button);

        return button;
    }

    public synchronized void setButton(Button button) {
        unbindOldButton();

        this.button = button;
        update();

        forwardEnabled(button);
        forwardVisible(button);

        button.addSelectionListener(selectionListener);
        // TODO: listen for disposal?
    }

    @Override
    protected void update() {
        if (button != null) {
            button.removeSelectionListener(selectionListener);

            Boolean value = getValue();
            button.setSelection(value);

            button.addSelectionListener(selectionListener);
        }
    }

    private void unbindOldButton() {
        if (button != null) {
            button.removeSelectionListener(selectionListener);

            unforwardEnabled(button);
            unforwardVisible(button);
        }
    }
}
