package edu.ualberta.med.biobank.treeview.dispatch;

import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.dispatch.DispatchRetrievalAction;
import edu.ualberta.med.biobank.common.permission.dispatch.DispatchCreatePermission;
import edu.ualberta.med.biobank.common.util.DispatchState;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class InCreationDispatchGroup extends AbstractDispatchGroup {

    private Boolean createAllowed;

    public InCreationDispatchGroup(AdapterBase parent, int id) {
        super(parent, id, "Creation");
        try {
            this.createAllowed =
                SessionManager.getAppService().isAllowed(
                    new DispatchCreatePermission(SessionManager.getUser()
                        .getCurrentWorkingCenter().getId()));
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError("Error", "Unable to retrieve permissions");
        }
    }

    @Override
    protected List<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        return ModelWrapper.wrapModelCollection(SessionManager.getAppService(),
            SessionManager.getAppService().doAction(
                new DispatchRetrievalAction(DispatchState.CREATION,
                    SessionManager.getUser().getCurrentWorkingCenter().getId(),
                    true, true)).getList(), DispatchWrapper.class);
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        if (createAllowed) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText("Add Dispatch");
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    addDispatch();
                }
            });
        }
    }

    protected void addDispatch() {
        DispatchWrapper shipment = new DispatchWrapper(
            SessionManager.getAppService());
        DispatchAdapter shipNode = new DispatchAdapter(this, shipment);
        shipNode.openEntryForm();
    }

    @Override
    public int compareTo(AbstractAdapterBase o) {
        return 0;
    }

}
