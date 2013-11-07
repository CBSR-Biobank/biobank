package edu.ualberta.med.biobank.gui.common.dialogs;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.gui.common.BgcPlugin;

/**
 * Creates a dialog that remembers its size and position.
 * 
 * @author nelson
 * 
 */
public abstract class PersistedDialog extends BgcBaseDialog {

    public PersistedDialog(Shell parentShell) {
        super(parentShell);
    }

    protected IDialogSettings getDialogSettings() {
        return BgcPlugin.getDefault().getDialogSettings();
    }

    @Override
    protected IDialogSettings getDialogBoundsSettings() {
        return getDialogSettings();
    }

}
