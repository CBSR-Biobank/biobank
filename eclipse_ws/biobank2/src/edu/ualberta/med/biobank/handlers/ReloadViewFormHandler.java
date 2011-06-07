package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.gui.common.BiobankGuiCommonPlugin;
import edu.ualberta.med.biobank.forms.BiobankViewForm;

public class ReloadViewFormHandler extends AbstractHandler implements IHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            @Override
            public void run() {
                try {
                    ((BiobankViewForm) PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow().getActivePage()
                        .getActiveEditor()).reload();
                } catch (Exception e) {
                    BiobankGuiCommonPlugin.openAsyncError("Reload error",
                        "An error occurred while reloading the form.");
                }
            }
        });
        return null;
    }
}
