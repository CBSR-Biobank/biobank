package edu.ualberta.med.biobank.handlers;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.ui.ISourceProviderListener;

import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.LoginSessionState;

public abstract class LogoutSensitiveHandler extends AbstractHandler {
    protected Boolean allowed = null;

    public LogoutSensitiveHandler() {
        super();
        BgcPlugin.getLoginStateSourceProvider()
            .addSourceProviderListener(new ISourceProviderListener() {

                @SuppressWarnings("rawtypes")
                @Override
                public void sourceChanged(int sourcePriority,
                    Map sourceValuesByName) {
                }

                @Override
                public void sourceChanged(int sourcePriority,
                    String sourceName, Object sourceValue) {
                    if (sourceName
                        .equals(LoginSessionState.LOGIN_STATE_SOURCE_NAME)
                        && sourceValue.equals(LoginSessionState.LOGGED_OUT))
                        allowed = null;
                }
            });
    }
}
