package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.treeview.shipment.ShipmentAdapter;
import edu.ualberta.med.biobank.views.ShipmentAdministrationView;

public class ShipmentAddHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        CollectionEventWrapper shipment = new CollectionEventWrapper(
            SessionManager.getAppService());
        ShipmentAdapter shipNode = new ShipmentAdapter(
            ShipmentAdministrationView.getCurrent().getSearchedNode(), shipment);
        shipNode.openEntryForm();
        return null;
    }

    @Override
    public boolean isEnabled() {
        return SessionManager.canCreate(CollectionEventWrapper.class, null);
    }
}