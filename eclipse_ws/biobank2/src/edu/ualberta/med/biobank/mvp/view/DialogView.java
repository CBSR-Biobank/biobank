package edu.ualberta.med.biobank.mvp.view;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class DialogView {
    private TitleAreaDialog dialog;

    // TODO: separate open() method for DialogView?
    public void create(Composite parent) {
        dialog = new Dialog(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell());
        if (dialog.open() == Dialog.OK) {
        }
    }

    public void close() {
        dialog.close();
    }

    public static class Dialog extends TitleAreaDialog {
        public Dialog(Shell parentShell) {
            super(parentShell);
        }
    }
}
