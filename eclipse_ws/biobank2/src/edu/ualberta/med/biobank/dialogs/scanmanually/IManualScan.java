package edu.ualberta.med.biobank.dialogs.scanmanually;

import java.util.Map;
import java.util.Set;

import org.eclipse.swt.widgets.Shell;

public interface IManualScan {

    public Map<String, String> getInventoryIds(Shell parentShell, Set<String> labels,
        Map<String, String> existingInventoryIdsByLabel);

}
