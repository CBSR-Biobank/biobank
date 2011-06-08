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
import java.util.Map;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.reports.filters.FilterOperator;
import edu.ualberta.med.biobank.common.reports.filters.FilterType;
import edu.ualberta.med.biobank.common.reports.filters.FilterTypes;
import edu.ualberta.med.biobank.common.reports.filters.SelectableFilterType;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.model.Entity;
import edu.ualberta.med.biobank.model.EntityColumn;
import edu.ualberta.med.biobank.model.EntityFilter;
import edu.ualberta.med.biobank.model.Report;
import edu.ualberta.med.biobank.model.ReportColumn;
import edu.ualberta.med.biobank.model.ReportFilter;
import edu.ualberta.med.biobank.model.ReportFilterValue;
import gov.nih.nci.system.applicationservice.ApplicationException;

class FilterRow extends Composite {
    // TODO: make configurable?
    private static final int MAX_QUERY_TIME = 3;
    private static final int MAX_SUGGESTIONS = 100;
    private static final SimpleDateFormat SQL_DATE_FORMAT = new SimpleDateFormat(
        "yyyy-MM-dd HH:mm:ss");
    private final FilterSelectWidget filtersWidget;
    private final EntityFilter filter;
    private Composite container;
    private Composite inputContainer;
    private Button checkbox, autoButton;
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
        if (op != null) {
            operators.setSelection(new StructuredSelection(op), true);
        }
    }

    public Collection<ReportFilterValue> getValues() {
        if (filterValueWidget != null) {
            return new HashSet<ReportFilterValue>(filterValueWidget.getValues());
        }
        return new HashSet<ReportFilterValue>();
    }

    public void setValues(Collection<ReportFilterValue> values) {
        if (filterValueWidget != null) {
            filterValueWidget.setValues(values);
        }
    }

    public EntityFilter getEntityFilter() {
        return filter;
    }

    private void init() {
        GridLayout layout = new GridLayout(1, false);
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        setLayout(layout);

        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
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
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
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
                    isChecked, true));
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

        if (ops.isEmpty()) {
            operators.getControl().setVisible(false);
            ((GridData) operators.getControl().getLayoutData()).exclude = true;
        }
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
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.verticalAlignment = SWT.TOP;
        inputContainer.setLayoutData(layoutData);
    }

    private FilterValueWidget createSimpleFilterValueWidget() {
        FilterValueWidget result = null;
        boolean isDateProperty = "Date".equals(filter.getEntityProperty()
            .getPropertyType().getName());

        Integer filterTypeId = filter.getFilterType();
        FilterType filterType = FilterTypes.getFilterType(filterTypeId);
        if (filterType instanceof SelectableFilterType) {
            SelectableFilterType selectable = (SelectableFilterType) filterType;
            SelectFilterValueWidget widget;

            widget = new SelectFilterValueWidget(inputContainer);

            // TODO: cache options?
            Map<String, String> options;
            options = selectable.getOptions(SessionManager.getAppService());

            if (suggestions != null) {
                // only include options that are in the suggestions
                Collection<String> toRemove = new ArrayList<String>();
                for (Map.Entry<String, String> entry : options.entrySet()) {
                    String key = entry.getKey();
                    if (!suggestions.contains(key)) {
                        toRemove.add(key);
                    }
                }

                if (!toRemove.isEmpty()) {
                    // TODO: ask before removing?
                    options.keySet().removeAll(toRemove);
                    filtersWidget
                        .notifyListeners(new FilterChangeEvent(filter));
                }
            }

            widget.setOptions(options);

            result = widget;
        } else if (suggestions != null) {
            ComboFilterValueWidget combo;
            combo = new ComboFilterValueWidget(inputContainer);
            combo.getComboViewer().add(suggestions.toArray());
            result = combo;
        } else if (isDateProperty) {
            FilterOperator op = getOperator();

            int style = SWT.DATE;
            if (op != null
                && !EnumSet.of(FilterOperator.SAME_DAY_AS_ANY,
                    FilterOperator.SAME_WEEK_AS_ANY,
                    FilterOperator.SAME_MONTH_AS_ANY,
                    FilterOperator.SAME_YEAR_AS_ANY).contains(op)) {
                style |= SWT.TIME;
            }

            result = new DateTimeFilterValueWidget(inputContainer, style);
        } else {
            result = new TextFilterValueWidget(inputContainer);
        }

        return result;
    }

    private FilterValueWidget createFilterValueWidget(boolean isEditMode) {
        FilterValueWidget result = null;

        FilterOperator op = getOperator();

        if (op != null && op.isValueRequired()) {
            result = createSimpleFilterValueWidget();

            if (EnumSet.of(FilterOperator.BETWEEN, FilterOperator.BETWEEN_ANY,
                FilterOperator.NOT_BETWEEN, FilterOperator.NOT_BETWEEN_ANY)
                .contains(op)) {
                result = new BetweenFilterValueWidget(inputContainer, result,
                    createSimpleFilterValueWidget());
            }

            if (op.isSetOperator()) {
                SetFilterValueWidget set;
                set = new SetFilterValueWidget(inputContainer, result);

                if (isEditMode) {
                    set.setMode(SetFilterValueWidget.Mode.EditMode);
                }

                result = set;
            }
        }

        return result;
    }

    private void createInputs() {
        createInputs(false);
    }

    private void createInputs(boolean isEditMode) {
        Collection<ReportFilterValue> oldValues = getValues();

        // remember current setting, if possible
        if (!isEditMode && filterValueWidget instanceof SetFilterValueWidget) {
            isEditMode = ((SetFilterValueWidget) filterValueWidget).getMode() == SetFilterValueWidget.Mode.EditMode;
        }

        disposeInputContainer();
        createInputContainer();

        filterValueWidget = createFilterValueWidget(isEditMode);

        if (filterValueWidget == null) {
            disposeInputContainer();
            return;
        }

        filterValueWidget.addChangeListener(new ChangeListener<ChangeEvent>() {
            @Override
            public void handleEvent(ChangeEvent event) {
                boolean isDataChange = event == null ? true : event
                    .isDataChange();

                filtersWidget.notifyListeners(new FilterChangeEvent(filter,
                    true, isDataChange));
            }
        });

        Control control = filterValueWidget.getControl();
        if (control != null) {
            GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
            layoutData.grabExcessHorizontalSpace = true;
            control.setLayoutData(layoutData);
        }

        // try to reset the old values
        setValues(oldValues);

        // auto-suggest
        createAutoSuggest(inputContainer);
    }

    private void createAutoSuggest(Composite parent) {
        autoButton = new Button(parent, SWT.NONE);
        GridData layoutData = new GridData();
        layoutData.verticalAlignment = SWT.TOP;
        autoButton.setLayoutData(layoutData);

        autoButton
            .setToolTipText("Suggest possible values (considers other filters)");
        autoButton.setImage(BiobankPlugin.getDefault().getImageRegistry()
            .get(BiobankPlugin.IMG_WAND));
        autoButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {

                autoButton.setEnabled(false);

                BusyIndicator.showWhile(autoButton.getDisplay(),
                    new Runnable() {
                        @Override
                        public void run() {
                            if (autoSuggest()) {
                                filtersWidget
                                    .notifyListeners(new FilterChangeEvent(
                                        filter, true, false));
                            }
                            autoButton.setEnabled(true);
                        }
                    });
            }
        });
    }

    private Report getAutoSuggestReport() {
        Entity entity = filtersWidget.getReport().getEntity()
            .getWrappedObject();

        Collection<ReportFilter> reportFilters = new HashSet<ReportFilter>();
        for (ReportFilter filter : filtersWidget.getReportFilters()) {
            if (filter.getEntityFilter().equals(this.filter)) {
                // do not include the filter we're finding suggestions for
                continue;
            }

            Integer opId = filter.getOperator();
            if (opId != null) {
                FilterOperator op = FilterOperator.getFilterOperator(opId);

                if (op.isValueRequired()
                    && (filter.getReportFilterValueCollection() == null || filter
                        .getReportFilterValueCollection().isEmpty())) {
                    // do not consider filters that require a value, yet none is
                    // set
                    continue;
                }
            }
            reportFilters.add(filter);
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

    private boolean autoSuggest() {
        Report report = getAutoSuggestReport();

        long start = System.currentTimeMillis();
        List<Object> results = null;
        try {
            results = SessionManager.getAppService().runReport(report,
                MAX_SUGGESTIONS, 0, MAX_QUERY_TIME);

            if (results.size() >= MAX_SUGGESTIONS) {
                BgcPlugin.openError("Cannot Suggest Options",
                    "There are too many possible suggestions to display.");
                return false;
            }
        } catch (ApplicationException e) {
            long end = System.currentTimeMillis();

            if ((end - start) / 1000 >= MAX_QUERY_TIME) {
                BgcPlugin.openError("Cannot Suggest Options",
                    "It is taking too long to find suggestions.", e);
            } else {
                BgcPlugin.openError("Cannot Suggest Options",
                    "There was a problem trying to find suggestions.", e);
            }
            return false;
        }

        List<String> suggestions = new ArrayList<String>();
        for (Object result : results) {
            if (result instanceof Object[]) {
                Object[] row = (Object[]) result;
                if (row.length > 1 && row[1] != null) {
                    Object o = row[1];
                    if (o instanceof Date) {
                        suggestions.add(SQL_DATE_FORMAT.format((Date) o));
                    } else {
                        suggestions.add(o.toString());
                    }
                }
            }
        }

        Collections.sort(suggestions);

        if (suggestions.isEmpty()) {
            BgcPlugin.openError("Cannot Suggest Options",
                "There are no possible values to suggest.");

            // forget old suggestions, if any
            suggestions = null;
        }

        this.suggestions = suggestions;

        createInputs(true);

        return true;
    }
}
