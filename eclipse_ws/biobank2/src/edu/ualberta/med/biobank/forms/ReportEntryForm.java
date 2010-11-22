package edu.ualberta.med.biobank.forms;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.reports.filters.FilterOperator;
import edu.ualberta.med.biobank.common.reports.filters.FilterType;
import edu.ualberta.med.biobank.common.reports.filters.FilterTypes;
import edu.ualberta.med.biobank.common.wrappers.ReportWrapper;
import edu.ualberta.med.biobank.model.EntityColumn;
import edu.ualberta.med.biobank.model.EntityFilter;
import edu.ualberta.med.biobank.model.ReportColumn;
import edu.ualberta.med.biobank.model.ReportFilter;
import edu.ualberta.med.biobank.model.ReportFilterValue;
import edu.ualberta.med.biobank.treeview.report.ReportAdapter;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;
import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectWidget;
import edu.ualberta.med.biobank.widgets.report.ColumnSelectWidget;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ReportEntryForm extends BiobankEntryForm {
    // TODO: put this somewhere so it can be shared
    private static final SimpleDateFormat SQL_DATE_FORMAT = new SimpleDateFormat(
        "yyyy-MM-dd HH:mm:ss");

    public static final String ID = "edu.ualberta.med.biobank.forms.ReportEntryForm";

    private static final String OK_MSG = "Report details.";
    private static final String OK_MSG_NEW = "New report details.";

    private static final Comparator<EntityFilter> COMPARE_FILTERS_BY_NAME = new Comparator<EntityFilter>() {
        @Override
        public int compare(EntityFilter lhs, EntityFilter rhs) {
            return lhs.getName().compareToIgnoreCase(rhs.getName());
        }
    };

    private final Map<EntityFilter, FilterRow> filterRowMap = new LinkedHashMap<EntityFilter, FilterRow>();

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
                    getReportFilterCollection());
            }
        });

        report.persist();
        reportAdapter.getParent().performExpand();
    }

    // TODO: arrange methods from most to least accessible
    private Collection<ReportFilter> getReportFilterCollection() {
        Collection<ReportFilter> rfCollection = new HashSet<ReportFilter>();

        int filterPosition = 0;
        for (Map.Entry<EntityFilter, FilterRow> entry : filterRowMap.entrySet()) {
            EntityFilter entityFilter = entry.getKey();
            FilterRow filterRow = entry.getValue();

            // don't include filters that the user has hidden
            if (!filterRow.getVisible()) {
                continue;
            }

            ReportFilter reportFilter = new ReportFilter();
            reportFilter.setPosition(filterPosition);
            // TODO: check if operator is null or not, do what?
            reportFilter.setOperator(filterRow.getOperator().getId());
            reportFilter.setEntityFilter(entityFilter);

            // TODO: extract method
            Collection<ReportFilterValue> rfvCollection = new HashSet<ReportFilterValue>();
            int valuePosition = 0;
            for (String value : filterRow.getValues()) {
                ReportFilterValue rfv = new ReportFilterValue();
                rfv.setPosition(valuePosition);
                rfv.setValue(value);

                rfvCollection.add(rfv);

                valuePosition++;
            }
            reportFilter.setReportFilterValueCollection(rfvCollection);

            rfCollection.add(reportFilter);

            filterPosition++;
        }

        return rfCollection;
    }

    @Override
    protected String getOkMessage() {
        return report.isNew() ? OK_MSG_NEW : OK_MSG;
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

        String tabName;
        if (report.isNew())
            tabName = "New Report";
        else
            tabName = "Report " + report.getName();

        setPartName(tabName);

        // TODO: add "Save As" toolbar icon?
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Report Details");
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
            BiobankText.class, SWT.NONE, "Name", null, report, "name",
            new NonEmptyStringValidator("Name is required.")));

        createBoundWidgetWithLabel(container, BiobankText.class, SWT.MULTI,
            "Description", null, report, "description", null);
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
                        getReportFilterCollection());

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

        Composite container = toolkit.createComposite(filtersSection,
            SWT.BORDER_DASH);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        layout.verticalSpacing = 0;
        container.setLayout(layout);
        container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(container);

        Collection<EntityFilter> sortedEntityFilters = getSortedEntityFilters(
            report, COMPARE_FILTERS_BY_NAME);

        createFilterRows(container, sortedEntityFilters);
        createFilterCombo(container, sortedEntityFilters);

        filtersSection.setClient(container);
    }

    private void createFilterRows(Composite parent,
        Collection<EntityFilter> filters) {
        Composite table = toolkit.createComposite(parent);

        GridLayout layout = new GridLayout(1, false);
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        table.setLayout(layout);

        GridData layoutData = new GridData();
        layoutData.grabExcessHorizontalSpace = true;
        table.setLayoutData(layoutData);

        Collection<ReportFilter> reportFilters = report
            .getReportFilterCollection();

        for (EntityFilter entityFilter : filters) {
            FilterRow filterRow = new FilterRow(table, entityFilter);

            for (ReportFilter reportFilter : reportFilters) {
                if (entityFilter.getId().equals(
                    reportFilter.getEntityFilter().getId())) {
                    filterRow.setValues(ReportWrapper
                        .getFilterValueStrings(reportFilter));
                    filterRow.setOperatorId(reportFilter.getOperator());
                    filterRow.setVisible(true);
                    break;
                }
            }

            filterRowMap.put(entityFilter, filterRow);
        }
    }

    private void createFilterCombo(Composite parent,
        Collection<EntityFilter> filters) {

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
        for (EntityFilter entityFilter : filters) {
            FilterRow filterRow = filterRowMap.get(entityFilter);
            if (!filterRow.getVisible()) {
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
                        EntityFilter filter = (EntityFilter) selection;
                        filterCombo.remove(filter);

                        FilterRow filterRow = filterRowMap.get(filter);
                        if (filterRow != null) {
                            filterRow.setVisible(true);
                            form.layout(true, true);
                        }

                        ReportEntryForm.this.setDirty(true);
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

        // TODO: eager-load the Report

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

    /**
     * GUI representation of a filter row.
     * 
     * @author jferland
     * 
     */
    // TODO: externalize to widget or something and allow registering of
    // listeners for modification events?
    private class FilterRow {
        private Composite container;
        private EntityFilter filter;
        private Button checkbox;
        private ComboViewer operators;
        private ValueAccessor valueAccessor;

        public FilterRow(Composite parent, EntityFilter filter) {
            this.filter = filter;

            createContainer(parent);
            createCheckbox();
            createOperators();
            createInputs(null);

            setVisible(false);
        }

        public void setVisible(boolean visible) {
            ((GridData) container.getLayoutData()).exclude = !visible;
            container.setVisible(visible);
            checkbox.setSelection(visible);
        }

        public boolean getVisible() {
            return container.getVisible();
        }

        public FilterOperator getOperator() {
            return getSelectedFilterOperator(operators.getSelection());
        }

        public void setOperatorId(int id) {
            setOperator(FilterOperator.getFilterOperator(id));
        }

        public void setOperator(FilterOperator op) {
            operators.setSelection(new StructuredSelection(op), true);
        }

        public Collection<String> getValues() {
            return valueAccessor.getValues();
        }

        public void setValues(Collection<String> values) {
            valueAccessor.setValues(values);
        }

        private void createContainer(Composite parent) {
            container = toolkit.createComposite(parent);
            GridLayout layout = new GridLayout(3, false);
            layout.horizontalSpacing = 5;
            layout.verticalSpacing = 0;
            layout.marginWidth = 0;
            layout.marginHeight = 2;
            container.setLayout(layout);
            GridData layoutData = new GridData();
            container.setLayoutData(layoutData);
        }

        private void createCheckbox() {
            checkbox = new Button(container, SWT.CHECK);
            GridData checkboxGridData = new GridData();
            checkboxGridData.widthHint = 250;
            checkbox.setLayoutData(checkboxGridData);
            checkbox.setText(filter.getName());
            checkbox.setSelection(true);
            checkbox.addListener(SWT.Selection, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    if (!checkbox.getSelection()) {
                        ((GridData) container.getLayoutData()).exclude = true;
                        container.setVisible(false);
                        form.layout(true, true);

                        filterCombo.add(filter);
                    }
                }
            });
        }

        private void createOperators() {
            operators = new ComboViewer(container, SWT.NONE);
            operators.setLabelProvider(new LabelProvider() {
                @Override
                public String getText(Object element) {
                    if (element instanceof FilterOperator) {
                        return ((FilterOperator) element).getDisplayString();
                    }
                    return "";
                }
            });

            FilterType filterType = FilterTypes.getFilterType(filter
                .getFilterType());
            Collection<FilterOperator> ops = filterType.getOperators();
            operators.add(ops.toArray());

            // set default to be first (if any)
            for (FilterOperator op : ops) {
                setOperator(op);
                break;
            }

            operators
                .addSelectionChangedListener(new ISelectionChangedListener() {
                    @Override
                    public void selectionChanged(SelectionChangedEvent event) {
                        FilterOperator oldOp = getSelectedFilterOperator(operators
                            .getSelection());
                        // TODO: in the future may need to re-call createInputs
                        // when the chosen operator changes

                        ReportEntryForm.this.setDirty(true);
                    }
                });
        }

        private FilterOperator getSelectedFilterOperator(ISelection selection) {
            if (selection instanceof IStructuredSelection) {
                Object structuredSelection = ((IStructuredSelection) selection)
                    .getFirstElement();
                if (structuredSelection instanceof FilterOperator) {
                    return (FilterOperator) structuredSelection;
                }
            }
            return null;
        }

        private void createInputs(FilterOperator oldOp) {
            FilterOperator op = getOperator();

            // TODO: for now, use comma-delimited values. In the future can
            // create and use a set-widget (see Nebula's TableCombo for a
            // candidate).

            // TODO: some operators may require NO VALUES/ ARGUMENTS!!

            // TODO: I don't like that I have to compare to seemingly
            // arbitrary strings
            String propertyTypeName = filter.getEntityProperty()
                .getPropertyType().getName();

            if ("Date".equals(propertyTypeName)) {
                final DateTimeWidget dateTimeWidget = new DateTimeWidget(
                    container, SWT.DATE | SWT.TIME, null);
                GridData dateTimeWidgetLayoutData = new GridData();
                dateTimeWidgetLayoutData.grabExcessHorizontalSpace = true;
                dateTimeWidget.setLayoutData(dateTimeWidgetLayoutData);
                dateTimeWidget.adaptToToolkit(toolkit, true);
                // TODO: add listener to execute
                // ReportEntryForm.this.setDirty(true);
                valueAccessor = new ValueAccessor() {
                    @Override
                    public Collection<String> getValues() {
                        if (dateTimeWidget.getDate() != null) {
                            String dateString = SQL_DATE_FORMAT
                                .format(dateTimeWidget.getDate());
                            return Arrays.asList(dateString);
                        }
                        return new ArrayList<String>();
                    }

                    @Override
                    public void setValues(Collection<String> values) {
                        for (String dateString : values) {
                            try {
                                Date date = SQL_DATE_FORMAT.parse(dateString);
                                dateTimeWidget.setDate(date);
                            } catch (ParseException e) {
                                // TODO: show appropriate message?
                            }
                            break;
                        }
                    }
                };
            } else {
                final Text text = new Text(container, SWT.BORDER);

                text.addModifyListener(new ModifyListener() {
                    @Override
                    public void modifyText(ModifyEvent e) {
                        ReportEntryForm.this.setDirty(true);
                    }
                });

                GridData layoutData = new GridData();
                layoutData.widthHint = 150;
                text.setLayoutData(layoutData);

                valueAccessor = new ValueAccessor() {
                    @Override
                    public Collection<String> getValues() {
                        Collection<String> cleanedValues = new ArrayList<String>();
                        String[] rawValues = text.getText().split(",");
                        for (String rawValue : rawValues) {
                            cleanedValues.add(rawValue.trim());
                        }
                        return cleanedValues;
                    }

                    @Override
                    public void setValues(Collection<String> values) {
                        text.setText(StringUtils.join(values.toArray(), ", "));
                    }
                };
            }
        }
    }

    /**
     * Different widgets need to provide a way to get and set their values.
     * 
     * @author jferland
     * 
     */
    private interface ValueAccessor {
        public Collection<String> getValues();

        public void setValues(Collection<String> values);
    }
}
