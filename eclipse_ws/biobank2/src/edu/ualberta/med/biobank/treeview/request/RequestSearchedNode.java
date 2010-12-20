package edu.ualberta.med.biobank.treeview.request;

import java.util.Date;

import org.eclipse.core.runtime.Assert;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestWrapper;
import edu.ualberta.med.biobank.treeview.AbstractSearchedNode;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class RequestSearchedNode extends AbstractSearchedNode {

    public RequestSearchedNode(AdapterBase parent, int id) {
        super(parent, id, true);
    }

    @Override
    protected AdapterBase createChildNode(ModelWrapper<?> child) {
        Assert.isTrue(child instanceof RequestWrapper);
        return new RequestAdapter(this, (RequestWrapper) child);
    }

    @Override
    protected AdapterBase createChildNode() {
        return new RequestAdapter(this, null);
    }

    @Override
    protected boolean isParentTo(ModelWrapper<?> parent, ModelWrapper<?> child) {
        if (child instanceof RequestWrapper) {
            return parent
                .equals(((RequestWrapper) child).getSiteLinkedToObject());
        }
        return false;
    }

    @Override
    public AdapterBase search(Object searchedObject) {
        if (searchedObject instanceof Date) {
            Date date = (Date) searchedObject;
            return getChild((int) date.getTime());
        } else if (searchedObject instanceof RequestWrapper) {
            return getChild((ModelWrapper<?>) searchedObject, true);
        }
        return searchChildren(searchedObject);
    }

    @Override
    protected void addNode(ModelWrapper<?> wrapper) {
        RequestAdapter ship = new RequestAdapter(this, (RequestWrapper) wrapper);
        ship.setParent(this);
        addChild(ship);
    }

}
