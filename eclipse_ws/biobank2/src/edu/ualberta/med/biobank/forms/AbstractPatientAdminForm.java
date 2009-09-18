package edu.ualberta.med.biobank.forms;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.logs.ActivityLogAppender;

public abstract class AbstractPatientAdminForm extends BiobankEntryForm {

    /**
     * Indicate if this form has been saved
     */
    private boolean isSaved = false;
    private ActivityLogAppender appender;
    private static Logger logger;

    @Override
    protected void init() {
        if (logger == null) {
            logger = Logger.getLogger("edu.ualberta.med.biobank.logs");
            logger.setLevel(Level.TRACE);
            appender = new ActivityLogAppender(getActivityTitle());
            logger.addAppender(appender);
            // TODO we need all this to be registered in the log database...
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
            appender.close();
            logger = null;
            return true;
        }
        return false;
    }

    protected abstract void print();

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
