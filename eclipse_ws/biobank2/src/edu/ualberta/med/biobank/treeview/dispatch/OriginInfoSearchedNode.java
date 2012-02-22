package edu.ualberta.med.biobank.treeview.dispatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.Assert;

import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AbstractSearchedNode;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.DateNode;
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
        if (searchedClass.equals(Date.class)) {
            List<AbstractAdapterBase> list =
                new ArrayList<AbstractAdapterBase>();
            for (AbstractAdapterBase child : getChildren()) {
                if (child instanceof DateNode
                    && ((DateNode) child).getId().equals(objectId))
                    list.add(child);
            }
            return list;
        }
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
