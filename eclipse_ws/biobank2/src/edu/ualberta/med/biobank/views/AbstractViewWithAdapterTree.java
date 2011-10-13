package edu.ualberta.med.biobank.views;

import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISourceProviderListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.BgcSessionState;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.widgets.trees.AdapterTreeWidget;

public abstract class AbstractViewWithAdapterTree extends
    AbstractViewWithTree<AbstractAdapterBase> {

    protected AdapterTreeWidget adaptersTree;

    protected AbstractAdapterBase rootNode;

    private ISourceProviderListener sourceListener;

    private static BgcLogger logger = BgcLogger
        .getLogger(AbstractViewWithAdapterTree.class.getName());

    protected AbstractViewWithAdapterTree() {
        sourceListener = new ISourceProviderListener() {
            @Override
            public void sourceChanged(int sourcePriority, String sourceName,
                Object sourceValue) {
                if (sourceName
                    .equals(BgcSessionState.SESSION_STATE_SOURCE_NAME)) {
                    if (sourceValue != null) {
                        if (sourceValue.equals(BgcSessionState.LOGGED_IN))
                            reload();
                        else if (sourceValue.equals(BgcSessionState.LOGGED_OUT))
                            clear();
                    }
                }
            }

            @SuppressWarnings("rawtypes")
            @Override
            public void sourceChanged(int sourcePriority, Map sourceValuesByName) {
                //
            }
        };
        BgcPlugin.getSessionStateSourceProvider().addSourceProviderListener(
            sourceListener);
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
    public List<AbstractAdapterBase> searchNode(Class<?> searchedClass,
        Integer objectId) {
        return rootNode.search(searchedClass, objectId);
    }

    public abstract void reload();

    public void opened() {
        // default does nothing
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
                    logger
                        .error(
                            Messages.AbstractViewWithAdapterTree_view_activate_error_title,
                            pie);
                }
            }
        }
    }

    public void clear() {
        if (rootNode != null)
            rootNode.removeAll();
    }

    @Override
    public void createPartControl(Composite parent) {
        getSite().setSelectionProvider(getTreeViewer());
        getSite().getWorkbenchWindow().getSelectionService()
            .addSelectionListener(new ISelectionListener() {
                @Override
                public void selectionChanged(IWorkbenchPart part,
                    ISelection selection) {
                    if (part != AbstractViewWithAdapterTree.this
                        && selection instanceof IStructuredSelection) {
                        IStructuredSelection strucSel = (IStructuredSelection) selection;
                        if (!strucSel.isEmpty()) {
                            if (strucSel.getFirstElement() instanceof AbstractAdapterBase) {
                                // if a form is selected, the corresponding
                                // adapter is in strucSel
                                setSelectedNode((AbstractAdapterBase) strucSel
                                    .getFirstElement());
                            }
                        }
                    }
                }
            });
        createPartControlInternal(parent);
    }

    protected abstract void createPartControlInternal(Composite parent);

    @Override
    public void dispose() {
        if (sourceListener != null)
            BgcPlugin.getSessionStateSourceProvider()
                .removeSourceProviderListener(sourceListener);
        super.dispose();
    }
}
