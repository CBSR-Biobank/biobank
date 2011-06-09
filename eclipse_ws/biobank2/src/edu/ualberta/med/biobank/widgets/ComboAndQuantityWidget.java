package edu.ualberta.med.biobank.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseWidget;

public class ComboAndQuantityWidget extends BgcBaseWidget {

    private Combo valuesCombo;

    private BgcBaseText quantitiesText;

    public ComboAndQuantityWidget(Composite parent, int style) {
        super(parent, style | SWT.BORDER);

        GridLayout layout = new GridLayout(3, false);
        layout.horizontalSpacing = 10;
        setLayout(layout);
        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        valuesCombo = new Combo(this, SWT.NONE);
        Label l = new Label(this, SWT.NONE);
        l.setText("Quantity:");
        quantitiesText = new BgcBaseText(this, SWT.NONE);
    }

    public void addValues(String[] values) {
        for (String value : values) {
            valuesCombo.add(value);
        }
    }

    public void setText(String value, int quantity) {
        valuesCombo.setText(value);
        quantitiesText.setText(String.format("%d", quantity));
    }

    public String getText() {
        if ((valuesCombo.getText().length() != 1)
            || (valuesCombo.getText().length() != 1))
            return null;
        return valuesCombo.getText() + " " + quantitiesText.getText();
    }

    public void addSelectionListener(SelectionListener listener) {
        valuesCombo.addSelectionListener(listener);
    }

    public void addModifyListener(ModifyListener listener) {
        valuesCombo.addModifyListener(listener);
        quantitiesText.addModifyListener(listener);
    }
}
