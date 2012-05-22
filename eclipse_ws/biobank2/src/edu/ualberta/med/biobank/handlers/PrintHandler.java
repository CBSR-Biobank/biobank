package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.forms.linkassign.AbstractSpecimenAdminForm;

public class PrintHandler extends AbstractHandler implements IHandler {
    private static final I18n i18n = I18nFactory
        .getI18n(PrintHandler.class);

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        AbstractSpecimenAdminForm form =
            (AbstractSpecimenAdminForm) HandlerUtil
                .getActiveEditor(event);
        @SuppressWarnings("nls")
        boolean doPrint = MessageDialog.openQuestion(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(),
            // dialog title
            i18n.tr("Print"),
            // dialog message
            i18n.tr("Do you want to print information ?"));
        if (doPrint) {
            form.print();
        }

        return null;
    }
}
