package edu.ualberta.med.biobank.handlers;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;

import edu.ualberta.med.biobank.gui.common.BiobankGuiCommonPlugin;

public class CbsrHelp extends AbstractHandler {

    public static final String CBSR_HELP_URL = "http://biosample.ca/index.php/help";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWebBrowser browser;
        try {
            browser = PlatformUI.getWorkbench().getBrowserSupport()
                .createBrowser(null);
            browser.openURL(new URL(CBSR_HELP_URL));
        } catch (PartInitException e) {
            BiobankGuiCommonPlugin.openAsyncError("Could not open web browser",
                e);
        } catch (MalformedURLException e) {
            BiobankGuiCommonPlugin.openAsyncError(
                "Could not open web browser URL", e);
        }
        return null;
    }
}
