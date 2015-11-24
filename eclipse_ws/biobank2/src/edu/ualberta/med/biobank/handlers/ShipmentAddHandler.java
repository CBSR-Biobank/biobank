package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentInfoWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.treeview.shipment.ShipmentAdapter;
import edu.ualberta.med.biobank.views.SpecimenTransitView;

public class ShipmentAddHandler extends AbstractHandler {

    private static final I18n i18n = I18nFactory.getI18n(ShipmentAddHandler.class);

    @SuppressWarnings("nls")
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        if (SpecimenTransitView.getCurrent() == null) {
            BgcPlugin.openAsyncError(
                // TR: dialog title
                i18n.tr("Shipping Error"),
                // TR: dialog message
                i18n.tr("Please select the \"Specimen Transit\" tab first."));
            return null;
        }

        OriginInfoWrapper shipment =
            new OriginInfoWrapper(SessionManager.getAppService());
        ShipmentInfoWrapper shipmentInfo =
            new ShipmentInfoWrapper(SessionManager.getAppService());
        shipment.setShipmentInfo(shipmentInfo);

        ShipmentAdapter shipNode = new ShipmentAdapter(
            SpecimenTransitView.getCurrent().getSearchedNode(), shipment);
        shipNode.openEntryForm();
        return null;
    }
}