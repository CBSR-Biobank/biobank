package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.forms.ScanPlateForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.gui.common.BgcLogger;

public class ScanPlateHandler extends AbstractHandler implements IHandler {
    private static final I18n i18n = I18nFactory
        .getI18n(ScanPlateHandler.class);

    private static BgcLogger logger = BgcLogger
        .getLogger(ScanPlateHandler.class.getName());

    @SuppressWarnings("nls")
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        FormInput input = new FormInput(null,
            // tooltip
            i18n.tr("Scan Plate"));
        try {
            return PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getActivePage().openEditor(input, ScanPlateForm.ID, false);
        } catch (PartInitException e) {
            logger.error("Can't open form with id " + ScanPlateForm.ID, e);
            return null;
        }
    }
}
