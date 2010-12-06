package edu.ualberta.med.biobank.treeview.order;

import java.util.Date;

import org.eclipse.core.runtime.Assert;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.OrderWrapper;
import edu.ualberta.med.biobank.treeview.AbstractSearchedNode;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class OrderSearchedNode extends AbstractSearchedNode {

    public OrderSearchedNode(AdapterBase parent, int id) {
        super(parent, id, true);
    }

    @Override
    protected AdapterBase createChildNode(ModelWrapper<?> child) {
        Assert.isTrue(child instanceof OrderWrapper);
        return new OrderAdapter(this, (OrderWrapper) child);
    }

    @Override
    protected AdapterBase createChildNode() {
        return new OrderAdapter(this, null);
    }

    @Override
    protected boolean isParentTo(ModelWrapper<?> parent, ModelWrapper<?> child) {
        if (child instanceof OrderWrapper) {
            return parent
                .equals(((OrderWrapper) child).getSiteLinkedToObject());
        }
        return false;
    }

    @Override
    public AdapterBase search(Object searchedObject) {
        if (searchedObject instanceof Date) {
            Date date = (Date) searchedObject;
            return getChild((int) date.getTime());
        } else if (searchedObject instanceof OrderWrapper) {
            return getChild((ModelWrapper<?>) searchedObject, true);
        }
        return searchChildren(searchedObject);
    }

    @Override
    protected void addNode(ModelWrapper<?> wrapper) {
        OrderAdapter ship = new OrderAdapter(this, (OrderWrapper) wrapper);
        ship.setParent(this);
        addChild(ship);
    }

}
