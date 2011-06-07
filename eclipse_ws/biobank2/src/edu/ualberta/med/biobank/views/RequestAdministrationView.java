package edu.ualberta.med.biobank.views;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.gui.common.BiobankGuiCommonPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.request.RequestSearchedNode;
import edu.ualberta.med.biobank.treeview.request.RequestSiteAdapter;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class RequestAdministrationView extends AbstractAdministrationView {

    public static final String ID = "edu.ualberta.med.biobank.views.RequestAdminView";

    private Button radioRequestNumber;

    private List<SiteWrapper> siteNodes;

    private RequestSearchedNode searchedNode;

    private static RequestAdministrationView currentInstance;

    public RequestAdministrationView() {
        currentInstance = this;
        SessionManager.addView(this);
    }

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
    }

    public void createNodes() {
        try {
            siteNodes = SiteWrapper.getSites(SessionManager.getAppService());
        } catch (Exception e) {
            BiobankGuiCommonPlugin.openAsyncError("Failed to load sites", e);
        }
        if (siteNodes != null) {
            for (SiteWrapper site : siteNodes) {
                RequestSiteAdapter siteAdapter = new RequestSiteAdapter(
                    rootNode, site);
                siteAdapter.setParent(rootNode);
                rootNode.addChild(siteAdapter);
            }

            searchedNode = new RequestSearchedNode(rootNode, 2);
            searchedNode.setParent(rootNode);
            rootNode.addChild(searchedNode);
        }
    }

    @Override
    protected void createTreeTextOptions(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(3, false);
        layout.horizontalSpacing = 0;
        layout.marginHeight = 0;
        layout.verticalSpacing = 0;
        composite.setLayout(layout);

        radioRequestNumber = new Button(composite, SWT.RADIO);
        radioRequestNumber.setText("Request Number");
        radioRequestNumber.setSelection(true);
        radioRequestNumber.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (radioRequestNumber.getSelection()) {
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
                String msg = "No Request found";
                if (radioRequestNumber.getSelection())
                    msg += " for number " + treeText.getText();
                BiobankGuiCommonPlugin.openMessage("Request not found", msg);
            } else {
                showSearchedObjectsInTree(searchedObject, true);
                getTreeViewer().expandToLevel(searchedNode, 3);
            }
        } catch (Exception e) {
            BiobankGuiCommonPlugin.openError("Search error", e);
        }
    }

    protected List<RequestWrapper> search() throws ApplicationException {
        if (radioRequestNumber.getSelection()) {
            return RequestWrapper.getRequestByNumber(
                SessionManager.getAppService(), treeText.getText().trim());
        }
        return null;
    }

    protected void showSearchedObjectsInTree(
        List<? extends ModelWrapper<?>> searchedObjects, boolean doubleClick) {
        for (ModelWrapper<?> searchedObject : searchedObjects) {
            List<AdapterBase> nodeRes = rootNode.search(searchedObject);
            if (nodeRes.size() == 0) {
                searchedNode.addSearchObject(searchedObject);
                searchedNode.performExpand();
                nodeRes = searchedNode.search(searchedObject);
            }
            if (nodeRes.size() > 0) {
                setSelectedNode(nodeRes.get(0));
                if (doubleClick) {
                    nodeRes.get(0).performDoubleClick();
                }
            }
        }
    }

    public static RequestAdministrationView getCurrent() {
        return currentInstance;
    }

    @Override
    protected String getTreeTextToolTip() {
        return null;
    }

    @Override
    public String getId() {
        return ID;
    }

}
