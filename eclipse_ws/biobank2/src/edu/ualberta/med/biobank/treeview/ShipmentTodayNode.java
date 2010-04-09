package edu.ualberta.med.biobank.treeview;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.springframework.remoting.RemoteAccessException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.views.ShipmentAdministrationView;

public class ShipmentTodayNode extends AbstractTodayNode {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(ShipmentTodayNode.class.getName());

    public ShipmentTodayNode(AdapterBase parent, int id) {
        super(parent, id);
        setName("Today's shipments");
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
    public void performExpand() {
        if (!SessionManager.getInstance().isAllSitesSelected()) {
            try {
                List<ShipmentWrapper> todayShipments = ShipmentWrapper
                    .getTodayShipments(SessionManager.getAppService(),
                        SessionManager.getInstance().getCurrentSite());
                for (ShipmentWrapper shipment : todayShipments) {
                    ShipmentAdministrationView.getCurrent().addToNode(this,
                        shipment);
                }
            } catch (final RemoteAccessException exp) {
                BioBankPlugin.openRemoteAccessErrorMessage();
            } catch (Exception e) {
                logger.error("Error while getting today's shipment", e);
            }
        }
    }
}
