package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.WorkbenchException;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.rcp.perspective.ReportsPerspective;

public class ReportsHandler extends LogoutSensitiveHandler {
    private static final I18n i18n = I18nFactory.getI18n(ReportsHandler.class);

    @SuppressWarnings("nls")
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbench workbench = BiobankPlugin.getDefault().getWorkbench();
        try {
            if (workbench.getActiveWorkbenchWindow().getActivePage()
                .closeAllEditors(true))
                workbench.showPerspective(ReportsPerspective.ID,
                    workbench.getActiveWorkbenchWindow());
        } catch (WorkbenchException e) {
            throw new ExecutionException(
                // exception message
                i18n.tr("Perspective could not be initialized"), e);
        }
        return null;

    }
}
