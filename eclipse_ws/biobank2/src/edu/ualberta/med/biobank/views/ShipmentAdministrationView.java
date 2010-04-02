package edu.ualberta.med.biobank.views;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.listener.WrapperEvent;
import edu.ualberta.med.biobank.common.wrappers.listener.WrapperListenerAdapter;
import edu.ualberta.med.biobank.dialogs.SelectShipmentClinicDialog;
import edu.ualberta.med.biobank.rcp.ShipmentAdministrationPerspective;
import edu.ualberta.med.biobank.treeview.AbstractSearchedNode;
import edu.ualberta.med.biobank.treeview.AbstractTodayNode;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.ClinicAdapter;
import edu.ualberta.med.biobank.treeview.NodeSearchVisitor;
import edu.ualberta.med.biobank.treeview.ShipmentAdapter;
import edu.ualberta.med.biobank.treeview.ShipmentSearchedNode;
import edu.ualberta.med.biobank.treeview.ShipmentTodayNode;
import edu.ualberta.med.biobank.treeview.ShipmentViewNodeSearchVisitor;

public class ShipmentAdministrationView extends AbstractAdministrationView {

    public static final String ID = "edu.ualberta.med.biobank.views.ShipmentAdminView";

    private static ShipmentAdministrationView currentInstance;

    public ShipmentAdministrationView() {
        currentInstance = this;
        SessionManager.addView(ShipmentAdministrationPerspective.ID, this);
    }

    @Override
    protected ModelWrapper<?> search(String text) throws Exception {
        List<ShipmentWrapper> shipments = ShipmentWrapper.getShipmentsInSite(
            SessionManager.getAppService(), text, SessionManager.getInstance()
                .getCurrentSite());
        if (shipments.size() == 1) {
            return shipments.get(0);
        }
        if (shipments.size() > 1) {
            SelectShipmentClinicDialog dlg = new SelectShipmentClinicDialog(
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                shipments);
            if (dlg.open() == Dialog.OK) {
                return dlg.getSelectedShipment();
            }
        }
        return null;
    }

    @Override
    public AdapterBase addToNode(AdapterBase parentNode, ModelWrapper<?> wrapper) {
        if (wrapper instanceof ShipmentWrapper) {
            ShipmentWrapper shipment = (ShipmentWrapper) wrapper;
            ClinicAdapter clinicAdapter = (ClinicAdapter) parentNode
                .accept(new ShipmentViewNodeSearchVisitor(shipment.getClinic()));
            if (clinicAdapter == null) {
                clinicAdapter = new ClinicAdapter(parentNode, shipment
                    .getClinic(), false);
                parentNode.addChild(clinicAdapter);
            }
            ShipmentAdapter shipmentAdapter = (ShipmentAdapter) clinicAdapter
                .accept(new ShipmentViewNodeSearchVisitor(shipment));
            if (shipmentAdapter == null) {
                shipmentAdapter = new ShipmentAdapter(clinicAdapter, shipment);
                clinicAdapter.addChild(shipmentAdapter);
            }
            return shipmentAdapter;
        }
        return null;
    }

    @Override
    protected NodeSearchVisitor getVisitor(ModelWrapper<?> searchedObject) {
        return new ShipmentViewNodeSearchVisitor(searchedObject);
    }

    @Override
    protected void notFound(String text) {
        boolean create = BioBankPlugin.openConfirm("Shipment not found",
            "Do you want to create this shipment ?");
        if (create) {
            ShipmentWrapper shipment = new ShipmentWrapper(SessionManager
                .getAppService());
            shipment.setWaybill(text);
            ShipmentAdapter adapter = new ShipmentAdapter(searchedNode,
                shipment);
            adapter.openEntryForm();
            shipment.addWrapperListener(new ShipmentAddListener(adapter));
        }
    }

    @Override
    protected AbstractTodayNode getTodayNode() {
        return new ShipmentTodayNode(rootNode, 0);
    }

    @Override
    protected AbstractSearchedNode getSearchedNode() {
        return new ShipmentSearchedNode(rootNode, 1);
    }

    public static void showShipment(ShipmentWrapper shipment) {
        if (currentInstance != null) {
            currentInstance.showSearchedObjectInTree(shipment);
        }
    }

    public static ShipmentAdministrationView getCurrent() {
        return currentInstance;
    }

    public class ShipmentAddListener extends WrapperListenerAdapter {
        private ShipmentAdapter shipAdapter;

        public ShipmentAddListener(ShipmentAdapter ship) {
            this.shipAdapter = ship;
        }

        public void removeListener() {
            shipAdapter.getWrapper().removeWrapperListener(this);
        }

        @Override
        public void inserted(WrapperEvent event) {
            if (shipAdapter.getWrapper().isReceivedToday()) {
                shipAdapter.getParent().removeChild(shipAdapter);
                reloadTodayNode();
            }
        }
    }

}
