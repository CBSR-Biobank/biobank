package edu.ualberta.med.biobank.views;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.dispatch.DispatchSearchedNode;
import edu.ualberta.med.biobank.treeview.dispatch.InCreationDispatchGroup;
import edu.ualberta.med.biobank.treeview.dispatch.ReceivingInTransitDispatchGroup;
import edu.ualberta.med.biobank.treeview.dispatch.ReceivingNoErrorsDispatchGroup;
import edu.ualberta.med.biobank.treeview.dispatch.SentInTransitDispatchGroup;
import edu.ualberta.med.biobank.treeview.order.FilledOrderNode;
import edu.ualberta.med.biobank.treeview.order.NewOrderNode;
import edu.ualberta.med.biobank.treeview.order.ProcessingOrderNode;
import edu.ualberta.med.biobank.treeview.order.ShippedOrderNode;

public class OrderAdministrationView extends AbstractAdministrationView {

    public static final String ID = "edu.ualberta.med.biobank.views.OrderAdminView";

    public InCreationDispatchGroup creationNode;

    public SentInTransitDispatchGroup sentTransitNode;

    public ReceivingInTransitDispatchGroup receivedTransitNode;

    public ReceivingNoErrorsDispatchGroup receivingNode;

    private DispatchSearchedNode searchedNode;

    private NewOrderNode newOrderNode;

    private ProcessingOrderNode processingNode;

    private Button radioOrderNumber;

    private FilledOrderNode filledOrderNode;

    private ShippedOrderNode shippedOrderNode;

    private static OrderAdministrationView currentInstance;

    public OrderAdministrationView() {
        currentInstance = this;
        SessionManager.addView(this);
    }

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        createNodes();
    }

    private void createNodes() {

        newOrderNode = new NewOrderNode(rootNode, 0);
        newOrderNode.setParent(rootNode);
        rootNode.addChild(newOrderNode);

        processingNode = new ProcessingOrderNode(rootNode, 1);
        processingNode.setParent(rootNode);
        rootNode.addChild(processingNode);

        filledOrderNode = new FilledOrderNode(rootNode, 2);
        filledOrderNode.setParent(rootNode);
        rootNode.addChild(filledOrderNode);

        shippedOrderNode = new ShippedOrderNode(rootNode, 3);
        shippedOrderNode.setParent(rootNode);
        rootNode.addChild(shippedOrderNode);

        searchedNode = new DispatchSearchedNode(rootNode, 4);
        searchedNode.setParent(rootNode);
        rootNode.addChild(searchedNode);
    }

    @Override
    protected void createTreeTextOptions(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(3, false);
        layout.horizontalSpacing = 0;
        layout.marginHeight = 0;
        layout.verticalSpacing = 0;
        composite.setLayout(layout);

        radioOrderNumber = new Button(composite, SWT.RADIO);
        radioOrderNumber.setText("Order Number");
        radioOrderNumber.setSelection(true);
        radioOrderNumber.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (radioOrderNumber.getSelection()) {
                    // showTextOnly(true);
                }
            }
        });

    }

    @Override
    public void reload() {
        rootNode.removeAll();
        createNodes();
        for (AdapterBase adaper : rootNode.getChildren()) {
            if (!adaper.equals(searchedNode))
                adaper.rebuild();
        }
        super.reload();
    }

    @Override
    protected void internalSearch() {
        try {
            List<? extends ModelWrapper<?>> searchedObject = search();
            if (searchedObject == null || searchedObject.size() == 0) {
                String msg = "No Dispatch found";
                if (radioOrderNumber.getSelection())
                    msg += " for waybill " + treeText.getText();
                BioBankPlugin.openMessage("Dispatch not found", msg);
            } else {
                showSearchedObjectsInTree(searchedObject, true);
                getTreeViewer().expandToLevel(searchedNode, 3);
            }
        } catch (Exception e) {
            BioBankPlugin.openError("Search error", e);
        }
    }

    protected List<DispatchWrapper> search() {
        if (radioOrderNumber.getSelection()) {
            return null;
        }
        return null;
    }

    protected void showSearchedObjectsInTree(
        List<? extends ModelWrapper<?>> searchedObjects, boolean doubleClick) {
        for (ModelWrapper<?> searchedObject : searchedObjects) {
            List<AdapterBase> nodeRes = rootNode.search(searchedObject);
            if (nodeRes == null) {
                searchedNode.addSearchObject(searchedObject);
                searchedNode.performExpand();
                nodeRes = searchedNode.search(searchedObject);
            }
            if (nodeRes != null) {
                setSelectedNode(nodeRes.get(0));
                if (doubleClick) {
                    nodeRes.get(0).performDoubleClick();
                }
            }
        }
    }

    public static OrderAdministrationView getCurrent() {
        return currentInstance;
    }

    @Override
    protected String getTreeTextToolTip() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getId() {
        return ID;
    }

}
