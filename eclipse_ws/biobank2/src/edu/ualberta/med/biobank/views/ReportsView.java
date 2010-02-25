package edu.ualberta.med.biobank.views;

import java.awt.Color;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
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
import edu.ualberta.med.biobank.common.reports.QueryObject;
import edu.ualberta.med.biobank.common.reports.QueryObject.DateRange;
import edu.ualberta.med.biobank.common.reports.QueryObject.Option;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.reporting.ReportingUtils;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.widgets.FileBrowser;
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

    private ComboViewer querySelect;
    private List<Widget> widgetFields;
    private List<Label> textLabels;

    private Button searchButton;
    private Collection<Object> searchData;
    private SearchResultsInfoTable searchTable;

    private Button printButton;
    private Button exportButton;

    private QueryObject currentQuery;

    public ReportsView() {
        searchData = new ArrayList<Object>();
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
        querySelect
            .addSelectionChangedListener(new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    comboChanged();
                }
            });

        searchButton = new Button(header, SWT.NONE);
        searchButton.setText("Search");
        searchButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                BusyIndicator.showWhile(PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow().getShell().getDisplay(),
                    new Runnable() {
                        @Override
                        public void run() {
                            try {
                                searchData = search();
                            } catch (ApplicationException ae) {
                                BioBankPlugin
                                    .openAsyncError("Search error", ae);
                            }
                        }
                    });
                if (searchData.size() > 0) {
                    String[] names = currentQuery.getColumnNames();
                    int[] bounds = new int[names.length];

                    for (int i = 0; i < names.length; i++) {
                        bounds[i] = 100 + names[i].length() * 2;
                    }
                    searchTable.dispose();
                    searchTable = new SearchResultsInfoTable(top, searchData,
                        names, bounds);
                    GridData searchLayoutData = new GridData(SWT.FILL,
                        SWT.FILL, true, true);
                    searchLayoutData.minimumHeight = 500;
                    searchTable.setLayoutData(searchLayoutData);
                    searchTable.moveBelow(subSection);
                    printButton.setEnabled(true);
                    exportButton.setEnabled(true);
                } else {
                    printButton.setEnabled(false);
                    exportButton.setEnabled(false);
                }
                // searchTable.setCollection(searchData); caused big
                // problems... dunno why

                searchTable.redraw();
                top.layout();
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
        Label resultsLabel = new Label(top, SWT.NONE);
        resultsLabel.setText("Results:");

        searchTable = new SearchResultsInfoTable(top, searchData,
            new String[] {}, null);
        GridData searchLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
        searchTable.setLayoutData(searchLayoutData);

        querySelect.setSelection(new StructuredSelection(QueryObject
            .getQueryObjectNames()[0]));
        top.layout();
        sc.setContent(top);
        sc.setMinSize(top.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }

    private Collection<Object> search() throws ApplicationException {
        IStructuredSelection typeSelection = (IStructuredSelection) querySelect
            .getSelection();
        try {
            Class<? extends QueryObject> cls = QueryObject
                .getQueryObjectByName((String) typeSelection.getFirstElement());
            Constructor<?> c = cls.getConstructor(String.class, Integer.class);
            SiteWrapper site = SessionManager.getInstance()
                .getCurrentSiteWrapper();
            String op = "=";
            if (site.getName().compareTo("All Sites") == 0)
                op = "!=";
            currentQuery = (QueryObject) c.newInstance(new Object[] { op,
                site.getId() });
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList<Object> params = getParams();

        return currentQuery
            .executeQuery(SessionManager.getAppService(), params);
    }

    private ArrayList<Object> getParams() {
        ArrayList<Object> params = new ArrayList<Object>();
        for (int i = 0; i < widgetFields.size(); i++) {
            if (widgetFields.get(i) instanceof Text)
                params.add(((Text) widgetFields.get(i)).getText());
            else if (widgetFields.get(i) instanceof Combo) {
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

    public void comboChanged() {
        IStructuredSelection typeSelection = (IStructuredSelection) querySelect
            .getSelection();
        try {
            Class<? extends QueryObject> cls = QueryObject
                .getQueryObjectByName((String) typeSelection.getFirstElement());
            Constructor<?> c = cls.getConstructor(String.class, Integer.class);
            SiteWrapper site = SessionManager.getInstance()
                .getCurrentSiteWrapper();
            String op = "=";
            if (site.getName().compareTo("All Sites") == 0)
                op = "!=";
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
                widget = new FileBrowser(subSection, SWT.NONE);
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

    @Override
    public void setFocus() {
        querySelect.getControl().setFocus();
    }

    public void updateScrollBars() {
        sc.layout(true, true);
        sc.setMinSize(top.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }

    public void resetSearch() {
        if (searchTable != null) {
            searchTable.dispose();
            searchTable = new SearchResultsInfoTable(top, null, null, null);
        }
        printButton.setEnabled(false);
        exportButton.setEnabled(false);
    }

    protected static ComboViewer createCombo(Composite parent) {
        // SmartCombo testCombo = new SmartCombo(parent, new String[] { "test1",
        // "test2", "thirdtest", "zzz" });

        Combo combo;
        ComboViewer comboViewer;
        combo = new Combo(parent, SWT.READ_ONLY);

        GridData combodata = new GridData();
        combodata.widthHint = 250;
        combo.setLayoutData(combodata);

        comboViewer = new ComboViewer(combo);
        comboViewer.setContentProvider(new ArrayContentProvider());
        comboViewer.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                return (String) element;
            }
        });
        comboViewer.setInput(QueryObject.getQueryObjectNames());
        return comboViewer;
    }

    public class CBSRReportCollection {
        private List<Object> list;

        public CBSRReportCollection(List<Object> objects) {
            setList(objects);
        }

        public void setList(List<Object> list) {
            this.list = list;
        }

        public List<Object> getList() {
            return list;
        }
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
            for (Object object : searchTable.getCollection()) {
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
                String path = fd.open();
                ReportingUtils.saveReport(createDynamicReport(currentQuery
                    .toString(), params, columnInfo, listData), path);
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
}
