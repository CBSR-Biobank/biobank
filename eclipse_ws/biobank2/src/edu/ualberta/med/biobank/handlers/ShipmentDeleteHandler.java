package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.treeview.clinicShipment.ClinicShipmentAdapter;
import edu.ualberta.med.biobank.views.ShipmentAdministrationView;

public class ShipmentDeleteHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ClinicShipmentAdapter shipAdapter = ShipmentAdministrationView
            .getCurrentShipment();
        try {
            shipAdapter
                .delete("Are you sure you want to delete this shipment?");
        } catch (Exception e) {
            BioBankPlugin.openAsyncError("Delete Failed", e);
        }
        return null;
    }
}