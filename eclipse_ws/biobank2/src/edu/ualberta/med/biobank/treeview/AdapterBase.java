package edu.ualberta.med.biobank.treeview;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteAccessException;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.treeview.admin.ContainerAdapter;
import edu.ualberta.med.biobank.treeview.listeners.AdapterChangedEvent;
import edu.ualberta.med.biobank.treeview.listeners.AdapterChangedListener;
import edu.ualberta.med.biobank.treeview.util.DeltaEvent;
import edu.ualberta.med.biobank.treeview.util.IDeltaListener;
import edu.ualberta.med.biobank.treeview.util.NullDeltaListener;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

/**
 * Base class for all "Session" tree view nodes. Generally, most of the nodes in
 * the tree are adapters for classes in the ORM model.
 */
public abstract class AdapterBase {

    private static BgcLogger logger = BgcLogger.getLogger(AdapterBase.class
        .getName());

    protected static final String BGR_LOADING_LABEL = "loading...";

    protected IDeltaListener deltaListener = NullDeltaListener
        .getSoleInstance();

    protected ModelWrapper<?> modelObject;

    private Integer id;

    private String label;

    protected AdapterBase parent;

    protected boolean hasChildren;

    protected List<AdapterBase> children;

    /**
     * if true, edit button and actions will be visible
     */
    private boolean editable = true;

    private boolean loadChildrenInBackground;

    private Thread childUpdateThread;

    private Semaphore loadChildrenSemaphore;

    // FIXME can we merge this list of listeners with the DeltaListener ?
    private List<AdapterChangedListener> listeners;

    public AdapterBase(AdapterBase parent, ModelWrapper<?> object,
        boolean loadChildrenInBackground) {
        this.modelObject = object;
        this.parent = parent;
        this.loadChildrenInBackground = loadChildrenInBackground;
        loadChildrenSemaphore = new Semaphore(10, true);
        children = new ArrayList<AdapterBase>();
        if (parent != null) {
            addListener(parent.deltaListener);
        }
        listeners = new ArrayList<AdapterChangedListener>();
        Assert.isTrue(checkIntegrity(), "integrity checks failed");
    }

    public AdapterBase(AdapterBase parent, ModelWrapper<?> object) {
        this(parent, object, true);
    }

    public AdapterBase(AdapterBase parent, int id, String name,
        boolean hasChildren, boolean loadChildrenInBackground) {
        this(parent, null, loadChildrenInBackground);
        setId(id);
        setName(name);
        setHasChildren(hasChildren);
    }

    public ModelWrapper<?> getModelObject() {
        return modelObject;
    }

    public ModelWrapper<?> getModelObjectClone() throws Exception {
        return modelObject.getDatabaseClone();
    }

    /*
     * Used when updating tree nodes from a background thread.
     */
    protected void setModelObject(ModelWrapper<?> modelObject) {
        this.modelObject = modelObject;
    }

    /**
     * return true if the integrity of the object is ok
     */
    private boolean checkIntegrity() {
        if (modelObject != null) {
            return modelObject.checkIntegrity();
        }
        return true;
    }

    public void setParent(AdapterBase parent) {
        this.parent = parent;
    }

    public AdapterBase getParent() {
        return parent;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        if (modelObject != null) {
            return ((ModelWrapper<?>) modelObject).getId();
        }
        return id;
    }

    public void setName(String name) {
        this.label = name;
    }

    /**
     * Derived classes should not override this method. Instead they should
     * implement getNameInternal().
     * 
     * @return the name for the node.
     */
    public String getLabel() {
        if (modelObject != null) {
            return getLabelInternal();
        } else if (parent != null && parent.loadChildrenInBackground) {
            return BGR_LOADING_LABEL;
        }
        return label;
    }

    /**
     * Derived classses should implement this method instead of overriding
     * getName().
     * 
     * @return the name of the node. The name is the label displayed in the
     *         treeview.
     */
    protected abstract String getLabelInternal();

    /**
     * The string to display in the tooltip for the form.
     */
    public abstract String getTooltipText();

