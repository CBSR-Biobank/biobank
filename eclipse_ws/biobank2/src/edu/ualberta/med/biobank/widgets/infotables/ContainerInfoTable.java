package edu.ualberta.med.biobank.widgets.infotables;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class ContainerInfoTable extends InfoTableWidget<ContainerWrapper> {

    class TableRowData {
        ContainerWrapper container;
        String label;
        String typeNameShort;
        String status;
        String barcode;
        Double temperature;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { label, typeNameShort,
                status, barcode,
                (temperature != null) ? temperature.toString() : "" }, "\t");
        }
    }

    class TableSorter extends BiobankTableSorter {
        @Override
        public int compare(Viewer viewer, Object e1, Object e2) {
            TableRowData i1 = (TableRowData) ((BiobankCollectionModel) e1).o;
            TableRowData i2 = (TableRowData) ((BiobankCollectionModel) e2).o;
            if (i1 == null) {
                return -1;
            } else if (i2 == null) {
                return 1;
            }

            int rc = 0;
            switch (propertyIndex) {
            case 0:
                rc = compare(i1.label, i2.label);
                break;
            case 1:
                rc = compare(i1.typeNameShort, i2.typeNameShort);
                break;
            case 2:
                rc = compare(i1.status, i2.status);
                break;
            case 3:
                rc = compare(i1.barcode, i2.barcode);
                break;
            case 4:
                rc = compare(i1.temperature, i2.temperature);
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
        List<ContainerWrapper> collection) {
        super(parent, true, collection, HEADINGS, BOUNDS, true);
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
                    return item.label;
                case 1:
                    return item.typeNameShort;
                case 2:
                    return item.status;
                case 3:
                    return item.barcode;
                case 4:
                    if (item.temperature == null) {
                        return "";
                    }
                    return item.temperature.toString();
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

        info.container = container;
        info.label = container.getLabel();
        ContainerTypeWrapper type = container.getContainerType();
        if (type != null) {
            info.typeNameShort = type.getNameShort();
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

    @Override
    public List<ContainerWrapper> getCollection() {
        List<ContainerWrapper> result = new ArrayList<ContainerWrapper>();
        for (BiobankCollectionModel item : model) {
            result.add(((TableRowData) item.o).container);
        }
        return result;
    }

    @Override
    public ContainerWrapper getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        TableRowData row = (TableRowData) item.o;
        Assert.isNotNull(row);
        return row.container;
    }

}
