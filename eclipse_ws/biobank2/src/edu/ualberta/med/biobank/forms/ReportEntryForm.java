package edu.ualberta.med.biobank.forms;

import java.io.FileWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Section;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.util.Holder;
import edu.ualberta.med.biobank.common.util.ReportListProxy;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.ReportWrapper;
import edu.ualberta.med.biobank.model.EntityFilter;
import edu.ualberta.med.biobank.model.Report;
import edu.ualberta.med.biobank.model.ReportColumn;
import edu.ualberta.med.biobank.model.ReportFilter;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.treeview.report.ReportAdapter;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.views.ReportAdministrationView;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.infotables.ReportResultsTableWidget;
import edu.ualberta.med.biobank.widgets.report.ChangeListener;
import edu.ualberta.med.biobank.widgets.report.ColumnChangeEvent;
import edu.ualberta.med.biobank.widgets.report.ColumnSelectWidget;
import edu.ualberta.med.biobank.widgets.report.FilterChangeEvent;
import edu.ualberta.med.biobank.widgets.report.FilterSelectWidget;

public class ReportEntryForm extends BiobankEntryForm {
    // TODO: put this somewhere so it can be shared

    public static final String ID = "edu.ualberta.med.biobank.forms.ReportEntryForm";

    private static final Comparator<EntityFilter> COMPARE_FILTERS_BY_NAME = new Comparator<EntityFilter>() {
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

    private Button generateButton, exportButton;

    private Composite resultsContainer;

    private List<Object> results;
    private ReportResultsTableWidget<Object> resultsTable;

    @Override
    protected void doAfterSave() {
        ReportAdministrationView.getCurrent().reload();
    }

    @Override
    protected void saveForm() throws Exception {
        form.getDisplay().syncExec(new Runnable() {
            @Override
            public void run() {
                // update the model before saving
                updateReport();
            }
        });

        report.persist();
        reportAdapter.getParent().performExpand();
    }

    private void updateReport() {
        // don't set through the wrappers because we don't want to alert
        // anything listening to the wrapper (for example, the
        // FilterSelectWidget and the ColumnSelectWidget).
        Report nakedReport = report.getWrappedObject();
        nakedReport.setReportColumnCollection(new HashSet<ReportColumn>(
            columnsWidget.getReportColumns()));
        nakedReport.setReportFilterCollection(new HashSet<ReportFilter>(
            filtersWidget.getReportFilters()));
    }

    @Override
    protected String getOkMessage() {
        return "";
    }

    @Override
    public String getNextOpenedFormID() {
        return ReportEntryForm.ID;
    }

    @Override
    protected void init() throws Exception {
        reportAdapter = (ReportAdapter) adapter;
        report = reportAdapter.getWrapper();
        report.reload();

        String entityName = report.getEntity().getName();

        String tabName;
        if (report.isNew()) {
            tabName = "New " + entityName + " Report";
        } else {
            String reportName = report.getName();
            if (reportName == null || reportName.isEmpty()) {
                tabName = "Unnamed " + entityName + " Report";
            } else {
                tabName = reportName;
            }
        }

        setPartName(tabName);

        // TODO: add "Save As" toolbar icon?
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(report.getEntity().getName() + " Report");
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

    private void createProperties() {
        Composite container = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        container.setLayout(layout);
        container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(container);

        setFirstControl(createBoundWidgetWithLabel(container,
            BiobankText.class, SWT.NONE, "Name", null, report,
            ReportWrapper.PROPERTY_NAME, new NonEmptyStringValidator(
                "Name is required.")));

        createBoundWidgetWithLabel(container, BiobankText.class, SWT.MULTI,
            "Description", null, report, ReportWrapper.PROPERTY_DESCRIPTION,
            null);
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

    private Control createGenerateButton(Composite parent) {
        generateButton = new Button(parent, SWT.NONE);
        generateButton.setText("Generate");
        generateButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                // update the model before running
                updateReport();

                for (Control control : resultsContainer.getChildren()) {
                    if (!control.isDisposed()) {
                        control.dispose();
                    }
                }

                // TODO: push getHeaders, openViewForm, etc. into
                // ReportResultsTableWidget class?
                Report rawReport = report.getWrappedObject();
                results = new ReportListProxy(
                    (BiobankApplicationService) appService, rawReport);

                resultsTable = new ReportResultsTableWidget<Object>(
                    resultsContainer, results, getHeaders());

                if (!report.getIsCount()) {
                    resultsTable
                        .addDoubleClickListener(new IDoubleClickListener() {
                            @Override
                            public void doubleClick(DoubleClickEvent event) {
                                ISelection selection = event.getSelection();
                                openViewForm(selection);
                            }
                        });
                }

                exportButton.setEnabled(true);

                book.reflow(true);
                form.layout(true, true);
            }
        });

        return generateButton;
    }

