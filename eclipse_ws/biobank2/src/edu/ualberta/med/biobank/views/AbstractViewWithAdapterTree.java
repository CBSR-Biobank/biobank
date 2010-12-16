package edu.ualberta.med.biobank.views;

import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.RootNode;
import edu.ualberta.med.biobank.widgets.AdapterTreeWidget;

public abstract class AbstractViewWithAdapterTree extends
    AbstractViewWithTree<AdapterBase> {

    protected AdapterTreeWidget adaptersTree;

    protected RootNode rootNode;

    private static BiobankLogger logger = BiobankLogger
        .getLogger(AbstractViewWithAdapterTree.class.getName());

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
}
