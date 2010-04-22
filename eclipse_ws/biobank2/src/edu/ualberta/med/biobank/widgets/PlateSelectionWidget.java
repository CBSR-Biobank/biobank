package edu.ualberta.med.biobank.widgets;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import edu.ualberta.med.scannerconfig.ScannerConfigPlugin;

public class PlateSelectionWidget extends BiobankWidget {

    private Map<Integer, Button> plateButtons;

    public PlateSelectionWidget(Composite parent, int style) {
        super(parent, style);

        GridLayout layout = new GridLayout(
            ScannerConfigPlugin.getPlatesMax() + 1, false);
        setLayout(layout);

        Label label = new Label(this, SWT.NONE);
        label.setText("Select plate:");
        plateButtons = new HashMap<Integer, Button>();

        for (int i = 1, n = ScannerConfigPlugin.getPlatesMax(); i <= n; ++i) {
            Button b = new Button(this, SWT.RADIO);
            b.setText("Plate" + i);
            b.setVisible(ScannerConfigPlugin.getDefault().getPlateEnabled(i));
            plateButtons.put(i, b);
        }
    }

    public void reload() {
        for (int i = 1, n = ScannerConfigPlugin.getPlatesMax(); i <= n; ++i) {
            Button b = plateButtons.get(i);
            boolean visible = ScannerConfigPlugin.getDefault().getPlateEnabled(
                i);
            b.setVisible(visible);
            GridData gd = (GridData) b.getLayoutData();
            gd.exclude = !visible;
        }
        layout(true);
    }

    public Integer getSelectedPlate() {
        Integer selectedId = null;
        for (Integer plateId : plateButtons.keySet()) {
            if (plateButtons.get(plateId).getSelection()) {
                selectedId = plateId;
                break;
            }
        }
        return selectedId;
    }
}
