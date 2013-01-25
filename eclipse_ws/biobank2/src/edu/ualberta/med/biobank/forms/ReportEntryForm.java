package edu.ualberta.med.biobank.forms;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Section;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.reports.filters.FilterOperator;
import edu.ualberta.med.biobank.common.util.AbstractBiobankListProxy;
import edu.ualberta.med.biobank.common.util.ReportListProxy;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.ReportWrapper;
import edu.ualberta.med.biobank.common.wrappers.util.WrapperUtil;
import edu.ualberta.med.biobank.export.CsvDataExporter;
import edu.ualberta.med.biobank.export.Data;
import edu.ualberta.med.biobank.export.DataExporter;
import edu.ualberta.med.biobank.export.PdfDataExporter;
import edu.ualberta.med.biobank.export.PrintPdfDataExporter;
import edu.ualberta.med.biobank.forms.listener.ProgressMonitorDialogBusyListener;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.model.EntityFilter;
import edu.ualberta.med.biobank.model.HasName;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.Report;
import edu.ualberta.med.biobank.model.ReportColumn;
import edu.ualberta.med.biobank.model.ReportFilter;
import edu.ualberta.med.biobank.model.ReportFilterValue;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.report.ReportAdapter;
import edu.ualberta.med.biobank.views.AdvancedReportsView;
import edu.ualberta.med.biobank.widgets.infotables.ReportResultsTableWidget;
import edu.ualberta.med.biobank.widgets.report.ChangeListener;
import edu.ualberta.med.biobank.widgets.report.ColumnChangeEvent;
import edu.ualberta.med.biobank.widgets.report.ColumnSelectWidget;
import edu.ualberta.med.biobank.widgets.report.FilterChangeEvent;
import edu.ualberta.med.biobank.widgets.report.FilterSelectWidget;

public class ReportEntryForm extends BiobankEntryForm {
    private static final I18n i18n = I18nFactory
        .getI18n(ReportEntryForm.class);

    private static BgcLogger logger = BgcLogger.getLogger(ReportEntryForm.class
        .getName());

    private static ImageDescriptor SAVE_AS_NEW_ACTION_IMAGE = ImageDescriptor
        .createFromImage(BgcPlugin.getDefault().getImageRegistry()
            .get(BgcPlugin.IMG_SAVE_AS_NEW));

    @SuppressWarnings("nls")
    public static final String ID =
    "edu.ualberta.med.biobank.forms.ReportEntryForm";

    private static final Comparator<EntityFilter> COMPARE_FILTERS_BY_NAME =
        new Comparator<EntityFilter>() {
        @Override
        public int compare(EntityFilter lhs, EntityFilter rhs) {
            return lhs.getName().compareToIgnoreCase(rhs.getName());
        }
    };

    private FilterSelectWidget filtersWidget;
    private ColumnSelectWidget columnsWidget;
    private ReportAdapter reportAdapter;
    private ReportWrapper report;

    private ComboViewer filterCombo;

    private Section filtersSection;

    private Button generateButton;
    private final Collection<Button> exportButtons = new ArrayList<Button>();

    private Composite resultsContainer;

    private List<Object> results;
    private ReportResultsTableWidget<Object> resultsTable;

    private PrintPdfDataExporter printPdfDataExporter;

    @SuppressWarnings("deprecation")
    @Override
    protected void init() throws Exception {
        reportAdapter = (ReportAdapter) adapter;
        report = (ReportWrapper) getModelObject();
        report.reload();

        updatePartName();
    }

