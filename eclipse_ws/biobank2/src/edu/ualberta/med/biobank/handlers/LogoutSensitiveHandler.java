package edu.ualberta.med.biobank.handlers;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.ui.ISourceProviderListener;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.gui.common.BgcSessionState;

public abstract class LogoutSensitiveHandler extends AbstractHandler {
    protected Boolean createAllowed = null;

    public LogoutSensitiveHandler() {
        super();
        BiobankPlugin.getSessionStateSourceProvider()
            .addSourceProviderListener(new ISourceProviderListener() {

                @Override
                public void sourceChanged(int sourcePriority,
                    @SuppressWarnings("rawtypes") Map sourceValuesByName) {
                }

                @Override
                public void sourceChanged(int sourcePriority,
                    String sourceName, Object sourceValue) {
                    if (sourceName
                        .equals(BgcSessionState.SESSION_STATE_SOURCE_NAME)
                        && sourceValue.equals(BgcSessionState.LOGGED_OUT))
                        createAllowed = null;
                }
            });
    }
}
