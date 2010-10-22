package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class DispatchInfoTable extends InfoTableWidget<DispatchShipmentWrapper> {

    AliquotWrapper a;

    protected class TableRowData {

        DispatchShipmentWrapper ds;
        Date dispatchTime;
        Date dateReceived;
        String waybill;
        String dstatus;
        String astatus;

        @Override
        public String toString() {
            return StringUtils.join(
                new String[] { DateFormatter.formatAsDate(dispatchTime),
                    DateFormatter.formatAsDate(dateReceived), waybill, dstatus,
                    astatus }, "\t");
        }
    }

    private static final String[] HEADINGS = new String[] { "Dispatch Time",
        "Date Received", "Waybill", "Dispatch State", "Aliquot State" };

    private static final int[] BOUNDS = new int[] { 100, 100, 100, 100, 100 };

    private boolean editMode = false;

    public DispatchInfoTable(Composite parent, AliquotWrapper a) {
        super(parent, null, HEADINGS, BOUNDS, 15);
        this.a = a;
        setCollection(a.getDispatchShipments());
    }

    @Override
    protected boolean isEditMode() {
        return editMode;
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
                    return DateFormatter.formatAsDate(info.dispatchTime);
                case 1:
                    return DateFormatter.formatAsDate(info.dateReceived);
                case 2:
                    return info.waybill;
                case 3:
                    return info.dstatus;
                case 4:
                    return info.astatus;
                default:
                    return "";
                }
            }
        };
    }

    @Override
    public TableRowData getCollectionModelObject(DispatchShipmentWrapper ds)
        throws Exception {
        TableRowData info = new TableRowData();
        info.ds = ds;
        info.dispatchTime = ds.getDateShipped();
        info.dateReceived = ds.getDateReceived();
        info.dstatus = ds.getStateDescription();
        info.astatus = ds.getDispatchShipmentAliquot(a.getInventoryId())
            .getStateDescription();
        info.waybill = ds.getWaybill();
        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        TableRowData r = (TableRowData) o;
        return r.toString();
    }

    public void setSelection(DispatchShipmentWrapper selected) {
        if (selected == null)
            return;
        for (BiobankCollectionModel item : model) {
            TableRowData info = (TableRowData) item.o;
            if (info.ds.equals(selected)) {
                getTableViewer().setSelection(new StructuredSelection(item),
                    true);
            }
        }
    }

    @Override
    public DispatchShipmentWrapper getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        TableRowData row = (TableRowData) item.o;
        Assert.isNotNull(row);
        return row.ds;
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }

    public void reloadCollection() {
        reloadCollection(a.getDispatchShipments());
    }

}
