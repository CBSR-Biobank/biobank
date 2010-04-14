package edu.ualberta.med.biobank.forms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JasperPrint;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.logs.ActivityLogAppender;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.logs.LogInfo;
import edu.ualberta.med.biobank.reporting.ReportingUtils;

public abstract class AbstractAliquotAdminForm extends BiobankEntryForm {

    protected boolean finished = true;
    protected boolean printed = false;

    private static Logger activityLogger;
    private static ActivityLogAppender appender;

    @Override
    protected void init() {
        if (activityLogger == null) {
            activityLogger = Logger.getLogger(ActivityLogAppender.class
                .getPackage().getName());
            activityLogger.setLevel(Level.TRACE);
        }
        if (appender == null) {
            appender = new ActivityLogAppender(getActivityTitle());
            activityLogger.addAppender(appender);
        }
    }

    public boolean onClose() {
        if (finished) {
            if (!printed) {
                if (BioBankPlugin.isAskPrint()) {
                    boolean doPrint = MessageDialog.openQuestion(PlatformUI
                        .getWorkbench().getActiveWorkbenchWindow().getShell(),
                        "Print", "Do you want to print information ?");
                    if (doPrint) {
                        if (print())
                            printed = true;
                        else {
                            printed = false;
                            return false;
                        }
                    }
                }
            }
            activityLogger.removeAppender(appender);
            appender.close();
            appender = null;

            return true;
        }
        return false;
    }

    public boolean print() {
        if (appender == null) {
            BioBankPlugin.openError("Print error", "Can't print: log error.");
        }
        try {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("username", SessionManager.getInstance().getSession()
                .getUserName());
            List<LogInfo> logsList = appender.getLogsList();

            JasperPrint jp = ReportingUtils.createStandardReport(
                "ActivityReportForm", map, logsList);
            ReportingUtils.printReport(jp);
            return true;
        } catch (Exception e) {
            BioBankPlugin.openAsyncError("Print error", e);
            return false;
        }
    }

    protected abstract String getActivityTitle();

    public void appendLog(String message) {
        if (activityLogger != null) {
            activityLogger.trace(message);
        }
        printed = false;
    }

    public void appendLogNLS(String key, Object... params) {
        appendLog(Messages.getFormattedString(key, params));
    }

    protected void setFinished(boolean finished) {
        this.finished = finished;
    }

    public abstract BiobankLogger getErrorLogger();

    public void setPrinted(boolean b) {
        this.printed = b;
    }

}
