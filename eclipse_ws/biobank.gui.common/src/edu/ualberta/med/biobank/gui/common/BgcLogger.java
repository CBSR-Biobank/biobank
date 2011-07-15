package edu.ualberta.med.biobank.gui.common;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Log into Eclipse RCP logs (see Logs view) and .logs file
 */
public class BgcLogger {

    private static Map<String, BgcLogger> loggers = new HashMap<String, BgcLogger>();

    private String name;

    public BgcLogger(String name) {
        this.name = name;
    }

    public static BgcLogger getLogger(String name) {
        BgcLogger logger = loggers.get(name);
        if (logger == null) {
            logger = new BgcLogger(name);
            loggers.put(name, logger);
        }
        return logger;
    }

    public void error(String message) {
        error(message, null);
    }

    public void error(String message, Throwable e) {
        addRcpLogStatus(IStatus.ERROR, message, e);
    }

    public void debug(String message) {
        debug(message, null);
    }

    public void debug(String message, Throwable e) {
        addRcpLogStatus(IStatus.INFO, message, e);
    }

    public void addRcpLogStatus(int severity, String message, Throwable e) {
        ILog rcpLogger = BgcPlugin.getDefault().getLog();
        IStatus status = new Status(severity, BgcPlugin.PLUGIN_ID, name + ": " //$NON-NLS-1$
            + message, e);
        rcpLogger.log(status);
    }

    public void addRcpLogStatus(IStatus status) {
        ILog rcpLogger = BgcPlugin.getDefault().getLog();
        rcpLogger.log(status);
    }

}
