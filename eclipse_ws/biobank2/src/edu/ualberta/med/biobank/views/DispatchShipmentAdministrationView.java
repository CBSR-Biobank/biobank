package edu.ualberta.med.biobank.views;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentWrapper;
import edu.ualberta.med.biobank.rcp.DispatchShipmentAdministrationPerspective;
import edu.ualberta.med.biobank.treeview.DispatchNode;
import edu.ualberta.med.biobank.treeview.RootNode;
import edu.ualberta.med.biobank.widgets.AdapterTreeWidget;

public class DispatchShipmentAdministrationView extends
    AbstractViewWithAdapterTree {

    public static final String ID = "edu.ualberta.med.biobank.views.DispatchShipmentAdmininistrationView";

    public DispatchShipmentAdministrationView() {
        SessionManager.addView(DispatchShipmentAdministrationPerspective.ID,
            this);
    }

    @Override
    public void createPartControl(Composite parent) {
        adaptersTree = new AdapterTreeWidget(parent, false);
        rootNode = new RootNode();
        getTreeViewer().setInput(rootNode);
    }

    @Override
    public void reload() {
        DispatchNode<DispatchShipmentWrapper> received = new DispatchNode<DispatchShipmentWrapper>(
            rootNode, "Received");
        received.addChildren(SessionManager.getInstance().getCurrentSite()
            .getReceivedDispatchShipmentCollection());
        rootNode.addChild(received);
        DispatchNode<DispatchShipmentWrapper> sent = new DispatchNode<DispatchShipmentWrapper>(
            rootNode, "Sent");
        sent.addChildren(SessionManager.getInstance().getCurrentSite()
            .getSentDispatchShipmentCollection());
        rootNode.addChild(sent);
    }

}
