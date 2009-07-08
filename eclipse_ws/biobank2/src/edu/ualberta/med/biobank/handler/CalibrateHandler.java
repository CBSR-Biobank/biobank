
package edu.ualberta.med.biobank.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.dialogs.CalibrationDialog;

public class CalibrateHandler extends AbstractHandler implements IHandler {

    public Object execute(ExecutionEvent event) throws ExecutionException {
        CalibrationDialog dlg = new CalibrationDialog(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
        dlg.open();
        return null;
    }

}
