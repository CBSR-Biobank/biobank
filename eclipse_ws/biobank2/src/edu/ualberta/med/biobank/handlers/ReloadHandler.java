package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.forms.BiobankFormBase;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;

public class ReloadHandler extends AbstractHandler implements IHandler {
    private static final I18n i18n = I18nFactory.getI18n(ReloadHandler.class);

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            @SuppressWarnings("nls")
            @Override
            public void run() {
                try {
                    ((BiobankFormBase) PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow().getActivePage()
                        .getActiveEditor()).reload();
                } catch (Exception e) {
                    BgcPlugin.openAsyncError(
                        // dialog title
                        i18n.tr("Reload error"),
                        // dialog message
                        i18n.tr("An error occurred while reloading the form."));
                }
            }
        });
        return null;
    }
}
