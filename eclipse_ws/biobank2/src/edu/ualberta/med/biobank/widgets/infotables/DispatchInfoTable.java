package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;

public class DispatchInfoTable extends InfoTableWidget<DispatchWrapper> {

    SpecimenWrapper a;

    protected static class TableRowData {

        DispatchWrapper ds;
        String sender;
        Date dispatchTime;
        String receiver;
        Date dateReceived;
        String waybill;
        String dstatus;
        String astatus;

        @Override
        public String toString() {
            return StringUtils.join(
                new String[] { sender,
                    DateFormatter.formatAsDate(dispatchTime), receiver,
                    DateFormatter.formatAsDate(dateReceived), waybill, dstatus,
                    astatus }, "\t"); //$NON-NLS-1$
        }
    }

    private static final String[] HEADINGS = new String[] {
        Messages.DispatchInfoTable_sender_label,
        Messages.DispatchInfoTable_time_label,
        Messages.DispatchInfoTable_receiver_label,
        Messages.DispatchInfoTable_received_label,
        Messages.DispatchInfoTable_waybill_label,
        Messages.DispatchInfoTable_state_label,
        Messages.DispatchInfoTable_spec_state_label };

    private boolean editMode = false;

    public DispatchInfoTable(Composite parent, SpecimenWrapper a) {
        super(parent, null, HEADINGS, 15, DispatchWrapper.class);
        this.a = a;
        setList(a.getDispatches());
    }

    @Override
    protected boolean isEditMode() {
        return editMode;
    }

    @Override
    protected BgcLabelProvider getLabelProvider() {
        return new BgcLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                TableRowData info = (TableRowData) ((BiobankCollectionModel) element).o;
                if (info == null) {
                    if (columnIndex == 0) {
                        return Messages.infotable_loading_msg;
                    }
                    return ""; //$NON-NLS-1$
                }
                switch (columnIndex) {
                case 0:
                    return info.sender;
                case 1:
                    return DateFormatter.formatAsDate(info.dispatchTime);
                case 2:
                    return info.receiver;
                case 3:
                    return DateFormatter.formatAsDate(info.dateReceived);
                case 4:
                    return info.waybill;
                case 5:
                    return info.dstatus;
                case 6:
                    return info.astatus;
                default:
                    return ""; //$NON-NLS-1$
                }
            }
        };
    }

    @Override
    public TableRowData getCollectionModelObject(Object obj) throws Exception {
        TableRowData info = new TableRowData();
        info.ds = (DispatchWrapper) obj;
        info.sender = info.ds.getSenderCenter().getNameShort();
        info.dispatchTime = info.ds.getShipmentInfo() == null ? null : info.ds
            .getShipmentInfo().getPackedAt();
        info.receiver = info.ds.getReceiverCenter().getNameShort();
        info.dateReceived = info.ds.getShipmentInfo() == null ? null : info.ds
            .getShipmentInfo().getReceivedAt();
        info.dstatus = info.ds.getStateDescription();
        info.astatus = info.ds.getDispatchSpecimen(a.getInventoryId())
            .getStateDescription();
        info.waybill = info.ds.getShipmentInfo() == null ? null : info.ds
            .getShipmentInfo().getWaybill();
        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        TableRowData r = (TableRowData) o;
        return r.toString();
    }

    public void setSelection(DispatchWrapper selected) {
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
    public DispatchWrapper getSelection() {
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
        reloadCollection(a.getDispatches());
    }

}
