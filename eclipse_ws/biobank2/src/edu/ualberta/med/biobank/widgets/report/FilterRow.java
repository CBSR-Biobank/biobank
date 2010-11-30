package edu.ualberta.med.biobank.widgets.report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.reports.filters.FilterOperator;
import edu.ualberta.med.biobank.common.reports.filters.FilterType;
import edu.ualberta.med.biobank.common.reports.filters.FilterTypes;
import edu.ualberta.med.biobank.model.Entity;
import edu.ualberta.med.biobank.model.EntityColumn;
import edu.ualberta.med.biobank.model.EntityFilter;
import edu.ualberta.med.biobank.model.Report;
import edu.ualberta.med.biobank.model.ReportColumn;
import edu.ualberta.med.biobank.model.ReportFilter;
import edu.ualberta.med.biobank.model.ReportFilterValue;
import gov.nih.nci.system.applicationservice.ApplicationException;

class FilterRow extends Composite {
    private static final SimpleDateFormat SQL_DATE_FORMAT = new SimpleDateFormat(
        "yyyy-MM-dd HH:mm:ss");
    private final FilterSelectWidget filtersWidget;
    private final EntityFilter filter;
    private Composite container;
    private Composite inputContainer;
    private Button checkbox;
    private ComboViewer operators;
    private FilterValueWidget filterValueWidget;
    private Collection<String> suggestions;

