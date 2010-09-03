package edu.ualberta.med.biobank.views;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.treeview.ReceivedDispatchShipmentGroup;
import edu.ualberta.med.biobank.treeview.SentDispatchShipmentGroup;

public class DispatchShipmentAdministrationView extends
    AbstractAdministrationView {

    public static final String ID = "edu.ualberta.med.biobank.views.DispatchShipmentAdmininistrationView";

    public SentDispatchShipmentGroup sentNode;
    public ReceivedDispatchShipmentGroup receivedNode;

    public DispatchShipmentAdministrationView() {
        SessionManager.addView(DispatchShipmentAdministrationView.ID, this);
    }

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        sentNode = new SentDispatchShipmentGroup(rootNode, 0);
        sentNode.setParent(rootNode);
        rootNode.addChild(sentNode);

        receivedNode = new ReceivedDispatchShipmentGroup(rootNode, 0);
        receivedNode.setParent(rootNode);
        rootNode.addChild(receivedNode);

    }

    @Override
    public void siteChanged(Object sourceValue) {
        if (sourceValue != null
            && !SessionManager.getInstance().isAllSitesSelected()) {
            reload();
        }
    }

    @Override
    public void reload() {
        sentNode.rebuild();
        receivedNode.rebuild();
        super.reload();
    }

    @Override
    protected void internalSearch() {

    }

}
