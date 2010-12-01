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
    private final Map<Integer, FilterRow> filterRowMap = new LinkedHashMap<Integer, FilterRow>();
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

    public void notifyListeners(FilterChangeEvent event) {
        for (ChangeListener<FilterChangeEvent> listener : listeners) {
            listener.handleEvent(event);
        }
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
        }
        return filterRow;
    }

    void removeFilterRow(EntityFilter entityFilter) {
        Integer id = entityFilter.getId();
        FilterRow filterRow = filterRowMap.get(id);
        if (filterRow != null) {
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
            filterRow.setValues(reportFilter.getReportFilterValueCollection());
            filterRow.setOperatorId(reportFilter.getOperator());
        }
        return filterRow;
    }
}
