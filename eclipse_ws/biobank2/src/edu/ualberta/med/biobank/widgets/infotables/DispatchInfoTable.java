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
                    astatus }, "\t"); //$NON-NLS-1$
        }
    }

    private static final String[] HEADINGS = new String[] {
        Messages.DispatchInfoTable_time_label,
        Messages.DispatchInfoTable_received_label,
        Messages.DispatchInfoTable_waybill_label,
        Messages.DispatchInfoTable_state_label,
        Messages.DispatchInfoTable_spec_state_label };

    private boolean editMode = false;

    public DispatchInfoTable(Composite parent, SpecimenWrapper a) {
        super(parent, null, HEADINGS, 15, DispatchWrapper.class);
        this.a = a;
        setCollection(a.getDispatches());
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
                        return Messages.DispatchInfoTable_loading;
                    }
                    return ""; //$NON-NLS-1$
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
                    return ""; //$NON-NLS-1$
                }
            }
        };
    }

    @Override
    public TableRowData getCollectionModelObject(DispatchWrapper ds)
        throws Exception {
        TableRowData info = new TableRowData();
        info.ds = ds;
        info.dispatchTime = ds.getShipmentInfo() == null ? null : ds
            .getShipmentInfo().getPackedAt();
        info.dateReceived = ds.getShipmentInfo() == null ? null : ds
            .getShipmentInfo().getReceivedAt();
        info.dstatus = ds.getStateDescription();
        info.astatus = ds.getDispatchSpecimen(a.getInventoryId())
            .getStateDescription();
        info.waybill = ds.getShipmentInfo() == null ? null : ds
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
