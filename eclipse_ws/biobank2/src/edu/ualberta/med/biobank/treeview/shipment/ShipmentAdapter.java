package edu.ualberta.med.biobank.treeview.shipment;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.originInfo.OriginInfoDeleteAction;
import edu.ualberta.med.biobank.common.permission.shipment.OriginInfoReadPermission;
import edu.ualberta.med.biobank.common.permission.shipment.OriginInfoUpdatePermission;
import edu.ualberta.med.biobank.common.permission.shipment.ShipmentDeletePermission;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.listener.WrapperEvent;
import edu.ualberta.med.biobank.common.wrappers.listener.WrapperEvent.WrapperEventType;
import edu.ualberta.med.biobank.forms.ShipmentEntryForm;
import edu.ualberta.med.biobank.forms.ShipmentViewForm;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ShipmentAdapter extends AdapterBase {
    private static final I18n i18n = I18nFactory
        .getI18n(ShipmentAdapter.class);

    @SuppressWarnings("nls")
    private static final String SHIPMENT = i18n.tr("Shipment");

    @SuppressWarnings("nls")
    public ShipmentAdapter(AdapterBase parent, OriginInfoWrapper originInfo) {
        super(parent, originInfo);
        if (originInfo.getShipmentInfo() == null) {
            throw new NullPointerException(
                // exception message.
                i18n.tr("No shipment information is associated with the given origin information."));
        }

        setHasChildren(false);
    }

    @Override
    public void init() {
        this.isDeletable = isAllowed(
            new ShipmentDeletePermission(
                ((OriginInfoWrapper) getModelObject())
                    .getId(),
                SessionManager.getUser().getCurrentWorkingCenter()
                    .getId()));
        this.isReadable = isAllowed(
            new OriginInfoReadPermission(getModelObject().getId()));
        this.isEditable = isAllowed(
            new OriginInfoUpdatePermission(
                ((OriginInfoWrapper) getModelObject())
                    .getReceiverSite()
                    .getId()));
    }

    @SuppressWarnings("nls")
    @Override
    protected String getLabelInternal() {
        OriginInfoWrapper originInfo = (OriginInfoWrapper) getModelObject();
        ShipmentInfoWrapper shipmentInfo = originInfo.getShipmentInfo();

        String label = StringUtil.EMPTY_STRING;
        if (shipmentInfo.getReceivedAt() != null)
            label += shipmentInfo.getFormattedDateReceived();
        if (shipmentInfo.getWaybill() != null) {
            label += " (" + shipmentInfo.getWaybill() + ")";
        }

        return label;
    }

    @SuppressWarnings("nls")
    @Override
    public String getTooltipTextInternal() {
        OriginInfoWrapper originInfo = (OriginInfoWrapper) getModelObject();
        if (originInfo != null) {
            CenterWrapper<?> center = originInfo.getCenter();
            if (center != null)
                return center.getName()
                    + " - " + getTooltipText(SHIPMENT);
        }
        return getTooltipText(SHIPMENT);
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addEditMenu(menu, SHIPMENT);
        addViewMenu(menu, SHIPMENT);
        addDeleteMenu(menu, SHIPMENT);
    }

    @SuppressWarnings("nls")
    @Override
    protected String getConfirmDeleteMessage() {
        return i18n.tr("Are you sure you want to delete this shipment?");
    }

    @Override
    public List<AbstractAdapterBase> search(Class<?> klazz, Integer id) {
        List<AbstractAdapterBase> list = new ArrayList<AbstractAdapterBase>();
        if (klazz.equals(Dispatch.class) && id.equals(getId()))
            list.add(this);
        return list;
    }

    @Override
    protected AdapterBase createChildNode() {
        return null;
    }

    @Override
    protected AdapterBase createChildNode(Object child) {
        return null;
    }

    @Override
    protected List<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        return null;
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
    protected void additionalRefreshAfterDelete() {
        WrapperEventType eventType = WrapperEventType.DELETE;
        WrapperEvent event = new WrapperEvent(eventType, getModelObject());
        getModelObject().notifyListeners(event);
        getParent().getParent().getParent().performExpand();
    }

    @Override
    public int compareTo(AbstractAdapterBase o) {
        if (o instanceof ShipmentAdapter)
            return internalCompareTo(o);
        return 0;
    }

    @Override
    public void runDelete() throws ApplicationException {

        OriginInfoDeleteAction action =
            new OriginInfoDeleteAction((OriginInfo)
                getModelObject().getWrappedObject(),
                SessionManager.getUser().getCurrentWorkingCenter()
                    .getWrappedObject());
        SessionManager.getAppService().doAction(action);
    }

}
