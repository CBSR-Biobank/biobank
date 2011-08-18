package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.forms.DecodePlateForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.gui.common.BgcLogger;

public class DecodePlateHandler extends AbstractHandler implements IHandler {

    private static BgcLogger logger = BgcLogger
        .getLogger(DecodePlateHandler.class.getName());

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        FormInput input = new FormInput(null, Messages.DecodePlateHandler_decode_label);
        try {
            return PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getActivePage().openEditor(input, DecodePlateForm.ID, false);
        } catch (PartInitException e) {
            logger.error("Can't open form with id " + DecodePlateForm.ID, e); //$NON-NLS-1$
            return null;
        }
    }

}
