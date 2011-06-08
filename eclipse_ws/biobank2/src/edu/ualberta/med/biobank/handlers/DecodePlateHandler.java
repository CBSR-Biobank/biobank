package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.forms.DecodePlateForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.gui.common.BiobankLogger;

public class DecodePlateHandler extends AbstractHandler implements IHandler {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(DecodePlateHandler.class.getName());

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        FormInput input = new FormInput(null, "Decode Plate");
        try {
            return PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getActivePage().openEditor(input, DecodePlateForm.ID, false);
        } catch (PartInitException e) {
            logger.error("Can't open form with id " + DecodePlateForm.ID, e);
            return null;
        }
    }

}
