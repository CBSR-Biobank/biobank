package edu.ualberta.med.biobank.treeview;

import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;

public class DispatchShipmentAdapter extends AdapterBase {

    public DispatchShipmentAdapter(AdapterBase parent,
        DispatchShipmentWrapper ship) {
        super(parent, ship);
    }

    @Override
    protected void executeDoubleClick() {
        performExpand();
    }

    @Override
    public String getTooltipText() {
        return null;
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addViewMenu(menu, "Dispatch Shipment");
        if (SessionManager.canUpdate(DispatchShipmentWrapper.class)) {
            if (SessionManager.getInstance().getCurrentSite()
                .equals(((DispatchShipmentWrapper) modelObject).getReceiver())) {
                MenuItem mi = new MenuItem(menu, SWT.PUSH);
                mi.setText("Process reception");
                mi.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        System.out
                            .println("should open entry form to edit the shipment as a receiver");
                    }
                });
            }
            if (SessionManager.getInstance().getCurrentSite()
                .equals(((DispatchShipmentWrapper) modelObject).getSender())) {
                MenuItem mi = new MenuItem(menu, SWT.PUSH);
                mi.setText("Modify shipment");
                // FIXME is there any way to know if the shipment has been sent
                // already ?
                mi.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        System.out
                            .println("should open entry form to edit the shipment as a sender");
                    }
                });
            }
        }
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
                DispatchShipmentAdapter node = new DispatchShipmentAdapter(
                    this, child);
                this.addChild(node);
            }
    }

    @Override
    protected String getLabelInternal() {
        return null;
    }
}
