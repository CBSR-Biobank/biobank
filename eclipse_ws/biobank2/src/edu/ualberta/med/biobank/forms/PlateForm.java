package edu.ualberta.med.biobank.forms;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Button;

import edu.ualberta.med.scannerconfig.ScannerConfigPlugin;

public abstract class PlateForm extends BiobankViewForm {

    protected Button scanButton;

    protected IPropertyChangeListener propertyListener = new IPropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent event) {
            boolean plateEnabledChange = false;
            int plateEnabledCount = 0;

            for (int i = 0; i < edu.ualberta.med.scannerconfig.preferences.PreferenceConstants.SCANNER_PALLET_ENABLED.length; ++i) {
                if (!event
                    .getProperty()
                    .equals(
                        edu.ualberta.med.scannerconfig.preferences.PreferenceConstants.SCANNER_PALLET_ENABLED[i]))
                    continue;

                plateEnabledChange = true;
                int plateId = i + 1;
                if (ScannerConfigPlugin.getDefault().getPlateEnabled(plateId)) {
                    ++plateEnabledCount;
                }
            }

            if (plateEnabledChange) {
                scanButton.setEnabled(plateEnabledCount > 0);
            }
        }
    };

}
