package edu.ualberta.med.biobank.forms.reports;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.reports.ReportAction;
import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.common.reports.ReportTreeNode;
import edu.ualberta.med.biobank.common.util.AbstractBiobankListProxy;
import edu.ualberta.med.biobank.common.util.HQLCriteriaListProxy;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.export.CsvDataExporter;
import edu.ualberta.med.biobank.export.Data;
import edu.ualberta.med.biobank.export.GuiDataExporter;
import edu.ualberta.med.biobank.export.PdfDataExporter;
import edu.ualberta.med.biobank.export.PrintPdfDataExporter;
import edu.ualberta.med.biobank.forms.BiobankEntryForm;
import edu.ualberta.med.biobank.forms.input.ReportInput;
import edu.ualberta.med.biobank.forms.listener.ProgressMonitorDialogBusyListener;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.widgets.infotables.ReportTableWidget;

public abstract class ReportsEditor extends BiobankEntryForm {
    private static final I18n i18n = I18nFactory.getI18n(ReportsEditor.class);

    // Report
    protected ReportTreeNode node;
    protected BiobankReport report;

    @SuppressWarnings("nls")
    public static String ID = "edu.ualberta.med.biobank.editors.ReportsEditor";

    // Sections
    protected Composite buttonSection;
    private Composite parameterSection;
    private ReportTableWidget<Object> reportTable;

    // Table Data
    private List<Object> reportData;

    // Buttons
    protected Button generateButton;
    protected Button printButton;
    protected Button exportPDFButton;
    protected Button exportCSVButton;

    // Mostly for visibility reasons
    private String path;

    // Global status
    private IObservableValue statusObservable;

    @SuppressWarnings("nls")
    ProgressMonitorDialogBusyListener listener =
        new ProgressMonitorDialogBusyListener(
            // progress monitor message
            i18n.tr("Generating report..."));

    @Override
    protected void init() throws Exception {
        widgetCreator.initDataBinding();
        reportData = new ArrayList<Object>();
        node = (ReportTreeNode) ((ReportInput) getEditorInput()).getNode();
        report = node.getReport();
        this.setPartName(report.getClassName());
    }

    @SuppressWarnings("nls")
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

