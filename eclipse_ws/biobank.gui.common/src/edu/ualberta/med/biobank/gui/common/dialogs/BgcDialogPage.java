package edu.ualberta.med.biobank.gui.common.dialogs;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public abstract class BgcDialogPage extends DialogPage {

    private Point size = null;
    protected BgcDialogWithPages dialog;

    public BgcDialogPage(BgcDialogWithPages dialog) {
        this.dialog = dialog;
    }

    public void setSize(Point uiSize) {
        Control control = getControl();
        if (control != null) {
            control.setSize(uiSize);
            size = uiSize;
        }
    }

    public Point computeSize() {
        if (size != null) {
            return size;
        }
        Control control = getControl();
        if (control != null) {
            size = control.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
            return size;
        }
        return new Point(0, 0);
    }

    public abstract void runAddAction();

    @Override
    public Shell getShell() {
        // need this to avoid a org.eclipse.swt.SWTException: Invalid thread
        // access
        return dialog.getShell();
    }

}
