package edu.ualberta.med.biobank.widgets.infotables;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class AliquotListInfoTable extends InfoTableWidget<AliquotWrapper> {

    protected class TableRowData {
        AliquotWrapper aliquot;
        String inventoryId;
        String type;
        String position;
        String linkDate;
        Double quantity;
        String comment;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { inventoryId, type, position,
                linkDate, (quantity != null) ? quantity.toString() : "",
                comment }, "\t");
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
                rc = compare(i1.inventoryId, i2.inventoryId);
                break;
            case 1:
                rc = compare(i1.type, i2.type);
                break;
            case 2:
                rc = compare(i1.position, i2.position);
                break;
            case 3:
                rc = compare(i1.linkDate, i2.linkDate);
                break;
            case 4:
                rc = compare(i1.quantity, i2.quantity);
                break;
            case 5:
                rc = compare(i1.comment, i2.comment);
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

    private static final String[] HEADINGS = new String[] { "Inventory ID",
        "Type", "Position", "Link Date", "Quantity (ml)", "Comment" };

    private static final int[] BOUNDS = new int[] { 130, 130, 150, 150, 150,
        150, -1 };

    public AliquotListInfoTable(Composite parent,
        List<AliquotWrapper> sampleCollection) {
        super(parent, true, sampleCollection, HEADINGS, BOUNDS, 20);
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
                    return info.inventoryId;
                case 1:
                    return info.type;
                case 2:
                    return info.position;
                case 3:
                    return info.linkDate;
                case 4:
                    return (info.quantity != null) ? info.quantity.toString()
                        : "";
                case 5:
                    return info.comment;
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
    public Object getCollectionModelObject(AliquotWrapper sample)
        throws Exception {
        TableRowData info = new TableRowData();
        info.aliquot = sample;
        info.inventoryId = sample.getInventoryId();
        SampleTypeWrapper type = sample.getSampleType();
        Assert.isNotNull(type, "sample with null for sample type");
        info.type = type.getName();
        info.position = sample.getPositionString();
        info.linkDate = DateFormatter.formatAsDateTime(sample.getLinkDate());
        info.quantity = sample.getQuantity();
        info.comment = sample.getComment();
        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((TableRowData) o).toString();
    }

    public void setSelection(AliquotWrapper selectedSample) {
        if (selectedSample == null)
            return;
        for (BiobankCollectionModel item : model) {
            TableRowData info = (TableRowData) item.o;
            if (info.aliquot == selectedSample) {
                getTableViewer().setSelection(new StructuredSelection(item),
                    true);
            }
        }
    }

    @Override
    public List<AliquotWrapper> getCollection() {
        List<AliquotWrapper> result = new ArrayList<AliquotWrapper>();
        for (BiobankCollectionModel item : model) {
            result.add(((TableRowData) item.o).aliquot);
        }
        return result;
    }

    @Override
    public AliquotWrapper getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        TableRowData row = (TableRowData) item.o;
        Assert.isNotNull(row);
        return row.aliquot;
    }
}
