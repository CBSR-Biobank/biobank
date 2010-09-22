package edu.ualberta.med.biobank.treeview.dispatch;

import java.util.Collection;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class IncomingNode extends AdapterBase {

    private ReceivingInTransitDispatchShipmentGroup receivedTransitNode;
    private ReceivingDispatchShipmentGroup receivingNode;

    public IncomingNode(AdapterBase parent, int id) {
        super(parent, id, "Incoming", true, false);
        receivedTransitNode = new ReceivingInTransitDispatchShipmentGroup(this,
            0);
        receivedTransitNode.setParent(this);
        addChild(receivedTransitNode);

        receivingNode = new ReceivingDispatchShipmentGroup(this, 1);
        receivingNode.setParent(this);
        addChild(receivingNode);

    }

    @Override
    protected String getLabelInternal() {
        return null;
    }

    @Override
    public String getTooltipText() {
        return null;
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {

    }

    @Override
    protected AdapterBase createChildNode() {
        return null;
    }

    @Override
    protected AdapterBase createChildNode(ModelWrapper<?> child) {
        return null;
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        return null;
    }

    @Override
    protected int getWrapperChildCount() throws Exception {
        return 0;
    }

    @Override
    public String getViewFormId() {
        return null;
    }

    @Override
    public String getEntryFormId() {
        return null;
    }

    @Override
    public void rebuild() {
        for (AdapterBase adaper : getChildren()) {
            adaper.rebuild();
        }
    }

}
