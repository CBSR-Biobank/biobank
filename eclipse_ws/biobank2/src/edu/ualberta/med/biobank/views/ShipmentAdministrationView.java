package edu.ualberta.med.biobank.views;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.dialogs.SelectShipmentClinicDialog;
import edu.ualberta.med.biobank.rcp.ShipmentAdministrationPerspective;
import edu.ualberta.med.biobank.treeview.ClinicAdapter;
import edu.ualberta.med.biobank.treeview.PatientAdapter;
import edu.ualberta.med.biobank.treeview.ShipmentAdapter;
import edu.ualberta.med.biobank.treeview.SiteAdapter;

public class ShipmentAdministrationView extends AbstractAdministrationView {

    public static final String ID = "edu.ualberta.med.biobank.views.ShipmentAdminView";

    public static ShipmentAdministrationView currentInstance;

    private SiteAdapter currentSiteAdapter;

    public ShipmentAdministrationView() {
        currentInstance = this;
        SessionManager.addView(ShipmentAdministrationPerspective.ID, this);
    }

    @Override
    protected Object search(String text) throws Exception {
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
    protected String getNoFoundText() {
        return "- No shipment found -";
    }

    @Override
    public void showInTree(Object searchedObject) {
        rootNode.removeAll();
        ShipmentWrapper shipment = (ShipmentWrapper) searchedObject;
        currentSiteAdapter = new SiteAdapter(rootNode, SessionManager
            .getInstance().getCurrentSite(), false);
        rootNode.addChild(currentSiteAdapter);
        ClinicAdapter clinicAdapter = new ClinicAdapter(currentSiteAdapter,
            shipment.getClinic(), false);
        currentSiteAdapter.addChild(clinicAdapter);
        ShipmentAdapter shipmentAdapter = new ShipmentAdapter(clinicAdapter,
            shipment);
        clinicAdapter.addChild(shipmentAdapter);
        shipmentAdapter.performExpand();
        shipmentAdapter.performDoubleClick();
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
            adapter.openEntryForm();
        }
    }

    public void displayPatient(PatientWrapper patient) {
        PatientAdapter patientAdapter = new PatientAdapter(
            currentInstance.rootNode, patient, false);
        if (patient.isNew()) {
            patientAdapter.openEntryForm(true);
        } else {
            patientAdapter.setEditable(false);
            patientAdapter.openViewForm();
        }
    }

}
