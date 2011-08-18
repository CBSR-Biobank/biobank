package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.ualberta.med.biobank.forms.linkassign.AbstractSpecimenAdminForm;

public class PrintHandler extends AbstractHandler implements IHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        AbstractSpecimenAdminForm form = (AbstractSpecimenAdminForm) HandlerUtil
            .getActiveEditor(event);
        boolean doPrint = MessageDialog.openQuestion(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), Messages.PrintHandler_print_dialog_title,
            Messages.PrintHandler_print_dialog_question);
        if (doPrint) {
            form.print();
        }

        return null;
    }
}
