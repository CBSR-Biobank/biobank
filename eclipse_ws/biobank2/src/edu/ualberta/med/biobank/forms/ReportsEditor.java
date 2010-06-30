package edu.ualberta.med.biobank.forms;

import java.awt.Color;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

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
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Widget;
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
import edu.ualberta.med.biobank.common.reports.AliquotCount;
import edu.ualberta.med.biobank.common.reports.AliquotInvoiceByClinic;
import edu.ualberta.med.biobank.common.reports.AliquotInvoiceByPatient;
import edu.ualberta.med.biobank.common.reports.AliquotRequest;
import edu.ualberta.med.biobank.common.reports.AliquotSCount;
import edu.ualberta.med.biobank.common.reports.AliquotsByPallet;
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
import edu.ualberta.med.biobank.common.reports.SampleTypePvCount;
import edu.ualberta.med.biobank.common.reports.SampleTypeSUsage;
import edu.ualberta.med.biobank.common.reports.QueryObject.DateGroup;
import edu.ualberta.med.biobank.common.reports.QueryObject.Option;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.forms.input.ReportInput;
import edu.ualberta.med.biobank.reporting.ReportingUtils;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.validators.DoubleNumberValidator;
import edu.ualberta.med.biobank.validators.IntegerNumberValidator;
import edu.ualberta.med.biobank.views.ReportsView;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.widgets.FileBrowser;
import edu.ualberta.med.biobank.widgets.infotables.ReportTableWidget;

public class ReportsEditor extends BiobankFormBase {

    public static String ID = "edu.ualberta.med.biobank.editors.ReportsEditor";

    // private Composite top;
    private Composite buttonSection;
    private Composite parameterSection;

    private ReportTableWidget<Object> reportTable;
    private List<Widget> widgetFields;
    private List<Label> textLabels;

    private Button generateButton;
    private List<Object> reportData;

    private Button printButton;
    private Button exportButton;

    private QueryObject query;

    private ReportTreeNode node;

    private IObservableValue statusObservable;

    private IObservableValue comboStatus = new WritableValue(Boolean.FALSE,
        Boolean.class);

