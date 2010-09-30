package edu.ualberta.med.biobank.treeview.dispatch;

import java.util.ArrayList;
import java.util.Collection;

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
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class InCreationDispatchShipmentGroup extends
    AbstractDispatchShipmentGroup {

    public InCreationDispatchShipmentGroup(AdapterBase parent, int id) {
        super(parent, id, "Creation");
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        SiteWrapper site = SessionManager.getInstance().getCurrentSite();
        if (!SessionManager.getInstance().isAllSitesSelected()) {
            site.reload();
            return site.getInCreationDispatchShipmentCollection();
        }
        return new ArrayList<ModelWrapper<?>>();
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        if (SessionManager.canCreate(DispatchShipmentWrapper.class)) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText("Add Dispatch Shipment");
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    addDispatchShipment();
                }
            });
        }
    }

    protected void addDispatchShipment() {
        DispatchShipmentWrapper shipment =
            new DispatchShipmentWrapper(SessionManager.getAppService());
        shipment.setSender(SessionManager.getInstance().getCurrentSite());
        DispatchShipmentAdapter shipNode =
            new DispatchShipmentAdapter(this, shipment);
        shipNode.openEntryForm();
    }

}
