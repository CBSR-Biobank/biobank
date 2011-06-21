package edu.ualberta.med.biobank.gui.common.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.gui.common.forms.BgcEntryForm;

public class BgcResetHandler extends AbstractHandler implements IHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            @Override
            public void run() {
                ((BgcEntryForm) PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow().getActivePage()
                    .getActiveEditor()).reset();
            }
        });
        return null;
    }
}
