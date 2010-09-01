package edu.ualberta.med.biobank.treeview;

import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;

public class DispatchNode<DispatchWrapper> extends AdapterBase {

    public DispatchNode(AdapterBase parent, ModelWrapper<?> object) {
        super(parent, object);
    }

    public DispatchNode(AdapterBase parent, String label) {
        super(parent, null);
        this.setName(label);
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
    public AdapterBase accept(NodeSearchVisitor visitor) {
        return null;
    }

    public void addChildren(List<DispatchShipmentWrapper> list) {
        if (list != null)
            for (DispatchShipmentWrapper child : list) {
                DispatchNode<DispatchShipmentWrapper> node = new DispatchNode<DispatchShipmentWrapper>(
                    this, child);
                this.addChild(node);
            }
    }

    @Override
    protected String getLabelInternal() {
        return null;
    }
}