    protected String getTooltipText(String string) {
        String name = getLabel();
        if (name == null) {
            return new StringBuilder("New ").append(string).toString();
        }
        return new StringBuilder(string).append(" ").append(name).toString();
    }

    public List<AdapterBase> getItems() {
        return children;
    }

    public List<AdapterBase> getChildren() {
        return children;
    }

    public AdapterBase getChild(ModelWrapper<?> wrapper) {
        return getChild(wrapper, false);
    }

    public AdapterBase getChild(ModelWrapper<?> wrapper, boolean reloadChildren) {
        if (reloadChildren) {
            loadChildren(false);
        }
        if (children.size() == 0)
            return null;

        Class<?> wrapperClass = wrapper.getClass();
        Integer wrapperId = wrapper.getId();
        for (AdapterBase child : children) {
            ModelWrapper<?> childModelObject = child.getModelObject();
            if ((childModelObject != null)
                && childModelObject.getClass().equals(wrapperClass)
                && child.getId().equals(wrapperId))
                return child;
        }
        return null;
    }

    public AdapterBase getChild(int id) {
        return getChild(id, false);
    }

    public AdapterBase getChild(int id, boolean reloadChildren) {
        if (reloadChildren) {
            loadChildren(false);
        }
        if (children.size() == 0)
            return null;

        for (AdapterBase child : children) {
            if (child.getId().equals(id))
                return child;
        }
        return null;
    }

    public void addChild(AdapterBase child) {
        hasChildren = true;
        AdapterBase existingNode = contains(child);
        if (existingNode != null) {
            // don't add - assume our model is up to date
            return;
        }

        child.setParent(this);
        children.add(child);
        child.addListener(deltaListener);
        fireAdd(child);
    }

    public void insertAfter(AdapterBase existingNode, AdapterBase newNode) {
        int pos = children.indexOf(existingNode);
        Assert.isTrue(pos >= 0,
            "existing node not found: " + existingNode.getLabel());
        newNode.setParent(this);
        children.add(pos + 1, newNode);
        newNode.addListener(deltaListener);
        fireAdd(newNode);
    }

    public void removeChild(AdapterBase item) {
        removeChild(item, true);
    }

    public void removeChild(AdapterBase item, boolean closeForm) {
        if (children.size() == 0)
            return;
        AdapterBase itemToRemove = null;
        for (AdapterBase child : children) {
            if ((child.getId() == null && item.getId() == null)
                || (child.getId().equals(item.getId()) && child.getLabel()
                    .equals(item.getLabel())))
                itemToRemove = child;
        }
        if (itemToRemove != null) {
            if (closeForm) {
                closeEditor(new FormInput(itemToRemove));
            }
            children.remove(itemToRemove);
            fireRemove(itemToRemove);
        }
    }

    public void removeByName(String name) {
        if (children.size() == 0)
            return;
        AdapterBase itemToRemove = null;
        for (AdapterBase child : children) {
            if (child.getLabel().equals(name))
                itemToRemove = child;
        }
        if (itemToRemove != null) {
            children.remove(itemToRemove);
            fireRemove(itemToRemove);
        }
    }

    public void removeAll() {
        for (AdapterBase child : new ArrayList<AdapterBase>(getChildren())) {
            removeChild(child);
        }
        notifyListeners();
    }

