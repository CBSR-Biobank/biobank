package edu.ualberta.med.biobank.views;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISourceProvider;
import org.eclipse.ui.ISourceProviderListener;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.ISourceProviderService;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.sourceproviders.SiteSelectionState;
import edu.ualberta.med.biobank.treeview.AbstractSearchedNode;
import edu.ualberta.med.biobank.treeview.AbstractTodayNode;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.NodeSearchVisitor;
import edu.ualberta.med.biobank.treeview.RootNode;
import edu.ualberta.med.biobank.widgets.AdapterTreeWidget;

public abstract class AbstractAdministrationView extends AbstractViewWithTree {

    protected Text treeText;

    private ISourceProviderListener siteStateListener;

    protected AbstractTodayNode todayNode;

    protected AbstractSearchedNode searchedNode;

    @Override
    public void createPartControl(Composite parent) {
        parent.setLayout(new GridLayout(1, false));

        treeText = new Text(parent, SWT.SINGLE | SWT.BORDER);
        Listener searchListener = new Listener() {
            public void handleEvent(Event e) {
                internalSearch();
            }
        };
        treeText.addListener(SWT.DefaultSelection, searchListener);
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        treeText.setLayoutData(gd);

        adaptersTree = new AdapterTreeWidget(parent, this, false);
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        adaptersTree.setLayoutData(gd);

        rootNode = new RootNode();
        rootNode.setTreeViewer(adaptersTree.getTreeViewer());
        adaptersTree.getTreeViewer().setInput(rootNode);
        getSite().setSelectionProvider(adaptersTree.getTreeViewer());
        adaptersTree.getTreeViewer().expandAll();

        todayNode = getTodayNode();
        todayNode.setParent(rootNode);
        rootNode.addChild(todayNode);

        searchedNode = getSearchedNode();
        searchedNode.setParent(rootNode);
        rootNode.addChild(searchedNode);

        setSiteManagement();
    }

    protected abstract AbstractTodayNode getTodayNode();

    protected abstract AbstractSearchedNode getSearchedNode();

    protected void internalSearch() {
        String text = treeText.getText();
        try {
            ModelWrapper<?> searchedObject = search(text);
            if (searchedObject == null) {
                notFound(text);
            } else {
                showSearchedObjectInTree(searchedObject);
                getTreeViewer().expandToLevel(searchedNode, 3);
            }
        } catch (Exception e) {
            BioBankPlugin.openError("Search error", e);
            notFound(text);
        }
    }

    protected void showSearchedObjectInTree(ModelWrapper<?> searchedObject) {
        NodeSearchVisitor visitor = getVisitor(searchedObject);
        AdapterBase node = todayNode.accept(visitor);
        if (node == null) {
            node = searchedNode.accept(visitor);
            if (node == null) {
                node = addToNode(searchedNode, searchedObject);
            }
        }
        if (node != null) {
            setSelectedNode(node);
            node.performDoubleClick();
        }
    }

    protected abstract NodeSearchVisitor getVisitor(
        ModelWrapper<?> searchedObject);

    public abstract AdapterBase addToNode(AdapterBase parentNode,
        ModelWrapper<?> wrapper);

    protected abstract void notFound(String text);

    protected abstract ModelWrapper<?> search(String text) throws Exception;

    private void setSiteManagement() {
        ISourceProvider siteSelectionStateSourceProvider = getSiteSelectionStateSourceProvider();
        Integer siteId = (Integer) siteSelectionStateSourceProvider
            .getCurrentState().get(SiteSelectionState.SITE_SELECTION_ID);
        setTextEnablement(siteId);

        siteStateListener = new ISourceProviderListener() {
            @Override
            public void sourceChanged(int sourcePriority, String sourceName,
                Object sourceValue) {
                if (sourceName.equals(SiteSelectionState.SITE_SELECTION_ID)) {
                    setTextEnablement((Integer) sourceValue);
                    getSite().getPage().closeAllEditors(true);
                    todayNode.removeAll();
                    searchedNode.removeAll();
                    if (sourceValue != null
                        && !SessionManager.getInstance().isAllSitesSelected()) {
                        reloadTodayNode();
                    }
                }
            }

            @SuppressWarnings("unchecked")
            @Override
            public void sourceChanged(int sourcePriority, Map sourceValuesByName) {
            }
        };

        siteSelectionStateSourceProvider
            .addSourceProviderListener(siteStateListener);
    }

    public void reloadTodayNode() {
        todayNode.performExpand();
        getTreeViewer().expandToLevel(3);
    }

    protected void setTextEnablement(Integer siteId) {
        treeText.setEnabled(siteId != null && siteId >= 0);
    }

    @Override
    public void dispose() {
        super.dispose();
        getSiteSelectionStateSourceProvider().removeSourceProviderListener(
            siteStateListener);
    }

    private ISourceProvider getSiteSelectionStateSourceProvider() {
        IWorkbenchWindow window = PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow();
        ISourceProviderService service = (ISourceProviderService) window
            .getService(ISourceProviderService.class);
        ISourceProvider siteSelectionStateSourceProvider = service
            .getSourceProvider(SiteSelectionState.SITE_SELECTION_ID);
        return siteSelectionStateSourceProvider;
    }

    @Override
    public void setFocus() {
        treeText.setFocus();
    }

    @Override
    public void opened() {
        reloadTodayNode();
    }

}
