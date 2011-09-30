package edu.ualberta.med.biobank.treeview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.springframework.remoting.RemoteAccessException;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.listener.WrapperEvent;
import edu.ualberta.med.biobank.common.wrappers.listener.WrapperListenerAdapter;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.treeview.dispatch.DispatchAdapter;

public abstract class AbstractSearchedNode extends AdapterBase {

    private static BgcLogger logger = BgcLogger
        .getLogger(AbstractSearchedNode.class.getName());

    protected List<ModelWrapper<?>> searchedObjects = new ArrayList<ModelWrapper<?>>();

    private boolean keepDirectLeafChild;

    public AbstractSearchedNode(AdapterBase parent, int id,
        boolean keepDirectLeafChild) {
        super(parent, id, Messages.AbstractSearchedNode_searched, true, false);
        this.keepDirectLeafChild = keepDirectLeafChild;
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        MenuItem mi = new MenuItem(menu, SWT.PUSH);
        mi.setText(Messages.AbstractSearchedNode_clear);
        mi.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                clear();
            }
        });
    }

    @Override
    public void performExpand() {
        List<ModelWrapper<?>> alreadyHasListener = new ArrayList<ModelWrapper<?>>();
        try {
            for (AbstractAdapterBase child : getChildren()) {
                ModelWrapper<?> childWrapper = ((AdapterBase) child)
                    .getModelObject();
                if (childWrapper != null) {
                    childWrapper.reload();
                }
                List<AbstractAdapterBase> subChildren = new ArrayList<AbstractAdapterBase>(
                    child.getChildren());
                List<AbstractAdapterBase> toRemove = new ArrayList<AbstractAdapterBase>();
                for (AbstractAdapterBase subChild : subChildren) {
                    ModelWrapper<?> subChildWrapper = ((AdapterBase) subChild)
                        .getModelObject();
                    subChildWrapper.reload();
                    if (!searchedObjects.contains(subChildWrapper)) {
                        toRemove.add(subChild);
                    } else {
                        subChild.rebuild();
                        alreadyHasListener.add(subChildWrapper);
                    }
                }
                for (AbstractAdapterBase subChild : toRemove)
                    child.removeChild(subChild);
            }
            // add searched objects is not yet there
            for (final ModelWrapper<?> wrapper : searchedObjects) {
                if (!alreadyHasListener.contains(wrapper)) {
                    wrapper.addWrapperListener(new WrapperListenerAdapter() {
                        @Override
                        public void deleted(WrapperEvent event) {
                            searchedObjects.remove(wrapper);
                            performExpand();
                        }
                    });
                }
                addNode(wrapper);
            }

            if (!keepDirectLeafChild) {
                // remove sub children without any children
                List<AbstractAdapterBase> children = new ArrayList<AbstractAdapterBase>(
                    getChildren());
                for (AbstractAdapterBase child : children) {
                    if (!(child instanceof DispatchAdapter)
                        && child.getChildren().size() == 0) {
                        removeChild(child);
                    }
                }
            }
        } catch (final RemoteAccessException exp) {
            BgcPlugin.openRemoteAccessErrorMessage(exp);
        } catch (Exception e) {
            logger.error("Error while refreshing searched elements", e); //$NON-NLS-1$
        }
    }

    protected abstract void addNode(ModelWrapper<?> wrapper);

    @Override
    protected void executeDoubleClick() {
        performExpand();
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        return null;
    }

    @Override
    protected int getWrapperChildCount() throws Exception {
        return 0;
    }

    @Override
    public String getViewFormId() {
        return null;
    }

    @Override
    public String getTooltipText() {
        return null;
    }

    @Override
    protected String getLabelInternal() {
        return null;
    }

    @Override
    public String getEntryFormId() {
        return null;
    }

    public void addSearchObject(ModelWrapper<?> searchedObject) {
        searchedObjects.add(searchedObject);
    }

    protected abstract boolean isParentTo(ModelWrapper<?> parent,
        ModelWrapper<?> child);

    @Override
    public List<AbstractAdapterBase> search(Object searchedObject) {
        return searchChildren(searchedObject);
    }

    public void clear() {
        searchedObjects.clear();
        removeAll();
    }

    public void removeObjects(List<? extends ModelWrapper<?>> children) {
        searchedObjects.removeAll(children);
    }
}
