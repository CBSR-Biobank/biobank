package edu.ualberta.med.biobank.views;

import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.ISourceProviderListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcSessionState;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.RootNode;
import edu.ualberta.med.biobank.widgets.trees.AdapterTreeWidget;

public abstract class AbstractViewWithAdapterTree extends
    AbstractViewWithTree<AdapterBase> {

    protected AdapterTreeWidget adaptersTree;

    protected RootNode rootNode;

    private static BgcLogger logger = BgcLogger
        .getLogger(AbstractViewWithAdapterTree.class.getName());

    protected AbstractViewWithAdapterTree() {
        BgcPlugin.getSessionStateSourceProvider()
            .addSourceProviderListener(new ISourceProviderListener() {
                @Override
                public void sourceChanged(int sourcePriority,
                    String sourceName, Object sourceValue) {
                    if (sourceName
                        .equals(BgcSessionState.SESSION_STATE_SOURCE_NAME)) {
                        if (sourceValue != null) {
                            if (sourceValue
                                .equals(BgcSessionState.LOGGED_IN))
                                reload();
                            else if (sourceValue
                                .equals(BgcSessionState.LOGGED_OUT))
                                clear();
                        }
                    }
                }

                @SuppressWarnings("rawtypes")
                @Override
                public void sourceChanged(int sourcePriority,
                    Map sourceValuesByName) {
                }
            });
    }

    @Override
    public TreeViewer getTreeViewer() {
        if (adaptersTree == null) {
            return null;
        }
        return adaptersTree.getTreeViewer();
    }

    @Override
    public void setFocus() {
        adaptersTree.setFocus();
    }

    @Override
    public List<AdapterBase> searchNode(ModelWrapper<?> wrapper) {
        return rootNode.search(wrapper);
    }

    public abstract void reload();

    public void opened() {

    }

    public abstract String getId();

    public void activate() {
        IWorkbenchWindow window = PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow();
        if (window != null) {
            IWorkbenchPage page = window.getActivePage();
            if (page != null) {
                try {
                    page.showView(getId());
                } catch (PartInitException pie) {
                    logger.error("Error activating the view", pie);
                }
            }
        }
    }

    public void clear() {
        if (rootNode != null)
            rootNode.removeAll();
    }

}
