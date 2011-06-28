package edu.ualberta.med.biobank.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseWidget;

public class SelectMultipleWidget extends BgcBaseWidget {

    private HashMap<String, Button> checkBoxes;

    public SelectMultipleWidget(Composite parent, int style, String[] values,
        SelectionListener listener) {
        super(parent, style | SWT.BORDER);

        if (values == null)
            return;

        GridLayout layout = new GridLayout(1, false);
        setLayout(layout);
        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        checkBoxes = new HashMap<String, Button>();

        for (String value : values) {
            Button b = new Button(this, SWT.CHECK);
            b.setText(value);
            b.addSelectionListener(listener);
            checkBoxes.put(value, b);
        }
    }

    public void setSelections(String[] values) {
        if (values == null)
            return;

        List<String> valuesToSelect = Arrays.asList(values);
        for (String value : checkBoxes.keySet()) {
            Button b = checkBoxes.get(value);
            if (b == null)
                continue;
            b.setSelection(valuesToSelect.contains(value));
        }
    }

    public String[] getSelections() {
        ArrayList<String> al = new ArrayList<String>();
        for (Button checkBox : checkBoxes.values()) {
            if (!checkBox.getSelection())
                continue;
            al.add(checkBox.getText());
        }
        return al.toArray(new String[0]);
    }

}
