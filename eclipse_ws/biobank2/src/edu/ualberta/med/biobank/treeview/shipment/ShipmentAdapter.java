package edu.ualberta.med.biobank.treeview.shipment;

import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentInfoWrapper;
import edu.ualberta.med.biobank.forms.ShipmentEntryForm;
import edu.ualberta.med.biobank.forms.ShipmentViewForm;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class ShipmentAdapter extends AdapterBase {

    public ShipmentAdapter(AdapterBase parent, OriginInfoWrapper originInfo) {
        super(parent, originInfo);

        if (originInfo.getShipmentInfo() == null) {
            throw new NullPointerException(
                Messages.ShipmentAdapter_noShipment_error_msg);
        }

        setHasChildren(false);
    }

    public OriginInfoWrapper getWrapper() {
        return (OriginInfoWrapper) modelObject;
    }

    @Override
    protected String getLabelInternal() {
        OriginInfoWrapper originInfo = getWrapper();
        ShipmentInfoWrapper shipmentInfo = originInfo.getShipmentInfo();

        String label = ""; //$NON-NLS-1$
        if (shipmentInfo.getReceivedAt() != null)
            label += shipmentInfo.getFormattedDateReceived();
        if (shipmentInfo.getWaybill() != null) {
            label += " (" + shipmentInfo.getWaybill() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
        }

        return label;
    }

    @Override
    public String getTooltipText() {
        OriginInfoWrapper originInfo = getWrapper();
        if (originInfo != null) {
            CenterWrapper<?> center = originInfo.getCenter();
            if (center != null)
                return center.getName()
                    + " - " + getTooltipText(Messages.ShipmentAdapter_tooltip_no_origin); //$NON-NLS-1$ 
        }
        return getTooltipText(Messages.ShipmentAdapter_tooltip_no_origin);
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addEditMenu(menu, Messages.ShipmentAdapter_shipment_label);
        addViewMenu(menu, Messages.ShipmentAdapter_shipment_label);
        addDeleteMenu(menu, Messages.ShipmentAdapter_shipment_label);
    }

    @Override
    protected String getConfirmDeleteMessage() {
        return Messages.ShipmentAdapter_delete_confirm;
    }

    @Override
    public List<AdapterBase> search(Object searchedObject) {
        return findChildFromClass(searchedObject, ProcessingEventWrapper.class);
    }

    @Override
    protected AdapterBase createChildNode() {
        return null;
    }

    @Override
    protected AdapterBase createChildNode(ModelWrapper<?> child) {
        return null;
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        return null;
    }

    @Override
    protected int getWrapperChildCount() throws Exception {
        return 0;
    }

    @Override
    public String getEntryFormId() {
        return ShipmentEntryForm.ID;
    }

    @Override
    public String getViewFormId() {
        return ShipmentViewForm.ID;
    }

    @Override
    public boolean isDeletable() {
        return internalIsDeletable();
    }

    @Override
    protected void additionalRefreshAfterDelete() {
        getParent().getParent().rebuild();
    }

}
