package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.permission.shipment.OriginInfoUpdatePermission;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentInfoWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.handlers.LogoutSensitiveHandler;
import edu.ualberta.med.biobank.treeview.shipment.ShipmentAdapter;
import edu.ualberta.med.biobank.views.SpecimenTransitView;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ShipmentAddHandler extends LogoutSensitiveHandler {
    private static final I18n i18n = I18nFactory
        .getI18n(ShipmentAddHandler.class);

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

    @SuppressWarnings("nls")
    @Override
    public boolean isEnabled() {
        try {
            if (allowed == null)
                allowed = SessionManager.getAppService().isAllowed(
                    new
                    OriginInfoUpdatePermission(SessionManager.getUser()
                        .getCurrentWorkingCenter().getId()));
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError(
                // exception message
                i18n.tr("Unable to retrieve permissions"));
            return false;
        }
        return allowed;
    }
}