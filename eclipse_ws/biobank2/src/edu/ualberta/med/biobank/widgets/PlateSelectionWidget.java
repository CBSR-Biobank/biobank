package edu.ualberta.med.biobank.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseWidget;
import edu.ualberta.med.biobank.gui.common.widgets.BgcEntryFormWidgetListener;
import edu.ualberta.med.biobank.gui.common.widgets.MultiSelectEvent;
import edu.ualberta.med.scannerconfig.ScannerConfigPlugin;

public class PlateSelectionWidget extends BgcBaseWidget {
    private static final I18n i18n = I18nFactory
        .getI18n(PlateSelectionWidget.class);

    private final Map<Integer, Button> plateButtons;

    IPropertyChangeListener propertyListener = new IPropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent event) {
            boolean layoutRequired = false;

            for (int i = 0; i < edu.ualberta.med.scannerconfig.preferences.PreferenceConstants.SCANNER_PALLET_ENABLED.length; ++i) {
                if (!event
                    .getProperty()
                    .equals(
                        edu.ualberta.med.scannerconfig.preferences.PreferenceConstants.SCANNER_PALLET_ENABLED[i]))
                    continue;

                int plateId = i + 1;
                Button b = plateButtons.get(plateId);
                boolean visible =
                    ScannerConfigPlugin.getDefault().getPlateEnabled(plateId);
                b.setVisible(visible);
                GridData gd = (GridData) b.getLayoutData();
                gd.exclude = !visible;
                layoutRequired = true;
            }

            if (layoutRequired) {
                getParent().layout(true);
            }
        }
    };

    @SuppressWarnings("nls")
    public PlateSelectionWidget(Composite parent, int style) {
        super(parent, style);

        GridLayout layout =
            new GridLayout(ScannerConfigPlugin.getPlatesMax() + 1, false);
        setLayout(layout);
        GridData gd = new GridData(SWT.LEFT, SWT.TOP, true, false);
        setLayoutData(gd);

        Label label = new Label(this, SWT.NONE);
        label.setText(i18n.tr("Select plate:"));
        plateButtons = new HashMap<Integer, Button>();

        List<Integer> enabledPlates = new ArrayList<Integer>();

        for (int i = 1, n = ScannerConfigPlugin.getPlatesMax(); i <= n; ++i) {
            Button b = new Button(this, SWT.RADIO);
            b.setText("PLATE " + i);
            boolean visible =
                ScannerConfigPlugin.getDefault().getPlateEnabled(i);
            b.setVisible(visible);
            gd = new GridData();
            gd.exclude = !visible;
            b.setLayoutData(gd);
            plateButtons.put(i, b);

            if (visible) {
                enabledPlates.add(i);
            }
        }

        if (enabledPlates.size() == 1) {
            plateButtons.get(enabledPlates.get(0)).setSelection(true);
        }

        ScannerConfigPlugin.getDefault().getPreferenceStore()
            .addPropertyChangeListener(propertyListener);

        addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent event) {
                ScannerConfigPlugin.getDefault().getPreferenceStore()
                    .removePropertyChangeListener(propertyListener);
            }
        });
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

    public void addPlateSelectionListener(SelectionListener listener) {
        for (Button b : plateButtons.values()) {
            b.addSelectionListener(listener);
        }
        return;
    }
}
