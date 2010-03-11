package edu.ualberta.med.biobank.forms;

import java.awt.Color;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
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
import edu.ualberta.med.biobank.common.reports.ReportTreeNode;
import edu.ualberta.med.biobank.common.reports.SampleCount;
import edu.ualberta.med.biobank.common.reports.SampleInvoiceByClinic;
import edu.ualberta.med.biobank.common.reports.SampleInvoiceByPatient;
import edu.ualberta.med.biobank.common.reports.SampleRequest;
import edu.ualberta.med.biobank.common.reports.SampleSCount;
import edu.ualberta.med.biobank.common.reports.advanced.HQLField;
import edu.ualberta.med.biobank.common.reports.advanced.QueryTreeNode;
import edu.ualberta.med.biobank.common.reports.advanced.SearchUtils;
import edu.ualberta.med.biobank.forms.input.ReportInput;
import edu.ualberta.med.biobank.reporting.ReportingUtils;
import edu.ualberta.med.biobank.views.ReportsView;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.widgets.infotables.SearchResultsInfoTable;

public class AdvancedReportsEditor extends EditorPart {

    public static String ID = "edu.ualberta.med.biobank.editors.AdvancedReportsEditor";

    private Composite top;
    private Composite buttonSection;
    private Composite parameterSection;

    private SearchResultsInfoTable reportTable;

    private Button generateButton;
    private List<Object> reportData;

    private Button printButton;
    private Button exportButton;

    private List<HQLField> fields;

    private TreeViewer tree;

    private ReportTreeNode node;

    private ArrayList<Widget> widgetFields;

    private ArrayList<Label> textLabels;

    private static Map<Class<?>, int[]> columnWidths;

    public void displayFields(QueryTreeNode node) {
        if (parameterSection != null)
            parameterSection.dispose();
        parameterSection = new Composite(top, SWT.NONE);
        GridLayout gl = new GridLayout();
        gl.marginWidth = 0;
        gl.numColumns = 3;
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.TOP;
        parameterSection.setLayout(gl);
        parameterSection.setLayoutData(gd);

        Label headerLabel = new Label(parameterSection, SWT.NONE);
        GridData gdl = new GridData();
        gdl.horizontalSpan = 3;
        headerLabel.setLayoutData(gdl);
        headerLabel.setText(node.getTreePath());

        widgetFields = new ArrayList<Widget>();
        textLabels = new ArrayList<Label>();
        fields = node.getFieldData();
        for (HQLField field : fields) {
            Label fieldLabel = new Label(parameterSection, SWT.NONE);
            fieldLabel.setText(field.getFname() + ":");
            textLabels.add(fieldLabel);
            Combo operatorCombo = new Combo(parameterSection, SWT.NONE);
            operatorCombo.setItems(SearchUtils.getOperatorSet(field.getType())
                .toArray(new String[] {}));
            operatorCombo.select(0);
            Widget widget;

            if (field.getType() == Date.class)
                widget = new DateTimeWidget(parameterSection, SWT.NONE, null);
            else if (field.getType() == String.class) {
                widget = new Text(parameterSection, SWT.BORDER);
            } else if (field.getType() == Integer.class) {
                widget = new Text(parameterSection, SWT.BORDER);
            } else
                widget = null;
            widgetFields.add(widget);
        }
        parameterSection.moveBelow(tree.getTree());
        top.layout(true, true);
    }

    private void generate() {

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
                                // reportData = query.generate(SessionManager
                                // .getAppService(), params);
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
                                reportData, getColumnNames(), columnWidths
                                    .get(node.getObjClass()));
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

