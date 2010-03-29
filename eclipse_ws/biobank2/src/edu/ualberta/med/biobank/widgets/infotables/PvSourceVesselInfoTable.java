package edu.ualberta.med.biobank.widgets.infotables;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.PvSourceVesselWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class PvSourceVesselInfoTable extends
    InfoTableWidget<PvSourceVesselWrapper> {

    private static final int PAGE_SIZE_ROWS = 5;

    protected class TableRowData {
        PvSourceVesselWrapper pvSourceVessel;
        String name;
        Integer quantity;
        String dateDrawn;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { name,
                (quantity != null) ? quantity.toString() : "", dateDrawn },
                "\t");
        }
    }

    private class TableSorter extends BiobankTableSorter {
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
                rc = compare(i1.name, i2.name);
                break;
            case 1:
                rc = compare(i1.quantity, i2.quantity);
                break;
            case 2:
                rc = compare(i1.dateDrawn, i2.dateDrawn);
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

    private final static String[] HEADINGS = new String[] { "Name", "Quantity",
        "Date Drawn" };

    private final static int[] BOUNDS = new int[] { 250, 100, -1, -1, -1 };

    public PvSourceVesselInfoTable(Composite parent,
        List<PvSourceVesselWrapper> collection) {
        super(parent, collection, HEADINGS, BOUNDS, PAGE_SIZE_ROWS);
    }

    @Override
    protected BiobankLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                TableRowData info = (TableRowData) ((BiobankCollectionModel) element).o;
                if (info == null) {
                    if (columnIndex == 0) {
                        return "loading...";
                    }
                    return "";
                }
                switch (columnIndex) {
                case 0:
                    return info.name;
                case 1:
                    return (info.quantity != null) ? info.quantity.toString()
                        : "";
                case 2:
                    return info.dateDrawn;
                default:
                    return "";
                }
            }
        };
    }

    @Override
    protected BiobankTableSorter getTableSorter() {
        // return new TableSorter();
        return null;
    }

    @Override
    public Object getCollectionModelObject(PvSourceVesselWrapper pvSourceVessel)
        throws Exception {
        TableRowData info = new TableRowData();
        info.pvSourceVessel = pvSourceVessel;
        Assert.isNotNull(pvSourceVessel.getSourceVessel(),
            "patient visit source vessel has null for source vessel");
        info.name = pvSourceVessel.getSourceVessel().getName();
        info.quantity = pvSourceVessel.getQuantity();
        info.dateDrawn = pvSourceVessel.getFormattedDateDrawn();
        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((TableRowData) o).toString();
    }

    @Override
    public List<PvSourceVesselWrapper> getCollection() {
        List<PvSourceVesselWrapper> result = new ArrayList<PvSourceVesselWrapper>();
        for (BiobankCollectionModel item : model) {
            result.add(((TableRowData) item.o).pvSourceVessel);
        }
        return result;
    }

    @Override
    public PvSourceVesselWrapper getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        TableRowData row = (TableRowData) item.o;
        Assert.isNotNull(row);
        return row.pvSourceVessel;
    }

}
