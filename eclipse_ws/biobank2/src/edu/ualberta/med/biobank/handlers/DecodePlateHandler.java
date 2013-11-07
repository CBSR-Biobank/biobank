package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.scannerconfig.dialogs.DecodePlateDialog;

public class DecodePlateHandler extends AbstractHandler implements IHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        DecodePlateDialog dialog = new DecodePlateDialog(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
        dialog.open();
        return null;
    }

}
