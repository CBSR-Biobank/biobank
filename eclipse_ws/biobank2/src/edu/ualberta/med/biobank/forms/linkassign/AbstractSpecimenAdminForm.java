package edu.ualberta.med.biobank.forms.linkassign;

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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.forms.BiobankEntryForm;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.forms.BgcEntryFormActions;
import edu.ualberta.med.biobank.logs.ActivityLogAppender;
import edu.ualberta.med.biobank.logs.LogInfo;
import edu.ualberta.med.biobank.reporting.ReportingUtils;

public abstract class AbstractSpecimenAdminForm extends BiobankEntryForm {

    private static final String FILE_NAME_SEPARATOR = "_"; //$NON-NLS-1$
    private static final String LOG_EXTENSION = ".log"; //$NON-NLS-1$
    private static final String FORM_USERNAME_PARAM = "username"; //$NON-NLS-1$
    private static final String LOG_ACTION_PRINT = "print"; //$NON-NLS-1$
    private static final String FORM_NAME_SUFFIX = "EntryForm"; //$NON-NLS-1$
    private static final String JASPER_FORM_NAME = "ActivityReportForm"; //$NON-NLS-1$
    private static final SimpleDateFormat fileDateFormatter = new SimpleDateFormat(
        "yyyy-MM-dd_HHmmss"); //$NON-NLS-1$

    protected boolean finished = true;
    protected boolean printed = false;

    private static Logger activityLogger;
    private static ActivityLogAppender appender;
    private static FileAppender fileAppender;

    protected boolean afterInitialization = true;

    protected boolean afterKeyCancel = false;

    protected KeyListener textFieldKeyListener = new KeyAdapter() {
        @Override
        public void keyReleased(KeyEvent e) {
            if (e.character == SWT.CR && !afterKeyCancel
                && !afterInitialization) {
                ((Control) e.widget).traverse(SWT.TRAVERSE_TAB_NEXT);
            }
            afterKeyCancel = false;
            afterInitialization = false;
        }
    };

    @Override
    protected synchronized void init() throws Exception {
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
            String path = BiobankPlugin.getActivityLogPath();
            if (path != null) {
                fileAppender = new FileAppender(ActivityLogAppender.layout,
                    path + File.separator + getClass().getSimpleName()
                        + FILE_NAME_SEPARATOR
                        + fileDateFormatter.format(new Date()) + LOG_EXTENSION,
                    true);
                activityLogger.addAppender(fileAppender);
            }
        }
        widgetCreator.setKeyListener(null);
        widgetCreator.setModifyListener(null);
        widgetCreator.setSelectionListener(null);
    }

    @Override
    protected void addToolbarButtons() {
        formActions = new BgcEntryFormActions(this);
        addPrintAction();
        addResetAction();
        addConfirmAction();
        form.updateToolBar();
    }

    public boolean onClose() {
        if (finished) {
            if (!printed && appender.getLogsList().size() > 0) {
                if (BiobankPlugin.isAskPrintActivityLog()) {
                    boolean doPrint = MessageDialog
                        .openQuestion(
                            PlatformUI.getWorkbench()
                                .getActiveWorkbenchWindow().getShell(),
                            Messages.AbstractSpecimenAdminForm_print_question_title,
                            Messages.AbstractSpecimenAdminForm_print_question_msg);
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
            BgcPlugin.openError(
                Messages.AbstractSpecimenAdminForm_print_error_title,
                Messages.AbstractSpecimenAdminForm_print_error_msg);
        }
        try {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(FORM_USERNAME_PARAM, SessionManager.getInstance()
                .getSession().getUser().getLogin());
            List<LogInfo> logsList = appender.getLogsList();

            JasperPrint jp = ReportingUtils.createStandardReport(
                JASPER_FORM_NAME, map, logsList);

            ReportingUtils.printReport(jp);
            printed = true;
            String printName = getClass().getSimpleName();
            int i = printName.indexOf(FORM_NAME_SUFFIX);
            if (i > 0) {
                printName = printName.substring(0, i);
            }
            SessionManager.log(LOG_ACTION_PRINT, null, printName);
            return true;
        } catch (Exception e) {
            BgcPlugin.openAsyncError(
                Messages.AbstractSpecimenAdminForm_print_error_title, e);
            printed = false;
            return false;
        }
    }

    protected abstract String getActivityTitle();

    public void appendLogs(List<String> messages) {
        if (activityLogger != null) {
            for (String msg : messages) {
                activityLogger.trace(msg);
            }
        }
        printed = false;
    }

    public void appendLog(String message) {
        if (activityLogger != null) {
            activityLogger.trace(message);
        }
        printed = false;
    }

    protected void setFinished(boolean finished) {
        this.finished = finished;
    }

    public abstract BgcLogger getErrorLogger();

    public void setPrinted(boolean b) {
        this.printed = b;
    }

    public void setAfterKeyCancel() {
        afterKeyCancel = true;
    }

}
