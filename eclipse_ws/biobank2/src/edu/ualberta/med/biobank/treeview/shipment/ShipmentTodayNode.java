package edu.ualberta.med.biobank.treeview.shipment;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentInfoWrapper;
import edu.ualberta.med.biobank.treeview.AbstractTodayNode;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.admin.ClinicAdapter;
import edu.ualberta.med.biobank.views.SpecimenTransitView;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ShipmentTodayNode extends AbstractTodayNode<OriginInfoWrapper> {

    public ShipmentTodayNode(AdapterBase parent, int id) {
        super(parent, id);
        setName(Messages.ShipmentTodayNode_today_label);
    }

    @Override
    protected AdapterBase createChildNode(ModelWrapper<?> child) {
        Assert.isTrue(child instanceof ClinicWrapper);
        return new ClinicAdapter(this, (ClinicWrapper) child);
    }

    @Override
    protected AdapterBase createChildNode() {
        return new ClinicAdapter(this, null);
    }

    @Override
    protected List<OriginInfoWrapper> getTodayElements()
        throws ApplicationException {
        if (SessionManager.getInstance().isConnected()
            && SessionManager.getUser().getCurrentWorkingCenter() != null)
            return OriginInfoWrapper.getTodayShipments(SessionManager
                .getAppService(), SessionManager.getUser()
                .getCurrentWorkingCenter());
        else
            return null;
    }

    @Override
    protected boolean isParentTo(ModelWrapper<?> parent, ModelWrapper<?> child) {
        if (child instanceof OriginInfoWrapper) {
            return parent.equals(((OriginInfoWrapper) child).getCenter());
        }
        return false;
    }

    @Override
    public List<AdapterBase> search(Object searchedObject) {
        return findChildFromClass(searchedObject, ClinicWrapper.class);
    }

    @Override
    protected void addChild(OriginInfoWrapper child) {
        SpecimenTransitView.addToNode(this, child);
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        if (SessionManager.canCreate(OriginInfoWrapper.class)) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText(Messages.ShipmentTodayNode_add_label);
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    addShipment();
                }
            });
        }
    }

    protected void addShipment() {
        OriginInfoWrapper shipment = new OriginInfoWrapper(
            SessionManager.getAppService());
        ShipmentInfoWrapper shipmentInfo = new ShipmentInfoWrapper(
            SessionManager.getAppService());
        shipment.setShipmentInfo(shipmentInfo);
        ShipmentAdapter shipNode = new ShipmentAdapter(SpecimenTransitView
            .getCurrent().getSearchedNode(), shipment);
        shipNode.openEntryForm();
    }

}
