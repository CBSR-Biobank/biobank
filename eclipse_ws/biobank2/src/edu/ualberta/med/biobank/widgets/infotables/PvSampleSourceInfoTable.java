package edu.ualberta.med.biobank.widgets.infotables;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.PvSampleSourceWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class PvSampleSourceInfoTable extends
    InfoTableWidget<PvSampleSourceWrapper> {

    protected class TableRowData {
        PvSampleSourceWrapper pvSampleSource;
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

    public PvSampleSourceInfoTable(Composite parent, boolean multiSelectRows,
        List<PvSampleSourceWrapper> collection) {
        super(parent, multiSelectRows, collection, HEADINGS, BOUNDS, 10);
    }

    public PvSampleSourceInfoTable(Composite parent,
        List<PvSampleSourceWrapper> collection) {
        this(parent, true, collection);
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
        return new TableSorter();
    }

    @Override
    public Object getCollectionModelObject(PvSampleSourceWrapper pvSampleSource)
        throws Exception {
        TableRowData info = new TableRowData();
        info.pvSampleSource = pvSampleSource;
        Assert.isNotNull(pvSampleSource.getSampleSource(),
            "patient visit sample source has null for sample source");
        info.name = pvSampleSource.getSampleSource().getName();
        info.quantity = pvSampleSource.getQuantity();
        info.dateDrawn = pvSampleSource.getFormattedDateDrawn();
        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((TableRowData) o).toString();
    }

    @Override
    public List<PvSampleSourceWrapper> getCollection() {
        List<PvSampleSourceWrapper> result = new ArrayList<PvSampleSourceWrapper>();
        for (BiobankCollectionModel item : model) {
            result.add(((TableRowData) item.o).pvSampleSource);
        }
        return result;
    }

    @Override
    public PvSampleSourceWrapper getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        TableRowData row = (TableRowData) item.o;
        Assert.isNotNull(row);
        return row.pvSampleSource;
    }

}