    private String[] getColumnNames() {
        // modelObj.getColumnNames();
        return null;
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
    public void createPartControl(Composite parent) {

        top = new Composite(parent, SWT.BORDER);
        GridData gdfill = new GridData();
        gdfill.horizontalAlignment = SWT.FILL;
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        top.setLayout(layout);
        top.setLayoutData(gdfill);

        tree = new TreeViewer(top, SWT.BORDER);
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
        tree.setInput(SearchUtils.constructTree(new HQLField("", node
            .getLabel(), node.getObjClass())));
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

                Object element = ((StructuredSelection) tree.getSelection())
                    .getFirstElement();
                final QueryTreeNode node = (QueryTreeNode) element;
                if (node != null && node.getParent() != null) {
                    MenuItem mi = new MenuItem(menu, SWT.NONE);
                    mi.setText("OR");
                    mi.addSelectionListener(new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent event) {
                            QueryTreeNode newOperator = new QueryTreeNode(
                                new HQLField(node.getNodeInfo().getPath(),
                                    "OR", String.class));
                            QueryTreeNode parent = node.getParent();
                            parent.removeChild(node);
                            parent.addChild(newOperator);
                            newOperator.setParent(parent);
                            newOperator.addChild(node);
                            node.setParent(newOperator);
                            QueryTreeNode copy = node.clone();
                            newOperator.addChild(copy);
                            copy.setParent(newOperator);
                            tree.refresh(true);
                        }
                    });
                    if (node.getNodeInfo().getType() == String.class) {
                        MenuItem mi2 = new MenuItem(menu, SWT.NONE);
                        mi2.setText("Remove Node");
                        mi2.addSelectionListener(new SelectionAdapter() {
                            @Override
                            public void widgetSelected(SelectionEvent event) {
                                QueryTreeNode parent = node.getParent();
                                List<QueryTreeNode> children = node
                                    .getChildren();
                                QueryTreeNode child = children.get(0);
                                child.setParent(parent);
                                node.removeChild(child);
                                parent.addChild(child);
                                parent.removeChild(node);
                                tree.refresh(true);
                            }
                        });
                    }
                    if (node.getNodeInfo().getFname().contains("Collection")
                        && !((node.getParent().getLabel().compareTo("All") == 0) || (node
                            .getParent().getLabel().compareTo("None") == 0))) {
                        MenuItem mi3 = new MenuItem(menu, SWT.NONE);
                        mi3.setText("All");
                        mi3.addSelectionListener(new SelectionAdapter() {
                            @Override
                            public void widgetSelected(SelectionEvent event) {
                                QueryTreeNode newOperator = new QueryTreeNode(
                                    new HQLField(node.getNodeInfo().getPath(),
                                        "All", String.class));
                                QueryTreeNode parent = node.getParent();
                                parent.removeChild(node);
                                parent.addChild(newOperator);
                                newOperator.setParent(parent);
                                newOperator.addChild(node);
                                node.setParent(newOperator);
                                tree.refresh(true);
                            }
                        });
                        MenuItem mi4 = new MenuItem(menu, SWT.NONE);
                        mi4.setText("None");
                        mi4.addSelectionListener(new SelectionAdapter() {
                            @Override
                            public void widgetSelected(SelectionEvent event) {
                                QueryTreeNode newOperator = new QueryTreeNode(
                                    new HQLField(node.getNodeInfo().getPath(),
                                        "None", String.class));
                                QueryTreeNode parent = node.getParent();
                                parent.removeChild(node);
                                parent.addChild(newOperator);
                                newOperator.setParent(parent);
                                newOperator.addChild(node);
                                node.setParent(newOperator);
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

        parameterSection = new Composite(top, SWT.NONE);
        GridData pgd = new GridData();
        pgd.horizontalAlignment = SWT.FILL;
        pgd.grabExcessHorizontalSpace = true;
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

        createEmptyReportTable();
        top.layout(true, true);

    }

    protected void printTable(boolean b) {
        // TODO Auto-generated method stub

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

        node = (ReportTreeNode) ((ReportInput) input).node;

        reportData = new ArrayList<Object>();
        this.setPartName(node.getLabel());

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
    public void setFocus() {
        ReportsView.getTree().setSelection(new StructuredSelection(node));
    }

}