        generateButton = toolkit.createButton(buttonSection,
            // button text
            i18n.tr("Generate"), SWT.NONE);
        generateButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                generate();
            }
        });
        setFirstControl(generateButton);

        printButton = toolkit.createButton(buttonSection,
            // button text
            i18n.tr("Print"), SWT.NONE);
        printButton.setImage(BgcPlugin.getDefault().getImageRegistry()
            .get(BgcPlugin.IMG_PRINTER));
        printButton.setEnabled(false);
        printButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                printPDF();
            }
        });

        exportPDFButton = toolkit.createButton(buttonSection, "Export PDF",
            SWT.NONE);
        exportPDFButton.setEnabled(false);
        exportPDFButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                exportPDF();
            }
        });

        exportCSVButton = toolkit.createButton(buttonSection,
            // button label
            i18n.tr("Export CSV"),
            SWT.NONE);
        exportCSVButton.setEnabled(false);
        exportCSVButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                exportCSV();
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

    @SuppressWarnings("nls")
    public static String containerIdsToString(List<Integer> list) {
        String containerListString = StringUtil.EMPTY_STRING;
        for (Object item : (List<?>) list)
            containerListString = containerListString.concat(item.toString()
                + ",");
        containerListString = containerListString.substring(0,
            Math.max(containerListString.length() - 1, 0));
        return containerListString;
    }

    @SuppressWarnings("nls")
    private void generate() {
        try {
            initReport();
            Log logMessage = new Log();
            logMessage.setType("report");
            logMessage.setAction(report.getName());
            SessionManager.getAppService().logActivity(logMessage);
        } catch (Exception e1) {
            BgcPlugin.openAsyncError(
                // dialog title
                i18n.tr("Failed to load parameters"), e1);
        }

        try {

            IRunnableContext context = new ProgressMonitorDialog(Display
                .getDefault().getActiveShell());
            context.run(true, true, new IRunnableWithProgress() {
                @Override
                public void run(final IProgressMonitor monitor) {
                    Thread t = new Thread("Querying") {
                        @Override
                        public void run() {
                            try {
                                reportData =
                                    SessionManager.getAppService()
                                        .doAction(new ReportAction(report))
                                        .getList();
                                if (reportData instanceof AbstractBiobankListProxy)
                                    ((AbstractBiobankListProxy<?>) reportData)
                                        .setAppService(SessionManager
                                            .getAppService());
                            } catch (Exception e) {
                                reportData = new ArrayList<Object>();
                                BgcPlugin.openAsyncError(
                                    // dialog title
                                    i18n.tr("Query Error"), e);
                            }
                        }
                    };
                    monitor.beginTask(
                        // progress monitor message
                        i18n.tr("Generating Report..."),
                        IProgressMonitor.UNKNOWN);
                    t.start();
                    while (true) {
                        if (monitor.isCanceled()) {
                            try {
                            } catch (Exception e) {
                                BgcPlugin.openAsyncError("Stop Failed", e);
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
            BgcPlugin.openAsyncError(
                // dialog title
                i18n.tr("Failed to load query"), e1);
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

        reportTable = new ReportTableWidget<Object>(page, reportData,
            getColumnNames());
        reportTable.adaptToToolkit(toolkit, true);
        page.layout(true, true);
        book.reflow(true);
    }

    @SuppressWarnings("nls")
    private void createEmptyReportTable() {
        if (reportTable != null) {
            reportTable.dispose();
        }
        reportTable = new ReportTableWidget<Object>(page,
            new ArrayList<Object>(), new String[] { " " });
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

    @SuppressWarnings("nls")
    public void exportCSV() {
        export(new CsvDataExporter(), prepareData(),
            reportTable.getLabelProvider());
        logPrint("exportCSV");
    }

    @SuppressWarnings("nls")
    public void exportPDF() {
        export(new PdfDataExporter(), prepareData(),
            reportTable.getLabelProvider());
        logPrint("exportPDF");
    }

    @SuppressWarnings("nls")
    private void printPDF() {
        export(new PrintPdfDataExporter(), prepareData(),
            reportTable.getLabelProvider());
        logPrint("printPDF");
    }

    @SuppressWarnings("nls")
    private void logPrint(String type) {
        try {
            SessionManager.log(type, report.getName(),
                "report");
        } catch (Exception e) {
            BgcPlugin
                .openAsyncError(i18n.tr("Unable to log successful print."));
        }
    }

    private void export(GuiDataExporter exporter, Data data,
        BgcLabelProvider labelProvider) {
        try {
            exporter.export(data, labelProvider);
        } catch (Exception e) {
            MessageDialog.openError(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell(),
                i18n.tr("Error exporting"), e.getMessage()); //$NON-NLS-1$
            return;
        }
    }

    private Data prepareData() {
        Data data = new Data();
        final List<String> printParams = new ArrayList<String>();
        final List<Object> paramVals = getPrintParams();
        int i = 0;
        for (String name : getParamNames()) {
            printParams.add(name + ":" + paramVals.get(i)); //$NON-NLS-1$
            i++;
        }
        final List<String> columnInfo = new ArrayList<String>();
        String[] names = getColumnNames();
        for (int i1 = 0; i1 < names.length; i1++) {
            columnInfo.add(names[i1]);
        }
        data.setTitle(report.getName());
        data.setRows(reportData);
        data.setDescription(printParams);
        data.setColumnNames(columnInfo);
        return data;
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

    protected abstract List<Object> getPrintParams();

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
        }
        return processedDate;
    }

    @Override
    protected void addToolbarButtons() {
    }

    @Override
    protected void saveForm() throws Exception {
        //
    }

    @Override
    protected String getOkMessage() {
        return StringUtil.EMPTY_STRING;
    }

    @Override
    public void setValues() throws Exception {
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
    public String getNextOpenedFormId() {
        return ID;
    }

}
