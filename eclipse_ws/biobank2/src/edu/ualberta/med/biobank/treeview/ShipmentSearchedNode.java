package edu.ualberta.med.biobank.treeview;

import org.eclipse.core.runtime.Assert;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicShipmentWrapper;

public class ShipmentSearchedNode extends AbstractSearchedNode {

    public ShipmentSearchedNode(AdapterBase parent, int id) {
        super(parent, id);
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
    protected boolean isParentTo(ModelWrapper<?> parent, ModelWrapper<?> child) {
        if (child instanceof ClinicShipmentWrapper) {
            return parent.equals(((ClinicShipmentWrapper) child).getClinic());
        }
        return false;
    }
}
