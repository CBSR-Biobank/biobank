package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class ContainerTypeInfoTable extends
    InfoTableWidget<ContainerTypeWrapper> {

    class TableRowData {
        String name;
        String nameShort;
        Integer capacity;
        String status;
        Long inUseCount;
        Double temperature;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { name, nameShort,
                (capacity != null) ? capacity.toString() : "",
                (status != null) ? status : "",
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
                rc = c1.capacity.compareTo(c2.capacity);
                break;
            case 3:
                rc = c1.status.compareTo(c2.status);
                break;
            case 4:
                rc = c1.inUseCount.compareTo(c2.inUseCount);
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
        addClipboadCopySupport();
    }

    @Override
    public BiobankLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                TableRowData ct = (TableRowData) ((BiobankCollectionModel) element).o;
                if (ct == null)
                    return "";
                switch (columnIndex) {
                case 0:
                    return ct.name;
                case 1:
                    return ct.nameShort;
                case 2:
                    return (ct.capacity != null) ? ct.capacity.toString() : "";
                case 3:
                    return (ct.status != null) ? ct.status : "";
                case 4:
                    return (ct.inUseCount != null) ? ct.inUseCount.toString()
                        : "";
                case 5:
                    return (ct.temperature != null) ? ct.temperature.toString()
                        : "";
                default:
                    return "";
                }
            }
        };
    }

    @Override
    public Object getCollectionModelObject(ContainerTypeWrapper type)
        throws Exception {
        TableRowData info = new TableRowData();
        info.name = type.getName();
        info.nameShort = type.getNameShort();
        info.capacity = type.getColCapacity() * type.getRowCapacity();
        info.status = type.getActivityStatus();
        info.inUseCount = type.getContainersCount();
        info.temperature = type.getDefaultTemperature();
        return info;
    }
}
