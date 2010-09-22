package edu.ualberta.med.biobank.treeview.dispatch;

import java.util.Collection;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class OutgoingNode extends AdapterBase {

    private InCreationDispatchShipmentGroup creationNode;
    private SentInTransitDispatchShipmentGroup sentTransitNode;

    public OutgoingNode(AdapterBase parent, int id) {
        super(parent, id, "Outgoing", true, false);
        creationNode = new InCreationDispatchShipmentGroup(this, 0);
        creationNode.setParent(this);
        addChild(creationNode);

        sentTransitNode = new SentInTransitDispatchShipmentGroup(this, 1);
        sentTransitNode.setParent(this);
        addChild(sentTransitNode);
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