    private ArrayList<Object> params;

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
        aMap.put(AliquotsByPallet.class, new int[] { 100, 100, 100, 100 });
        aMap.put(AliquotCount.class, new int[] { 100, 100 });
        aMap
            .put(AliquotInvoiceByClinic.class, new int[] { 100, 100, 150, 100 });
        aMap.put(AliquotInvoiceByPatient.class,
            new int[] { 100, 100, 150, 100 });
        aMap.put(AliquotRequest.class, new int[] { 100, 100, 100, 100, 100 });
        aMap.put(AliquotSCount.class, new int[] { 100, 150, 100 });
        aMap
            .put(SampleTypePvCount.class, new int[] { 100, 100, 100, 100, 100 });
        aMap.put(SampleTypeSUsage.class, new int[] { 150, 100 });
        columnWidths = Collections.unmodifiableMap(aMap);
    }

    private void generate() {

        try {
            params = getParams();
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
                            } catch (Exception e) {
                                reportData = new ArrayList<Object>();
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
                            if (reportData.size() == -1)
                                printButton.setEnabled(false);
                            reportTable = new ReportTableWidget<Object>(form
                                .getBody(), reportData, query.getColumnNames(),
                                columnWidths.get(query.getClass()), 24);
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
        reportTable = new ReportTableWidget<Object>(form.getBody(), null,
            new String[] { " " }, new int[] { 500 });
        reportTable.adaptToToolkit(toolkit, true);
        form.layout(true, true);
    }

    public void resetSearch() {
        Assert.isNotNull(reportTable);
        createEmptyReportTable();
        reportData = new ArrayList<Object>();

        printButton.setEnabled(false);
        exportButton.setEnabled(false);
    }

    private ArrayList<Object> getParams() throws Exception {
        ArrayList<Object> params = new ArrayList<Object>();
        List<Option> queryOptions = query.getOptions();
        for (int i = 0; i < widgetFields.size(); i++) {
            if (widgetFields.get(i) instanceof BiobankText) {
                if (queryOptions.get(i).getName().compareTo("Pallet Label") == 0) {
                    params.add(((BiobankText) widgetFields.get(i)).getText());
                } else if (((BiobankText) widgetFields.get(i)).getText()
                    .compareTo("") == 0)
                    params.add(query.getOptions().get(i).getDefaultValue());
                else
                    try {
                        params.add(Integer.parseInt(((BiobankText) widgetFields
                            .get(i)).getText()));
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
                    StringTokenizer stnewline = new StringTokenizer(csv, "\n");
                    int lines = 0;
                    while (stnewline.hasMoreTokens()) {
                        StringTokenizer stseparator = new StringTokenizer(
                            stnewline.nextToken(), ",\" ");
                        lines++;
                        if (stseparator.countTokens() != 4)
                            throw new Exception("Failed to parse CSV: Line "
                                + lines + " \n4 Columns Required: "
                                + stseparator.countTokens() + " found.");
                        else {
                            while (stseparator.hasMoreTokens())
                                params.add(stseparator.nextToken());
                        }
                    }
                }
            }
        }

        for (int i = 0; i < queryOptions.size() && i < params.size(); i++) {
            Option option = queryOptions.get(i);
            if (params.get(i) == null)
                params.set(i, option.getDefaultValue());
        }

        return params;
    }

    public void printTable(final Boolean export) throws Exception {
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
            final List<Object[]> params = new ArrayList<Object[]>();
            final List<Object> paramVals = getParams();
            List<Option> queryOptions = query.getOptions();
            int i = 0;
            for (Option option : queryOptions) {
                params.add(new Object[] { option.getName(), paramVals.get(i) });
                i++;
            }
            final List<String> columnInfo = new ArrayList<String>();
            String[] names = query.getColumnNames();
            for (int i1 = 0; i1 < names.length; i1++) {
                columnInfo.add(names[i1]);
            }
            final List<Map<String, String>> listData = new ArrayList<Map<String, String>>();
            IRunnableContext context = new ProgressMonitorDialog(Display
                .getDefault().getActiveShell());
            context.run(true, true, new IRunnableWithProgress() {
                @Override
                public void run(final IProgressMonitor monitor) {
                    monitor.beginTask("Preparing Report...",
                        IProgressMonitor.UNKNOWN);
                    try {
                        for (Object object : reportData) {
                            if (monitor.isCanceled()) {
                                throw new Exception("Exporting canceled.");
                            }
                            Map<String, String> map = new HashMap<String, String>();
                            for (int j = 0; j < columnInfo.size(); j++) {
                                map.put(columnInfo.get(j),
                                    (((Object[]) object)[j]).toString());
                            }
                            listData.add(map);
                        }
                    } catch (Exception e) {
                        BioBankPlugin.openAsyncError("Error exporting results",
                            e);
                        return;
                    }
                    Display.getDefault().syncExec(new Runnable() {
                        @Override
                        public void run() {
                            monitor.done();
                            if (export) {
                                FileDialog fd = new FileDialog(exportButton
                                    .getShell(), SWT.SAVE);
                                fd.setOverwrite(true);
                                fd.setText("Export as");
                                String[] filterExt = { "*.csv", "*.pdf" };
                                fd.setFilterExtensions(filterExt);
                                fd.setFileName(query.getName().replaceAll(" ",
                                    "_")
                                    + "_"
                                    + DateFormatter.formatAsDate(new Date()));
                                final String path = fd.open();
                                if (path == null) {
                                    BioBankPlugin.openAsyncError(
                                        "Exporting canceled.",
                                        "Select a valid path and try again.");
                                    return;
                                }
                                if (path.endsWith(".pdf"))
                                    try {
                                        ReportingUtils.saveReport(
                                            createDynamicReport(
                                                query.getName(), params,
                                                columnInfo, listData), path);
                                    } catch (Exception e) {
                                        BioBankPlugin.openAsyncError(
                                            "Error saving to PDF", e);
                                        return;
                                    }
                                else {
                                    // csv
                                    PrintWriter bw = null;
                                    try {
                                        bw = new PrintWriter(new FileWriter(
                                            path));
                                    } catch (IOException e) {
                                        BioBankPlugin.openAsyncError(
                                            "Error writing to file.", e);
                                        return;
                                    }
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
                                        bw.write("\""
                                            + ob.get(columnInfo.get(0)) + "\"");
                                        for (int j = 1; j < columnInfo.size(); j++) {
                                            bw.write(",\""
                                                + ob.get(columnInfo.get(j))
                                                + "\"");
                                        }
                                        bw.println();
                                    }
                                    bw.close();
                                }
                            } else {
                                try {
                                    ReportingUtils
                                        .printReport(createDynamicReport(query
                                            .getName(), params, columnInfo,
                                            listData));
                                } catch (Exception e) {
                                    BioBankPlugin.openAsyncError(
                                        "Printer Error", e);
                                    return;
                                }
                                ((BiobankApplicationService) SessionManager
                                    .getAppService())
                                    .logActivity("print", null, null, null,
                                        query.getName(), "report");
                            }
                        }
                    });
                }
            });
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
        ReportsView.getTree().setSelection(new StructuredSelection(node));
    }

    @Override
    protected void createFormContent() throws Exception {
        GridLayout formLayout = new GridLayout();
        formLayout.marginWidth = 0;
        form.getBody().setLayout(formLayout);

        form.setText(query.getDescription());

        SiteWrapper site = SessionManager.getInstance().getCurrentSite();
        List<Option> queryOptions = query.getOptions();
        textLabels = new ArrayList<Label>();
        widgetFields = new ArrayList<Widget>();

        if (parameterSection != null)
            parameterSection.dispose();

        parameterSection = toolkit.createComposite(form.getBody(), SWT.NONE);
        GridData pgd = new GridData();
        GridLayout pgl = new GridLayout(2, false);
        pgd.grabExcessHorizontalSpace = true;
        parameterSection.setLayout(pgl);
        parameterSection.setLayoutData(pgd);

        buttonSection = toolkit.createComposite(form.getBody(), SWT.NONE);
        GridLayout gl = new GridLayout();
        gl.numColumns = 3;
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
        printButton.setImage(BioBankPlugin.getDefault().getImageRegistry().get(
            BioBankPlugin.IMG_PRINTER));
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

        exportButton = toolkit.createButton(buttonSection, "Export", SWT.NONE);
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
            Label fieldLabel = toolkit.createLabel(parameterSection, option
                .getName()
                + ":", SWT.NONE);
            textLabels.add(fieldLabel);
            final Widget widget;
            GridData widgetData = new GridData();
            widgetData.horizontalAlignment = SWT.FILL;

            if (option.getType() == DateGroup.class) {
                widget = new Combo(parameterSection, SWT.READ_ONLY);
                Object values[] = DateGroup.values();
                for (int j = 0; j < values.length; j++)
                    ((Combo) widget).add(values[j].toString());
                ((Combo) widget).select(0);
                toolkit.adapt((Combo) widget, true, true);
            } else if (option.getType() == Date.class) {
                widget = new DateTimeWidget(parameterSection, SWT.DATE
                    | SWT.TIME, null);
                ((DateTimeWidget) widget).adaptToToolkit(toolkit, true);
            } else if (option.getType() == String.class) {
                if (option.getName().compareTo("Sample Type") == 0) {
                    Collection<SampleTypeWrapper> sampleTypeWrappers = site
                        .getAllSampleTypeCollection(true);
                    ArrayList<String> sampleTypes = new ArrayList<String>();
                    for (SampleTypeWrapper w : sampleTypeWrappers)
                        sampleTypes.add(w.getNameShort());
                    widget = new Combo(parameterSection, SWT.READ_ONLY);
                    ((Combo) widget).setItems(sampleTypes
                        .toArray(new String[] {}));
                    ((Combo) widget).select(0);
                    ((Combo) widget).setLayoutData(widgetData);
                    toolkit.adapt((Combo) widget, true, true);
                } else if (option.getName().compareTo("Top Container Type") == 0) {
                    widget = new Combo(parameterSection, SWT.READ_ONLY);
                    ((Combo) widget).setLayoutData(widgetData);
                    toolkit.adapt((Combo) widget, true, true);
                    ((Combo) widget).setEnabled(false);
                    widgetCreator.addBooleanBinding(new WritableValue(
                        Boolean.FALSE, Boolean.class), comboStatus,
                        "Pallet not found", IStatus.ERROR);
                } else if (option.getName().compareTo("Pallet Label") == 0) {
                    widget = new BiobankText(parameterSection, SWT.NONE);
                    ((BiobankText) widget).addKeyListener(new KeyListener() {
                        @Override
                        public void keyPressed(KeyEvent e) {
                        }

                        @Override
                        public void keyReleased(KeyEvent e) {
                            if (e.keyCode == SWT.CR)
                                populateTopCombos(((BiobankText) widget)
                                    .getText());
                        }
                    });
                    ((BiobankText) widget)
                        .addTraverseListener(new TraverseListener() {
                            @Override
                            public void keyTraversed(TraverseEvent e) {
                                populateTopCombos(((BiobankText) widget)
                                    .getText());
                            }

                        });
                    ((BiobankText) widget)
                        .addModifyListener(new ModifyListener() {
                            @Override
                            public void modifyText(ModifyEvent e) {
                                for (Widget widget : widgetFields)
                                    if (widget instanceof Combo) {
                                        ((Combo) widget).removeAll();
                                        ((Combo) widget).setEnabled(false);
                                        comboStatus.setValue(false);
                                    }
                            }

                        });
                } else if (option.getName().compareTo("Study") == 0) {
                    Collection<StudyWrapper> studyWrappers;
                    if (site.getName().compareTo("All Sites") != 0)
                        studyWrappers = site.getStudyCollection(true);
                    else
                        studyWrappers = StudyWrapper
                            .getAllStudies(SessionManager.getAppService());
                    ArrayList<String> studyNames = new ArrayList<String>();
                    for (StudyWrapper s : studyWrappers)
                        studyNames.add(s.getNameShort());
                    widget = new Combo(parameterSection, SWT.READ_ONLY);
                    ((Combo) widget).setItems(studyNames
                        .toArray(new String[] {}));
                    ((Combo) widget).select(0);
                    ((Combo) widget).setLayoutData(widgetData);
                    toolkit.adapt((Combo) widget, true, true);
                } else if (option.getName().compareTo("CSV File") == 0) {
                    widget = new FileBrowser(parameterSection, SWT.NONE);
                    toolkit.adapt((FileBrowser) widget, true, true);
                } else {
                    widget = new BiobankText(parameterSection, SWT.NONE);
                }
            } else if (option.getType() == Integer.class) {
                IObservableValue numAliquots = new WritableValue("",
                    String.class);
                widget = widgetCreator.createBoundWidget(parameterSection,
                    BiobankText.class, SWT.BORDER, fieldLabel, new String[0],
                    numAliquots, new IntegerNumberValidator(
                        "Enter a valid integer.", false));
                ((BiobankText) widget).setLayoutData(widgetData);
            } else if (option.getType() == Double.class) {
                IObservableValue numAliquots = new WritableValue("",
                    String.class);
                widget = widgetCreator.createBoundWidget(parameterSection,
                    BiobankText.class, SWT.BORDER, fieldLabel, new String[0],
                    numAliquots, new DoubleNumberValidator(
                        "Enter a valid integer.", false));
                ((BiobankText) widget).setText("0");
            } else
                widget = null;
            widgetFields.add(widget);
        }

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

        // update parents
        createEmptyReportTable();
        parameterSection.moveAbove(buttonSection);
    }

    protected void populateTopCombos(String label) {
        appService = SessionManager.getAppService();
        List<ContainerWrapper> containers = new ArrayList<ContainerWrapper>();
        List<String> topContainerTypes = new ArrayList<String>();
        boolean enable = true;
        try {
            List<SiteWrapper> sites = SiteWrapper.getSites(appService);
            for (SiteWrapper site : sites) {
                containers.addAll(ContainerWrapper.getContainersInSite(
                    appService, site, label));
            }
            for (ContainerWrapper c : containers) {
                for (int i = 0; i < (label.length() / 2) - 1; i++)
                    c = c.getParent();
                if (c.getContainerType().getNameShort().startsWith("F"))
                    topContainerTypes.add(c.getContainerType().getNameShort());
            }
        } catch (Exception e) {
            enable = false;
        }
        if (topContainerTypes.size() < 1)
            enable = false;
        if (enable) {
            for (Widget widget : widgetFields) {
                if (widget instanceof Combo) {
                    ((Combo) widget).setItems(topContainerTypes
                        .toArray(new String[] {}));
                    ((Combo) widget).select(0);
                    ((Combo) widget).setEnabled(true);
                    comboStatus.setValue(true);
                }
            }
        } else {
            for (Widget widget : widgetFields) {
                if (widget instanceof Combo) {
                    ((Combo) widget).removeAll();
                    ((Combo) widget).setEnabled(false);
                    comboStatus.setValue(false);
                }
            }
        }
    }

    @Override
    protected void init() throws Exception {
        widgetCreator.initDataBinding();

        reportData = new ArrayList<Object>();
        node = ((ReportInput) getEditorInput()).getNode();
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
}
