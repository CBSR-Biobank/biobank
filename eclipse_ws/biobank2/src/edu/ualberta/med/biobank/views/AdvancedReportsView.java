package edu.ualberta.med.biobank.views;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
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

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TableColumn;
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
import edu.ualberta.med.biobank.common.reports.QueryObject.Option;
import edu.ualberta.med.biobank.common.reports.advanced.HQLField;
import edu.ualberta.med.biobank.common.reports.advanced.QueryTreeNode;
import edu.ualberta.med.biobank.common.reports.advanced.SearchUtils;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.reporting.ReportingUtils;
import edu.ualberta.med.biobank.widgets.AutoTextWidget;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.widgets.FileBrowser;
import edu.ualberta.med.biobank.widgets.SmartCombo;
import edu.ualberta.med.biobank.widgets.infotables.SearchResultsInfoTable;

public class AdvancedReportsView extends ViewPart {

    public static Logger LOGGER = Logger.getLogger(ReportsView.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.views.ReportsView";

    private ScrolledComposite sc;
    private Composite top;
    private Composite header;
    private Composite subSection;
    private Composite fieldSection;

    private List<Widget> widgetFields;
    private List<Label> textLabels;

    private TreeViewer tree;
    private Button generateButton;
    private List<Object> reportData;
    private SearchResultsInfoTable reportTable;

    private Button printButton;
    private Button exportButton;

    private List<Class<? extends ModelWrapper<?>>> searchableModelObjects;
    private SmartCombo objectSelector;

    private QueryObject currentQuery;
    Class<? extends ModelWrapper<?>> type;
    List<HQLField> fields;

    public AdvancedReportsView() {
        reportData = new ArrayList<Object>();
        searchableModelObjects = SearchUtils.getSearchableObjs();
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

        objectSelector = createCombo(header);
        objectSelector.addSelectionListener(new SelectionListener() {
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

        reportTable = new SearchResultsInfoTable(top, reportData, null, null);
        GridData searchLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
        reportTable.setLayoutData(searchLayoutData);

        top.layout();
        sc.setContent(top);
        sc.setMinSize(top.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        comboChanged();
    }

    private void generate() {
        try {

            // TODO: Set currentQuery to the built query

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
                                // TODO: run the query
                                // reportData = currentQuery.executeQuery(
                                // SessionManager.getAppService(), params);
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
                                String[] names = currentQuery.getColumnNames();
                                int[] bounds = new int[names.length];
                                for (int i = 0; i < names.length; i++) {
                                    bounds[i] = 100 + names[i].length() * 2;
                                }
                                reportTable.dispose();
                                reportTable = new SearchResultsInfoTable(top,
                                    reportData, names, bounds);
                                GridData searchLayoutData = new GridData(
                                    SWT.FILL, SWT.FILL, true, true);
                                searchLayoutData.minimumHeight = 500;
                                reportTable.setLayoutData(searchLayoutData);
                                reportTable.moveBelow(subSection);
                                printButton.setEnabled(true);
                                exportButton.setEnabled(true);
                            } else {
                                printButton.setEnabled(false);
                                exportButton.setEnabled(false);
                            }
                            reportTable.redraw();
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
        objectSelector.setEnabled(enabled);
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

    public void comboChanged() {
        String typeSelection = objectSelector.getSelection();
        for (Class<? extends ModelWrapper<?>> searchableModelObject : searchableModelObjects)
            if (searchableModelObject.getSimpleName().startsWith(typeSelection))
                type = searchableModelObject;

        if (subSection != null)
            subSection.dispose();

        subSection = new Composite(top, SWT.BORDER);
        GridData gdfill = new GridData();
        gdfill.horizontalAlignment = SWT.FILL;
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        subSection.setLayout(layout);
        subSection.setLayoutData(gdfill);

        tree = new TreeViewer(subSection, SWT.BORDER);
        tree.setContentProvider(new ITreeContentProvider() {

            @Override
            public void inputChanged(Viewer viewer, Object oldInput,
                Object newInput) {

            }

            @Override
            public void dispose() {
                // TODO Auto-generated method stub

            }

            @Override
            public Object[] getElements(Object inputElement) {
                return ((QueryTreeNode) inputElement).getChildren().toArray();
            }

            @Override
            public boolean hasChildren(Object element) {
                return !((QueryTreeNode) element).isLeaf();
            }

            @Override
            public Object getParent(Object element) {
                return ((QueryTreeNode) element).getParent();
            }

            @Override
            public Object[] getChildren(Object parentElement) {
                return ((QueryTreeNode) parentElement).getChildren().toArray();
            }
        });
        tree.setLabelProvider(new ILabelProvider() {

            @Override
            public Image getImage(Object element) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String getText(Object element) {
                return ((QueryTreeNode) element).getLabel();
            }

            @Override
            public void addListener(ILabelProviderListener listener) {
                // TODO Auto-generated method stub

            }

            @Override
            public void dispose() {
                // TODO Auto-generated method stub

            }

            @Override
            public boolean isLabelProperty(Object element, String property) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void removeListener(ILabelProviderListener listener) {
                // TODO Auto-generated method stub

            }
        });
        tree.setInput(SearchUtils.constructTree(new HQLField("", typeSelection,
            type)));
        tree.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                QueryTreeNode selectedNode = (QueryTreeNode) ((IStructuredSelection) event
                    .getSelection()).getFirstElement();
                if (selectedNode != null)
                    displayFields(selectedNode);
            }
        });

        Menu menu = new Menu(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), SWT.NONE);
        menu.addListener(SWT.Show, new Listener() {
            @Override
            public void handleEvent(Event event) {
                Menu menu = tree.getTree().getMenu();
                for (MenuItem menuItem : menu.getItems()) {
                    menuItem.dispose();
                }

                final Object element = ((StructuredSelection) tree
                    .getSelection()).getFirstElement();
                if (element != null) {
                    MenuItem mi = new MenuItem(menu, SWT.NONE);
                    mi.setText("Add OR Node");
                    mi.addSelectionListener(new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent event) {
                            QueryTreeNode selectedNode = ((QueryTreeNode) element);
                            QueryTreeNode newOperator = new QueryTreeNode(
                                new HQLField(selectedNode.getNodeInfo()
                                    .getPath(), "OR", Boolean.class));
                            QueryTreeNode parent = selectedNode.getParent();
                            parent.removeChild(selectedNode);
                            parent.addChild(newOperator);
                            newOperator.setParent(parent);
                            newOperator.addChild(selectedNode);
                            selectedNode.setParent(newOperator);
                            QueryTreeNode copy = selectedNode.clone();
                            newOperator.addChild(copy);
                            copy.setParent(newOperator);
                            tree.refresh(true);
                        }
                    });
                    if (((QueryTreeNode) element).getLabel().compareTo("OR") == 0) {
                        MenuItem mi2 = new MenuItem(menu, SWT.NONE);
                        mi2.setText("Remove OR Node");
                        mi2.addSelectionListener(new SelectionAdapter() {
                            @Override
                            public void widgetSelected(SelectionEvent event) {
                                QueryTreeNode selectedNode = ((QueryTreeNode) element);
                                QueryTreeNode parent = selectedNode.getParent();
                                List<QueryTreeNode> children = selectedNode
                                    .getChildren();
                                QueryTreeNode child = children.get(0);
                                child.setParent(parent);
                                selectedNode.removeChild(child);
                                parent.addChild(child);
                                parent.removeChild(selectedNode);
                                tree.refresh(true);
                            }
                        });
                    }
                }
            }
        });
        tree.getTree().setMenu(menu);

