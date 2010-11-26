package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.ComboViewer;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ReportWrapper;
import edu.ualberta.med.biobank.model.EntityColumn;
import edu.ualberta.med.biobank.model.EntityFilter;
import edu.ualberta.med.biobank.model.ReportColumn;
import edu.ualberta.med.biobank.treeview.report.ReportAdapter;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;
import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectWidget;
import edu.ualberta.med.biobank.widgets.report.ChangeListener;
import edu.ualberta.med.biobank.widgets.report.ColumnSelectWidget;
import edu.ualberta.med.biobank.widgets.report.FilterChangeEvent;
import edu.ualberta.med.biobank.widgets.report.FilterSelectWidget;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ReportEntryForm extends BiobankEntryForm {
    // TODO: put this somewhere so it can be shared

    public static final String ID = "edu.ualberta.med.biobank.forms.ReportEntryForm";

    private static final Comparator<EntityFilter> COMPARE_FILTERS_BY_NAME = new Comparator<EntityFilter>() {
        @Override
        public int compare(EntityFilter lhs, EntityFilter rhs) {
            return lhs.getName().compareToIgnoreCase(rhs.getName());
        }
    };

    private FilterSelectWidget filters;
    private ReportAdapter reportAdapter;
    private ReportWrapper report;

    private ComboViewer filterCombo;

    private Section filtersSection;

    @Override
    protected void saveForm() throws Exception {
        form.getDisplay().syncExec(new Runnable() {
            @Override
            public void run() {
                // pull the ReportFilter-s from the GUI before saving
                report.getWrappedObject().setReportFilterCollection(
                    filters.getReportFilters());
            }
        });

        report.persist();
        reportAdapter.getParent().performExpand();
    }

    // TODO: arrange methods from most to least accessible

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

        createRunButtons();

        // TODO: createResultsArea();
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

    private void createRunButtons() {
        Composite container = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        container.setLayout(layout);
        container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(container);

        Button runReport = new Button(container, SWT.NONE);
        runReport.setText("Run Report");
        runReport.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                System.out.println("12312123");
                try {
                    report.getWrappedObject().setReportFilterCollection(
                        filters.getReportFilters());

                    List<Object> results = SessionManager.getAppService()
                        .runReport(report.getWrappedObject());

                    for (Object o : results) {
                        if (o instanceof Object[]) {
                            Object[] row = (Object[]) o;
                            System.out.println(Arrays.toString(row));
                        } else {
                            System.out.println(o);
                        }
                    }

                    System.out.println(results.size());

                } catch (ApplicationException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void createFiltersSection() {
        filtersSection = createSection("Filters");

        Composite container = toolkit.createComposite(filtersSection, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        layout.verticalSpacing = 0;
        container.setLayout(layout);
        container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(container);

        Collection<EntityFilter> sortedEntityFilters = getSortedEntityFilters(
            report, COMPARE_FILTERS_BY_NAME);

        filters = new FilterSelectWidget(container, SWT.NONE, report);
        filters
            .addFilterChangedListener(new ChangeListener<FilterChangeEvent>() {
                @Override
                public void handleEvent(FilterChangeEvent event) {
                    setDirty(true);
                    form.layout(true, true);

                    EntityFilter entityFilter = event.getEntityFilter();
                    if (event.isSelected()) {
                        filterCombo.remove(entityFilter);
                    } else {
                        filterCombo.add(entityFilter);
                    }
                }
            });

        createFilterCombo(container, sortedEntityFilters);

        filtersSection.setClient(container);
    }

    private void createFilterCombo(Composite parent,
        Collection<EntityFilter> entityFilters) {

        // TODO: should put combo "above" filters, not to their right, so if
        // filters expand, then the combo can go ontop, otherwise float top
        // right (think css "float: right").
        Composite container = toolkit.createComposite(parent);

        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 5;
        layout.verticalSpacing = 0;
        layout.marginWidth = 0;
        layout.marginHeight = 2;
        container.setLayout(layout);

        GridData layoutData = new GridData();
        layoutData.verticalAlignment = SWT.TOP;
        container.setLayoutData(layoutData);

        Label label = new Label(container, SWT.NONE);
        label.setText("Add filter:");

        filterCombo = new ComboViewer(container, SWT.NONE);
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
        for (EntityFilter entityFilter : entityFilters) {
            if (filters.getFilterRow(entityFilter) == null) {
                filterCombo.add(entityFilter);
            }
        }

        filterCombo
            .addPostSelectionChangedListener(new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    Object selection = ((IStructuredSelection) filterCombo
                        .getSelection()).getFirstElement();
                    if (selection instanceof EntityFilter) {
                        EntityFilter entityFilter = (EntityFilter) selection;
                        filterCombo.remove(entityFilter);
                        filters.addFilterRow(entityFilter);

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

        Label columnsLabel = new Label(options, SWT.NONE);
        columnsLabel.setText("Columns:");

        // TODO: support reseting to original columns.

        // TODO: support re-ordering
        final MultiSelectWidget columns = new MultiSelectWidget(options,
            SWT.NONE, "Displayed Columns", "Available Columns", 75);

        final Map<Integer, EntityColumn> entityColumnMap = new HashMap<Integer, EntityColumn>();
        final LinkedHashMap<Integer, String> columnNameMap = new LinkedHashMap<Integer, String>();

        for (EntityColumn entityCol : report.getEntityColumnCollection()) {
            entityColumnMap.put(entityCol.getId(), entityCol);
            columnNameMap.put(entityCol.getId(), entityCol.getName());
        }

        final List<Integer> selectedColumns = new ArrayList<Integer>();
        for (ReportColumn reportCol : report.getReportColumnCollection()) {
            selectedColumns.add(reportCol.getEntityColumn().getId());
        }

        columns.setSelections(columnNameMap, selectedColumns);
        columns
            .addSelectionChangedListener(new BiobankEntryFormWidgetListener() {
                @Override
                public void selectionChanged(MultiSelectEvent event) {
                    Set<ReportColumn> cols = new HashSet<ReportColumn>();

                    int position = 0;
                    for (Integer columnId : columns.getSelected()) {
                        ReportColumn col = new ReportColumn();
                        col.setEntityColumn(entityColumnMap.get(columnId));
                        col.setPosition(position);

                        cols.add(col);
                        position++;
                    }

                    report.getWrappedObject().setReportColumnCollection(cols);
                    ReportEntryForm.this.setDirty(true);
                }
            });

        section.setClient(options);

        Label tmp = new Label(options, SWT.NONE);
        Label tmp2 = new Label(options, SWT.NONE);
        ColumnSelectWidget csw = new ColumnSelectWidget(options, SWT.NONE,
            report);
    }
}
