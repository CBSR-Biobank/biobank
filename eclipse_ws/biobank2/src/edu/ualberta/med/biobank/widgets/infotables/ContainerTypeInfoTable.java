package edu.ualberta.med.biobank.widgets.infotables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class ContainerTypeInfoTable extends
    InfoTableWidget<ContainerTypeWrapper> {

    class TableRowData {
        ContainerTypeWrapper containerType;
        String name;
        String nameShort;
        Integer capacity;
        String status;
        Long inUseCount;
        Double temperature;

        TableRowData(String name, String nameShort, Integer capacity,
            String status, Long inUseCount, Double temperature) {
            this.name = (name != null) ? name : "";
            this.nameShort = (nameShort != null) ? nameShort : "";
            this.status = (status == null) ? status : "";
            this.capacity = capacity;
            this.inUseCount = inUseCount;
            this.temperature = temperature;
        }

        @Override
        public String toString() {
            return StringUtils.join(new String[] { name, nameShort,
                (capacity != null) ? capacity.toString() : "", status,
                (inUseCount != null) ? inUseCount.toString() : "",
                (temperature != null) ? temperature.toString() : "" }, "\t");
        }
    }

    class TableSorter extends BiobankTableSorter {
        @Override
        public int compare(Viewer viewer, Object e1, Object e2) {
            TableRowData c1 = (TableRowData) ((BiobankCollectionModel) e1).o;
            TableRowData c2 = (TableRowData) ((BiobankCollectionModel) e2).o;
            if ((c1 == null) || (c2 == null)) {
                return -1;
            }
            int rc = 0;
            switch (propertyIndex) {
            case 0:
                rc = c1.name.compareTo(c2.name);
                break;
            case 1:
                rc = c1.nameShort.compareTo(c2.nameShort);
                break;
            case 2:
                if (c1.capacity == null) {
                    rc = -1;
                } else if (c2.capacity == null) {
                    rc = 1;
                } else {
                    rc = c1.capacity.compareTo(c2.capacity);
                }
                break;
            case 3:
                rc = c1.status.compareTo(c2.status);
                break;
            case 4:
                if (c1.inUseCount == null) {
                    rc = -1;
                } else if (c2.inUseCount == null) {
                    rc = 1;
                } else {
                    rc = c1.inUseCount.compareTo(c2.inUseCount);
                }
                break;
            case 5:
                if (c1.temperature == null) {
                    rc = -1;
                } else if (c2.temperature == null) {
                    rc = 1;
                } else {
                    rc = c1.temperature.compareTo(c2.temperature);
                }
                break;
            default:
                rc = 0;
            }
            // If descending order, flip the direction
            if (direction == 1) {
                rc = -rc;
            }
            return rc;
        }
    }

    private static final String[] HEADINGS = new String[] { "Name",
        "Short Name", "Capacity", "Status", "In Use", "Temperature" };

    private static final int[] BOUNDS = new int[] { 160, 130, 130, 130, 130,
        130 };

    public ContainerTypeInfoTable(Composite parent,
        Collection<ContainerTypeWrapper> collection) {
        super(parent, true, collection, HEADINGS, BOUNDS);
        setSorter(new TableSorter());
    }

    @Override
    public BiobankLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                TableRowData item = (TableRowData) ((BiobankCollectionModel) element).o;
                if (item == null) {
                    if (columnIndex == 0) {
                        return "loading...";
                    }
                    return "";
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
                    return "";
                }
            }
        };
    }

    @Override
    public Object getCollectionModelObject(ContainerTypeWrapper type)
        throws Exception {
        Integer rowCapacity = type.getRowCapacity();
        Integer colCapacity = type.getColCapacity();

        if ((rowCapacity != null) && (colCapacity != null)) {
            return new TableRowData(type.getName(), type.getNameShort(),
                rowCapacity * colCapacity, type.getActivityStatus(), type
                    .getContainersCount(), type.getDefaultTemperature());
        }
        return new TableRowData(type.getName(), type.getNameShort(), null, type
            .getActivityStatus(), type.getContainersCount(), type
            .getDefaultTemperature());
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((TableRowData) o).toString();
    }

    @Override
    public List<ContainerTypeWrapper> getCollection() {
        List<ContainerTypeWrapper> result = new ArrayList<ContainerTypeWrapper>();
        for (BiobankCollectionModel item : model) {
            result.add(((TableRowData) item.o).containerType);
        }
        return result;
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
}
