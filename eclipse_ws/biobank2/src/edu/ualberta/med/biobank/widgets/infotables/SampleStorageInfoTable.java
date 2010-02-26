package edu.ualberta.med.biobank.widgets.infotables;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.SampleStorageWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class SampleStorageInfoTable extends
    InfoTableWidget<SampleStorageWrapper> {

    protected class TableRowData {
        SampleStorageWrapper sampleStorage;
        String typeName;
        Double volume;
        Integer quantity;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { typeName,
                (volume != null) ? volume.toString() : "",
                (quantity != null) ? quantity.toString() : "" }, "\t");
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
                rc = compare(i1.typeName, i2.typeName);
                break;
            case 1:
                rc = compare(i1.volume, i2.volume);
                break;
            case 2:
                rc = compare(i1.quantity, i2.quantity);
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

    private static final String[] HEADINGS = new String[] { "Sample type",
        "Volume (ml)", "Quantity" };

    private static final int[] BOUNDS = new int[] { 300, 130, 100, -1, -1, -1,
        -1 };

    public SampleStorageInfoTable(Composite parent,
        List<SampleStorageWrapper> sampleStorageCollection) {
        super(parent, sampleStorageCollection, HEADINGS, BOUNDS);
        setSorter(new TableSorter());
    }

    @Override
    public Object getCollectionModelObject(SampleStorageWrapper sampleStorage)
        throws Exception {
        TableRowData info = new TableRowData();
        info.sampleStorage = sampleStorage;
        SampleTypeWrapper type = sampleStorage.getSampleType();
        Assert.isNotNull(type, "sample storage - sample type is null");
        info.typeName = type.getName();
        info.volume = sampleStorage.getVolume();
        info.quantity = sampleStorage.getQuantity();
        return info;
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
                    return item.typeName;
                case 1:
                    return (item.volume != null) ? item.volume.toString() : "";
                case 2:
                    return (item.quantity != null) ? item.quantity.toString()
                        : "";
                default:
                    return "";
                }
            }
        };
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((TableRowData) o).toString();
    }

    @Override
    public List<SampleStorageWrapper> getCollection() {
        List<SampleStorageWrapper> result = new ArrayList<SampleStorageWrapper>();
        for (BiobankCollectionModel item : model) {
            result.add(((TableRowData) item.o).sampleStorage);
        }
        return result;
    }

    @Override
    public SampleStorageWrapper getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        TableRowData row = (TableRowData) item.o;
        Assert.isNotNull(row);
        return row.sampleStorage;
    }
}
