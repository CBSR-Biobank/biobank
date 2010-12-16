package edu.ualberta.med.biobank.treeview.dispatch;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.Assert;

import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.treeview.AbstractSearchedNode;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class DispatchSearchedNode extends AbstractSearchedNode {

    public DispatchSearchedNode(AdapterBase parent, int id) {
        super(parent, id, true);
    }

    @Override
    protected AdapterBase createChildNode(ModelWrapper<?> child) {
        Assert.isTrue(child instanceof DispatchWrapper);
        return new DispatchAdapter(this, (DispatchWrapper) child);
    }

    @Override
    protected AdapterBase createChildNode() {
        return new DispatchAdapter(this, null);
    }

    @Override
    protected boolean isParentTo(ModelWrapper<?> parent, ModelWrapper<?> child) {
        if (child instanceof DispatchWrapper) {
            return parent.equals(((DispatchWrapper) child).getSender());
        }
        return false;
    }

    @Override
    public List<AdapterBase> search(Object searchedObject) {
        if (searchedObject instanceof Date) {
            Date date = (Date) searchedObject;
            return Arrays.asList(getChild((int) date.getTime()));
        } else
            return findChildFromClass(searchedObject, DispatchWrapper.class);
    }

    @Override
    protected void addNode(ModelWrapper<?> wrapper) {
        DispatchAdapter ship = new DispatchAdapter(this,
            (DispatchWrapper) wrapper);
        ship.setParent(this);
        addChild(ship);
    }

}
