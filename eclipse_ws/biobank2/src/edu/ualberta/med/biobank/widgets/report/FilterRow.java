package edu.ualberta.med.biobank.widgets.report;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

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
import edu.ualberta.med.biobank.widgets.DateTimeWidget;
import gov.nih.nci.system.applicationservice.ApplicationException;

class FilterRow extends Composite {
    private static final SimpleDateFormat SQL_DATE_FORMAT = new SimpleDateFormat(
        "yyyy-MM-dd HH:mm:ss");
    private final FilterSelectWidget filtersWidget;
    private final EntityFilter filter;
    private Composite container;
    private Button checkbox;
    private ComboViewer operators;
    private ValueAccessor valueAccessor;

    public FilterRow(FilterSelectWidget filters, Composite parent, int style,
        EntityFilter filter) {
        super(parent, style);
        this.filtersWidget = filters;
        this.filter = filter;

        init();

        createContainer();
        createCheckbox();
        createOperators();
        createInputs(null);
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
        return valueAccessor.getValues();
    }

    public void setValues(Collection<ReportFilterValue> values) {
        valueAccessor.setValues(values);
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
        GridLayout layout = new GridLayout(4, false);
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
        GridData layoutData = new GridData();
        layoutData.grabExcessHorizontalSpace = true;
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

        operators.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                FilterOperator oldOp = getSelectedFilterOperator(operators
                    .getSelection());
                // TODO: in the future may need to re-call createInputs
                // when the chosen operator changes

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

    private void createInputs(FilterOperator oldOp) {
        FilterOperator op = getOperator();

        // TODO: for now, use comma-delimited values. In the future can
        // create and use a set-widget (see Nebula's TableCombo for a
        // candidate).

        // TODO: some operators may require NO VALUES/ ARGUMENTS!!

        // TODO: I don't like that I have to compare to seemingly
        // arbitrary strings
        String propertyTypeName = filter.getEntityProperty().getPropertyType()
            .getName();

        if ("Date".equals(propertyTypeName)) {
            final DateTimeWidget dateTimeWidget = new DateTimeWidget(container,
                SWT.DATE | SWT.TIME, null);
            GridData dateTimeWidgetLayoutData = new GridData();
            dateTimeWidgetLayoutData.grabExcessHorizontalSpace = true;
            dateTimeWidget.setLayoutData(dateTimeWidgetLayoutData);
            dateTimeWidget.addModifyListener(new ModifyListener() {
                @Override
                public void modifyText(ModifyEvent e) {
                    filtersWidget
                        .notifyListeners(new FilterChangeEvent(filter));
                }
            });

            valueAccessor = new ValueAccessor() {
                @Override
                public Collection<ReportFilterValue> getValues() {
                    if (!dateTimeWidget.isDisposed()
                        && dateTimeWidget.getDate() != null) {
                        String dateString = SQL_DATE_FORMAT
                            .format(dateTimeWidget.getDate());

                        ReportFilterValue value = new ReportFilterValue();
                        value.setPosition(0);
                        value.setValue(dateString);
                        return Arrays.asList(value);
                    }
                    return new ArrayList<ReportFilterValue>();
                }

                @Override
                public void setValues(Collection<ReportFilterValue> values) {
                    if (dateTimeWidget.isDisposed()) {
                        return;
                    }

                    for (ReportFilterValue value : values) {
                        try {
                            Date date = SQL_DATE_FORMAT.parse(value.getValue());
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
                    filtersWidget
                        .notifyListeners(new FilterChangeEvent(filter));
                }
            });

            GridData layoutData = new GridData();
            layoutData.widthHint = 150;
            text.setLayoutData(layoutData);

            valueAccessor = new ValueAccessor() {
                @Override
                public Collection<ReportFilterValue> getValues() {
                    Collection<ReportFilterValue> values = new ArrayList<ReportFilterValue>();
                    if (!text.isDisposed()) {
                        String[] rawValues = text.getText().split(",");
                        int position = 0;
                        for (String rawValue : rawValues) {
                            ReportFilterValue value = new ReportFilterValue();
                            value.setPosition(position);
                            value.setValue(rawValue.trim());
                            values.add(value);
                            position++;
                        }
                    }
                    return values;
                }

                @Override
                public void setValues(Collection<ReportFilterValue> values) {
                    if (!text.isDisposed()) {
                        final String delimiter = ", ";
                        StringBuilder builder = new StringBuilder();
                        for (ReportFilterValue value : values) {
                            builder.append(value.getValue());
                            builder.append(delimiter);
                        }
                        builder.delete(builder.length() - delimiter.length()
                            - 1, builder.length());
                        text.setText(builder.toString());
                    }
                }
            };

            // auto-suggest
            Button button = new Button(container, SWT.NONE);
            // TODO: replace with icon?
            button.setText("Suggest");
            button.addListener(SWT.Selection, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    Collection<String> suggestions = autoSuggest();
                    System.out.println(Arrays.toString(suggestions.toArray()));
                }
            });
        }
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

    private Collection<String> autoSuggest() {

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
                if (row.length > 0 && row[0] instanceof String) {
                    suggestions.add((String) row[0]);
                }
            }
        }

        Collections.sort(suggestions);

        return suggestions;
    }
}
