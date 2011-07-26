package edu.ualberta.med.biobank.forms.reports;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.common.reports.QueryHandle;
import edu.ualberta.med.biobank.common.reports.ReportTreeNode;
import edu.ualberta.med.biobank.common.util.HQLCriteriaListProxy;
import edu.ualberta.med.biobank.forms.BiobankEntryForm;
import edu.ualberta.med.biobank.forms.input.ReportInput;
import edu.ualberta.med.biobank.forms.listener.ProgressMonitorDialogBusyListener;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.forms.BgcEntryFormActions;
import edu.ualberta.med.biobank.reporting.ReportingUtils;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import edu.ualberta.med.biobank.widgets.infotables.ReportTableWidget;

public abstract class ReportsEditor extends BiobankEntryForm {

    // Report
    protected ReportTreeNode node;
    protected BiobankReport report;

    public static String ID = "edu.ualberta.med.biobank.editors.ReportsEditor"; //$NON-NLS-1$

    // Sections
    protected Composite buttonSection;
    private Composite parameterSection;
    private ReportTableWidget<Object> reportTable;

    // Table Data
    private List<Object> reportData;

    // Warning label
    private Label warning;

    // Buttons
    private Button generateButton;
    private Button printButton;
    private Button exportPDFButton;
    private Button exportCSVButton;

    // Mostly for visibility reasons
    private String path;

    // Global status
    private IObservableValue statusObservable;

    QueryHandle query;
    ProgressMonitorDialogBusyListener listener = new ProgressMonitorDialogBusyListener(
        "Generating report..."); //$NON-NLS-1$