    public FilterRow(FilterSelectWidget filters, Composite parent, int style,
        EntityFilter filter) {
        super(parent, style);
        this.filtersWidget = filters;
        this.filter = filter;

        init();

        createContainer();
        createCheckbox();
        createOperators();
        createInputs();
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

    public Collection<ReportFilterValue> getValues() {
        if (filterValueWidget != null) {
            return filterValueWidget.getValues();
        }
        return Arrays.asList();
    }

    public void setValues(Collection<ReportFilterValue> values) {
        if (filterValueWidget != null) {
            filterValueWidget.setValues(values);
        }
    }

    private void init() {
        GridLayout layout = new GridLayout(1, false);
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        setLayout(layout);

        GridData layoutData = new GridData();
        layoutData.grabExcessHorizontalSpace = true;
        setLayoutData(layoutData);
    }

    private void createContainer() {
        container = new Composite(this, SWT.NONE);
        GridLayout layout = new GridLayout(3, false);
        layout.horizontalSpacing = 5;
        layout.verticalSpacing = 0;
        layout.marginWidth = 0;
        layout.marginHeight = 2;
        container.setLayout(layout);
        GridData layoutData = new GridData();
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.verticalAlignment = SWT.TOP;
        container.setLayoutData(layoutData);
    }

    private void createCheckbox() {
        // TODO: replace with label that checks box so can wrap label text
        checkbox = new Button(container, SWT.CHECK);
        GridData layoutData = new GridData();
        layoutData.widthHint = 225;
        layoutData.minimumWidth = 225;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.verticalAlignment = SWT.TOP;
        layoutData.verticalIndent = 3;
        checkbox.setLayoutData(layoutData);

        checkbox.setText(filter.getName());
        checkbox.setSelection(true);
        checkbox.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                boolean isChecked = checkbox.getSelection();
                if (!isChecked) {
                    filtersWidget.removeFilterRow(filter);
                }
                filtersWidget.notifyListeners(new FilterChangeEvent(filter,
                    isChecked));
            }
        });
    }

    private void createOperators() {
        operators = new ComboViewer(container, SWT.READ_ONLY);

        Control control = operators.getControl();

        GridData layoutData = new GridData();
        layoutData.verticalAlignment = SWT.TOP;
        control.setLayoutData(layoutData);

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

        operators.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                createInputs();
                filtersWidget.notifyListeners(new FilterChangeEvent(filter));
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

    private void disposeInputContainer() {
        if (inputContainer != null && !inputContainer.isDisposed()) {
            inputContainer.dispose();
        }
    }

    private void createInputContainer() {
        inputContainer = new Composite(container, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 5;
        layout.verticalSpacing = 0;
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        inputContainer.setLayout(layout);
        GridData layoutData = new GridData();
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.verticalAlignment = SWT.TOP;
        inputContainer.setLayoutData(layoutData);
    }

    private FilterValueWidget createSimpleFilterValueWidget() {
        FilterValueWidget result = null;
        boolean isDateProperty = "Date".equals(filter.getEntityProperty()
            .getPropertyType().getName());

        if (suggestions != null) {
            ComboFilterValueWidget combo;
            combo = new ComboFilterValueWidget(inputContainer);
            combo.getComboViewer().add(suggestions.toArray());
            result = combo;
        } else if (isDateProperty) {
            result = new DateTimeFilterValueWidget(inputContainer);
        } else {
            result = new TextFilterValueWidget(inputContainer);
        }

        return result;
    }

    private FilterValueWidget createFilterValueWidget() {
        FilterValueWidget result = null;

        // TODO: for now, use comma-delimited values. In the future can
        // create and use a set-widget (see Nebula's TableCombo for a
        // candidate).

        result = createSimpleFilterValueWidget();

        FilterOperator op = getOperator();
        if (EnumSet.of(FilterOperator.BETWEEN, FilterOperator.NOT_BETWEEN)
            .contains(op)) {
            result = new BetweenFilterValueWidget(inputContainer, result,
                createSimpleFilterValueWidget());
        }

        if (op.isSetOperator()) {
            result = new SetFilterValueWidget(inputContainer, result);
        }

        return result;
    }

    private void createInputs() {
        Collection<ReportFilterValue> oldValues = getValues();

        disposeInputContainer();
        createInputContainer();

        filterValueWidget = null;

        FilterOperator op = getOperator();

        if (!op.isValueRequired()) {
            return;
        }

        filterValueWidget = createFilterValueWidget();
        filterValueWidget.addChangeListener(new ChangeListener<Object>() {
            @Override
            public void handleEvent(Object event) {
                filtersWidget.notifyListeners(new FilterChangeEvent(filter));
            }
        });

        Control control = filterValueWidget.getControl();
        if (control != null) {
            control.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        }

        // try to reset the old values
        setValues(oldValues);

        // auto-suggest
        createAutoSuggest(inputContainer);
    }

    private void createAutoSuggest(Composite parent) {
        Button button = new Button(parent, SWT.NONE);
        GridData layoutData = new GridData();
        layoutData.verticalAlignment = SWT.TOP;
        button.setLayoutData(layoutData);

        // TODO: replace with icon?
        button.setText("Suggest");
        button.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                autoSuggest();
                filtersWidget.notifyListeners(new FilterChangeEvent(filter));
            }
        });
    }

    private Report getAutoSuggestReport() {
        Entity entity = filtersWidget.getReport().getEntity()
            .getWrappedObject();

        Collection<ReportFilter> reportFilters = new HashSet<ReportFilter>();
        for (ReportFilter filter : filtersWidget.getReportFilters()) {
            if (!filter.getEntityFilter().equals(this.filter)) {
                reportFilters.add(filter);
            }
        }

        ReportColumn rc = new ReportColumn();
        rc.setPosition(0);
        EntityColumn ec = new EntityColumn();
        ec.setEntityProperty(filter.getEntityProperty());
        rc.setEntityColumn(ec);

        Report report = new Report();
        report.setEntity(entity);
        report.setReportFilterCollection(reportFilters);
        report.setIsCount(true);
        report.setReportColumnCollection(new HashSet<ReportColumn>(Arrays
            .asList(rc)));

        return report;
    }

    private void autoSuggest() {
        Report report = getAutoSuggestReport();

        List<Object> results = null;
        try {
            // TODO: set max query results
            // TODO: set max query time
            // TODO: display monitor when loading suggestions
            results = SessionManager.getAppService().runReport(report);
        } catch (ApplicationException e) {
            // TODO: appropriate error message
            e.printStackTrace();
        }

        List<String> suggestions = new ArrayList<String>();
        for (Object result : results) {
            if (result instanceof Object[]) {
                Object[] row = (Object[]) result;
                if (row.length > 0 && row[0] != null) {
                    Object o = row[0];
                    if (o instanceof Date) {
                        suggestions.add(SQL_DATE_FORMAT.format((Date) o));
                    } else {
                        suggestions.add(row[0].toString());
                    }
                }
            }
        }

        Collections.sort(suggestions);

        this.suggestions = suggestions;

        createInputs();
    }
}
