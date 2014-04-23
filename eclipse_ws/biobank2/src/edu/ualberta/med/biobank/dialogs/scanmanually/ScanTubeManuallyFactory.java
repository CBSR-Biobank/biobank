package edu.ualberta.med.biobank.dialogs.scanmanually;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.mvp.view.DialogView.Dialog;

public class ScanTubeManuallyFactory implements IManualScan {

    /**
     * Displays a dialog that accepts a single tube's inventory ID and returns the results.
     * 
     * @param parentShell the parent SWT shell
     * @param labels the labels that the user should be prompted for. The order is important.
     * @param existingInventoryIdsByLabel a map of inventory IDs that the user should not enter. The
     *            key for the map is the position label where this inventory ID is present.
     * @return A map of the inventory IDs entered by the user. The key is the label the inventory ID
     *         corresponds to.
     */
    @Override
    public Map<String, String> getInventoryIds(Shell parentShell, Set<String> labels,
        Map<String, String> existingInventoryIdsByLabel) {
        ScanTubeManually dialog = new ScanTubeManually(parentShell, labels, existingInventoryIdsByLabel);
        dialog.create();

        Map<String, String> result = new HashMap<String, String>(0);
        if (dialog.open() == Dialog.OK) {
            result.putAll(dialog.getInventoryId());
        }
        return result;
    }

}
