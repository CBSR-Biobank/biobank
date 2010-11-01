package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.PvSourceVesselWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class PvSourceVesselInfoTable extends
    InfoTableWidget<PvSourceVesselWrapper> {

    private static final int PAGE_SIZE_ROWS = 5;

    protected class TableRowData {
        PvSourceVesselWrapper pvSourceVessel;
        public String name;
        public Integer quantity;
        public String timeDrawn;
        public String volume;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { name,
                (quantity != null) ? quantity.toString() : "", timeDrawn,
                volume }, "\t");
        }
    }

    private final static String[] HEADINGS = new String[] { "Name", "Quantity",
        "Time Drawn", "Volume (ml)" };

    public PvSourceVesselInfoTable(Composite parent,
        List<PvSourceVesselWrapper> collection) {
        super(parent, collection, HEADINGS, PAGE_SIZE_ROWS);
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
                    return info.timeDrawn;
                case 3:
                    return info.volume;
                default:
                    return "";
                }
            }
        };
    }

    @Override
    public TableRowData getCollectionModelObject(
        PvSourceVesselWrapper pvSourceVessel) throws Exception {
        TableRowData info = new TableRowData();
        info.pvSourceVessel = pvSourceVessel;
        Assert.isNotNull(pvSourceVessel.getSourceVessel(),
            "patient visit source vessel has null for source vessel");
        info.name = pvSourceVessel.getSourceVessel().getName();
        info.quantity = pvSourceVessel.getQuantity();
        info.timeDrawn = pvSourceVessel.getFormattedTimeDrawn();
        info.volume = pvSourceVessel.getVolume();
        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((TableRowData) o).toString();
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

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }

}
