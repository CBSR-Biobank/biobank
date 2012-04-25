package edu.ualberta.med.biobank.treeview;

import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.common.util.StringUtil;

// FIXME see code commented below. Need to know what is still needed for the new adapters
public abstract class NewAbstractSearchedNode extends AbstractNewAdapterBase {

    public NewAbstractSearchedNode(AbstractAdapterBase parent, int id) {
        super(parent, id, StringUtil.EMPTY_STRING, null, false);
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        MenuItem mi = new MenuItem(menu, SWT.PUSH);
        mi.setText(StringUtil.EMPTY_STRING);
        mi.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                clear();
            }
        });
    }

    // @Override
    // public void performExpand() {
    // List<ModelWrapper<?>> alreadyHasListener = new
    // ArrayList<ModelWrapper<?>>();
    // try {
    // for (AbstractAdapterBase child : getChildren()) {
    // if (child instanceof AdapterBase) {
    // ModelWrapper<?> childWrapper = ((AdapterBase) child)
    // .getModelObject();
    // if (childWrapper != null) {
    // childWrapper.reload();
    // }
    // }
    // List<AbstractAdapterBase> subChildren = new
    // ArrayList<AbstractAdapterBase>(
    // child.getChildren());
    // List<AbstractAdapterBase> toRemove = new
    // ArrayList<AbstractAdapterBase>();
    // for (AbstractAdapterBase subChild : subChildren) {
    // ModelWrapper<?> wrapper = null;
    // if (subChild instanceof AdapterBase) {
    // Object subChildObj = ((AdapterBase) subChild)
    // .getModelObject();
    // if (subChildObj instanceof ModelWrapper) {
    // wrapper = (ModelWrapper<?>) subChildObj;
    // wrapper.reload();
    // }
    // }
    // Integer subChildId = subChild.getId();
    // if (!searchedObjectIds.contains(subChildId)) {
    // toRemove.add(subChild);
    // } else {
    // // subChild.rebuild();
    // if (wrapper != null) {
    // alreadyHasListener.add(wrapper);
    // }
    // }
    // }
    // for (AbstractAdapterBase subChild : toRemove)
    // child.removeChild(subChild);
    // }
    // // add searched objects is not yet there
    // for (final Object o : searchedObjects) {
    // if (o instanceof ModelWrapper) {
    // ModelWrapper<?> w = (ModelWrapper<?>) o;
    // if (!alreadyHasListener.contains(w)) {
    // w.addWrapperListener(new WrapperListenerAdapter() {
    // @Override
    // public void deleted(WrapperEvent event) {
    // searchedObjects.remove(o);
    // performExpand();
    // }
    // });
    // }
    // }
    // addNode(o);
    // }
    //
    // // if (!keepDirectLeafChild) {
    // // remove sub children without any children
    // List<AbstractAdapterBase> children = new ArrayList<AbstractAdapterBase>(
    // getChildren());
    // for (AbstractAdapterBase child : children) {
    // if (!(child instanceof DispatchAdapter)
    // && child.getChildren().size() == 0) {
    // removeChild(child);
    // }
    // }
    // // }
    // } catch (final RemoteAccessException exp) {
    // BgcPlugin.openRemoteAccessErrorMessage(exp);
    // } catch (Exception e) {
    //            logger.error("Error while refreshing searched elements", e); //$NON-NLS-1$
    // }
    // }

    @Override
    protected void executeDoubleClick() {
        performExpand();
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
    public String getEntryFormId() {
        return null;
    }

    @Override
    public List<AbstractAdapterBase> search(Class<?> searchedClass,
        Integer objectId) {
        return searchChildren(searchedClass, objectId);
    }

    public void clear() {
        removeAll();
    }
}
