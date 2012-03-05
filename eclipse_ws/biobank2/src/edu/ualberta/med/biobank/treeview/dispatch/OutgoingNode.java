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
import edu.ualberta.med.biobank.common.permission.dispatch.DispatchCreatePermission;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class OutgoingNode extends AdapterBase {

    private InCreationDispatchGroup creationNode;
    private SentInTransitDispatchGroup sentTransitNode;
    private Boolean createAllowed;

    public OutgoingNode(AdapterBase parent, int id, CenterWrapper<?> center) {
        super(parent, id, Messages.OutgoingNode_outgoing_node_label, true);
        creationNode = new InCreationDispatchGroup(this, 0);
        creationNode.setParent(this);
        addChild(creationNode);

        sentTransitNode = new SentInTransitDispatchGroup(this, 1);
        sentTransitNode.setParent(this);
        addChild(sentTransitNode);

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
    protected String getLabelInternal() {
        return null;
    }

    @Override
    public String getTooltipTextInternal() {
        return null;
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        if (createAllowed) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText(Messages.OutgoingNode_add_label);
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    addDispatch();
                }
            });
        }
    }

    @Override
    protected AdapterBase createChildNode() {
        return null;
    }

    @Override
    protected AdapterBase createChildNode(Object child) {
        return null;
    }

    @Override
    protected List<? extends ModelWrapper<?>> getWrapperChildren()
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
        for (AbstractAdapterBase adaper : getChildren()) {
            adaper.rebuild();
        }
    }

    @Override
    public List<AbstractAdapterBase> search(Class<?> searchedClass,
        Integer objectId) {
        return searchChildren(searchedClass, objectId);
    }

    public void addDispatch() {
        creationNode.addDispatch();
    }

    @Override
    public int compareTo(AbstractAdapterBase o) {
        return 0;
    }
}
