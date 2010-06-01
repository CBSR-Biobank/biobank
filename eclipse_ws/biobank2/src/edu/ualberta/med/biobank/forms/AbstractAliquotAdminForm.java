package edu.ualberta.med.biobank.forms;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JasperPrint;

import org.apache.log4j.FileAppender;
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
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;

public abstract class AbstractAliquotAdminForm extends BiobankEntryForm {

    protected boolean finished = true;
    protected boolean printed = false;

    private static Logger activityLogger;
    private static ActivityLogAppender appender;
    private static FileAppender fileAppender;

    private static final SimpleDateFormat fileDateFormatter = new SimpleDateFormat(
        "yyyy-MM-dd_HHmmss");

    @Override
    protected void init() throws Exception {
        if (activityLogger == null) {
            activityLogger = Logger.getLogger(ActivityLogAppender.class
                .getPackage().getName());
            activityLogger.setLevel(Level.TRACE);
        }
        if (appender == null) {
            appender = new ActivityLogAppender(getActivityTitle());
            activityLogger.addAppender(appender);
        }

        if (fileAppender == null) {
            String path = BioBankPlugin.getActivityLogPath();
            if (path != null) {
                fileAppender = new FileAppender(ActivityLogAppender.layout,
                    path + File.separator + getClass().getSimpleName() + "_"
                        + fileDateFormatter.format(new Date()) + ".log", true);
                activityLogger.addAppender(fileAppender);
            }
        }
        widgetCreator.setKeyListener(null);
        widgetCreator.setModifyListener(null);
        widgetCreator.setSelectionListener(null);
    }

    public boolean onClose() {
        if (finished) {
            if (!printed && appender.getLogsList().size() > 0) {
                if (BioBankPlugin.isAskPrintActivityLog()) {
                    boolean doPrint = MessageDialog.openQuestion(PlatformUI
                        .getWorkbench().getActiveWorkbenchWindow().getShell(),
                        "Print", "Do you want to print information ?");
                    if (doPrint) {
                        print();
                    }
                }
            }
            activityLogger.removeAllAppenders();
            appender.close();
            appender = null;
            if (fileAppender != null) {
                fileAppender.close();
                fileAppender = null;
            }
            return true;
        }
        return false;
    }

    @Override
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
            printed = true;
            String printName = getClass().getSimpleName();
            int i = printName.indexOf("EntryForm");
            if (i > 0) {
                printName = printName.substring(0, i);
            }
            ((BiobankApplicationService) appService).logActivity("print", null,
                null, null, null, printName);
            return true;
        } catch (Exception e) {
            BioBankPlugin.openAsyncError("Print error", e);
            printed = false;
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

    @Override
    protected void addToolbarButtons() {
        addPrintAction();
        addResetAction();
        addConfirmAction();
        form.updateToolBar();
    }

}
