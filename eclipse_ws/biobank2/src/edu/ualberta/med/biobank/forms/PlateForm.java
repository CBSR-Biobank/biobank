package edu.ualberta.med.biobank.forms;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Button;

import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.widgets.grids.cell.PalletWell;
import edu.ualberta.med.biobank.widgets.grids.cell.UICellStatus;
import edu.ualberta.med.scannerconfig.ScannerConfigPlugin;

public abstract class PlateForm extends BiobankViewForm {

    protected Button scanButton;

    protected Map<RowColPos, PalletWell> cells;

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

    /**
     * go through cells retrieved from scan, set status and update the types
     * combos components
     */
    protected void processScanResult() {
        Map<Integer, Integer> typesRows = new HashMap<Integer, Integer>();
        for (RowColPos rcp : cells.keySet()) {
            Integer typesRowsCount = typesRows.get(rcp.getRow());
            if (typesRowsCount == null) {
                typesRowsCount = 0;
            }
            PalletWell cell = null;
            cell = cells.get(rcp);
            processCellStatus(cell);
            if (PalletWell.hasValue(cell)) {
                typesRowsCount++;
                typesRows.put(rcp.getRow(), typesRowsCount);
            }
        }
    }

    /**
     * Process the cell: apply a status and set correct information
     */
    protected void processCellStatus(PalletWell cell) {
        if (cell != null) {
            cell.setStatus((cell.getValue() != null) ? UICellStatus.FILLED
                : UICellStatus.EMPTY);
            cell.setTitle(cell.getValue());
        }
    }

}