    private Control createExportButtons(Composite parent) {
        exportButton = new Button(parent, SWT.NONE);
        exportButton.setText("Export CSV");
        exportButton.setEnabled(false);
        exportButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                exportCsv();
            }
        });

        return exportButton;
    }

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

                        ModelWrapper<?> wrapper = ModelWrapper.wrapObject(
                            appService, instance);

                        SessionManager.openViewForm(wrapper);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private String[] getHeaders() {
        Report nakedReport = report.getWrappedObject();
        List<ReportColumn> reportColumns = new ArrayList<ReportColumn>(
            nakedReport.getReportColumnCollection());

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
            headers[i] = report.getEntity().getName() + " Count";
        }

        return headers;
    }

    private void createFiltersSection() {
        filtersSection = createSection("Filters");

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
        label.setText("Add filter:");

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
                return "";
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

    private void createOptionsSection() {
        Section section = createSection("Options");
        Composite options = toolkit.createComposite(section);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        options.setLayout(layout);
        options.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(options);

        createBoundWidgetWithLabel(options, Button.class, SWT.CHECK,
            "Show count\r\n(for displayed columns)", null, report, "isCount",
            null);

        createBoundWidgetWithLabel(options, Button.class, SWT.CHECK,
            "Share report", null, report, "isPublic", null);

        GridData layoutData = new GridData();
        layoutData.widthHint = 225;
        Label columnsLabel = new Label(options, SWT.NONE);
        columnsLabel.setText("Columns:");
        columnsLabel.setLayoutData(layoutData);

        columnsWidget = new ColumnSelectWidget(options, SWT.NONE, report);
        columnsWidget
            .addColumnChangeListener(new ChangeListener<ColumnChangeEvent>() {
                @Override
                public void handleEvent(ColumnChangeEvent event) {
                    setDirty(true);
                    book.reflow(true);
                    form.layout(true, true);
                }
            });

        section.setClient(options);
    }

    // TODO: extract printing/ exporting methods into some interfaces and
    // classes

    public void exportCsv() {
        final Holder<String> path = new Holder<String>(null);

        if (!MessageDialog.openQuestion(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), "Confirm",
            "Export table contents?")) {
            return;
        }

        String defaultFilename = report.getName().replaceAll(" ", "_") + "_"
            + DateFormatter.formatAsDate(new Date());

        FileDialog fd = new FileDialog(form.getShell(), SWT.SAVE);
        fd.setOverwrite(true);
        fd.setText("Export as");
        fd.setFilterExtensions(new String[] { "*.csv" });
        fd.setFileName(defaultFilename);

        path.setValue(fd.open());

        if (path.getValue() == null || !path.getValue().endsWith(".csv")) {
            BioBankPlugin.openAsyncError("Exporting canceled.",
                "Select a valid path and try again.");
        }

        IRunnableContext context = new ProgressMonitorDialog(Display
            .getDefault().getActiveShell());
        try {
            context.run(true, true, new IRunnableWithProgress() {
                @Override
                public void run(final IProgressMonitor monitor) {
                    monitor.beginTask("Preparing Report...",
                        IProgressMonitor.UNKNOWN);
                    try {
                        exportCsv(path.getValue(), getHeaders(), monitor);
                    } catch (Exception e) {
                        BioBankPlugin.openAsyncError("Error exporting results",
                            e);
                        return;
                    }
                }
            });
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void exportCsv(String path, String[] headers,
        IProgressMonitor monitor) throws Exception {

        CsvListWriter writer = new CsvListWriter(new FileWriter(path),
            CsvPreference.EXCEL_PREFERENCE);

        // TODO: write filter, operators, and their values?

        writer.writeHeader(headers);
        int numHeaders = headers.length;

        BiobankLabelProvider labelProvider = resultsTable.getLabelProvider();
        Object[] data = new Object[numHeaders];
        for (Object row : results) {
            if (monitor.isCanceled()) {
                throw new Exception("Exporting canceled.");
            }

            for (int i = 0; i < numHeaders; i++) {
                data[i] = labelProvider.getColumnText(row, i);
            }

            writer.write(data);
        }

        writer.close();
    }
}
