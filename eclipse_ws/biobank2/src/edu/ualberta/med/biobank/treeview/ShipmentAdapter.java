package edu.ualberta.med.biobank.treeview;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;

public class ShipmentAdapter extends AdapterBase {

    public ShipmentAdapter(AdapterBase parent, ShipmentWrapper shipment) {
        super(parent, shipment);
    }

    public ShipmentWrapper getWrapper() {
        return (ShipmentWrapper) modelObject;
    }

    @Override
    public String getTitle() {
        return getTitle("Shipment");
    }

    @Override
    public void loadChildren(boolean updateNode) {
    }

    @Override
    public void performDoubleClick() {
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
    }

    @Override
    public AdapterBase accept(NodeSearchVisitor visitor) {
        return null;
    }

}
