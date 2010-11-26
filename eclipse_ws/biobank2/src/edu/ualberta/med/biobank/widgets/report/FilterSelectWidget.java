package edu.ualberta.med.biobank.widgets.report;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ReportWrapper;
import edu.ualberta.med.biobank.model.EntityFilter;
import edu.ualberta.med.biobank.model.ReportFilter;

public class FilterSelectWidget extends Composite {
    private final ReportWrapper report;
    private final Map<EntityFilter, FilterRow> filterRowMap = new LinkedHashMap<EntityFilter, FilterRow>();
    private final Collection<ChangeListener<FilterChangeEvent>> listeners = new ArrayList<ChangeListener<FilterChangeEvent>>();
    private Composite container;

    public FilterSelectWidget(Composite parent, int style, ReportWrapper report) {
        super(parent, style);
        this.report = report;

        init();
        createContainer();

        report.addPropertyChangeListener(
            ReportWrapper.PROPERTY_REPORT_COLUMN_COLLECTION,
            new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent arg0) {
                    createContainer();
                }
            });
    }

    public ReportWrapper getReport() {
        return report;
    }

    public Collection<ReportFilter> getReportFilters() {
        Collection<ReportFilter> rfs = new HashSet<ReportFilter>();

        int filterPosition = 0;
        for (Map.Entry<EntityFilter, FilterRow> entry : filterRowMap.entrySet()) {
            EntityFilter entityFilter = entry.getKey();
            FilterRow filterRow = entry.getValue();

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

    public void notifyListeners(FilterChangeEvent event) {
        for (ChangeListener<FilterChangeEvent> listener : listeners) {
            listener.handleEvent(event);
        }
    }

    public FilterRow getFilterRow(EntityFilter entityFilter) {
        return filterRowMap.get(entityFilter);
    }

    public FilterRow addFilterRow(EntityFilter entityFilter) {
        FilterRow filterRow = null;
        if (!filterRowMap.containsKey(entityFilter)) {
            filterRow = new FilterRow(this, container, SWT.NONE, entityFilter);
            filterRowMap.put(entityFilter, filterRow);
        }
        return filterRow;
    }

    void removeFilterRow(EntityFilter entityFilter) {
        FilterRow filterRow = filterRowMap.get(entityFilter);
        if (filterRow != null) {
            filterRowMap.remove(entityFilter);
            filterRow.dispose();
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

        GridData layoutData = new GridData();
        layoutData.grabExcessHorizontalSpace = true;
        container.setLayoutData(layoutData);

        Collection<ReportFilter> reportFilters = report
            .getReportFilterCollection();

        for (ReportFilter reportFilter : reportFilters) {
            addFilterRow(reportFilter);
        }
    }

    private FilterRow addFilterRow(ReportFilter reportFilter) {
        FilterRow filterRow = addFilterRow(reportFilter.getEntityFilter());
        if (filterRow != null) {
            filterRow.setValues(reportFilter.getReportFilterValueCollection());
            filterRow.setOperatorId(reportFilter.getOperator());
        }
        return filterRow;
    }
}