    @Override
    protected void init() throws Exception {
        widgetCreator.initDataBinding();
        reportData = new ArrayList<Object>();
        node = (ReportTreeNode) ((ReportInput) getEditorInput()).getNode();
        report = node.getReport();
        this.setPartName(report.getName());
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(report.getName());
        page.setLayout(new GridLayout(1, false));

        Composite topSection = toolkit.createComposite(page, SWT.NONE);
        topSection.setLayout(new GridLayout(1, false));
        topSection.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
            true, false));
        Label l = toolkit.createLabel(topSection, report.getDescription(),
            SWT.WRAP);
        GridData gd = new GridData(GridData.FILL, GridData.FILL, true, true);
        // FIXME: see https://bugs.eclipse.org/bugs/show_bug.cgi?id=265389
        // once this gets fixed we can remove the width hint
        gd.widthHint = 400;
        l.setLayoutData(gd);

        if (parameterSection != null)
            parameterSection.dispose();

        parameterSection = toolkit.createComposite(topSection, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginTop = 0;
        layout.marginBottom = 0;
        layout.marginLeft = 0;
        layout.marginRight = 0;
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        parameterSection.setLayout(layout);
        parameterSection.setLayoutData(new GridData(GridData.FILL,
            GridData.FILL, true, false));

        buttonSection = toolkit.createComposite(page, SWT.NONE);
        GridLayout gl = new GridLayout();
        gl.numColumns = 4;
        buttonSection.setLayout(gl);
        toolkit.adapt(buttonSection);

        warning = toolkit.createLabel(buttonSection,
            Messages.ReportsEditor_print_disabled_msg);
        GridData wgd = new GridData();
        wgd.horizontalSpan = 4;
        warning.setVisible(false);
        warning.setLayoutData(wgd);

        generateButton = toolkit.createButton(buttonSection,
            Messages.ReportsEditor_generate_label, SWT.NONE);
        generateButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                generate();
            }
        });
        setFirstControl(generateButton);

        printButton = toolkit.createButton(buttonSection, "Print", SWT.NONE); //$NON-NLS-1$
        printButton.setImage(BgcPlugin.getDefault().getImageRegistry()
            .get(BgcPlugin.IMG_PRINTER));
        printButton.setEnabled(false);
        printButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    printTable(false, false);
                } catch (Exception ex) {
                    BgcPlugin.openAsyncError(
                        "Error while printing the results", ex); //$NON-NLS-1$
                }
            }
        });

        exportPDFButton = toolkit.createButton(buttonSection, "Export PDF", //$NON-NLS-1$
            SWT.NONE);
        exportPDFButton.setEnabled(false);
        exportPDFButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    printTable(false, true);
                } catch (Exception ex) {
                    BgcPlugin.openAsyncError(
                        "Error while exporting the results", ex); //$NON-NLS-1$
                }
            }
        });

        exportCSVButton = toolkit.createButton(buttonSection, "Export CSV", //$NON-NLS-1$
            SWT.NONE);
        exportCSVButton.setEnabled(false);
        exportCSVButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    printTable(true, false);
                } catch (Exception ex) {
                    BgcPlugin.openAsyncError(
                        "Error while exporting the results", ex); //$NON-NLS-1$
                }
            }
        });

        statusObservable = new WritableValue();
        statusObservable.addChangeListener(new IChangeListener() {
            @Override
            public void handleChange(ChangeEvent event) {
                IObservableValue validationStatus = (IObservableValue) event
                    .getSource();
                handleStatusChanged((IStatus) validationStatus.getValue());
            }

            private void handleStatusChanged(IStatus status) {
                if (status.getSeverity() == IStatus.OK)
                    generateButton.setEnabled(true);
                else
                    generateButton.setEnabled(false);
            }
        });

        widgetCreator.addGlobalBindValue(statusObservable);

        createOptionSection(parameterSection);

        // update parents
        createEmptyReportTable();
    }

    public static String containerIdsToString(List<Integer> list) {
        String containerListString = ""; //$NON-NLS-1$
        for (Object item : (List<?>) list)
            containerListString = containerListString.concat(item.toString()
                + ","); //$NON-NLS-1$
        containerListString = containerListString.substring(0,
            Math.max(containerListString.length() - 1, 0));
        return containerListString;
    }

    private void generate() {
        appService = SessionManager.getAppService();
        try {
            initReport();
        } catch (Exception e1) {
            BgcPlugin.openAsyncError("Failed to load parameters", e1); //$NON-NLS-1$
        }

        try {
            query = appService.createQuery(report);

            IRunnableContext context = new ProgressMonitorDialog(Display
                .getDefault().getActiveShell());
            context.run(true, true, new IRunnableWithProgress() {
                @Override
                public void run(final IProgressMonitor monitor) {
                    Thread t = new Thread("Querying") { //$NON-NLS-1$
                        @Override
                        public void run() {
                            try {
                                reportData = appService.startQuery(query);
                            } catch (Exception e) {
                                reportData = new ArrayList<Object>();
                                BgcPlugin.openAsyncError("Query Error", e); //$NON-NLS-1$
                            }
                        }
                    };
                    monitor.beginTask("Generating Report...", //$NON-NLS-1$
                        IProgressMonitor.UNKNOWN);
                    t.start();
                    while (true) {
                        if (monitor.isCanceled()) {
                            try {
                                appService.stopQuery(query);
                            } catch (Exception e) {
                                BgcPlugin.openAsyncError("Stop Failed", e); //$NON-NLS-1$
                            }
                            reportData = new ArrayList<Object>();
                            break;
                        } else if (!t.isAlive())
                            break;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                };
            });
        } catch (Exception e1) {
            BgcPlugin.openAsyncError("Failed to load query", e1); //$NON-NLS-1$
        }

        if (reportData instanceof HQLCriteriaListProxy)
            ((HQLCriteriaListProxy<?>) reportData).addBusyListener(listener);

        if (!reportData.isEmpty()) {
            printButton.setEnabled(true);
            exportPDFButton.setEnabled(true);
            exportCSVButton.setEnabled(true);
        } else {
            printButton.setEnabled(false);
            exportPDFButton.setEnabled(false);
            exportCSVButton.setEnabled(false);
        }
        reportTable.dispose();
        // if size > 1000 or unknown, disable print and
        // export to pdf
        if ((reportData instanceof HQLCriteriaListProxy && (((HQLCriteriaListProxy<?>) reportData)
            .getRealSize() == -1 || ((HQLCriteriaListProxy<?>) reportData)
            .getRealSize() > 1000))
            || reportData.size() > 1000) {
            printButton.setEnabled(false);
            exportPDFButton.setEnabled(false);
            printButton.setToolTipText(Messages.ReportsEditor_exceed_1000_msg);
            exportPDFButton
                .setToolTipText(Messages.ReportsEditor_exceed_1000_msg);
            warning.setVisible(true);
            setEnablePrintAction(false);
        } else {
            printButton.setToolTipText(Messages.ReportsEditor_print_label);
            exportPDFButton
                .setToolTipText(Messages.ReportsEditor_pdfexport_label);
            setEnablePrintAction(true);
            warning.setVisible(false);
        }
        reportTable = new ReportTableWidget<Object>(page, reportData,
            getColumnNames());
        reportTable.adaptToToolkit(toolkit, true);
        page.layout(true, true);
        book.reflow(true);
    }

    @Override
    public boolean print() {
        try {
            printTable(false, false);
        } catch (Exception e) {
            BgcPlugin.openAsyncError(Messages.ReportsEditor_print_error_msg, e);
        }
        return true;
    }

    private void createEmptyReportTable() {
        if (reportTable != null) {
            reportTable.dispose();
        }
        reportTable = new ReportTableWidget<Object>(page,
            new ArrayList<Object>(), new String[] { " " }); //$NON-NLS-1$
        reportTable.adaptToToolkit(toolkit, true);
        page.layout(true, true);
        book.reflow(true);
    }

    public void resetSearch() {
        Assert.isNotNull(reportTable);
        createEmptyReportTable();
        reportData = new ArrayList<Object>();

        printButton.setEnabled(false);
        exportPDFButton.setEnabled(false);
        exportCSVButton.setEnabled(false);
    }

    private void exportCSV(List<String> columnInfo, List<Object[]> params,
        String path) {
        // csv
        PrintWriter bw = null;
        try {
            bw = new PrintWriter(new FileWriter(path));
        } catch (IOException e) {
            BgcPlugin.openAsyncError("Error writing to CSV.", e); //$NON-NLS-1$
            return;
        }
        // write title
        bw.println("#" + report.getName()); //$NON-NLS-1$
        // write params
        for (Object[] ob : params)
            bw.println("#" + ob[0] + ":" + ob[1]); //$NON-NLS-1$ //$NON-NLS-2$
        // write columnnames
        bw.println("#"); //$NON-NLS-1$
        bw.print("#" + columnInfo.get(0)); //$NON-NLS-1$
        for (int j = 1; j < columnInfo.size(); j++) {
            bw.write("," + columnInfo.get(j)); //$NON-NLS-1$
        }
        bw.println();
        BiobankLabelProvider stringConverter = reportTable
            .getLabelProvider(false);
        for (Object row : reportData) {
            Object[] castOb = (Object[]) row;
            bw.write("\"" + stringConverter.getColumnText(castOb, 0) + "\""); //$NON-NLS-1$ //$NON-NLS-2$
            for (int j = 1; j < columnInfo.size(); j++) {
                bw.write(",\"" + stringConverter.getColumnText(castOb, j) //$NON-NLS-1$
                    + "\""); //$NON-NLS-1$
            }
            bw.println();

        }
        bw.close();
    }

    private String runExportDialog(String name, String[] exts) {
        FileDialog fd = new FileDialog(form.getShell(), SWT.SAVE);
        fd.setOverwrite(true);
        fd.setText("Export as"); //$NON-NLS-1$
        fd.setFilterExtensions(exts);
        fd.setFileName(name);
        return fd.open();
    }

    public void printTable(final Boolean exportCSV, final Boolean exportPDF)
        throws Exception {
        boolean doPrint;
        if (exportCSV || exportPDF)
            doPrint = MessageDialog.openQuestion(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell(), "Confirm", //$NON-NLS-1$
                "Export table contents?"); //$NON-NLS-1$
        else
            doPrint = MessageDialog.openQuestion(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell(), "Confirm", //$NON-NLS-1$
                "Print table contents?"); //$NON-NLS-1$
        if (doPrint) {
            final List<Object[]> printParams = new ArrayList<Object[]>();
            final List<Object> paramVals = getPrintParams();
            int i = 0;
            for (String name : getParamNames()) {
                printParams.add(new Object[] { name, paramVals.get(i) });
                i++;
            }
            final List<String> columnInfo = new ArrayList<String>();
            String[] names = getColumnNames();
            for (int i1 = 0; i1 < names.length; i1++) {
                columnInfo.add(names[i1]);
            }

            if (exportCSV || exportPDF) {
                String fileName = report.getName().replaceAll(" ", "_") //$NON-NLS-1$ //$NON-NLS-2$
                    + "_" + DateFormatter.formatAsDate(new Date()); //$NON-NLS-1$ /
                String[] filterExt = (exportCSV) ? new String[] { "*.csv" } //$NON-NLS-1$ 
                    : new String[] { ".pdf" }; //$NON-NLS-1$
                path = runExportDialog(fileName, filterExt);
                if (path == null) {
                    BgcPlugin.openAsyncError("Exporting canceled.", //$NON-NLS-1$
                        "Select a valid path and try again."); //$NON-NLS-1$
                    return;
                }
            }
            IRunnableContext context = new ProgressMonitorDialog(Display
                .getDefault().getActiveShell());
            context.run(true, false, new IRunnableWithProgress() {
                @Override
                public void run(final IProgressMonitor monitor) {
                    monitor.beginTask("Preparing Report...", //$NON-NLS-1$
                        IProgressMonitor.UNKNOWN);
                    final List<Map<String, String>> listData = new ArrayList<Map<String, String>>();
                    try {
                        if (exportCSV) {
                            exportCSV(columnInfo, printParams, path);
                            SessionManager.log("exportCSV", report.getName(), //$NON-NLS-1$
                                "report"); //$NON-NLS-1$
                        } else {
                            for (Object object : reportData) {
                                Map<String, String> map = new HashMap<String, String>();
                                for (int j = 0; j < columnInfo.size(); j++) {
                                    map.put(columnInfo.get(j), (reportTable
                                        .getLabelProvider().getColumnText(
                                        object, j)));
                                }
                                listData.add(map);
                            }
                            monitor.done();
                            exportPDFOrPrint(listData, columnInfo, printParams,
                                path, exportPDF);
                        }
                    } catch (Exception e) {
                        BgcPlugin.openAsyncError("Error exporting results", e); //$NON-NLS-1$
                        return;
                    }
                }
            });
        }
    }

    public void exportPDFOrPrint(List<?> listData, List<String> columnInfo,
        List<Object[]> params, String path, Boolean exportPDF) {

        List<String> stringParams = new ArrayList<String>();
        for (int i = 0; i < params.size(); i++) {
            stringParams.add(params.get(i)[0] + " : " + params.get(i)[1]); //$NON-NLS-1$
        }

        if (exportPDF) {
            try {
                ReportingUtils.saveReport(
                    ReportingUtils.createDynamicReport(report.getName(),
                        stringParams, columnInfo, listData, false), path);
            } catch (Exception e) {
                BgcPlugin.openAsyncError("Error saving to PDF", e); //$NON-NLS-1$
                return;
            }
            try {
                SessionManager.log("exportPDF", report.getName(), "report"); //$NON-NLS-1$ //$NON-NLS-2$
            } catch (Exception e) {
                BgcPlugin.openAsyncError("Error logging export", e); //$NON-NLS-1$
            }
        } else {
            try {
                ReportingUtils
                    .printReport(ReportingUtils.createDynamicReport(
                        report.getName(), stringParams, columnInfo, listData,
                        false));
            } catch (Exception e) {
                BgcPlugin.openAsyncError("Printer Error", e); //$NON-NLS-1$
                return;
            }
            try {
                SessionManager.log("print", report.getName(), "report"); //$NON-NLS-1$ //$NON-NLS-2$
            } catch (Exception e) {
                BgcPlugin.openAsyncError("Error logging print", e); //$NON-NLS-1$
            }
        }
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
    }

    @Override
    public void doSaveAs() {
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    protected abstract void createOptionSection(Composite parameterSection)
        throws Exception;

    protected abstract String[] getColumnNames();

    protected abstract List<String> getParamNames();

    protected abstract void initReport() throws Exception;

    protected abstract List<Object> getPrintParams() throws Exception;

    public static Date removeTime(Date date) {
        Calendar cal = Calendar.getInstance(); // locale-specific
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date processDate(Date date, boolean startDate) {
        Date processedDate;
        if (date == null && startDate)
            processedDate = new Date(0);
        else if (date == null && !startDate)
            processedDate = new Date();
        else
            processedDate = date;
        processedDate = removeTime(processedDate);
        if (!startDate) {
            Calendar c = Calendar.getInstance();
            c.setTime(processedDate);
            c.add(Calendar.DAY_OF_YEAR, 1);
            c.add(Calendar.MINUTE, -1);
            return (c.getTime());
        } else
            return processedDate;
    }

    @Override
    protected void addToolbarButtons() {
        formActions = new BgcEntryFormActions(this);
        addResetAction();
        addCancelAction();
        addPrintAction();
        form.updateToolBar();
        setEnablePrintAction(false);
    }

    @Override
    protected void saveForm() throws Exception {
        //
    }

    @Override
    protected String getOkMessage() {
        return ""; //$NON-NLS-1$
    }

    @Override
    protected void onReset() throws Exception {
        createEmptyReportTable();
        setEnablePrintAction(false);

        printButton.setEnabled(false);
        exportPDFButton.setEnabled(false);
        exportCSVButton.setEnabled(false);
    }

    @Override
    public void cancel() {
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
            .closeEditor(this, false);
    }

    @Override
    public String getNextOpenedFormID() {
        return ID;
    }

}
