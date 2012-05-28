package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentInfoWrapper;
import edu.ualberta.med.biobank.treeview.shipment.ShipmentAdapter;
import edu.ualberta.med.biobank.views.SpecimenTransitView;

public class ShipmentAddHandler extends AbstractHandler {

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
}