
package edu.ualberta.med.biobank.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.scanlib.ScanLib;
import edu.ualberta.med.scanlib.ScanLibFactory;

public class SelectDefaultScannerHandler extends AbstractHandler implements
    IHandler {

    public Object execute(ExecutionEvent event) throws ExecutionException {
        ScanLib scanLib = ScanLibFactory.getScanLib();
        scanLib.slSelectSourceAsDefault();
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setFocus();
        return null;
    }

}
