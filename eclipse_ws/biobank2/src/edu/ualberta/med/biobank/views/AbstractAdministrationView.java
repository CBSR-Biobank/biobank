package edu.ualberta.med.biobank.views;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.ISourceProvider;
import org.eclipse.ui.ISourceProviderListener;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.ISourceProviderService;

import edu.ualberta.med.biobank.sourceproviders.SiteSelectionState;
import edu.ualberta.med.biobank.treeview.RootNode;
import edu.ualberta.med.biobank.widgets.AdapterTreeWidget;
import edu.ualberta.med.biobank.widgets.BiobankText;

public abstract class AbstractAdministrationView extends
    AbstractViewWithAdapterTree {

    protected BiobankText treeText;

    private ISourceProviderListener siteStateListener;

    protected Listener searchListener;

    @Override
    public void createPartControl(Composite parent) {
        parent.setLayout(new GridLayout(1, false));
        searchListener = new Listener() {
            @Override
            public void handleEvent(Event e) {
                internalSearch();
            }
        };

        createTreeTextOptions(parent);

        treeText = new BiobankText(parent, SWT.SINGLE);
        treeText.addListener(SWT.DefaultSelection, searchListener);
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        treeText.setLayoutData(gd);

        adaptersTree = new AdapterTreeWidget(parent, false);
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

    protected void createTreeTextOptions(
        @SuppressWarnings("unused") Composite parent) {
        // default do nothing
    }

    protected abstract void internalSearch();

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
                    siteChanged(sourceValue);
                }
            }

            @Override
            public void sourceChanged(int sourcePriority,
                @SuppressWarnings("rawtypes") Map sourceValuesByName) {
            }
        };

        siteSelectionStateSourceProvider
            .addSourceProviderListener(siteStateListener);
    }

    protected abstract void siteChanged(Object sourceValue);

    @Override
    public void reload() {
        getTreeViewer().refresh(true);
        getTreeViewer().expandToLevel(3);
    }

    protected void setTextEnablement(Integer siteId) {
        treeText.setEnabled(siteId != null && siteId >= 0);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (siteStateListener != null) {
            getSiteSelectionStateSourceProvider().removeSourceProviderListener(
                siteStateListener);
        }
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
        reload();
    }

}
