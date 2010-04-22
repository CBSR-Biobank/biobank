package edu.ualberta.med.biobank.forms;

import java.awt.Color;
import java.io.FileWriter;
import java.io.PrintWriter;
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
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
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
import ar.com.fdvs.dj.domain.AutoText;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Transparency;
import ar.com.fdvs.dj.domain.constants.VerticalAlign;
import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.reports.AliquotCount;
import edu.ualberta.med.biobank.common.reports.AliquotInvoiceByClinic;
import edu.ualberta.med.biobank.common.reports.AliquotInvoiceByPatient;
import edu.ualberta.med.biobank.common.reports.AliquotRequest;
import edu.ualberta.med.biobank.common.reports.AliquotSCount;
import edu.ualberta.med.biobank.common.reports.CabinetCAliquots;
import edu.ualberta.med.biobank.common.reports.CabinetDAliquots;
import edu.ualberta.med.biobank.common.reports.CabinetSAliquots;
import edu.ualberta.med.biobank.common.reports.FreezerCAliquots;
import edu.ualberta.med.biobank.common.reports.FreezerDAliquots;
import edu.ualberta.med.biobank.common.reports.FreezerSAliquots;
import edu.ualberta.med.biobank.common.reports.FvLPatientVisits;
import edu.ualberta.med.biobank.common.reports.NewPVsByStudyClinic;
import edu.ualberta.med.biobank.common.reports.NewPsByStudyClinic;
import edu.ualberta.med.biobank.common.reports.PVsByStudy;
import edu.ualberta.med.biobank.common.reports.PatientVisitSummary;
import edu.ualberta.med.biobank.common.reports.PatientWBC;
import edu.ualberta.med.biobank.common.reports.PsByStudy;
import edu.ualberta.med.biobank.common.reports.QACabinetAliquots;
import edu.ualberta.med.biobank.common.reports.QAFreezerAliquots;
import edu.ualberta.med.biobank.common.reports.QueryObject;
import edu.ualberta.med.biobank.common.reports.ReportTreeNode;
import edu.ualberta.med.biobank.common.reports.SampleTypeSUsage;
import edu.ualberta.med.biobank.common.reports.QueryObject.DateGroup;
import edu.ualberta.med.biobank.common.reports.QueryObject.Option;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.forms.input.ReportInput;
import edu.ualberta.med.biobank.reporting.ReportingUtils;
import edu.ualberta.med.biobank.views.ReportsView;
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

    private ScrolledComposite sc;

    private QueryObject query;

    private ReportTreeNode node;

    private static Map<Class<?>, int[]> columnWidths;

    static {
        Map<Class<?>, int[]> aMap = new HashMap<Class<?>, int[]>();
        aMap = new HashMap<Class<?>, int[]>();
        aMap.put(CabinetCAliquots.class, new int[] { 100, 100, 100 });
        aMap.put(CabinetDAliquots.class, new int[] { 100, 100, 100, 100 });
        aMap.put(CabinetSAliquots.class, new int[] { 100, 100 });
        aMap.put(FreezerCAliquots.class, new int[] { 100, 100, 100 });
        aMap.put(FreezerDAliquots.class, new int[] { 100, 100, 100, 100 });
        aMap.put(FreezerSAliquots.class, new int[] { 100, 100 });
        aMap.put(FvLPatientVisits.class, new int[] { 100, 100, 100, 100, 100 });
        aMap.put(NewPsByStudyClinic.class, new int[] { 100, 100, 100, 100 });
        aMap.put(NewPVsByStudyClinic.class, new int[] { 100, 100, 100, 100 });
        aMap.put(PsByStudy.class, new int[] { 100, 100, 100 });
        aMap.put(PVsByStudy.class, new int[] { 100, 100, 100 });
        aMap.put(PatientVisitSummary.class, new int[] { 100, 100, 100, 100,
            100, 100, 100, 100, 100 });
        aMap.put(PatientWBC.class, new int[] { 100, 100, 100, 100, 100, 100,
            100 });
        aMap.put(QACabinetAliquots.class, new int[] { 100, 100, 100, 100, 100,
            100 });
        aMap.put(QAFreezerAliquots.class, new int[] { 100, 100, 100, 100, 100,
            100 });
        aMap.put(AliquotCount.class, new int[] { 100, 100 });
        aMap
            .put(AliquotInvoiceByClinic.class, new int[] { 100, 100, 150, 100 });
        aMap.put(AliquotInvoiceByPatient.class,
            new int[] { 100, 100, 150, 100 });
        aMap.put(AliquotRequest.class, new int[] { 100, 100, 100, 100, 100 });
        aMap.put(AliquotSCount.class, new int[] { 100, 150, 100 });
        aMap.put(SampleTypeSUsage.class, new int[] { 150, 100 });
        columnWidths = Collections.unmodifiableMap(aMap);
    }

    private void generate() {

        final ArrayList<Object> params = getParams();

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
                                    .getCurrentSite();
                                String op = "=";
                                if (site.getName().compareTo("All Sites") == 0)
                                    op = "!=";
                                query = (QueryObject) ((Class<?>) node
                                    .getQuery()).getConstructor(String.class,
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
                            gd.grabExcessVerticalSpace = false;
                            gd.horizontalSpan = 2;
                            gd.horizontalAlignment = SWT.FILL;
                            gd.verticalAlignment = SWT.FILL;
                            reportTable.setLayoutData(gd);
                            top.layout();
                            updateScrollBars();
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

    private ArrayList<Object> getParams() {
        ArrayList<Object> params = new ArrayList<Object>();
        for (int i = 0; i < widgetFields.size(); i++) {
            if (widgetFields.get(i) instanceof Text) {
                if (((Text) widgetFields.get(i)).getText().compareTo("") == 0)
                    params.add(query.getOptions().get(i).getDefaultValue());
                else
                    try {
                        params.add(Integer
                            .parseInt(((Text) widgetFields.get(i)).getText()));
                    } catch (NumberFormatException e) {
                        BioBankPlugin
                            .openAsyncError("Invalid Number Format",
                                "Please enter a valid number. Searching with default value...");
                        params.add(query.getOptions().get(i).getDefaultValue());
                    }
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
                fd.setFileName(query.getName().replaceAll(" ", "_") + "_"
                    + DateFormatter.formatAsDate(new Date()));
                String path = fd.open();
                if (path == null)
                    throw new Exception("Exporting canceled.");
                if (path.endsWith(".pdf"))
                    ReportingUtils.saveReport(createDynamicReport(query
                        .getName(), params, columnInfo, listData), path);
                else {
                    // csv
                    PrintWriter bw = new PrintWriter(new FileWriter(path));
                    // write title
                    bw.println("#" + query.getName());
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
                    for (Map<String, String> ob : listData) {
                        bw.write("\"" + ob.get(columnInfo.get(0)) + "\"");
                        for (int j = 1; j < columnInfo.size(); j++) {
                            bw.write(",\"" + ob.get(columnInfo.get(j)) + "\"");
                        }
                        bw.println();
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
        drb.addAutoText(AutoText.AUTOTEXT_PAGE_X_OF_Y,
            AutoText.POSITION_FOOTER, AutoText.ALIGNMENT_RIGHT, 200, 40);
        drb.addAutoText("Printed on "
            + DateFormatter.formatAsDateTime(new Date()),
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
        node = ((ReportInput) input).node;
        SiteWrapper siteWrap = SessionManager.getInstance().getCurrentSite();
        String op = "=";
        if (siteWrap.getName().compareTo("All Sites") == 0)
            op = "!=";
        try {
            query = (QueryObject) ((Class<?>) node.getQuery()).getConstructor(
                String.class, Integer.class).newInstance(
                new Object[] { op, siteWrap.getId() });
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.setPartName(query.getName());
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
        sc = new ScrolledComposite(parent, SWT.V_SCROLL);
        sc.setLayout(new GridLayout(1, false));
        sc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        sc.setExpandHorizontal(true);
        sc.setExpandVertical(true);

        top = new Composite(sc, SWT.NONE);
        top.setLayout(new GridLayout());

        SiteWrapper site = SessionManager.getInstance().getCurrentSite();
        List<Option> queryOptions = query.getOptions();
        textLabels = new ArrayList<Label>();
        widgetFields = new ArrayList<Widget>();

        Label description = new Label(top, SWT.NONE);
        description.setText("Description: " + query.getDescription());
        GridData gd2 = new GridData();
        gd2.horizontalSpan = 2;
        description.setLayoutData(gd2);

        if (parameterSection != null)
            parameterSection.dispose();

        parameterSection = new Composite(top, SWT.NONE);
        GridData pgd = new GridData();
        GridLayout pgl = new GridLayout(2, false);
        pgd.grabExcessHorizontalSpace = true;
        parameterSection.setLayout(pgl);
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

        for (int i = 0; i < queryOptions.size(); i++) {
            Option option = queryOptions.get(i);
            Label fieldLabel = new Label(parameterSection, SWT.NONE);
            fieldLabel.setText(option.getName() + ":");
            textLabels.add(fieldLabel);
            Widget widget;
            GridData widgetData = new GridData();
            widgetData.horizontalAlignment = SWT.FILL;

            if (option.getType() == DateGroup.class) {
                widget = new Combo(parameterSection, SWT.READ_ONLY);
                Object values[] = DateGroup.values();
                for (int j = 0; j < values.length; j++)
                    ((Combo) widget).add(values[j].toString());
                ((Combo) widget).select(0);
            } else if (option.getType() == Date.class)
                widget = new DateTimeWidget(parameterSection, SWT.NONE, null);
            else if (option.getType() == String.class) {
                if (option.getName().compareTo("Sample Type") == 0)
                    try {
                        widget = new AutoTextWidget(parameterSection, SWT.NONE,
                            site.getAllSampleTypeCollection(true),
                            SampleTypeWrapper.class);
                        ((AutoTextWidget) widget).setLayoutData(widgetData);
                    } catch (ApplicationException e1) {
                        widget = new FileBrowser(parameterSection, SWT.NONE);
                    }
                else
                    widget = new FileBrowser(parameterSection, SWT.NONE);
            } else if (option.getType() == Integer.class
                || option.getType() == Double.class) {
                widget = new Text(parameterSection, SWT.BORDER);
                ((Text) widget).setText("0");
                ((Text) widget).setLayoutData(widgetData);
            } else
                widget = null;
            widgetFields.add(widget);
        }

        // update parents
        createEmptyReportTable();
        parameterSection.moveAbove(buttonSection);
        top.layout(true, true);

        top.layout();
        sc.setContent(top);
        sc.setMinSize(top.computeSize(SWT.DEFAULT, SWT.DEFAULT));

    }

    public void updateScrollBars() {
        sc.layout(true, true);
        sc.setMinSize(top.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }

    @Override
    public void setFocus() {
        ReportsView.getTree().setSelection(new StructuredSelection(node));
    }
}
