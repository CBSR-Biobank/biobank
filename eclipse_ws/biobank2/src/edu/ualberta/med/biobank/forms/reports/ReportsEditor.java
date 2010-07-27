package edu.ualberta.med.biobank.forms.reports;

import java.awt.Color;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

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
import org.eclipse.ui.PlatformUI;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.AutoText;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Transparency;
import ar.com.fdvs.dj.domain.constants.VerticalAlign;
import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.common.reports.ReportTreeNode;
import edu.ualberta.med.biobank.common.util.BiobankListProxy;
import edu.ualberta.med.biobank.common.util.DateGroup;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.forms.BiobankFormBase;
import edu.ualberta.med.biobank.forms.input.ReportInput;
import edu.ualberta.med.biobank.reporting.ReportingUtils;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.views.ReportsView;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import edu.ualberta.med.biobank.widgets.infotables.ReportTableWidget;
import gov.nih.nci.system.applicationservice.ApplicationException;

public abstract class ReportsEditor extends BiobankFormBase {

    // Report
    protected ReportTreeNode node;
    private BiobankReport report;

    public static String ID = "edu.ualberta.med.biobank.editors.ReportsEditor";

    // Sections
    protected Composite buttonSection;
    private Composite parameterSection;
    private ReportTableWidget<Object> reportTable;

    // Table Data
    private List<Object> reportData;

    // Buttons
    private Button generateButton;
    private Button printButton;
    private Button exportPDFButton;
    private Button exportCSVButton;

    // Mostly for visibility reasons
    private String path;

    // Global status
    private IObservableValue statusObservable;

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
        GridLayout formLayout = new GridLayout();
        formLayout.marginWidth = 0;
        page.setLayout(formLayout);

        form.setText(report.getDescription());

        if (parameterSection != null)
            parameterSection.dispose();

        parameterSection = toolkit.createComposite(page, SWT.NONE);
        GridData pgd = new GridData();
        GridLayout pgl = new GridLayout(2, false);
        pgd.grabExcessHorizontalSpace = true;
        parameterSection.setLayout(pgl);
        parameterSection.setLayoutData(pgd);

        buttonSection = toolkit.createComposite(page, SWT.NONE);
        GridLayout gl = new GridLayout();
        gl.numColumns = 4;
        buttonSection.setLayout(gl);
        toolkit.adapt(buttonSection);

