package edu.ualberta.med.biobank.treeview.dispatch;

import java.util.List;

import org.eclipse.core.runtime.Assert;

import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AbstractSearchedNode;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.shipment.ShipmentAdapter;
import edu.ualberta.med.biobank.views.SpecimenTransitView;

public class OriginInfoSearchedNode extends AbstractSearchedNode {

    public OriginInfoSearchedNode(AdapterBase parent, int id) {
        super(parent, id, false);
    }

    @Override
    protected AdapterBase createChildNode(Object child) {
        Assert.isTrue(child instanceof DispatchWrapper
            || child instanceof OriginInfoWrapper);
        if (child instanceof OriginInfoWrapper)
            return new ShipmentAdapter(this, (OriginInfoWrapper) child);
        return new DispatchAdapter(this, (DispatchWrapper) child);
    }

    @Override
    protected AdapterBase createChildNode() {
        return null;
    }

    @Override
    protected boolean isParentTo(Object parent, Object child) {
        if (child instanceof DispatchWrapper) {
            return parent.equals(((DispatchWrapper) child).getSenderCenter());
        }
        return false;
    }

    @Override
    public List<AbstractAdapterBase> search(Class<?> searchedClass,
        Integer objectId) {
        if (Integer.class.isAssignableFrom(searchedClass))
            return findChildFromClass(searchedClass, objectId, Integer.class);
        return searchChildren(searchedClass, objectId);
    }

    @Override
    protected void addNode(Object obj) {
        SpecimenTransitView.addToNode(this, obj);
    }

    @Override
    public void rebuild() {
        performExpand();
    }

    @Override
    public int compareTo(AbstractAdapterBase o) {
        return 0;
    }

}
