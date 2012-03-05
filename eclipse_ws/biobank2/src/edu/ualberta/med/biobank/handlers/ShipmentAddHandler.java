package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.permission.shipment.OriginInfoSavePermission;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentInfoWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.treeview.shipment.ShipmentAdapter;
import edu.ualberta.med.biobank.views.SpecimenTransitView;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ShipmentAddHandler extends AbstractHandler {

    private Boolean createAllowed;

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        OriginInfoWrapper shipment = new OriginInfoWrapper(
            SessionManager.getAppService());
        ShipmentInfoWrapper shipmentInfo = new ShipmentInfoWrapper(
            SessionManager.getAppService());
        shipment.setShipmentInfo(shipmentInfo);
        ShipmentAdapter shipNode = new ShipmentAdapter(SpecimenTransitView
            .getCurrent().getSearchedNode(), shipment);
        shipNode.openEntryForm();
        return null;
    }

    @Override
    public boolean isEnabled() {
        try {
            if (createAllowed == null)
                createAllowed = SessionManager.getAppService().isAllowed(
                    new
                    OriginInfoSavePermission(SessionManager.getUser()
                        .getCurrentWorkingCenter().getId()));
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError("Error", "Unable to retrieve permissions");
            return false;
        }
        return createAllowed;
    }
}