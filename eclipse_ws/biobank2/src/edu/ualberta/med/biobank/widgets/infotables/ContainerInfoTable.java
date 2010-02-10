package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class ContainerInfoTable extends InfoTableWidget<ContainerWrapper> {

    class TableRowData {
        String label;
        String containerTypeNameShort;
        String status;
        String barcode;
        Double temperature;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { label,
                containerTypeNameShort, status, barcode,
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
                rc = c1.label.compareTo(c2.label);
                break;
            case 1:
                rc = c1.containerTypeNameShort
                    .compareTo(c2.containerTypeNameShort);
                break;
            case 2:
                rc = c1.status.compareTo(c2.status);
                break;
            case 3:
                rc = c1.barcode.compareTo(c2.barcode);
                break;
            case 4:
                rc = c1.temperature.compareTo(c2.temperature);
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
        "Container Type", "Status", "Product Barcode", "Temperature" };

    private static final int[] BOUNDS = new int[] { 160, 130, 130, 130, 130 };

    public ContainerInfoTable(Composite parent,
        Collection<ContainerWrapper> collection) {
        super(parent, true, collection, HEADINGS, BOUNDS);
        setSorter(new TableSorter());
        addClipboadCopySupport();
    }

    @Override
    public BiobankLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                TableRowData container = (TableRowData) ((BiobankCollectionModel) element).o;
                switch (columnIndex) {
                case 0:
                    return container.label;
                case 1:
                    return container.containerTypeNameShort;
                case 2:
                    return container.status;
                case 3:
                    return container.barcode;
                case 4:
                    if (container.temperature == null) {
                        return "";
                    }
                    return container.temperature.toString();
                default:
                    return "";
                }
            }
        };
    }

    @Override
    public Object getCollectionModelObject(ContainerWrapper container)
        throws Exception {
        TableRowData info = new TableRowData();
        info.label = container.getLabel();
        ContainerTypeWrapper type = container.getContainerType();
        if (type != null) {
            info.containerTypeNameShort = type.getNameShort();
        } else {
            info.containerTypeNameShort = new String();
        }
        info.status = container.getActivityStatus();
        info.barcode = container.getProductBarcode();
        info.temperature = container.getTemperature();
        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((TableRowData) o).toString();
    }

}
