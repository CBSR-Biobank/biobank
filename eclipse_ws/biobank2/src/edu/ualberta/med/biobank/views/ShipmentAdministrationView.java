package edu.ualberta.med.biobank.views;

import org.apache.log4j.Logger;
import org.eclipse.ui.PartInitException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.forms.ShipmentEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.ClinicAdapter;
import edu.ualberta.med.biobank.treeview.RootNode;
import edu.ualberta.med.biobank.treeview.ShipmentAdapter;
import edu.ualberta.med.biobank.treeview.SiteAdapter;

public class ShipmentAdministrationView extends AbstractAdministrationView {

    private static Logger LOGGER = Logger
        .getLogger(ShipmentAdministrationView.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.views.shipmentAdmin";

    public static ShipmentAdministrationView currentInstance;

    public ShipmentAdministrationView() {
        currentInstance = this;
    }

    @Override
    protected Object search(String text) throws Exception {
        return ShipmentWrapper.getShipmentInSite(
            SessionManager.getAppService(), text, SessionManager.getInstance()
                .getCurrentSiteWrapper());
    }

    @Override
    protected String getNoFoundText() {
        return "- No shipment found -";
    }

    @Override
    public void showInTree(Object searchedObject) {
        rootNode.removeAll();
        ShipmentWrapper shipment = (ShipmentWrapper) searchedObject;
        SiteAdapter siteAdapter = new SiteAdapter(rootNode, SessionManager
            .getInstance().getCurrentSiteWrapper(), false);
        rootNode.addChild(siteAdapter);
        ClinicAdapter clinicAdapter = new ClinicAdapter(siteAdapter, shipment
            .getClinic(), false);
        siteAdapter.addChild(clinicAdapter);
        ShipmentAdapter shipmentAdapter = new ShipmentAdapter(clinicAdapter,
            shipment);
        clinicAdapter.addChild(shipmentAdapter);
        shipmentAdapter.performExpand();
    }

    @Override
    protected void notFound(String text) {
        rootNode.removeAll();
        rootNode.addChild(getNotFoundAdapter());
        boolean create = BioBankPlugin.openConfirm("Shipment not found",
            "Do you want to create this shipment ?");
        if (create) {
            ShipmentWrapper shipment = new ShipmentWrapper(SessionManager
                .getAppService());
            shipment.setWaybill(text);
            ShipmentAdapter adapter = new ShipmentAdapter(rootNode, shipment);
            try {
                BioBankPlugin.getDefault().getWorkbench()
                    .getActiveWorkbenchWindow().getActivePage().openEditor(
                        new FormInput(adapter), ShipmentEntryForm.ID, true);
            } catch (PartInitException e) {
                String msg = "Wasn't able to open the form";
                BioBankPlugin.openError("Shipment Form", msg);
                LOGGER.error(msg, e);
            }
        }
    }

    public static RootNode getRootNode() {
        return currentInstance.rootNode;
    }

    public static void setSelectedNode(AdapterBase node) {
        currentInstance.selectNode(node);
    }

}
