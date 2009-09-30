package edu.ualberta.med.biobank.forms;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.StyledTextPrintOptions;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.logs.ActivityLogAppender;

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
            logger.removeAppender(appender);
            appender.close();
            appender = null;
            return true;
        }
        return false;
    }

    protected void print() {
        StringBuffer sb = getLogBuffer();
        if (sb == null) {
            BioBankPlugin.openError("Print error",
                "Can't print: log buffer is null.");
            return;
        }

        PrinterData data = null;
        if (!BioBankPlugin.getDefault().isDebugging()) {
            data = Printer.getDefaultPrinterData();
        }
        if (data == null) {
            PrintDialog dialog = new PrintDialog(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell(), SWT.NONE);
            data = dialog.open();
        }
        if (data == null)
            return;

        // use existing print functionality of swt styled texts
        StyledText sText = new StyledText(form, SWT.NONE);
        sText.setText(sb.toString());

        FontData printerFd = new FontData("Sans", 8, SWT.NORMAL);
        final Font font = new Font(Display.getCurrent(), printerFd);
        sText.setFont(font);
        final Printer printer = new Printer(data);
        StyledTextPrintOptions options = new StyledTextPrintOptions();
        options.footer = "Printed on " + dateFormat.format(new Date())
            + StyledTextPrintOptions.SEPARATOR
            + StyledTextPrintOptions.SEPARATOR
            + StyledTextPrintOptions.PAGE_TAG;
        options.header = "Biobank2 - Activity Report"
            + StyledTextPrintOptions.SEPARATOR
            + StyledTextPrintOptions.SEPARATOR + "USER:"
            + SessionManager.getInstance().getSession().getUserName();
        options.jobName = "scannedLinkedActivity";
        final Runnable styledTextPrinter = sText.print(printer, options);
        Thread printingThread = new Thread("Printing") { //$NON-NLS-1$
            @Override
            public void run() {
                styledTextPrinter.run();
                printer.dispose();
                font.dispose();
            }
        };
        printingThread.start();
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

    protected StringBuffer getLogBuffer() {
        if (appender != null) {
            return appender.getLogBuffer();
        }
        return null;
    }
}
