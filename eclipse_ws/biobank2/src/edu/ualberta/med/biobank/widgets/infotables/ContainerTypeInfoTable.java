package edu.ualberta.med.biobank.widgets.infotables;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MenuItem;

import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.admin.SiteAdapter;
import edu.ualberta.med.biobank.treeview.util.AdapterFactory;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class ContainerTypeInfoTable extends
    InfoTableWidget<ContainerTypeWrapper> {

    private static class TableRowData {
        ContainerTypeWrapper containerType;
        String name;
        String nameShort;
        Integer capacity;
        String status;
        Long inUseCount;
        Double temperature;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { name, nameShort,
                (capacity != null) ? capacity.toString() : "", status, //$NON-NLS-1$
                (inUseCount != null) ? inUseCount.toString() : "", //$NON-NLS-1$
                (temperature != null) ? temperature.toString() : "" }, "\t"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    private static final String[] HEADINGS = new String[] { Messages.ContainerTypeInfoTable_name_label,
        Messages.ContainerTypeInfoTable_nameshort_label, Messages.ContainerTypeInfoTable_capacity_label, Messages.ContainerTypeInfoTable_status_label, Messages.ContainerTypeInfoTable_use_label, Messages.ContainerTypeInfoTable_temperature_label };

    private SiteAdapter siteAdapter;

    public ContainerTypeInfoTable(Composite parent, SiteAdapter site) {
        super(parent, site.getWrapper().getContainerTypeCollection(), HEADINGS,
            10);
        siteAdapter = site;
    }

    @Override
    protected BiobankLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                TableRowData item = (TableRowData) ((BiobankCollectionModel) element).o;
                if (item == null) {
                    if (columnIndex == 0) {
                        return Messages.ContainerTypeInfoTable_loading;
                    }
                    return ""; //$NON-NLS-1$
                }
                switch (columnIndex) {
                case 0:
                    return item.name;
                case 1:
                    return item.nameShort;
                case 2:
                    return (item.capacity != null) ? item.capacity.toString()
                        : null;
                case 3:
                    return item.status;
                case 4:
                    return (item.inUseCount != null) ? item.inUseCount
                        .toString() : null;
                case 5:
                    return (item.temperature != null) ? item.temperature
                        .toString() : null;
                default:
                    return ""; //$NON-NLS-1$
                }
            }
        };
    }

    @Override
    public Object getCollectionModelObject(ContainerTypeWrapper type)
        throws Exception {
        TableRowData info = new TableRowData();
        Integer rowCapacity = type.getRowCapacity();
        Integer colCapacity = type.getColCapacity();

        info.containerType = type;
        info.name = type.getName();
        info.nameShort = type.getNameShort();
        info.status = type.getActivityStatus().getName();
        if ((rowCapacity != null) && (colCapacity != null)) {
            info.capacity = rowCapacity * colCapacity;
        }
        info.inUseCount = type.getContainersCount();
        info.temperature = type.getDefaultTemperature();
        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((TableRowData) o).toString();
    }

    @Override
    public ContainerTypeWrapper getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        TableRowData row = (TableRowData) item.o;
        Assert.isNotNull(row);
        return row.containerType;
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }

    @Override
    public void addClickListener(IDoubleClickListener listener) {
        doubleClickListeners.add(listener);
        MenuItem mi = new MenuItem(getMenu(), SWT.PUSH);
        mi.setText(Messages.ContainerTypeInfoTable_edit_label);
        mi.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                ModelWrapper<?> selection = ContainerTypeInfoTable.this
                    .getSelection();
                if (selection != null) {
                    AdapterBase adapter = AdapterFactory.getAdapter(selection);
                    adapter.setParent(siteAdapter.getContainerTypesGroupNode());
                    adapter.openEntryForm();
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
    }
}