        GridData gd = new GridData();
        gd.minimumHeight = 600;
        gd.minimumWidth = 300;
        gd.heightHint = 300;
        gd.widthHint = 250;
        tree.getTree().setLayoutData(gd);

        subSection.moveBelow(header);
        subSection.setVisible(true);

        // update parents
        resetSearch();
        updateScrollBars();
        top.layout(true, true);

    }

    public void displayFields(QueryTreeNode node) {
        if (fieldSection != null)
            fieldSection.dispose();
        fieldSection = new Composite(subSection, SWT.NONE);
        GridLayout gl = new GridLayout();
        gl.marginWidth = 0;
        gl.numColumns = 2;
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        fieldSection.setLayout(gl);
        fieldSection.setLayoutData(gd);

        Label headerLabel = new Label(fieldSection, SWT.NONE);
        GridData gdl = new GridData();
        gdl.horizontalSpan = 2;
        headerLabel.setLayoutData(gdl);
        headerLabel.setText(node.getTreePath());

        widgetFields = new ArrayList<Widget>();
        textLabels = new ArrayList<Label>();
        fields = node.getFieldData();
        for (HQLField field : fields) {
            Label fieldLabel = new Label(fieldSection, SWT.NONE);
            fieldLabel.setText(field.getFname() + ":");
            textLabels.add(fieldLabel);
            Widget widget;

            if (field.getType() == Date.class)
                widget = new DateTimeWidget(fieldSection, SWT.NONE, null);
            else if (field.getType() == String.class) {
                widget = new Text(fieldSection, SWT.BORDER);
            } else if (field.getType() == Integer.class) {
                widget = new Text(fieldSection, SWT.BORDER);
            } else
                widget = null;
            widgetFields.add(widget);
        }

        top.layout(true, true);
    }

    @Override
    public void setFocus() {

    }

    public void updateScrollBars() {
        sc.layout(true, true);
        sc.setMinSize(top.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }

    public void resetSearch() {
        if (reportTable != null) {
            reportData = new ArrayList<Object>();
            reportTable.reset();
            TableColumn[] cols = reportTable.getTableViewer().getTable()
                .getColumns();
            for (TableColumn col : cols) {
                col.setText("");
            }
        }
        printButton.setEnabled(false);
        exportButton.setEnabled(false);
    }

    protected SmartCombo createCombo(Composite parent) {
        SmartCombo combo = new SmartCombo(parent, SWT.NONE);

        GridData combodata = new GridData();
        combodata.widthHint = 250;
        combo.setLayoutData(combodata);
        String[] names = new String[searchableModelObjects.size()];
        int i = 0;
        for (Class<?> objClass : searchableModelObjects) {
            names[i] = objClass.getSimpleName().replace("Wrapper", "");
            i++;
        }
        combo.setInput(names);
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
}