        generateButton = toolkit.createButton(buttonSection, "Generate",
            SWT.NONE);
        generateButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                generate();
            }
        });

        printButton = toolkit.createButton(buttonSection, "Print", SWT.NONE);
        printButton.setImage(BioBankPlugin.getDefault().getImageRegistry()
            .get(BioBankPlugin.IMG_PRINTER));
        printButton.setEnabled(false);
        printButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    printTable(false, false);
                } catch (Exception ex) {
                    BioBankPlugin.openAsyncError(
                        "Error while printing the results", ex);
                }
            }
        });

        exportPDFButton = toolkit.createButton(buttonSection, "Export PDF",
            SWT.NONE);
        exportPDFButton.setEnabled(false);
        exportPDFButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    printTable(false, true);
                } catch (Exception ex) {
                    BioBankPlugin.openAsyncError(
                        "Error while exporting the results", ex);
                }
            }
        });

        exportCSVButton = toolkit.createButton(buttonSection, "Export CSV",
            SWT.NONE);
        exportCSVButton.setEnabled(false);
        exportCSVButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    printTable(true, false);
                } catch (Exception ex) {
                    BioBankPlugin.openAsyncError(
                        "Error while exporting the results", ex);
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

    private void generate() {
        try {
            SiteWrapper site = SessionManager.getInstance().getCurrentSite();
            String op = "=";
            if (site.getName().compareTo("All Sites") == 0)
                op = "!=";
            report.setSiteInfo(op, site.getId());

            List<Object> params = new ArrayList<Object>();
            String grouping = "";
            for (Object ob : getParams()) {
                try {
                    // FIXME: horrible hack: need better way to test value
                    DateGroup.valueOf((String) ob);
                    grouping = (String) ob;
                } catch (Exception e) {
                    params.add(ob);
                }
            }
            report.setParams(params);
            report.setGroupBy(grouping);
        } catch (Exception e1) {
            BioBankPlugin.openAsyncError("Input Error", e1);
            return;
        }

        IRunnableContext context = new ProgressMonitorDialog(Display
            .getDefault().getActiveShell());
        try {
            context.run(true, true, new IRunnableWithProgress() {
                @Override
                public void run(final IProgressMonitor monitor) {
                    Thread t = new Thread("Querying") {
                        @Override
                        public void run() {
                            try {
                                reportData = generateReport();
                            } catch (Exception e) {
                                reportData = new ArrayList<Object>();
                                BioBankPlugin.openAsyncError(
                                    "Error while querying for results", e);
                            }
                        }

                        private List<Object> generateReport()
                            throws ApplicationException {
                            return report.generate(SessionManager
                                .getAppService());
                            // TODO: FIXME
                            /*
                             * if (report instanceof QueryObject) { return
                             * ((QueryObject) report).generate(
                             * SessionManager.getAppService(), params); }
                             */
                        }
                    };
                    monitor.beginTask("Generating Report...",
                        IProgressMonitor.UNKNOWN);
                    t.start();
                    while (true) {
                        if (monitor.isCanceled()) {
                            // TODO t.stop(); we need a safe way to kill query
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
                    Display.getDefault().syncExec(new Runnable() {
                        @Override
                        public void run() {
                            monitor.done();
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
                            if ((reportData instanceof BiobankListProxy && (((BiobankListProxy) reportData)
                                .getRealSize() == -1 || ((BiobankListProxy) reportData)
                                .getRealSize() > 1000))
                                || reportData.size() > 1000) {
                                printButton.setEnabled(false);
                                exportPDFButton.setEnabled(false);
                                printButton
                                    .setToolTipText("Results exceed 1000 rows");
                                exportPDFButton
                                    .setToolTipText("Results exceed 1000 rows");
                            } else {
                                printButton.setToolTipText("Print");
                                exportPDFButton.setToolTipText("Export PDF");
                            }
                            reportTable = new ReportTableWidget<Object>(page,
                                reportData, getColumnNames(),
                                getColumnWidths(), 24);
                            reportTable.adaptToToolkit(toolkit, true);
                            form.reflow(true);
                        }
                    });

                }
            });
        } catch (Exception e) {
            BioBankPlugin.openAsyncError("Query Error", e);
        }
    }

    private void createEmptyReportTable() {
        if (reportTable != null) {
            reportTable.dispose();
        }
        reportTable = new ReportTableWidget<Object>(page, null,
            new String[] { " " }, new int[] { 500 });
        reportTable.adaptToToolkit(toolkit, true);
        form.layout(true, true);
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
        String path, IProgressMonitor monitor) throws Exception {
        // csv
        PrintWriter bw = null;
        try {
            bw = new PrintWriter(new FileWriter(path));
        } catch (IOException e) {
            BioBankPlugin.openAsyncError("Error writing to CSV.", e);
            return;
        }
        // write title
        bw.println("#" + report.getName());
        // write params
        for (Object[] ob : params)
            bw.println("#" + ob[0] + ":" + ob[1]);
        // write columnnames
        bw.println("#");
        bw.print("#" + columnInfo.get(0));
        for (int j = 1; j < columnInfo.size(); j++) {
            bw.write("," + columnInfo.get(j));
        }
        bw.println();
        BiobankLabelProvider stringConverter = reportTable.getLabelProvider();
        for (Object row : reportData) {
            if (monitor.isCanceled()) {
                throw new Exception("Exporting canceled.");
            }
            Object[] castOb = (Object[]) row;
            bw.write("\"" + stringConverter.getColumnText(castOb, 0) + "\"");
            for (int j = 1; j < columnInfo.size(); j++) {
                bw.write(",\"" + stringConverter.getColumnText(castOb, j)
                    + "\"");
            }
            bw.println();

        }
        bw.close();
    }

    private String runExportDialog(String name, String[] exts) {
        FileDialog fd = new FileDialog(form.getShell(), SWT.SAVE);
        fd.setOverwrite(true);
        fd.setText("Export as");
        fd.setFilterExtensions(exts);
        fd.setFileName(name);
        return fd.open();
    }

    public void printTable(final Boolean exportCSV, final Boolean exportPDF)
        throws Exception {
        boolean doPrint;
        if (exportCSV || exportPDF)
            doPrint = MessageDialog.openQuestion(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell(), "Confirm",
                "Export table contents?");
        else
            doPrint = MessageDialog.openQuestion(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell(), "Confirm",
                "Print table contents?");
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

            if (exportCSV) {
                String[] filterExt = { "*.csv" };
                path = runExportDialog(report.getName().replaceAll(" ", "_")
                    + "_" + DateFormatter.formatAsDate(new Date()), filterExt);
                if (path == null) {
                    BioBankPlugin.openAsyncError("Exporting canceled.",
                        "Select a valid path and try again.");
                    return;
                } else if (path.endsWith(".csv")) {
                }
            } else if (exportPDF) {
                String[] filterExt = new String[] { ".pdf" };
                path = runExportDialog(report.getName().replaceAll(" ", "_")
                    + "_" + DateFormatter.formatAsDate(new Date()), filterExt);

            }
            IRunnableContext context = new ProgressMonitorDialog(Display
                .getDefault().getActiveShell());
            context.run(true, true, new IRunnableWithProgress() {
                @Override
                public void run(final IProgressMonitor monitor) {
                    monitor.beginTask("Preparing Report...",
                        IProgressMonitor.UNKNOWN);
                    final List<Map<String, String>> listData = new ArrayList<Map<String, String>>();
                    try {
                        if (exportCSV) {
                            exportCSV(columnInfo, printParams, path, monitor);
                            ((BiobankApplicationService) SessionManager
                                .getAppService()).logActivity("exportCSV",
                                SessionManager.getInstance().getCurrentSite()
                                    .getNameShort(), null, null, null,
                                report.getName(), "report");
                        } else {
                            for (Object object : reportData) {
                                if (monitor.isCanceled()) {
                                    throw new Exception("Exporting canceled.");
                                }
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
                        BioBankPlugin.openAsyncError("Error exporting results",
                            e);
                        return;
                    }
                }
            });
        }
    }

    public void exportPDFOrPrint(List<?> listData, List<String> columnInfo,
        List<Object[]> params, String path, Boolean exportPDF) {
        if (exportPDF) {
            try {
                ReportingUtils.saveReport(
                    createDynamicReport(report.getName(), params, columnInfo,
                        listData), path);
            } catch (Exception e) {
                BioBankPlugin.openAsyncError("Error saving to PDF", e);
                return;
            }
            ((BiobankApplicationService) SessionManager.getAppService())
                .logActivity("exportPDF", SessionManager.getInstance()
                    .getCurrentSite().getNameShort(), null, null, null,
                    report.getName(), "report");
        } else {
            try {
                ReportingUtils.printReport(createDynamicReport(
                    report.getName(), params, columnInfo, listData));
            } catch (Exception e) {
                BioBankPlugin.openAsyncError("Printer Error", e);
                return;
            }
            ((BiobankApplicationService) SessionManager.getAppService())
                .logActivity("print", SessionManager.getInstance()
                    .getCurrentSite().getNameShort(), null, null, null,
                    report.getName(), "report");
        }
    }

    public JasperPrint createDynamicReport(String reportName,
        List<Object[]> params, List<String> columnInfo, List<?> list)
        throws Exception {

        FastReportBuilder drb = new FastReportBuilder();
        for (int i = 0; i < columnInfo.size(); i++) {
            drb.addColumn(columnInfo.get(i), columnInfo.get(i), String.class,
                40, false).setPrintBackgroundOnOddRows(true)
                .setUseFullPageWidth(true);
        }

        Map<String, Object> fields = new HashMap<String, Object>();
        String paramString = "";
        for (int i = 0; i < params.size(); i++) {
            paramString += params.get(i)[0] + " : " + params.get(i)[1] + "\n";
        }
        fields.put("title", reportName);
        fields.put("infos", paramString);
        URL reportURL = ReportingUtils.class.getResource("BasicReport.jrxml");
        if (reportURL == null) {
            throw new Exception("No report available with name BasicReport");
        }
        drb.setTemplateFile(reportURL.getFile());
        drb.addAutoText(AutoText.AUTOTEXT_PAGE_X_OF_Y,
            AutoText.POSITION_FOOTER, AutoText.ALIGNMENT_RIGHT, 200, 40);
        drb.addAutoText(
            "Printed on " + DateFormatter.formatAsDateTime(new Date()),
            AutoText.POSITION_FOOTER, AutoText.ALIGNMENT_LEFT, 200);

        Style headerStyle = new Style();
        headerStyle.setFont(ReportingUtils.sansSerifBold);
        // headerStyle.setHorizontalAlign(HorizontalAlign.CENTER);
        headerStyle.setBorderBottom(Border.THIN);
        headerStyle.setVerticalAlign(VerticalAlign.MIDDLE);
        headerStyle.setBackgroundColor(Color.LIGHT_GRAY);
        headerStyle.setTransparency(Transparency.OPAQUE);
        Style detailStyle = new Style();
        detailStyle.setFont(ReportingUtils.sansSerif);
        drb.setDefaultStyles(null, null, headerStyle, detailStyle);

        JRDataSource ds = new JRBeanCollectionDataSource(list);
        JasperPrint jp = DynamicJasperHelper.generateJasperPrint(drb.build(),
            new ClassicLayoutManager(), ds, fields);
        return jp;
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

    @Override
    public void setFocus() {
        ReportsView.currentInstance.setSelectedNode(node);
    }

    protected abstract void createOptionSection(Composite parameterSection)
        throws Exception;

    protected abstract int[] getColumnWidths();

    protected abstract String[] getColumnNames();

    protected abstract List<String> getParamNames();

    protected abstract List<Object> getParams() throws Exception;

    protected List<Object> getPrintParams() throws Exception {
        return getParams();
    }

}
