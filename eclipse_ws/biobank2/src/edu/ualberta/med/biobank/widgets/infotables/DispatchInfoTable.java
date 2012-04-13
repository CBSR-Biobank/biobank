package edu.ualberta.med.biobank.widgets.infotables;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.permission.dispatch.DispatchDeletePermission;
import edu.ualberta.med.biobank.common.permission.dispatch.DispatchUpdatePermission;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import edu.ualberta.med.biobank.model.Dispatch;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class DispatchInfoTable extends InfoTableWidget<DispatchWrapper> {

    private List<DispatchWrapper> dispatches;

    protected static class TableRowData {
        DispatchWrapper dispatch;
        String sender;
        Date dispatchTime;
        String receiver;
        Date dateReceived;
        String waybill;
        String dstatus;

        @Override
        public String toString() {
            return StringUtils
                .join(
                    new String[] { sender,
                        DateFormatter.formatAsDate(dispatchTime), receiver,
                        DateFormatter.formatAsDate(dateReceived), waybill,
                        dstatus }, "\t"); //$NON-NLS-1$
        }
    }

    private static final String[] HEADINGS = new String[] {
        Messages.DispatchInfoTable_sender_label,
        Messages.DispatchInfoTable_time_label,
        Messages.DispatchInfoTable_receiver_label,
        Messages.DispatchInfoTable_received_label,
        Messages.DispatchInfoTable_waybill_label,
        Messages.DispatchInfoTable_state_label };

    private boolean editMode = false;

    public DispatchInfoTable(Composite parent,
        List<Dispatch> dispatchesRaw) {
        super(parent, null, HEADINGS, 15, DispatchWrapper.class);
        this.dispatches = new ArrayList<DispatchWrapper>();
        for (Dispatch dispatch : dispatchesRaw) {
            dispatches.add(new DispatchWrapper(SessionManager.getAppService(),
                dispatch));
        }
        setList(dispatches);
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
                TableRowData info =
                    (TableRowData) ((BiobankCollectionModel) element).o;
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
                default:
                    return ""; //$NON-NLS-1$
                }
            }
        };
    }

    @Override
    public TableRowData getCollectionModelObject(Object obj) throws Exception {
        TableRowData info = new TableRowData();
        info.dispatch = (DispatchWrapper) obj;
        info.sender = info.dispatch.getSenderCenter().getNameShort();
        info.dispatchTime =
            info.dispatch.getShipmentInfo() == null ? null : info.dispatch
                .getShipmentInfo().getPackedAt();
        info.receiver = info.dispatch.getReceiverCenter().getNameShort();
        info.dateReceived =
            info.dispatch.getShipmentInfo() == null ? null : info.dispatch
                .getShipmentInfo().getReceivedAt();
        info.dstatus = info.dispatch.getStateDescription();
        info.waybill =
            info.dispatch.getShipmentInfo() == null ? null : info.dispatch
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
            if (info.dispatch.equals(selected)) {
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
        return row.dispatch;
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }

    public void reloadCollection() {
        reloadCollection(dispatches);
    }

    @Override
    protected Boolean canEdit(DispatchWrapper target)
        throws ApplicationException {
        return SessionManager.getAppService().isAllowed(
            new DispatchUpdatePermission(target.getId()));
    }

    @Override
    protected Boolean canDelete(DispatchWrapper target)
        throws ApplicationException {
        return SessionManager.getAppService().isAllowed(
            new DispatchDeletePermission(target.getId()));
    }

}
