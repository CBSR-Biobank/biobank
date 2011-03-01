package edu.ualberta.med.biobank.treeview.shipment;

import java.util.List;

import org.eclipse.core.runtime.Assert;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.treeview.AbstractTodayNode;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.admin.ClinicAdapter;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ShipmentTodayNode extends AbstractTodayNode {

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

    @Deprecated
    @Override
    protected List<? extends ModelWrapper<?>> getTodayElements()
        throws ApplicationException {
        // return CollectionEventWrapper.getTodayShipments(SessionManager
        // .getAppService());
        return null;
    }

    @Override
    protected boolean isParentTo(ModelWrapper<?> parent, ModelWrapper<?> child) {
        if (child instanceof CollectionEventWrapper) {
            return parent.equals(((CollectionEventWrapper) child).getClinic());
        }
        return false;
    }

    @Override
    public List<AdapterBase> search(Object searchedObject) {
        return findChildFromClass(searchedObject, ClinicWrapper.class);
    }

}
