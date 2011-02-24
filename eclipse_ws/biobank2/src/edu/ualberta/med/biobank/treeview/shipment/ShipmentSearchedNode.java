package edu.ualberta.med.biobank.treeview.shipment;

import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.Assert;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.treeview.AbstractSearchedNode;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.admin.ClinicAdapter;
import edu.ualberta.med.biobank.views.ShipmentAdministrationView;

public class ShipmentSearchedNode extends AbstractSearchedNode {

    public ShipmentSearchedNode(AdapterBase parent, int id) {
        super(parent, id, false);
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
        if (child instanceof CollectionEventWrapper) {
            return parent.equals(((CollectionEventWrapper) child).getClinic());
        }
        return false;
    }

    @Override
    public List<AdapterBase> search(Object searchedObject) {
        return findChildFromClass(searchedObject, Date.class,
            ClinicWrapper.class);
    }

    @Override
    protected void addNode(ModelWrapper<?> wrapper) {
        ShipmentAdministrationView.addToNode(this, wrapper);
    }
}
