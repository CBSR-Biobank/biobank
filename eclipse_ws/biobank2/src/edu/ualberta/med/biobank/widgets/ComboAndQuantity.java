
package edu.ualberta.med.biobank.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.springframework.util.Assert;

public class ComboAndQuantity extends BiobankWidget {

    private Combo valuesCombo;

    private Combo quantitiesCombo;

    public ComboAndQuantity(Composite parent, int style) {
        super(parent, style | SWT.BORDER);

        GridLayout layout = new GridLayout(3, false);
        layout.horizontalSpacing = 10;
        setLayout(layout);
        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        valuesCombo = new Combo(this, SWT.NONE);
        Label l = new Label(this, SWT.NONE);
        l.setText("Quantity:");
        quantitiesCombo = new Combo(this, SWT.NONE);
    }

    public void addValues(String [] values, int min, int max, int step) {
        Assert.isTrue(max > min);
        for (String value : values) {
            valuesCombo.add(value);
        }

        for (int i = min; i <= max; i += step) {
            quantitiesCombo.add(String.format("%d", i));
        }
    }

    public void setText(String value, int quantity) {
        valuesCombo.setText(value);
        quantitiesCombo.setText(String.format("%d", quantity));
    }

    public String getText() {
        if ((valuesCombo.getText().length() != 1)
            || (valuesCombo.getText().length() != 1)) return null;
        return valuesCombo.getText() + " " + quantitiesCombo.getText();
    }

    public void addSelectionListener(SelectionListener listener) {
        valuesCombo.addSelectionListener(listener);
        quantitiesCombo.addSelectionListener(listener);
    }

    public void addModifyListener(ModifyListener listener) {
        valuesCombo.addModifyListener(listener);
        quantitiesCombo.addModifyListener(listener);
    }
}
