package edu.ualberta.med.biobank.treeview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
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

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.treeview.listeners.AdapterChangedEvent;
import edu.ualberta.med.biobank.treeview.listeners.AdapterChangedListener;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

/**
 * Base class for all "Session" tree view nodes. Generally, most of the nodes in
 * the tree are adapters for classes in the ORM model.
 */
public abstract class AdapterBase {

    private static Logger LOGGER = Logger
        .getLogger(AdapterBase.class.getName());

    protected IDeltaListener deltaListener = NullDeltaListener
        .getSoleInstance();

    protected ModelWrapper<?> modelObject;

    private Integer id;

    private String name;

    protected AdapterBase parent;

    protected boolean hasChildren;

    protected List<AdapterBase> children;

    /**
     * if true, enable normal actions of this adapter
     */
    protected boolean enableActions = true;

    /**
     * if true, edit button and actions will be visible
     */
    private boolean editable = true;

    private boolean loadChildrenInBackground;

    private Thread childUpdateThread;

    // FIXME can we merge this list of listeners with the DeltaListener ?
    private List<AdapterChangedListener> listeners;

    public AdapterBase(AdapterBase parent, ModelWrapper<?> object,
        boolean enableActions, boolean loadChildrenInBackground) {
        this.modelObject = object;
        this.parent = parent;
        this.enableActions = enableActions;
        this.loadChildrenInBackground = loadChildrenInBackground;
        children = new ArrayList<AdapterBase>();
        if (parent != null) {
            addListener(parent.deltaListener);
        }
        listeners = new ArrayList<AdapterChangedListener>();
        Assert.isTrue(checkIntegrity(), "integrity checks failed");
    }

    public AdapterBase(AdapterBase parent, ModelWrapper<?> object) {
        this(parent, object, true, true);
    }

    public AdapterBase(AdapterBase parent, int id, String name) {
        this(parent, null);
        setId(id);
        setName(name);
    }

    public AdapterBase(AdapterBase parent, int id, String name,
        boolean hasChildren) {
        this(parent, id, name);
        setHasChildren(hasChildren);
    }

    public ModelWrapper<?> getModelObject() {
        return modelObject;
    }

