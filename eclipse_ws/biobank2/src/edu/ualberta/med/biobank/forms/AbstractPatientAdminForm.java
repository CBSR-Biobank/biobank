package edu.ualberta.med.biobank.forms;

import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.logs.ActivityLogAppender;
import edu.ualberta.med.biobank.logs.LogInfo;

public abstract class AbstractPatientAdminForm extends BiobankEntryForm {

    /**
     * Indicate if this form has been saved
     */
    private boolean isSaved = false;
    private static Logger logger;
    private static ActivityLogAppender appender;
    private SimpleDateFormat dateFormat = new SimpleDateFormat(
        "EEEE, MMMM dd, yyyy");

    @Override
    protected void init() {
        if (logger == null) {
            logger = Logger.getLogger("edu.ualberta.med.biobank.logs");
            logger.setLevel(Level.TRACE);
        }
        if (appender == null) {
            appender = new ActivityLogAppender(getActivityTitle());
            logger.addAppender(appender);
        }
    }

    public boolean onClose() {
        if (!isSaved) {
            if (BioBankPlugin.isAskPrint()) {
                boolean doPrint = MessageDialog.openQuestion(PlatformUI
                    .getWorkbench().getActiveWorkbenchWindow().getShell(),
                    "Print", "Do you want to print information ?");
                if (doPrint) {
                    print();
                }
            }
            logger.removeAppender(appender);
            appender.close();
            appender = null;
            return true;
        }
        return false;
    }

    protected void print() {
        if (appender == null) {
            BioBankPlugin.openError("Print error", "Can't print: log error.");
        }
        try {
            List<LogInfo> logsList = appender.getLogsList();
            LogInfo.printLogReport(SessionManager.getInstance().getSession()
                .getUserName(), logsList);
        } catch (Exception e) {
            BioBankPlugin.openAsyncError("Print error", e);
        }
    }

    protected abstract String getActivityTitle();

    public void appendLog(String message) {
        if (logger != null) {
            logger.trace(message);
        }
    }

    protected void setSaved(boolean saved) {
        this.isSaved = saved;
    }

}