    @Override
    protected void doAfterSave() {
        AdvancedReportsView.getCurrent().reload();
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void saveForm() throws Exception {
        form.getDisplay().syncExec(new Runnable() {
            @Override
            public void run() {
                // update the model before saving
                updateReportModel();
            }
        });

        report.persist();
        reportAdapter.getParent().performExpand();
    }

    private void updateReportModel() {
        // don't set through the wrappers because we don't want to alert
        // anything listening to the wrapper (for example, the
        // FilterSelectWidget and the ColumnSelectWidget).
        Report nakedReport = report.getWrappedObject();
        nakedReport.setReportColumns(new HashSet<ReportColumn>(
            columnsWidget.getReportColumns()));
        nakedReport.setReportFilters(new HashSet<ReportFilter>(
            filtersWidget.getReportFilters()));
    }

    @Override
    protected String getOkMessage() {
        return StringUtil.EMPTY_STRING;
    }

    @Override
    public String getNextOpenedFormId() {
        return ReportEntryForm.ID;
    }

    @SuppressWarnings("nls")
    private void updatePartName() {
        String entityName = report.getEntity().getName();

        String tabName;
        if (report.isNew()) {
            tabName = i18n.tr("New {0} Report", entityName);
            report.setName(tabName);
        } else {
            String reportName = report.getName();
            if (reportName == null || reportName.isEmpty()) {
                tabName = i18n.tr("Unnamed {0} Report", entityName);
            } else {
                tabName = reportName;
            }
        }

        setPartName(tabName);
    }

    @SuppressWarnings("nls")
    @Override
    protected void createFormContent() throws Exception {
        form.setText(i18n.tr("{0} Report", report.getEntity().getName()));
        page.setLayout(new GridLayout(1, false));

        createProperties();
        createFiltersSection();
        createOptionsSection();

        createButtons();

        createResultsArea();
    }

    private void createResultsArea() {
        resultsContainer = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(1, false);
        resultsContainer.setLayout(layout);
        resultsContainer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    }

    @SuppressWarnings("nls")
    private void createProperties() {
        Composite container = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        container.setLayout(layout);
        container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(container);

        setFirstControl(createBoundWidgetWithLabel(container,
            BgcBaseText.class, SWT.NONE, HasName.PropertyName.NAME.toString(),
            null, report, ReportWrapper.PROPERTY_NAME,
            new NonEmptyStringValidator(i18n.tr("Name is required."))));

        createBoundWidgetWithLabel(container, BgcBaseText.class, SWT.MULTI,
            i18n.tr("Description"), null, report,
            ReportWrapper.PROPERTY_DESCRIPTION, null);
    }

    private void createButtons() {
        Composite container = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        container.setLayout(layout);
        container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(container);

        createGenerateButton(container);
        createExportButtons(container).setLayoutData(
            new GridData(SWT.END, SWT.TOP, true, false));
    }

    @SuppressWarnings("nls")
    private Control createGenerateButton(Composite parent) {
        generateButton = toolkit.createButton(parent,
            // button label.
            i18n.tr("Generate"), SWT.NONE);
        generateButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                generateReport();
            }
        });

        updateGenerateButton();

        return generateButton;
    }

    private void emptyResultsContainer() {
        for (Control control : resultsContainer.getChildren()) {
            if (!control.isDisposed()) {
                control.dispose();
            }
        }

        resultsTable = null;
    }

    private void generateReport() {
        for (Button button : exportButtons) {
            button.setEnabled(false);
        }

        updateReportModel();
        emptyResultsContainer();

        final Report rawReport = report.getWrappedObject();

        IRunnableWithProgress op = new IRunnableWithProgress() {
            @SuppressWarnings("nls")
            @Override
            public void run(IProgressMonitor monitor) {
                monitor.beginTask(
                    // progress monitor message.
                    i18n.tr("Generating report..."),
                    IProgressMonitor.UNKNOWN);
                try {
                    results = new ArrayList<Object>();

                    Thread thread = new Thread("Querying") {
                        @SuppressWarnings("unchecked")
                        @Override
                        public void run() {
                            results = (List<Object>) new ReportListProxy(
                                SessionManager.getAppService(), rawReport)
                            .init();

                            if (results instanceof AbstractBiobankListProxy)
                                ((AbstractBiobankListProxy<?>) results)
                                .addBusyListener(new ProgressMonitorDialogBusyListener(
                                    // progress monitor label.
                                    i18n.tr("Loading more results...")));
                        }
                    };

                    thread.start();
                    while (true) {
                        if (monitor.isCanceled()) {
                            thread.interrupt();
                            return;
                        } else if (!thread.isAlive()) {
                            break;
                        }
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            return;
                        }
                    }

                    generateButton.getDisplay().syncExec(new Runnable() {
                        @Override
                        public void run() {
                            resultsTable =
                                new ReportResultsTableWidget<Object>(
                                    resultsContainer, null, getHeaders());

                            resultsTable.adaptToToolkit(toolkit, true);

                            resultsTable.setList(results);

                            if (!report.getIsCount()) {
                                resultsTable
                                .addDoubleClickListener(new IDoubleClickListener() {
                                    @Override
                                    public void doubleClick(
                                        DoubleClickEvent event) {
                                        ISelection selection = event
                                            .getSelection();
                                        openViewForm(selection);
                                    }
                                });
                            }

                            for (Button button : exportButtons) {
                                button.setEnabled(true);
                            }
                        }
                    });

                    Log logMessage = new Log();
                    logMessage.setType("report");
                    logMessage.setAction(rawReport.getName());
                    SessionManager.getAppService().logActivity(logMessage);
                    addPrintAction();

                } catch (Exception e) {
                    BgcPlugin.openAsyncError(
                        i18n.tr("Report Generation Error"), e);
                }
                monitor.done();
            }
        };

        try {
            new ProgressMonitorDialog(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell()).run(true, true, op);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            book.reflow(true);
            form.layout(true, true);
        }
    }

    private Control createExportButtons(Composite parent) {
        Composite container = toolkit.createComposite(parent);
        GridLayout layout = new GridLayout(3, false);
        layout.horizontalSpacing = 10;
        container.setLayout(layout);
        container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(container);

        createExporterButton(container, new CsvDataExporter());
        createExporterButton(container, new PdfDataExporter());
        printPdfDataExporter = new PrintPdfDataExporter();
        Button printButton = createExporterButton(container,
            printPdfDataExporter);
        printButton.setImage(BgcPlugin.getDefault().getImageRegistry()
            .get(BgcPlugin.IMG_PRINTER));
        return container;
    }

    private Button createExporterButton(Composite parent,
        final DataExporter exporter) {
        Button button = toolkit.createButton(parent, exporter.getName(),
            SWT.NONE);
        button.setEnabled(false);
        button.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                export(exporter);
            }
        });

        exportButtons.add(button);
        return button;
    }

    @Override
    public boolean print() {
        export(printPdfDataExporter);
        return true;
    }

    @SuppressWarnings("nls")
    private void openViewForm(ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            Object o = ((IStructuredSelection) selection).getFirstElement();
            if (o instanceof Object[]) {
                Object[] row = (Object[]) o;
                if (row.length > 0 && row[0] instanceof Integer) {
                    Integer id = (Integer) row[0];
                    String entityClassName = report.getEntity().getClassName();

                    try {
                        Class<?> entityKlazz = Class.forName(entityClassName);

                        Constructor<?> constructor = entityKlazz
                            .getConstructor();
                        Object instance = constructor.newInstance();
                        Method setIdMethod = entityKlazz.getMethod("setId",
                            Integer.class);
                        setIdMethod.invoke(instance, id);

                        ModelWrapper<?> wrapper = WrapperUtil.wrapObject(
                            SessionManager.getAppService(), instance);

                        SessionManager.openViewForm(wrapper);
                    } catch (Exception e) {
                        logger.error(
                            "Error opening selection", e);
                    }
                }
            }
        }
    }

    @SuppressWarnings("nls")
    private String[] getHeaders() {
        Report nakedReport = report.getWrappedObject();
        List<ReportColumn> reportColumns = new ArrayList<ReportColumn>(
            nakedReport.getReportColumns());

        Collections.sort(reportColumns, new Comparator<ReportColumn>() {
            @Override
            public int compare(ReportColumn lhs, ReportColumn rhs) {
                return lhs.getPosition() - rhs.getPosition();
            }
        });

        int numHeaders = reportColumns.size();
        numHeaders += report.getIsCount() ? 1 : 0;

        String[] headers = new String[numHeaders];

        int i = 0;

        for (ReportColumn reportColumn : reportColumns) {
            headers[i] = ColumnSelectWidget.getColumnName(reportColumn);
            i++;
        }

        if (report.getIsCount()) {
            headers[i] = i18n.tr("{0} Count", report.getEntity().getName());
        }

        return headers;
    }

    @SuppressWarnings("nls")
    private void createFiltersSection() {
        filtersSection = createSection(i18n.tr("Filters"));

        Composite container = toolkit.createComposite(filtersSection, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        container.setLayout(layout);
        container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(container);

        createFilterCombo(container);

        filtersWidget = new FilterSelectWidget(container, SWT.NONE, report);
        filtersWidget
        .addFilterChangedListener(new ChangeListener<FilterChangeEvent>() {
            @Override
            public void handleEvent(FilterChangeEvent event) {
                if (event.isDataChange()) {
                    setDirty(true);
                }

                book.reflow(true);
                form.layout(true, true);

                EntityFilter entityFilter = event.getEntityFilter();
                if (event.isSelected()) {
                    filterCombo.remove(entityFilter);
                } else {
                    filterCombo.add(entityFilter);
                }
            }
        });

        Collection<EntityFilter> entityFilters = getSortedEntityFilters(report,
            COMPARE_FILTERS_BY_NAME);
        for (EntityFilter entityFilter : entityFilters) {
            if (filtersWidget.getFilterRow(entityFilter) == null) {
                filterCombo.add(entityFilter);
            }
        }

        filtersSection.setClient(container);
    }

    @SuppressWarnings("nls")
    private void createFilterCombo(Composite parent) {
        Composite container = toolkit.createComposite(parent);

        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 5;
        layout.verticalSpacing = 0;
        layout.marginWidth = 0;
        layout.marginHeight = 2;
        container.setLayout(layout);

        GridData layoutData = new GridData();
        layoutData.verticalAlignment = SWT.TOP;
        layoutData.horizontalAlignment = SWT.RIGHT;
        container.setLayoutData(layoutData);

        Label label = new Label(container, SWT.NONE);
        label.setText(i18n.tr("Add filter:"));

        GridData comboLayoutData = new GridData();
        comboLayoutData.widthHint = 200;

        filterCombo = new ComboViewer(container, SWT.READ_ONLY);
        filterCombo.getControl().setLayoutData(comboLayoutData);
        filterCombo.setSorter(new ViewerSorter());
        filterCombo.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof EntityFilter) {
                    return ((EntityFilter) element).getName();
                }
                return StringUtil.EMPTY_STRING;
            }
        });

        filterCombo
        .addPostSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                Object selection = ((IStructuredSelection) filterCombo
                    .getSelection()).getFirstElement();
                if (selection instanceof EntityFilter) {
                    EntityFilter entityFilter = (EntityFilter) selection;
                    filterCombo.remove(entityFilter);
                    filtersWidget.addFilterRow(entityFilter);

                    setDirty(true);
                    form.layout(true, true);
                }
            }
        });
    }

    private static Collection<EntityFilter> getSortedEntityFilters(
        ReportWrapper report, Comparator<EntityFilter> comparator) {
        List<EntityFilter> sortedFilters = new ArrayList<EntityFilter>();

        sortedFilters.addAll(report.getEntityFilterCollection());
        Collections.sort(sortedFilters, comparator);

        return sortedFilters;
    }

    @SuppressWarnings("nls")
    private void createOptionsSection() {
        Section section = createSection(i18n.tr("Options"));
        Composite options = toolkit.createComposite(section);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        options.setLayout(layout);
        options.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(options);

        createBoundWidgetWithLabel(options, Button.class, SWT.CHECK,
            i18n.tr("Show count\n(for displayed columns)"), null, report,
            "isCount",
            null);

        createBoundWidgetWithLabel(options, Button.class, SWT.CHECK,
            i18n.tr("Share report"), null, report,
            "isPublic", null);

        GridData layoutData = new GridData();
        layoutData.widthHint = 225;
        Label columnsLabel = new Label(options, SWT.NONE);
        columnsLabel.setText(i18n.tr("Columns:"));
        columnsLabel.setLayoutData(layoutData);

        columnsWidget = new ColumnSelectWidget(options, SWT.NONE, report);
        columnsWidget
        .addColumnChangeListener(new ChangeListener<ColumnChangeEvent>() {
            @Override
            public void handleEvent(ColumnChangeEvent event) {
                setDirty(true);
                book.reflow(true);
                form.layout(true, true);

                updateGenerateButton();
            }
        });

        section.setClient(options);
    }

    private void updateGenerateButton() {
        boolean hasColumnsSelected = !columnsWidget.getReportColumns()
            .isEmpty();
        if (generateButton != null && !generateButton.isDisposed()) {
            generateButton.setEnabled(hasColumnsSelected);
        }
    }

    @SuppressWarnings("nls")
    @Override
    protected void addToolbarButtons() {
        super.addToolbarButtons();

        Action saveAsNewAction = new Action() {
            @Override
            public void run() {
                updateReportModel();

                ReportWrapper report = new ReportWrapper(
                    ReportEntryForm.this.report);

                closeEntryOpenView(false, false);

                int userId = SessionManager.getUser().getId().intValue();
                report.setUserId(userId);

                ReportAdapter reportAdapter = new ReportAdapter(
                    (AdapterBase) getAdapter().getParent(), report);
                IEditorPart part = reportAdapter.openEntryForm();

                if (part instanceof ReportEntryForm) {
                    ReportEntryForm form = (ReportEntryForm) part;
                    form.setDirty(true);
                    form.confirm();
                }
            }
        };

        // TODO: Need to add a command (and handler) for a shortcut to work, but
        // it won't be able to invoke the Action's run method.
        // saveAsNewAction
        // .setActionDefinitionId("edu.ualberta.med.biobank.commands.saveAsNew");

        saveAsNewAction.setImageDescriptor(SAVE_AS_NEW_ACTION_IMAGE);
        saveAsNewAction.setToolTipText(i18n.tr("Save As New Report"));

        form.getToolBarManager().add(saveAsNewAction);
        form.updateToolBar();
    }

    @SuppressWarnings("nls")
    private void export(final DataExporter exporter) {
        final Data data = new Data();
        data.setColumnNames(Arrays.asList(getHeaders()));
        data.setTitle(report.getName());
        data.setDescription(getComments(report));
        data.setRows(results);

        // check if the exporter can export this data
        try {
            exporter.canExport(data);
        } catch (Exception e) {
            MessageDialog.openError(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell(),
                i18n.tr("Confirm Report Results Export"), e.getMessage());
            return;
        }

        // confirm exporting
        if (!MessageDialog.openQuestion(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            i18n.tr("Confirm Report Results Export"),
            i18n.tr("Are you sure you want to {0}?",
                exporter.getName()))) {
            return;
        }

        // export
        try {
            exporter.export(data, resultsTable
                .getLabelProvider(!(exporter instanceof CsvDataExporter)));
        } catch (Exception e) {
            MessageDialog.openError(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell(),
                i18n.tr("Error exporting"), e.getMessage());
            return;
        }
    }

    @SuppressWarnings("nls")
    private List<String> getComments(ReportWrapper report) {
        List<String> comments = new ArrayList<String>();

        if (report.getDescription() != null) {
            comments.add(report.getDescription());
        }

        Report nakedReport = report.getWrappedObject();
        List<ReportFilter> reportFilters = new ArrayList<ReportFilter>(
            nakedReport.getReportFilters());

        Collections.sort(reportFilters, new Comparator<ReportFilter>() {
            @Override
            public int compare(ReportFilter lhs, ReportFilter rhs) {
                return lhs.getPosition() - rhs.getPosition();
            }
        });

        for (ReportFilter filter : reportFilters) {
            StringBuilder sb = new StringBuilder();
            sb.append(filter.getEntityFilter().getName());

            Integer opId = filter.getOperator();
            if (opId != null) {
                FilterOperator op = FilterOperator.getFilterOperator(opId);
                if (op != null) {
                    sb.append(" ");
                    sb.append(op.getDisplayString());
                }
            }

            Collection<ReportFilterValue> values = filter
                .getReportFilterValues();
            if (values != null) {
                String and = i18n.tr("and");
                sb.append(": ");
                for (ReportFilterValue value : values) {
                    sb.append("'");
                    sb.append(value.getValue().replace("'", "\'"));

                    if (value.getSecondValue() != null) {
                        sb.append("' ");
                        sb.append(and);
                        sb.append(" '");
                        sb.append(value.getSecondValue().replace("'", "\'"));
                    }
                    sb.append("'; ");
                }
            }

            comments.add(sb.toString());
        }

        return comments;
    }

    @Override
    public void setValues() throws Exception {
        emptyResultsContainer();
        report.reset();
    }
}