    /**
     * Used when updating tree nodes from a background thread.
     * 
     * @param modelObject the object to be displayed by the tree node.
     */
    public void setModelObject(ModelWrapper<?> modelObject) {
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

    public void setId(int id) {
        this.id = id;
    }

    public Integer getId() {
        if (modelObject != null) {
            return ((ModelWrapper<?>) modelObject).getId();
        }
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract String getTitle();

    protected String getTitle(String string) {
        String name = getName();
        if (name == null) {
            return "New " + string;
        }
        return string + " " + name;
    }

    public List<AdapterBase> getItems() {
        return children;
    }

    public List<AdapterBase> getChildren() {
        return children;
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
            if (child.getId() == id)
                return child;
        }
        return null;
    }

    public boolean hasChild(int id) {
        for (AdapterBase child : children) {
            if (child.getId() == id)
                return true;
        }
        return false;
    }

    public void addChild(AdapterBase child) {
        System.out.println("adding child " + child.getName());
        hasChildren = true;
        AdapterBase existingNode = contains(child);
        if (existingNode != null) {
            // don't add - assume our model is up to date
            return;
        }

        String name = child.getName();
        if (!name.equals("loading...")) {
            AdapterBase namedChild = getChildByName(child.getName());
            if (namedChild != null) {
                // may have inserted a new object into database
                // replace current object with new one
                int index = children.indexOf(namedChild);
                children.remove(index);
            }
        }

        child.setParent(this);
        children.add(child);
        child.addListener(deltaListener);
        fireAdd(child);
    }

    public void insertAfter(AdapterBase existingNode, AdapterBase newNode) {
        int pos = children.indexOf(existingNode);
        Assert.isTrue(pos >= 0, "existing node not found: "
            + existingNode.getName());
        newNode.setParent(this);
        children.add(pos + 1, newNode);
        newNode.addListener(deltaListener);
        fireAdd(newNode);
    }

    public void removeChild(AdapterBase item) {
        if (children.size() == 0)
            return;
        AdapterBase itemToRemove = null;
        for (AdapterBase child : children) {
            if ((child.getId() == item.getId())
                && child.getName().equals(item.getName()))
                itemToRemove = child;
        }
        if (itemToRemove != null) {
            closeEditor(new FormInput(itemToRemove));
            children.remove(itemToRemove);
            fireRemove(itemToRemove);
        }
    }

    public void removeByName(String name) {
        if (children.size() == 0)
            return;
        AdapterBase itemToRemove = null;
        for (AdapterBase child : children) {
            if (child.getName().equals(name))
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
            if ((child.getId() == item.getId())
                && child.getName().equals(item.getName()))
                return child;
        }
        return null;
    }

    public AdapterBase getChildByName(String name) {
        if (children.size() == 0)
            return null;

        for (AdapterBase child : children) {
            if ((child.getName() != null) && child.getName().equals(name))
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
        Assert.isNotNull(parent, "parent is null");
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

    public abstract void executeDoubleClick();

    public void performDoubleClick() {
        if (!getName().equals("loading...")) {
            executeDoubleClick();
        }
    }

    public void performExpand() {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                if (loadChildrenInBackground) {
                    loadChildrenBackground(true);
                } else {
                    loadChildren(true);
                    getRootNode().expandChild(AdapterBase.this);
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
            Collection<? extends ModelWrapper<?>> children = getWrapperChildren();
            if (children != null) {
                for (ModelWrapper<?> child : children) {
                    AdapterBase node = getChild(child.getId());
                    if (node == null) {
                        node = createChildNode(child);
                        addChild(node);
                    }
                    if (updateNode) {
                        SessionManager.updateTreeNode(node);
                    }
                }
                notifyListeners();
            }
        } catch (final RemoteAccessException exp) {
            BioBankPlugin.openRemoteAccessErrorMessage();
        } catch (Exception e) {
            LOGGER.error("Error while loading children of node "
                + modelObject.toString(), e);
        }
    }

    public void loadChildrenBackground(final boolean updateNode) {
        if ((childUpdateThread != null) && childUpdateThread.isAlive())
            return;

        try {
            Collection<? extends ModelWrapper<?>> childObjects = getWrapperChildren();
            if (childObjects == null)
                return;
            for (int i = 0, n = childObjects.size() - children.size(); i < n; ++i) {
                final AdapterBase node = createChildNode(i);
                addChild(node);
                if (updateNode) {
                    SessionManager.updateTreeNode(node);
                }
                System.out.println("child stub added");
            }
            notifyListeners();
            getRootNode().expandChild(AdapterBase.this);

            childUpdateThread = new Thread() {
                @Override
                public void run() {
                    try {
                        System.out.println("child load thread started");
                        Collection<? extends ModelWrapper<?>> childObjects = getWrapperChildren();
                        if (childObjects != null) {
                            int count = 0;
                            int id = 0;
                            for (ModelWrapper<?> child : childObjects) {
                                // first see if this object is among the
                                // children, if not then it is being loaded
                                // for the first time
                                if (hasChild(child.getId())) {
                                    id = child.getId();
                                } else {
                                    id = count;
                                    count++;
                                }
                                final AdapterBase node = getChild(id);
                                Assert.isNotNull(node);
                                node.setModelObject(child);
                                System.out.println("child model object added");
                                Display.getDefault().asyncExec(new Runnable() {
                                    public void run() {
                                        SessionManager.refreshTreeNode(node);
                                    }
                                });
                            }
                            Display.getDefault().asyncExec(new Runnable() {
                                public void run() {
                                    SessionManager
                                        .refreshTreeNode(AdapterBase.this);
                                    notifyListeners();
                                }
                            });
                        }
                        System.out.println("child load thread finished");
                    } catch (final RemoteAccessException exp) {
                        BioBankPlugin.openRemoteAccessErrorMessage();
                    } catch (Exception e) {
                        LOGGER.error("Error while loading children of node "
                            + modelObject.toString() + " in background", e);
                        System.out.println(e);
                    }
                }
            };
            childUpdateThread.start();
        } catch (Exception e) {
            LOGGER.error("Error while expanding children of node "
                + modelObject.toString(), e);
        }
    }

    public abstract void popupMenu(TreeViewer tv, Tree tree, Menu menu);

    protected void addEditMenu(Menu menu, String objectName,
        final String editFormId) {
        if (isEditable() && enableActions) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText("Edit " + objectName);
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    openForm(new FormInput(AdapterBase.this), editFormId);
                }
            });
        }
    }

    protected void addViewMenu(Menu menu, String objectName,
        final String viewFormId) {
        if (enableActions) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText("View " + objectName);
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    openForm(new FormInput(AdapterBase.this), viewFormId);
                }
            });
        }
    }

    protected void addDeleteMenu(Menu menu, String objectName,
        final String question) {
        if (enableActions) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText("Delete " + objectName);
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    delete(question);
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

    public static void closeEditor(FormInput input) {
        IWorkbenchPage page = PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getActivePage();
        IEditorPart part = page.findEditor(input);
        if (part != null) {
            page.closeEditor(part, true);
        }
    }

    public static void openForm(FormInput input, String id) {
        closeEditor(input);
        try {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getActivePage().openEditor(input, id, true);
        } catch (PartInitException e) {
            LOGGER.error("Can't open form with id " + id, e);
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

    public abstract AdapterBase accept(NodeSearchVisitor visitor);

    public String getTreeText() {
        return getName();
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

    public void delete() {
        delete(null);
    }

    public void delete(String message) {
        boolean doDelete = true;
        if (message != null)
            doDelete = BioBankPlugin.openConfirm("Confirm Delete", message);
        if (doDelete) {
            BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
                @Override
                public void run() {
                    try {
                        if (modelObject != null) {
                            modelObject.delete();
                            getParent().removeChild(AdapterBase.this);
                            getParent().notifyListeners();
                            notifyListeners();
                        }
                    } catch (BiobankCheckException bce) {
                        BioBankPlugin.openAsyncError("Delete failed", bce);
                    } catch (Exception e) {
                        BioBankPlugin.openAsyncError("Delete failed", e);
                    }
                }
            });
        }
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
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

}
