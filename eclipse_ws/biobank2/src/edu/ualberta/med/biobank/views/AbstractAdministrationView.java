package edu.ualberta.med.biobank.views;

import java.util.Collection;
import java.util.Map;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.ISourceProvider;
import org.eclipse.ui.ISourceProviderListener;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.ISourceProviderService;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.sourceproviders.SiteSelectionState;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.NodeSearchVisitor;
import edu.ualberta.med.biobank.treeview.RootNode;
import edu.ualberta.med.biobank.widgets.AdapterTreeWidget;

public abstract class AbstractAdministrationView extends AbstractViewWithTree {

    protected Text treeText;

    private ISourceProviderListener siteStateListener;

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

        setSiteManagement();
    }

    protected void internalSearch() {
        getSite().getPage().closeAllEditors(true);
        String text = treeText.getText();
        try {
            Object searchedObject = search(text);
            if (searchedObject == null) {
                notFound(text);
            } else {
                showInTree(searchedObject);
            }
        } catch (Exception e) {
            BioBankPlugin.openError("Search error", e);
            notFound(text);
        }
    }

    protected abstract void showInTree(Object searchedObject);

    protected abstract void notFound(String text);

    protected abstract Object search(String text) throws Exception;

    private void setSiteManagement() {
        IWorkbenchWindow window = PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow();
        ISourceProviderService service = (ISourceProviderService) window
            .getService(ISourceProviderService.class);
        ISourceProvider siteSelectionStateSourceProvider = service
            .getSourceProvider(SiteSelectionState.SITE_SELECTION_ID);
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

    protected void setTextEnablement(Integer siteId) {
        treeText.setEnabled(siteId != null);
        rootNode.removeAll();
    }

    protected AdapterBase getNotFoundAdapter() {
        AdapterBase noPatientFoundAdapter = new AdapterBase(rootNode, 0,
            getNoFoundText()) {
            @Override
            public void executeDoubleClick() {
            }

            @Override
            public String getTitle() {
                return null;
            }

            @Override
            public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
            }

            @Override
            public AdapterBase accept(NodeSearchVisitor visitor) {
                return null;
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
        };
        return noPatientFoundAdapter;
    }

    protected abstract String getNoFoundText();

    @Override
    public void dispose() {
        super.dispose();
        IWorkbenchWindow window = PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow();
        ISourceProviderService service = (ISourceProviderService) window
            .getService(ISourceProviderService.class);
        ISourceProvider siteSelectionStateSourceProvider = service
            .getSourceProvider(SiteSelectionState.SITE_SELECTION_ID);
        siteSelectionStateSourceProvider
            .removeSourceProviderListener(siteStateListener);
    }

}
