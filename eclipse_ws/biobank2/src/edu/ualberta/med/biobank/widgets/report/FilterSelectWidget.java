package edu.ualberta.med.biobank.widgets.report;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.reports.filters.FilterOperator;
import edu.ualberta.med.biobank.common.wrappers.ReportWrapper;
import edu.ualberta.med.biobank.model.EntityFilter;
import edu.ualberta.med.biobank.model.ReportFilter;
import edu.ualberta.med.biobank.model.ReportFilterValue;

public class FilterSelectWidget extends Composite {
    private final ReportWrapper report;
    private final Map<Integer, FilterRow> filterRowMap = new LinkedHashMap<Integer, FilterRow>();
    private final Map<Integer, FilterData> previousDataMap = new HashMap<Integer, FilterData>();
    private final Collection<ChangeListener<FilterChangeEvent>> listeners = new ArrayList<ChangeListener<FilterChangeEvent>>();
    private Composite container;

    public FilterSelectWidget(Composite parent, int style, ReportWrapper report) {
        super(parent, style);
        this.report = report;

        init();
        createContainer();

        report.addPropertyChangeListener(
            ReportWrapper.REPORT_COLUMN_COLLECTION_CACHE_KEY,
            new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent event) {
                    if (!isDisposed()) {
                        createContainer();
                    }
                }
            });
    }

    public ReportWrapper getReport() {
        return report;
    }

    public Collection<ReportFilter> getReportFilters() {
        Collection<ReportFilter> rfs = new HashSet<ReportFilter>();

        int filterPosition = 0;
        for (FilterRow filterRow : filterRowMap.values()) {
            EntityFilter entityFilter = filterRow.getEntityFilter();

            ReportFilter reportFilter = new ReportFilter();
            reportFilter.setPosition(filterPosition);
            reportFilter.setEntityFilter(entityFilter);
            reportFilter.setReportFilterValueCollection(filterRow.getValues());

            if (filterRow.getOperator() != null) {
                reportFilter.setOperator(filterRow.getOperator().getId());
            }

            rfs.add(reportFilter);

            filterPosition++;
        }

        return rfs;
    }

    public void addFilterChangedListener(
        ChangeListener<FilterChangeEvent> listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeFilterChangedListener(
        ChangeListener<FilterChangeEvent> listener) {
        listeners.remove(listener);
    }

    public FilterRow getFilterRow(EntityFilter entityFilter) {
        return filterRowMap.get(entityFilter.getId());
    }

    public FilterRow addFilterRow(EntityFilter entityFilter) {
        FilterRow filterRow = null;
        Integer id = entityFilter.getId();
        if (!filterRowMap.containsKey(id)) {
            showContainer(true);
            filterRow = new FilterRow(this, container, SWT.NONE, entityFilter);
            filterRowMap.put(id, filterRow);
            recall(filterRow);
        }
        return filterRow;
    }

    void notifyListeners(FilterChangeEvent event) {
        for (ChangeListener<FilterChangeEvent> listener : listeners) {
            listener.handleEvent(event);
        }
    }

    void removeFilterRow(EntityFilter entityFilter) {
        Integer id = entityFilter.getId();
        FilterRow filterRow = filterRowMap.get(id);
        if (filterRow != null) {
            remember(filterRow);
            filterRowMap.remove(id);
            filterRow.dispose();

            if (filterRowMap.isEmpty()) {
                showContainer(false);
            }
        }
    }

    private void init() {
        GridLayout layout = new GridLayout(1, false);
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.marginBottom = 15;
        setLayout(layout);

        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.minimumHeight = 0;
        setLayoutData(layoutData);
    }

    private void disposeRows() {
        if (container != null && !container.isDisposed()) {
            container.dispose();
        }

        filterRowMap.clear();
    }

    private void createContainer() {
        disposeRows();

        container = new Composite(this, SWT.NONE);

        GridLayout layout = new GridLayout(1, false);
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        container.setLayout(layout);

        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.minimumHeight = 0;
        layoutData.horizontalIndent = 0;
        layoutData.verticalIndent = 0;
        container.setLayoutData(layoutData);

        showContainer(false);

        Collection<ReportFilter> reportFilters = report
            .getReportFilterCollection();

        for (ReportFilter reportFilter : reportFilters) {
            addFilterRow(reportFilter);
        }
    }

    private void showContainer(boolean isShown) {
        if (isShown == container.getVisible()) {
            return;
        }

        GridData layoutData = (GridData) container.getLayoutData();
        layoutData.exclude = !isShown;
        container.setVisible(isShown);
    }

    private FilterRow addFilterRow(ReportFilter reportFilter) {
        FilterRow filterRow = addFilterRow(reportFilter.getEntityFilter());
        if (filterRow != null) {
            // it's important to set the operator before setting the values so
            // that if the values do not apply to the default operator they will
            // not be lost
            if (reportFilter.getOperator() != null) {
                filterRow.setOperatorId(reportFilter.getOperator());
            }
            filterRow.setValues(reportFilter.getReportFilterValueCollection());
        }
        return filterRow;
    }

    private static class FilterData {
        public Collection<ReportFilterValue> values;
        public FilterOperator op;
    }

    private void remember(FilterRow filterRow) {
        FilterData data = new FilterData();
        data.op = filterRow.getOperator();
        data.values = filterRow.getValues();
        previousDataMap.put(filterRow.getEntityFilter().getId(), data);
    }

    private void recall(FilterRow filterRow) {
        Integer id = filterRow.getEntityFilter().getId();
        FilterData data = previousDataMap.get(id);
        if (data != null) {
            filterRow.setOperator(data.op);
            filterRow.setValues(data.values);
        }
    }
}
