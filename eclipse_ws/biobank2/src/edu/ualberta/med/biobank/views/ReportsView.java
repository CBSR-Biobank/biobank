package edu.ualberta.med.biobank.views;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
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
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

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
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.reporting.ReportingUtils;
import edu.ualberta.med.biobank.widgets.AutoTextWidget;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.widgets.FileBrowser;
import edu.ualberta.med.biobank.widgets.SmartCombo;
import edu.ualberta.med.biobank.widgets.infotables.SearchResultsInfoTable;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ReportsView extends ViewPart {

    public static BiobankLogger logger = BiobankLogger
        .getLogger(ReportsView.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.views.ReportsView";

    private ScrolledComposite sc;
    private Composite top;
    private Composite header;
    private Composite subSection;

    private SmartCombo querySelect;
    private List<Widget> widgetFields;
    private List<Label> textLabels;

    private Button generateButton;
    private List<Object> reportData;
    private SearchResultsInfoTable reportTable;

    private Button printButton;
    private Button exportButton;

    private QueryObject currentQuery;
    private HashMap<Class<?>, int[]> columnWidths;

    public ReportsView() {
        reportData = new ArrayList<Object>();
        columnWidths = new HashMap<Class<?>, int[]>();
        columnWidths.put(CabinetCSamples.class, new int[] { 50, 50, 50, 50 });
        columnWidths.put(CabinetDSamples.class, new int[] { 50, 50, 50, 50 });
        columnWidths.put(CabinetSSamples.class, new int[] { 50, 50, 50, 50 });
        columnWidths.put(FreezerCSamples.class, new int[] { 50, 50, 50, 50 });
        columnWidths.put(FreezerDSamples.class, new int[] { 50, 50, 50, 50 });
        columnWidths.put(FreezerSSamples.class, new int[] { 50, 50, 50, 50 });
        columnWidths.put(FvLPatientVisits.class, new int[] { 50, 50, 50, 50 });
        columnWidths
            .put(NewPsByStudyClinic.class, new int[] { 50, 50, 50, 50 });
        columnWidths.put(NewPVsByStudyClinic.class,
            new int[] { 50, 50, 50, 50 });
        columnWidths.put(PatientVisitSummary.class,
            new int[] { 50, 50, 50, 50 });
        columnWidths.put(PatientWBC.class, new int[] { 50, 50, 50, 50 });
        columnWidths.put(QACabinetSamples.class, new int[] { 50, 50, 50, 50 });
        columnWidths.put(QAFreezerSamples.class, new int[] { 50, 50, 50, 50 });
        columnWidths.put(SampleCount.class, new int[] { 50, 50, 50, 50 });
        columnWidths.put(SampleInvoiceByClinic.class, new int[] { 50, 50, 50,
            50 });
        columnWidths.put(SampleInvoiceByPatient.class, new int[] { 50, 50, 50,
            50 });
        columnWidths.put(SampleRequest.class, new int[] { 50, 50, 50, 50 });
        columnWidths.put(SampleSCount.class, new int[] { 50, 50, 50, 50 });
    }

    @Override
    public void createPartControl(Composite parent) {
        sc = new ScrolledComposite(parent, SWT.V_SCROLL);
        sc.setLayout(new GridLayout(1, false));
        sc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        sc.setExpandHorizontal(true);
        sc.setExpandVertical(true);

        top = new Composite(sc, SWT.NONE);
        top.setLayout(new GridLayout(1, false));
        top.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        header = new Composite(top, SWT.NONE);
        header.setLayout(new GridLayout(4, false));

        querySelect = createCombo(header);
        querySelect.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    comboChanged();
                } catch (Exception ex) {
                    BioBankPlugin.openAsyncError("Error",
                        "There was an error while building page.");
                }
            }
        });

        generateButton = new Button(header, SWT.NONE);
        generateButton.setText("Generate");
        generateButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                generate();
            }
        });

        printButton = new Button(header, SWT.NONE);
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

        exportButton = new Button(header, SWT.NONE);
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

        // create the query's display here
        subSection = new Composite(top, SWT.NONE);
        createEmptyReportTable();
        top.layout();
        sc.setContent(top);
        sc.setMinSize(top.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        try {
            comboChanged();
        } catch (ApplicationException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    private void createEmptyReportTable() {
        if (reportTable != null) {
            reportTable.dispose();
        }
        reportTable = new SearchResultsInfoTable(top, null,
            new String[] { " " }, new int[] { 500 });
    }

    private void generate() {
        try {
            String typeSelection = querySelect.getSelection();
            Class<? extends QueryObject> cls = QueryObject
                .getQueryObjectByName(typeSelection);
            Constructor<?> c = cls.getConstructor(String.class, Integer.class);
            SiteWrapper site = SessionManager.getInstance()
                .getCurrentSiteWrapper();
            String op = "=";
            if (site.getName().compareTo("All Sites") == 0)
                op = "!=";
            currentQuery = (QueryObject) c.newInstance(new Object[] { op,
                site.getId() });
            final ArrayList<Object> params = getParams();

            // we dont want the user to change options while the search is in
            // progress
            setEnabled(false);

            IRunnableContext context = new ProgressMonitorDialog(Display
                .getDefault().getActiveShell());
            context.run(true, true, new IRunnableWithProgress() {
                @Override
                public void run(final IProgressMonitor monitor) {
                    Thread t = new Thread("Querying") {
                        @Override
                        public void run() {
                            try {
                                reportData = currentQuery.executeQuery(
                                    SessionManager.getAppService(), params);
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
                            if (reportData.size() > 0) {
                                reportTable.dispose();
                                reportTable = new SearchResultsInfoTable(top,
                                    reportData, currentQuery.getColumnNames(),
                                    columnWidths.get(currentQuery.getClass()));
                                printButton.setEnabled(true);
                                exportButton.setEnabled(true);
                            } else {
                                printButton.setEnabled(false);
                                exportButton.setEnabled(false);
                            }
                            setEnabled(true);
                            top.layout();

                        }
                    });

                }
            });
        } catch (Exception e) {
            BioBankPlugin.openAsyncError("Error while querying for results", e);
        }
    }

    private void setEnabled(boolean enabled) {
        SessionManager.getInstance().getSiteCombo().setEnabled(enabled);
        querySelect.setEnabled(enabled);
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
                    params.add(currentQuery.getOptions().get(i)
                        .getDefaultValue());
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
        List<Option> queryOptions = currentQuery.getOptions();
        for (int i = 0; i < queryOptions.size() && i < params.size(); i++) {
            Option option = queryOptions.get(i);
            if (params.get(i) == null)
                params.set(i, option.getDefaultValue());
        }

        return params;
    }

    public void comboChanged() throws ApplicationException {
        String typeSelection = querySelect.getSelection();
        SiteWrapper site = SessionManager.getInstance().getCurrentSiteWrapper();
        String op = "=";
        if (site.getName().compareTo("All Sites") == 0)
            op = "!=";
        try {
            Class<? extends QueryObject> cls = QueryObject
                .getQueryObjectByName(typeSelection);
            Constructor<?> c = cls.getConstructor(String.class, Integer.class);
            currentQuery = (QueryObject) c.newInstance(new Object[] { op,
                site.getId() });
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Option> queryOptions = currentQuery.getOptions();
        textLabels = new ArrayList<Label>();
        widgetFields = new ArrayList<Widget>();

        if (subSection != null)
            subSection.dispose();

        subSection = new Composite(top, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        subSection.setLayout(layout);

        Label description = new Label(subSection, SWT.NONE);
        description.setText("Description: " + currentQuery.getDescription());
        GridData gd2 = new GridData();
        gd2.horizontalSpan = 2;
        description.setLayoutData(gd2);

        for (int i = 0; i < queryOptions.size(); i++) {
            Option option = queryOptions.get(i);
            Label fieldLabel = new Label(subSection, SWT.NONE);
            fieldLabel.setText(option.getName() + ":");
            textLabels.add(fieldLabel);
            Widget widget;

            if (option.getType() == DateRange.class) {
                widget = new Combo(subSection, SWT.READ_ONLY);
                Object values[] = DateRange.values();
                for (int j = 0; j < values.length; j++)
                    ((Combo) widget).add(values[j].toString());
                ((Combo) widget).select(0);
            } else if (option.getType() == Date.class)
                widget = new DateTimeWidget(subSection, SWT.NONE, null);
            else if (option.getType() == String.class) {
                if (option.getName().compareTo("Sample Type") == 0)
                    widget = new AutoTextWidget(subSection, SWT.None, site
                        .getAllSampleTypeCollection(true),
                        SampleTypeWrapper.class);
                else
                    widget = new FileBrowser(subSection, SWT.NONE);
            } else if (option.getType() == Integer.class) {
                widget = new Text(subSection, SWT.BORDER);
            } else
                widget = null;
            widgetFields.add(widget);
        }

        subSection.moveBelow(header);
        subSection.setVisible(true);

        // update parents
        resetSearch();
        updateScrollBars();
        top.layout(true, true);

    }

    public void updateScrollBars() {
        sc.layout(true, true);
        sc.setMinSize(top.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }

    public void resetSearch() {
        Assert.isNotNull(reportTable);
        createEmptyReportTable();
        reportData = new ArrayList<Object>();

        printButton.setEnabled(false);
        exportButton.setEnabled(false);
    }

    protected static SmartCombo createCombo(Composite parent) {
        SmartCombo combo = new SmartCombo(parent, SWT.NONE);

        GridData combodata = new GridData();
        combodata.widthHint = 250;
        combo.setLayoutData(combodata);

        combo.setInput(QueryObject.getQueryObjectNames());
        return combo;
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
            List<Option> queryOptions = currentQuery.getOptions();
            int i = 0;
            for (Option option : queryOptions) {
                params.add(new Object[] { option.getName(), paramVals.get(i) });
                i++;
            }
            List<String> columnInfo = new ArrayList<String>();
            String[] names = currentQuery.getColumnNames();
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
                fd.setFileName(currentQuery.toString() + "_"
                    + DateFormatter.formatAsDate(new Date()));
                String path = fd.open();
                if (path == null)
                    throw new Exception("Printing Canceled.");
                if (path.endsWith(".pdf"))
                    ReportingUtils.saveReport(createDynamicReport(currentQuery
                        .toString(), params, columnInfo, listData), path);
                else {
                    // csv
                    File file = new File(path);
                    FileWriter bw = new FileWriter(file);
                    // write title
                    bw.write("#" + currentQuery.getName() + "\r");
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
                ReportingUtils.printReport(createDynamicReport(currentQuery
                    .toString(), params, columnInfo, listData));

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
    public void setFocus() {

    }
}
