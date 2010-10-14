package edu.ualberta.med.biobank.treeview.dispatch;

import java.util.Date;

import org.eclipse.core.runtime.Assert;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.treeview.AbstractSearchedNode;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.admin.ClinicAdapter;

public class DispatchShipmentSearchedNode extends AbstractSearchedNode {

    public DispatchShipmentSearchedNode(AdapterBase parent, int id) {
        super(parent, id, true);
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
        if (child instanceof DispatchShipmentWrapper) {
            return parent.equals(((DispatchShipmentWrapper) child).getSender());
        }
        return false;
    }

    @Override
    public AdapterBase search(Object searchedObject) {
        if (searchedObject instanceof Date) {
            Date date = (Date) searchedObject;
            return getChild((int) date.getTime());
        } else if (searchedObject instanceof DispatchShipmentWrapper) {
            return getChild((ModelWrapper<?>) searchedObject, true);
        }
        return searchChildren(searchedObject);
    }

    @Override
    protected void addNode(ModelWrapper<?> wrapper) {
        DispatchShipmentAdapter ship = new DispatchShipmentAdapter(this,
            (DispatchShipmentWrapper) wrapper);
        ship.setParent(this);
        addChild(ship);
    }

}