    public AdapterBase contains(AdapterBase item) {
        if (children.size() == 0)
            return null;

        for (AdapterBase child : children) {
            if ((child.getId().equals(item.getId()))
                && child.getLabel().equals(item.getLabel()))
                return child;
        }
        return null;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public boolean hasChildren() {
        return hasChildren;
    }

    public WritableApplicationService getAppService() {
        if (modelObject != null) {
            return modelObject.getAppService();
        }
        return parent.getAppService();
    }

    public void addListener(IDeltaListener listener) {
        this.deltaListener = listener;
    }

    public void removeListener(IDeltaListener listener) {
        if (this.deltaListener.equals(listener)) {
            this.deltaListener = NullDeltaListener.getSoleInstance();
        }
    }

    protected void fireAdd(Object added) {
        deltaListener.add(new DeltaEvent(added));
    }

    protected void fireRemove(Object removed) {
        deltaListener.remove(new DeltaEvent(removed));
    }

    protected void executeDoubleClick() {
        openViewForm();
    }

    public void performDoubleClick() {
        executeDoubleClick();
    }

    public void performExpand() {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                loadChildren(true);
                RootNode root = getRootNode();
                if (root != null) {
                    root.expandChild(AdapterBase.this);
                }
            }
        });
    }

    /**
     * Called to load it's children;
     * 
     * @param updateNode If not null, the node in the treeview to update.
     */
    public void loadChildren(boolean updateNode) {
        try {
            loadChildrenSemaphore.acquire();
        } catch (InterruptedException e) {
            BgcPlugin.openAsyncError("Could not load children", e);
        }

        if (loadChildrenInBackground) {
            loadChildrenBackground(true);
            return;
        }

        try {
            Collection<? extends ModelWrapper<?>> children = getWrapperChildren();
            if (children != null) {
                for (ModelWrapper<?> child : children) {
                    AdapterBase node = getChild(child);
                    if (node == null) {
                        node = createChildNode(child);
                        addChild(node);
                    }
                    if (updateNode) {
                        SessionManager.updateAdapterTreeNode(node);
                    }
                }
                SessionManager.refreshTreeNode(AdapterBase.this);
            }
        } catch (final RemoteAccessException exp) {
            BgcPlugin.openRemoteAccessErrorMessage(exp);
        } catch (Exception e) {
            String text = getClass().getName();
            if (modelObject != null) {
                text = modelObject.toString();
            }
            logger.error("Error while loading children of node " + text, e);
        } finally {
            loadChildrenSemaphore.release();
        }
    }

    @SuppressWarnings("unused")
    public void loadChildrenBackground(final boolean updateNode) {
        if ((childUpdateThread != null) && childUpdateThread.isAlive()) {
            loadChildrenSemaphore.release();
            return;
        }

        try {
            int childCount = getWrapperChildCount();
            if (childCount == 0) {
                setHasChildren(false);
                loadChildrenSemaphore.release();
                return;
            }
            setHasChildren(true);
            final List<AdapterBase> newNodes = new ArrayList<AdapterBase>();
            for (int i = 0, n = childCount - children.size(); i < n; ++i) {
                final AdapterBase node = createChildNode(-i);
                addChild(node);
                newNodes.add(node);
            }

            childUpdateThread = new Thread() {
                @Override
                public void run() {
                    try {
                        Collection<? extends ModelWrapper<?>> childObjects = getWrapperChildren();
                        if (childObjects != null) {
                            for (ModelWrapper<?> child : childObjects) {
                                // first see if this object is among the
                                // children, if not then it is being loaded
                                // for the first time
                                AdapterBase node = getChild(child);
                                if (node == null) {
                                    Assert.isTrue(newNodes.size() > 0);
                                    node = newNodes.get(0);
                                    newNodes.remove(0);
                                }
                                Assert.isNotNull(node);
                                node.setModelObject(child);
                                final AdapterBase nodeToUpdate = node;
                                Display.getDefault().syncExec(new Runnable() {
                                    @Override
                                    public void run() {
                                        SessionManager
                                            .refreshTreeNode(nodeToUpdate);
                                    }
                                });
                            }
                            Display.getDefault().asyncExec(new Runnable() {
                                @Override
                                public void run() {
                                    SessionManager
                                        .refreshTreeNode(AdapterBase.this);
                                }
                            });
                        }
                    } catch (final RemoteAccessException exp) {
                        BgcPlugin.openRemoteAccessErrorMessage(exp);
                    } catch (Exception e) {
                        String modelString = "'unknown'";
                        if (modelObject != null) {
                            modelString = modelObject.toString();
                        }
                        logger.error("Error while loading children of node "
                            + modelString + " in background", e);
                    } finally {
                        loadChildrenSemaphore.release();
                    }
                }
            };
            childUpdateThread.start();
        } catch (Exception e) {
            String nodeString = "null";
            if (modelObject != null) {
                nodeString = modelObject.toString();
            }
            logger.error(
                "Error while expanding children of node " + nodeString, e);
            loadChildrenSemaphore.release();
        }
    }

    public abstract void popupMenu(TreeViewer tv, Tree tree, Menu menu);

    protected void addEditMenu(Menu menu, String objectName) {
        if (isEditable()) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText("Edit " + objectName);
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    AdapterBase.this.openEntryForm();
                }
            });
        }
    }

    protected void addViewMenu(Menu menu, String objectName) {
        MenuItem mi = new MenuItem(menu, SWT.PUSH);
        mi.setText("View " + objectName);
        mi.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                AdapterBase.this.openViewForm();
            }
        });
    }

    protected void addDeleteMenu(Menu menu, String objectName) {
        if (isDeletable()) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText("Delete " + objectName);
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    deleteWithConfirm();
                }
            });
        }
    }

    /**
     * Create a adequate child node for this node
     * 
     * @param child the child model object
     */
    protected abstract AdapterBase createChildNode();

    protected AdapterBase createChildNode(int id) {
        AdapterBase adapter = createChildNode();
        adapter.setId(id);
        return adapter;
    }

    /**
     * Create a adequate child node for this node
     * 
     * @param child the child model object
     */
    protected abstract AdapterBase createChildNode(ModelWrapper<?> child);

    /**
     * get the list of this model object children that this node should have as
     * children nodes.
     * 
     * @throws Exception
     */
    protected abstract Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception;

    protected abstract int getWrapperChildCount() throws Exception;

    public static boolean closeEditor(FormInput input) {
        IWorkbenchPage page = PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getActivePage();
        IEditorPart part = page.findEditor(input);
        if (part != null) {
            return page.closeEditor(part, true);
        }
        return false;
    }

    public static IEditorPart openForm(FormInput input, String id) {
        return openForm(input, id, true);
    }

    public static IEditorPart openForm(FormInput input, String id,
        boolean focusOnEditor) {
        closeEditor(input);
        try {
            IEditorPart part = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getActivePage()
                .openEditor(input, id, focusOnEditor);
            return part;
        } catch (PartInitException e) {
            logger.error("Can't open form with id " + id, e);
            return null;
        }

    }

    @SuppressWarnings("unchecked")
    public <E> E getParentFromClass(Class<E> parentClass) {
        AdapterBase node = this;
        while (node != null) {
            if (node.getClass().equals(parentClass)) {
                return (E) node;
            } else {
                node = node.getParent();
            }
        }
        return null;
    }

    public void openViewForm() {
        if (getViewFormId() != null && modelObject != null
            && modelObject.getWrappedObject() != null) {
            openForm(new FormInput(this), getViewFormId());
        }
    }

    public IEditorPart openEntryForm() {
        return openEntryForm(false);
    }

    public IEditorPart openEntryForm(boolean hasPreviousForm) {
        return openForm(new FormInput(this, hasPreviousForm), getEntryFormId());
    }

    public abstract String getViewFormId();

    public abstract String getEntryFormId();

    public List<AdapterBase> search(Object searchedObject) {
        if (modelObject != null && modelObject.equals(searchedObject))
            return Arrays.asList(this);
        return new ArrayList<AdapterBase>();
    }

    protected List<AdapterBase> searchChildren(Object searchedObject) {
        // FIXME children are loading in background most of the time:
        // they are not loaded then the objects are not found
        loadChildren(false);
        List<AdapterBase> result = new ArrayList<AdapterBase>();
        for (AdapterBase child : getChildren()) {
            List<AdapterBase> tmpRes = child.search(searchedObject);
            if (tmpRes.size() > 0)
                result.addAll(tmpRes);
        }
        return result;
    }

    protected List<AdapterBase> findChildFromClass(Object searchedObject,
        Class<?>... clazzList) {
        if (searchedObject != null) {
            for (Class<?> clazz : clazzList) {
                if (clazz.isAssignableFrom(searchedObject.getClass())) {
                    List<AdapterBase> res = new ArrayList<AdapterBase>();
                    AdapterBase child = null;
                    if (ModelWrapper.class.isAssignableFrom(clazz))
                        child = getChild((ModelWrapper<?>) searchedObject, true);
                    else if (Date.class.isAssignableFrom(clazz))
                        child = getChild((int) ((Date) searchedObject)
                            .getTime());
                    else if (Integer.class.isAssignableFrom(clazz))
                        child = getChild((Integer) searchedObject);
                    if (child != null) {
                        res.add(child);
                    }
                    return res;
                }
            }
        }
        return searchChildren(searchedObject);
    }

    public RootNode getRootNode() {
        return getParentFromClass(RootNode.class);
    }

    public void rebuild() {
        removeAll();
        loadChildren(false);
    }

    public void resetObject() throws Exception {
        if (modelObject != null) {
            modelObject.reset();
        }
    }

    public void deleteWithConfirm() {
        String msg = getConfirmDeleteMessage();
        if (msg == null) {
            throw new RuntimeException("adapter has no confirm delete msg: "
                + getClass().getName());
        }
        boolean doDelete = true;
        if (msg != null)
            doDelete = BgcPlugin.openConfirm("Confirm Delete", msg);
        if (doDelete) {
            BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
                @Override
                public void run() {
                    // the order is very important
                    if (modelObject != null) {
                        IWorkbenchPage page = PlatformUI.getWorkbench()
                            .getActiveWorkbenchWindow().getActivePage();
                        IEditorPart part = page.findEditor(new FormInput(
                            AdapterBase.this));
                        getParent().removeChild(AdapterBase.this, false);
                        try {
                            modelObject.delete();
                            page.closeEditor(part, true);
                        } catch (Exception e) {
                            BgcPlugin.openAsyncError("Delete failed", e);
                            getParent().addChild(AdapterBase.this);
                            return;
                        }
                        getParent().notifyListeners();
                        notifyListeners();
                        additionalRefreshAfterDelete();
                    }
                }
            });
        }
    }

    protected void additionalRefreshAfterDelete() {
        // default does nothing
    }

    public boolean isDeletable() {
        // should override it to activate deletion
        return false;
    }

    protected boolean internalIsDeletable() {
        return editable && modelObject != null
            && SessionManager.getInstance().isConnected()
            && modelObject.canDelete(SessionManager.getUser());
    }

    public boolean isEditable() {
        return editable && modelObject.canUpdate(SessionManager.getUser());
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public void setLoadChildrenInBackground(boolean loadChildrenInBackground) {
        this.loadChildrenInBackground = loadChildrenInBackground;
    }

    public void addChangedListener(AdapterChangedListener listener) {
        listeners.add(listener);
    }

    public void removeChangedListener(AdapterChangedListener listener) {
        listeners.remove(listener);
    }

    public void notifyListeners(AdapterChangedEvent event) {
        for (AdapterChangedListener listener : listeners) {
            listener.changed(event);
        }
    }

    public void notifyListeners() {
        notifyListeners(new AdapterChangedEvent(this));
    }

    protected String getConfirmDeleteMessage() {
        return null;
    }

    protected List<AdapterBase> searchChildContainers(Object searchedObject,
        ContainerAdapter container, final List<ContainerWrapper> parents) {
        List<AdapterBase> res = new ArrayList<AdapterBase>();
        if (parents.contains(container.getContainer())) {
            AdapterBase child = container.getChild(
                (ModelWrapper<?>) searchedObject, true);
            if (child == null) {
                for (AdapterBase childContainer : container.getChildren()) {
                    if (childContainer instanceof ContainerAdapter) {
                        res = searchChildContainers(searchedObject,
                            (ContainerAdapter) childContainer, parents);
                    } else {
                        res = childContainer.search(searchedObject);
                    }
                    if (res.size() > 0)
                        break;
                }
            } else {
                res.add(child);
            }
        }
        return res;
    }

}