package edu.ualberta.med.biobank.treeview.shipment;

import java.util.List;

import org.eclipse.core.runtime.Assert;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.treeview.AbstractTodayNode;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.admin.ClinicAdapter;
import edu.ualberta.med.biobank.views.SpecimenTransitView;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ShipmentTodayNode extends AbstractTodayNode<OriginInfoWrapper> {

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
    protected List<OriginInfoWrapper> getTodayElements()
        throws ApplicationException {
        if (SessionManager.getInstance().isConnected())
            return OriginInfoWrapper.getTodayShipments(SessionManager
                .getAppService());
        else
            return null;
    }

    @Override
    protected boolean isParentTo(ModelWrapper<?> parent, ModelWrapper<?> child) {
        if (child instanceof OriginInfoWrapper) {
            return parent.equals(((OriginInfoWrapper) child).getCenter());
        }
        return false;
    }

    @Override
    public List<AdapterBase> search(Object searchedObject) {
        return findChildFromClass(searchedObject, ClinicWrapper.class);
    }

    @Override
    protected void addChild(OriginInfoWrapper child) {
        SpecimenTransitView.addToNode(this, child);
    }
}
