package edu.ualberta.med.scannerconfig.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanLib;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanLibResult;

public class SelectSource extends AbstractHandler implements IHandler {

    private static final I18n i18n = I18nFactory.getI18n(SelectSource.class);

    @SuppressWarnings("nls")
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ScanLibResult result = ScanLib.getInstance().selectSourceAsDefault();

        if (result.getResultCode() != ScanLibResult.Result.SUCCESS) {
            BgcPlugin.openError(i18n.tr("Source Selection Error"),
                result.getMessage());
        }
        return null;
    }
}
