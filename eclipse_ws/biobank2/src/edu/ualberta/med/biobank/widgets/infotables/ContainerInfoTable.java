package edu.ualberta.med.biobank.widgets.infotables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
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

        TableRowData(ContainerWrapper container) {
            this.container = container;
            ContainerTypeWrapper type = container.getContainerType();
            this.label = container.getLabel();
            if (type != null) {
                this.typeNameShort = type.getNameShort();
            }
            this.status = container.getActivityStatus();
            this.barcode = container.getProductBarcode();
            this.temperature = container.getTemperature();
        }

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
                if (container == null)
                    return null;
                switch (columnIndex) {
                case 0:
                    return container.label;
                case 1:
                    return container.typeNameShort;
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
        return new TableRowData(container);
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
        return ((TableRowData) getSelectionInternal().o).container;
    }

}
