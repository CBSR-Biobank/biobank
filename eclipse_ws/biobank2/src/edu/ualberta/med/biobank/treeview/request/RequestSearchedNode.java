package edu.ualberta.med.biobank.treeview.request;

import java.util.Date;
import java.util.List;

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
        return false;
    }

    @Override
    public List<AdapterBase> search(Object searchedObject) {
        return findChildFromClass(searchedObject, Date.class,
            RequestWrapper.class);
    }

    @Override
    protected void addNode(ModelWrapper<?> wrapper) {
        RequestAdapter ship = new RequestAdapter(this, (RequestWrapper) wrapper);
        ship.setParent(this);
        addChild(ship);
    }

}
