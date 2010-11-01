package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleStorageWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class SampleStorageInfoTable extends
    InfoTableWidget<SampleStorageWrapper> {

    private static final int PAGE_SIZE_ROWS = 5;

    protected class TableRowData {
        public SampleStorageWrapper sampleStorage;
        public String typeName;
        public Double volume;
        public Integer quantity;
        public String status;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { typeName,
                (volume != null) ? volume.toString() : "",
                (quantity != null) ? quantity.toString() : "", status }, "\t");
        }
    }

    private static final String[] HEADINGS = new String[] { "Sample type",
        "Volume (ml)", "Quantity", "Activity status" };

    public SampleStorageInfoTable(Composite parent,
        List<SampleStorageWrapper> sampleStorageCollection) {
        super(parent, sampleStorageCollection, HEADINGS, PAGE_SIZE_ROWS);
    }

    @Override
    public TableRowData getCollectionModelObject(
        SampleStorageWrapper sampleStorage) throws Exception {
        TableRowData info = new TableRowData();
        info.sampleStorage = sampleStorage;
        SampleTypeWrapper type = sampleStorage.getSampleType();
        Assert.isNotNull(type, "sample storage - sample type is null");
        info.typeName = type.getName();
        info.volume = sampleStorage.getVolume();
        info.quantity = sampleStorage.getQuantity();
        ActivityStatusWrapper status = sampleStorage.getActivityStatus();
        Assert.isNotNull(status, "sample storage - activity status is null");
        info.status = status.getName();
        return info;
    }

    @Override
    protected BiobankLabelProvider getLabelProvider() {
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
                case 3:
                    return item.status;
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
    public SampleStorageWrapper getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        TableRowData row = (TableRowData) item.o;
        Assert.isNotNull(row);
        return row.sampleStorage;
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }
}
