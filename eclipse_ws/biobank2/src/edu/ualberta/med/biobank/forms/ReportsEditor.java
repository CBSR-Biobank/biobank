package edu.ualberta.med.biobank.forms;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Transparency;
import ar.com.fdvs.dj.domain.constants.VerticalAlign;
import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.reports.CabinetCSamples;
import edu.ualberta.med.biobank.common.reports.CabinetDSamples;
import edu.ualberta.med.biobank.common.reports.CabinetSSamples;
import edu.ualberta.med.biobank.common.reports.FreezerCSamples;
import edu.ualberta.med.biobank.common.reports.FreezerDSamples;
import edu.ualberta.med.biobank.common.reports.FreezerSSamples;
import edu.ualberta.med.biobank.common.reports.FvLPatientVisits;
import edu.ualberta.med.biobank.common.reports.NewPVsByStudyClinic;
import edu.ualberta.med.biobank.common.reports.NewPsByStudyClinic;
import edu.ualberta.med.biobank.common.reports.PatientVisitSummary;
import edu.ualberta.med.biobank.common.reports.PatientWBC;
import edu.ualberta.med.biobank.common.reports.QACabinetSamples;
import edu.ualberta.med.biobank.common.reports.QAFreezerSamples;
import edu.ualberta.med.biobank.common.reports.QueryObject;
import edu.ualberta.med.biobank.common.reports.SampleCount;
import edu.ualberta.med.biobank.common.reports.SampleInvoiceByClinic;
import edu.ualberta.med.biobank.common.reports.SampleInvoiceByPatient;
import edu.ualberta.med.biobank.common.reports.SampleRequest;
import edu.ualberta.med.biobank.common.reports.SampleSCount;
import edu.ualberta.med.biobank.common.reports.QueryObject.DateRange;
import edu.ualberta.med.biobank.common.reports.QueryObject.Option;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.forms.input.ReportInput;
import edu.ualberta.med.biobank.reporting.ReportingUtils;
import edu.ualberta.med.biobank.widgets.AutoTextWidget;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.widgets.FileBrowser;
import edu.ualberta.med.biobank.widgets.infotables.SearchResultsInfoTable;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ReportsEditor extends EditorPart {

    public static String ID = "edu.ualberta.med.biobank.editors.ReportsEditor";

    private Composite top;
    private Composite buttonSection;
    private Composite parameterSection;

    private SearchResultsInfoTable reportTable;
    private List<Widget> widgetFields;
    private List<Label> textLabels;

    private Button generateButton;
    private List<Object> reportData;

    private Button printButton;
    private Button exportButton;

    private QueryObject query;

    private Class<? extends QueryObject> queryClass;

    private static Map<Class<?>, int[]> columnWidths;

    private void generate() {

        final ArrayList<Object> params = getParams();

        // we dont want the user to change options while the search is in
        // progress
        setEnabled(false);

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
                                SiteWrapper site = SessionManager.getInstance()
                                    .getCurrentSiteWrapper();
                                String op = "=";
                                if (site.getName().compareTo("All Sites") == 0)
                                    op = "!=";
                                query = queryClass.getConstructor(String.class,
                                    Integer.class).newInstance(
                                    new Object[] { op, site.getId() });
                                reportData = query.generate(SessionManager
                                    .getAppService(), params);
                                if (reportData.size() >= 1000)
                                    BioBankPlugin
                                        .openAsyncError(
                                            "Size Limit Exceeded",
                                            "Your search criteria is too general. Please try refining your search. Displaying the first 1000 results.");
                            } catch (Exception e) {
                                BioBankPlugin.openAsyncError(
                                    "Error while querying for results", e);
                            }
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
                                exportButton.setEnabled(true);
                            } else {
                                printButton.setEnabled(false);
                                exportButton.setEnabled(false);
                            }
                            reportTable.dispose();
                            reportTable = new SearchResultsInfoTable(top,
                                reportData, query.getColumnNames(),
                                columnWidths.get(query.getClass()));
                            GridData gd = new GridData();
                            gd.grabExcessHorizontalSpace = true;
                            gd.grabExcessVerticalSpace = true;
                            gd.horizontalSpan = 2;
                            gd.horizontalAlignment = SWT.FILL;
                            gd.verticalAlignment = SWT.FILL;
                            reportTable.setLayoutData(gd);
                            setEnabled(true);
                            top.layout();
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
        reportTable = new SearchResultsInfoTable(top, null,
            new String[] { " " }, new int[] { 500 });
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalSpan = 2;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        reportTable.setLayoutData(gd);
    }

    public void resetSearch() {
        Assert.isNotNull(reportTable);
        createEmptyReportTable();
        reportData = new ArrayList<Object>();

        printButton.setEnabled(false);
        exportButton.setEnabled(false);
    }

    private void setEnabled(boolean enabled) {
        SessionManager.getInstance().getSiteCombo().setEnabled(enabled);
        generateButton.setEnabled(enabled);
        printButton.setEnabled(enabled);
        exportButton.setEnabled(enabled);
        for (int i = 0; i < widgetFields.size(); i++)
            ((Control) widgetFields.get(i)).setEnabled(enabled);
    }

    private ArrayList<Object> getParams() {
        ArrayList<Object> params = new ArrayList<Object>();
        for (int i = 0; i < widgetFields.size(); i++) {
            if (widgetFields.get(i) instanceof Text) {
                if (((Text) widgetFields.get(i)).getText().compareTo("") == 0)
                    params.add(query.getOptions().get(i).getDefaultValue());
                else
                    params.add(Integer.parseInt(((Text) widgetFields.get(i))
                        .getText()));
            } else if (widgetFields.get(i) instanceof Combo) {
                Combo tempCombo = (Combo) widgetFields.get(i);
                // would rather return a daterange but basic combo (necessary
                // since jface comboviewer is not a widget) won't let me
                // DateRange range
                // =tempCombo.getItem(tempCombo.getSelectionIndex());
                String range = tempCombo.getItem(tempCombo.getSelectionIndex());
                params.add(range);
            } else if (widgetFields.get(i) instanceof DateTimeWidget)
                params.add(((DateTimeWidget) widgetFields.get(i)).getDate());
            else if (widgetFields.get(i) instanceof FileBrowser) {
                String csv = ((FileBrowser) widgetFields.get(i)).getText();
                if (csv != null) {
                    StringTokenizer st = new StringTokenizer(csv, ", \n");
                    while (st.hasMoreTokens()) {
                        String token = st.nextToken();
                        if (DateFormatter.parseToDate(token) == null)
                            try {
                                params.add(Integer.parseInt(token));
                            } catch (NumberFormatException e) {
                                params.add("%" + token + "%");
                            }
                        else {
                            // Calendar c = Calendar.getInstance();
                            // c.setTime();
                            params.add(DateFormatter.parseToDate(token));
                        }
                    }
                }
            } else if (widgetFields.get(i) instanceof AutoTextWidget) {
                params.add(((AutoTextWidget) widgetFields.get(i)).getText());
            }
        }
        List<Option> queryOptions = query.getOptions();
        for (int i = 0; i < queryOptions.size() && i < params.size(); i++) {
            Option option = queryOptions.get(i);
            if (params.get(i) == null)
                params.set(i, option.getDefaultValue());
        }

        return params;
    }

    public boolean printTable(Boolean export) throws Exception {
        boolean doPrint;
        if (export)
            doPrint = MessageDialog.openQuestion(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell(), "Confirm",
                "Export table contents?");
        else
            doPrint = MessageDialog.openQuestion(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell(), "Confirm",
                "Print table contents?");
        if (doPrint) {
            List<Object[]> params = new ArrayList<Object[]>();
            List<Object> paramVals = getParams();
            List<Option> queryOptions = query.getOptions();
            int i = 0;
            for (Option option : queryOptions) {
                params.add(new Object[] { option.getName(), paramVals.get(i) });
                i++;
            }
            List<String> columnInfo = new ArrayList<String>();
            String[] names = query.getColumnNames();
            for (int i1 = 0; i1 < names.length; i1++) {
                columnInfo.add(names[i1]);
            }

            List<Map<String, String>> listData = new ArrayList<Map<String, String>>();
            for (Object object : reportData) {
                Map<String, String> map = new HashMap<String, String>();
                for (int j = 0; j < columnInfo.size(); j++) {
                    map.put(columnInfo.get(j), (((Object[]) object)[j])
                        .toString());
                }
                listData.add(map);
            }
            if (export) {
                FileDialog fd = new FileDialog(exportButton.getShell(),
                    SWT.SAVE);
                fd.setOverwrite(true);
                fd.setText("Export as");
                String[] filterExt = { "*.csv", "*.pdf" };
                fd.setFilterExtensions(filterExt);
                fd.setFileName(query.getName() + "_"
                    + DateFormatter.formatAsDate(new Date()));
                String path = fd.open();
                if (path == null)
                    throw new Exception("Printing Canceled.");
                if (path.endsWith(".pdf"))
                    ReportingUtils.saveReport(createDynamicReport(query
                        .getName(), params, columnInfo, listData), path);
                else {
                    // csv
                    File file = new File(path);
                    FileWriter bw = new FileWriter(file);
                    // write title
                    bw.write("#" + query.getName() + "\r");
                    // write params
                    for (Object[] ob : params)
                        bw.write("#" + ob[0] + ":" + ob[1] + "\r");
                    // write columnnames
                    bw.write("#\r#" + columnInfo.get(0));
                    for (int j = 1; j < columnInfo.size(); j++) {
                        bw.write("," + columnInfo.get(j));
                    }
                    bw.write("\r");
                    for (Map<String, String> ob : listData) {
                        bw.write("\"" + ob.get(columnInfo.get(0)) + "\"");
                        for (int j = 1; j < columnInfo.size(); j++) {
                            bw.write(",\"" + ob.get(columnInfo.get(j)) + "\"");
                        }
                        bw.write("\r");
                    }
                    bw.close();
                }
            } else
                ReportingUtils.printReport(createDynamicReport(query.getName(),
                    params, columnInfo, listData));

            return true;
        }
        return false;
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
        // TODO Auto-generated method stub

    }

    @Override
    public void doSaveAs() {
        // TODO Auto-generated method stub

    }

    @Override
    public void init(IEditorSite site, IEditorInput input)
        throws PartInitException {
        setSite(site);
        setInput(input);

        reportData = new ArrayList<Object>();
        queryClass = ((ReportInput) input).query;
        SiteWrapper siteWrap = SessionManager.getInstance()
            .getCurrentSiteWrapper();
        String op = "=";
        if (siteWrap.getName().compareTo("All Sites") == 0)
            op = "!=";
        try {
            query = queryClass.getConstructor(String.class, Integer.class)
                .newInstance(new Object[] { op, siteWrap.getId() });
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.setPartName(query.getName());

        columnWidths = new HashMap<Class<?>, int[]>();
        columnWidths.put(CabinetCSamples.class, new int[] { 100, 100, 100 });
        columnWidths.put(CabinetDSamples.class,
            new int[] { 100, 100, 100, 100 });
        columnWidths.put(CabinetSSamples.class, new int[] { 100, 100 });
        columnWidths.put(FreezerCSamples.class, new int[] { 100, 100, 100 });
        columnWidths.put(FreezerDSamples.class,
            new int[] { 100, 100, 100, 100 });
        columnWidths.put(FreezerSSamples.class, new int[] { 100, 100 });
        columnWidths.put(FvLPatientVisits.class,
            new int[] { 100, 100, 100, 100 });
        columnWidths.put(NewPsByStudyClinic.class, new int[] { 100, 100, 100,
            100 });
        columnWidths.put(NewPVsByStudyClinic.class, new int[] { 100, 100, 100,
            100 });
        columnWidths.put(PatientVisitSummary.class, new int[] { 100, 100, 100,
            100, 100, 100, 100, 100, 100 });
        columnWidths.put(PatientWBC.class, new int[] { 100, 100, 100, 100 });
        columnWidths.put(QACabinetSamples.class, new int[] { 100, 100, 100,
            100, 100, 100 });
        columnWidths.put(QAFreezerSamples.class, new int[] { 100, 100, 100,
            100, 100, 100 });
        columnWidths.put(SampleCount.class, new int[] { 100, 100 });
        columnWidths.put(SampleInvoiceByClinic.class, new int[] { 100, 100,
            100, 100 });
        columnWidths.put(SampleInvoiceByPatient.class, new int[] { 100, 100,
            100, 100 });
        columnWidths.put(SampleRequest.class, new int[] { 100, 100, 100, 100,
            100 });
        columnWidths.put(SampleSCount.class, new int[] { 100, 100, 100 });
        columnWidths = Collections.unmodifiableMap(columnWidths);
    }

    @Override
    public boolean isDirty() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    @Override
    public void createPartControl(Composite parent) {
        top = new Composite(parent, SWT.NONE);
        top.setLayout(new GridLayout());

        SiteWrapper site = SessionManager.getInstance().getCurrentSiteWrapper();
        List<Option> queryOptions = query.getOptions();
        textLabels = new ArrayList<Label>();
        widgetFields = new ArrayList<Widget>();

        if (parameterSection != null)
            parameterSection.dispose();

        parameterSection = new Composite(top, SWT.NONE);
        GridData pgd = new GridData();
        pgd.grabExcessHorizontalSpace = true;
        pgd.horizontalAlignment = SWT.FILL;
        parameterSection.setLayout(new GridLayout());
        parameterSection.setLayoutData(pgd);

        buttonSection = new Composite(top, SWT.NONE);
        GridLayout gl = new GridLayout();
        gl.numColumns = 3;
        buttonSection.setLayout(gl);

        generateButton = new Button(buttonSection, SWT.NONE);
        generateButton.setText("Generate");
        generateButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                generate();
            }
        });

        printButton = new Button(buttonSection, SWT.NONE);
        printButton.setImage(BioBankPlugin.getDefault().getImageRegistry().get(
            BioBankPlugin.IMG_PRINTER));
        printButton.setText("Print");
        printButton.setEnabled(false);
        printButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    printTable(false);
                } catch (Exception ex) {
                    BioBankPlugin.openAsyncError(
                        "Error while printing the results", ex);
                }
            }
        });

        exportButton = new Button(buttonSection, SWT.NONE);
        exportButton.setText("Export");
        exportButton.setEnabled(false);
        exportButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    printTable(true);
                } catch (Exception ex) {
                    BioBankPlugin.openAsyncError(
                        "Error while exporting the results", ex);
                }
            }
        });

        Label description = new Label(parameterSection, SWT.NONE);
        description.setText("Description: " + query.getDescription());
        GridData gd2 = new GridData();
        gd2.horizontalSpan = 2;
        description.setLayoutData(gd2);

        for (int i = 0; i < queryOptions.size(); i++) {
            Option option = queryOptions.get(i);
            Label fieldLabel = new Label(parameterSection, SWT.NONE);
            fieldLabel.setText(option.getName() + ":");
            textLabels.add(fieldLabel);
            Widget widget;

            if (option.getType() == DateRange.class) {
                widget = new Combo(parameterSection, SWT.READ_ONLY);
                Object values[] = DateRange.values();
                for (int j = 0; j < values.length; j++)
                    ((Combo) widget).add(values[j].toString());
                ((Combo) widget).select(0);
            } else if (option.getType() == Date.class)
                widget = new DateTimeWidget(parameterSection, SWT.NONE, null);
            else if (option.getType() == String.class) {
                if (option.getName().compareTo("Aliquot Type") == 0)
                    try {
                        widget = new AutoTextWidget(parameterSection, SWT.None,
                            site.getAllSampleTypeCollection(true),
                            SampleTypeWrapper.class);
                    } catch (ApplicationException e1) {
                        widget = new FileBrowser(parameterSection, SWT.NONE);
                    }
                else
                    widget = new FileBrowser(parameterSection, SWT.NONE);
            } else if (option.getType() == Integer.class) {
                widget = new Text(parameterSection, SWT.BORDER);
            } else
                widget = null;
            widgetFields.add(widget);
        }

        // update parents
        createEmptyReportTable();
        parameterSection.moveAbove(buttonSection);
        top.layout(true, true);

    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub

    }

}
