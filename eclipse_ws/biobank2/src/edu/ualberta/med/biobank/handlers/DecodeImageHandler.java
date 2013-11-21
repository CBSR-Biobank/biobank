package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.scannerconfig.dialogs.DecodeImageDialog;

public class DecodeImageHandler extends AbstractHandler implements IHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        DecodeImageDialog dialog = new DecodeImageDialog(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
        dialog.open();
        return null;
    }

}
