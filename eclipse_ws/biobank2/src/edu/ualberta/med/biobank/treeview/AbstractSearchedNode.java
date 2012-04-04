package edu.ualberta.med.biobank.treeview;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    protected Set<Object> searchedObjects = new HashSet<Object>();
    protected Set<Integer> searchedObjectIds = new HashSet<Integer>();

    private boolean keepDirectLeafChild;

    public AbstractSearchedNode(AdapterBase parent, int id,
        boolean keepDirectLeafChild) {
        super(parent, id, "Searched", true);
        this.keepDirectLeafChild = keepDirectLeafChild;
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        MenuItem mi = new MenuItem(menu, SWT.PUSH);
        mi.setText("Clear");
        mi.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                clear();
            }
        });
    }

    @Override
    public void performExpand() {
        List<ModelWrapper<?>> alreadyHasListener =
            new ArrayList<ModelWrapper<?>>();
        try {
            for (AbstractAdapterBase child : getChildren()) {
                if (child instanceof AdapterBase) {
                    ModelWrapper<?> childWrapper = ((AdapterBase) child)
                        .getModelObject();
                    if (childWrapper != null) {
                        childWrapper.reload();
                    }
                }
                List<AbstractAdapterBase> subChildren =
                    new ArrayList<AbstractAdapterBase>(
                        child.getChildren());
                List<AbstractAdapterBase> toRemove =
                    new ArrayList<AbstractAdapterBase>();
                for (AbstractAdapterBase subChild : subChildren) {
                    ModelWrapper<?> wrapper = null;
                    if (subChild instanceof AdapterBase) {
                        Object subChildObj = ((AdapterBase) subChild)
                            .getModelObject();
                        if (subChildObj instanceof ModelWrapper) {
                            wrapper = (ModelWrapper<?>) subChildObj;
                            // wrapper.reload();
                            // FIXME: using reload here breaks a lot of stuff,
                            // why?
                        }
                    }
                    Integer subChildId = subChild.getId();
                    if (!searchedObjectIds.contains(subChildId)) {
                        toRemove.add(subChild);
                    } else {
                        // subChild.rebuild();
                        if (wrapper != null) {
                            alreadyHasListener.add(wrapper);
                        }
                    }
                }
                for (AbstractAdapterBase subChild : toRemove)
                    child.removeChild(subChild);
            }
            // add searched objects is not yet there
            for (final Object o : searchedObjects) {
                if (o instanceof ModelWrapper) {
                    ModelWrapper<?> w = (ModelWrapper<?>) o;
                    if (!alreadyHasListener.contains(w)) {
                        w.addWrapperListener(new WrapperListenerAdapter() {
                            @Override
                            public void deleted(WrapperEvent event) {
                                searchedObjects.remove(o);
                                performExpand();
                            }
                        });
                    }
                }
                addNode(o);
            }

            if (!keepDirectLeafChild) {
                // remove sub children without any children
                List<AbstractAdapterBase> children =
                    new ArrayList<AbstractAdapterBase>(
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
            logger.error("Error while refreshing searched elements", e); 
        }
    }

    protected abstract void addNode(Object obj);

    @Override
    protected void executeDoubleClick() {
        performExpand();
    }

    @Override
    protected List<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        return null;
    }

    @Override
    public String getViewFormId() {
        return null;
    }

    @Override
    public String getTooltipTextInternal() {
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

    public void addSearchObject(Object searchedObject, Integer id) {
        searchedObjects.add(searchedObject);
        searchedObjectIds.add(id);
    }

    protected abstract boolean isParentTo(Object parent, Object child);

    @Override
    public List<AbstractAdapterBase> search(Class<?> searchedClass,
        Integer objectId) {
        return searchChildren(searchedClass, objectId);
    }

    public void clear() {
        searchedObjects.clear();
        searchedObjectIds.clear();
        removeAll();
        performExpand();
    }

    public void removeObject(Object child, Integer childId) {
        searchedObjects.remove(child);
        searchedObjectIds.remove(childId);
    }
}
