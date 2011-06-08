package edu.ualberta.med.biobank.gui.common;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Log into Eclipse RCP logs (see Logs view) and .logs file
 */
public class BiobankLogger {

    private static Map<String, BiobankLogger> loggers = new HashMap<String, BiobankLogger>();

    private String name;

    public BiobankLogger(String name) {
        this.name = name;
    }

    public static BiobankLogger getLogger(String name) {
        BiobankLogger logger = loggers.get(name);
        if (logger == null) {
            logger = new BiobankLogger(name);
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
        ILog rcpLogger = BiobankGuiCommonPlugin.getDefault().getLog();
        IStatus status = new Status(severity, BiobankGuiCommonPlugin.PLUGIN_ID,
            name + ": " + message, e);
        rcpLogger.log(status);
    }

}
