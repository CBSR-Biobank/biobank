package edu.ualberta.med.biobank.treeview;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.treeview.listeners.AdapterChangedEvent;
import edu.ualberta.med.biobank.treeview.listeners.AdapterChangedListener;
import edu.ualberta.med.biobank.treeview.util.DeltaEvent;
import edu.ualberta.med.biobank.treeview.util.IDeltaListener;
import edu.ualberta.med.biobank.treeview.util.NullDeltaListener;

/**
 * Base class for all "Session" tree view nodes. Generally, most of the nodes in
 * the tree are adapters for classes in the ORM model.
 */
public abstract class AbstractAdapterBase implements
    Comparable<AbstractAdapterBase> {

    private static BgcLogger logger = BgcLogger
        .getLogger(AbstractAdapterBase.class.getName());

    private Integer id;

    private String label;

    private String tooltip;

    protected AbstractAdapterBase parent;

    protected boolean hasChildren;

    protected List<AbstractAdapterBase> children;

    // used when add or remove children. Initialised to a listener that does
    // nothing. See NodeContentProvider for an implementation
    private IDeltaListener deltaListener = NullDeltaListener.getSoleInstance();

    // FIXME can we merge this list of listeners with the DeltaListener ?
    private List<AdapterChangedListener> listeners;

    /**
     * if true, edit button and actions will be visible
     */
    private boolean editable = true;

    public AbstractAdapterBase(AbstractAdapterBase parent, Integer id,
        String label, String tooltip, boolean hasChildren) {
        this.parent = parent;
        children = new ArrayList<AbstractAdapterBase>();
        this.id = id;
        this.label = label;
        this.tooltip = tooltip;
        this.hasChildren = hasChildren;
        listeners = new ArrayList<AdapterChangedListener>();
        init();
    }

    protected void init() {
        // TODO Auto-generated method stub
    }

    public void setParent(AbstractAdapterBase parent) {
        this.parent = parent;
    }

    public AbstractAdapterBase getParent() {
        return parent;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return the name for the node.
     */
    public String getLabel() {
        return label;
    }

    /**
     * The string to display in the tooltip for the form.
     */
    public final String getTooltipText() {
        if (tooltip == null)
            return getTooltipTextInternal();
        return tooltip;
    }

    public abstract String getTooltipTextInternal();

    protected String getTooltipText(String string) {
        String label = getLabel();
        if (label == null) {
            return new StringBuilder(Messages.AdapterBase_new_label).append(
                string).toString();
        }
        return new StringBuilder(string).append(" ").append(label).toString(); //$NON-NLS-1$
    }

    public List<AbstractAdapterBase> getChildren() {
        return children;
    }

    public AbstractAdapterBase getChild(Integer id) {
        return getChild(id, false);
    }

    public AbstractAdapterBase getChild(Integer id, boolean reloadChildren) {
        if (reloadChildren) {
            loadChildren(false);
        }
        if (children.size() == 0)
            return null;

        for (AbstractAdapterBase child : children) {
            if (child.getId().equals(id))
                return child;
        }
        return null;
    }

    public void addChild(AbstractAdapterBase child) {
        hasChildren = true;
        AbstractAdapterBase existingNode = contains(child);
        if (existingNode != null) {
            // don't add - assume our model is up to date
            return;
        }

        child.setParent(this);
        children.add(child);
        child.addListener(deltaListener);
    }

    public void insertAfter(AbstractAdapterBase existingNode,
        AbstractAdapterBase newNode) {
        int pos = children.indexOf(existingNode);
        Assert.isTrue(pos >= 0,
            "existing node not found: " + existingNode.getLabel()); //$NON-NLS-1$
        newNode.setParent(this);
        children.add(pos + 1, newNode);
        newNode.addListener(deltaListener);
    }

    public void removeChild(AbstractAdapterBase item) {
        removeChild(item, true);
    }

    public void removeChild(AbstractAdapterBase item, boolean closeForm) {
        if (children.size() == 0)
            return;
        AbstractAdapterBase itemToRemove = null;
        for (AbstractAdapterBase child : children) {
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
        }
    }

    public void removeAll() {
        for (AbstractAdapterBase child : new ArrayList<AbstractAdapterBase>(
            getChildren())) {
            removeChild(child);
        }
    }

    public AbstractAdapterBase contains(AbstractAdapterBase item) {
        if (children.size() == 0)
            return null;

        for (AbstractAdapterBase child : children) {
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

    protected void executeDoubleClick() {
        openViewForm();
    }

    public void performDoubleClick() {
        executeDoubleClick();
    }

    public abstract void performExpand();

    /**
     * Called to load it's children;
     * 
     * @param updateNode If not null, the node in the treeview to update.
     */
    public abstract void loadChildren(boolean updateNode);

    public abstract void popupMenu(TreeViewer tv, Tree tree, Menu menu);

    protected void addEditMenu(Menu menu, String objectName) {
        if (isEditable()) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText(Messages.AdapterBase_edit_label + objectName);
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    AbstractAdapterBase.this.openEntryForm();
                }
            });
        }
    }

    protected void addViewMenu(Menu menu, String objectName) {
        MenuItem mi = new MenuItem(menu, SWT.PUSH);
        mi.setText(Messages.AdapterBase_view_label + objectName);
        mi.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                AbstractAdapterBase.this.openViewForm();
            }
        });
    }

    protected void addDeleteMenu(Menu menu, String objectName) {
        if (isDeletable()) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText(Messages.AdapterBase_delete_label + objectName);
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    deleteWithConfirm();
                }
            });
        }
    }

    protected abstract void deleteWithConfirm();

    /**
     * Create a adequate child node for this node
     * 
     * @param child the child model object
     */
    protected abstract AbstractAdapterBase createChildNode();

    protected AbstractAdapterBase createChildNode(int id) {
        AbstractAdapterBase adapter = createChildNode();
        adapter.setId(id);
        return adapter;
    }

    /**
     * Create a adequate child node for this node
     * 
     * @param child the child model object
     */
    protected abstract AbstractAdapterBase createChildNode(Object child);

    /**
     * get the list of this model object children that this node should have as
     * children nodes.
     * 
     * @throws Exception
     */
    protected abstract Map<Integer, ?> getChildrenObjects() throws Exception;

    protected abstract int getChildrenCount() throws Exception;

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
            logger.error("Can't open form with id " + id, e); //$NON-NLS-1$
            return null;
        }

    }

    @SuppressWarnings("unchecked")
    public <E> E getParentFromClass(Class<E> parentClass) {
        AbstractAdapterBase node = this;
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
        if (getViewFormId() != null) {
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

    public abstract List<AbstractAdapterBase> search(Class<?> searchedClass,
        Integer objectId);

    protected List<AbstractAdapterBase> searchChildren(Class<?> searchedClass,
        Integer objectId) {
        // FIXME children are loading in background most of the time:
        // they are not loaded then the objects are not found
        loadChildren(false);
        List<AbstractAdapterBase> result = new ArrayList<AbstractAdapterBase>();
        for (AbstractAdapterBase child : getChildren()) {
            List<AbstractAdapterBase> tmpRes = child.search(searchedClass,
                objectId);
            if (tmpRes.size() > 0)
                result.addAll(tmpRes);
        }
        return result;
    }

    protected List<AbstractAdapterBase> findChildFromClass(
        Class<?> searchedClass, Integer objectId, Class<?>... clazzList) {
        for (Class<?> clazz : clazzList) {
            if (clazz.isAssignableFrom(searchedClass)) {
                List<AbstractAdapterBase> res = new ArrayList<AbstractAdapterBase>();
                AbstractAdapterBase child = null;
                // if (Date.class.isAssignableFrom(clazz))
                // child = getChild((int) ((Date) searchedObject).getTime());
                // else if (Integer.class.isAssignableFrom(clazz))
                // child = getChild(((Integer) searchedObject).intValue());
                // else
                child = getChild(objectId, true);
                if (child != null) {
                    res.add(child);
                }
                return res;
            }
        }
        return searchChildren(searchedClass, objectId);
    }

    public void rebuild() {
        removeAll();
        loadChildren(false);
    }

    protected void additionalRefreshAfterDelete() {
        // default does nothing
    }

    public boolean isDeletable() {
        // should override it to activate deletion
        return false;
    }

    protected boolean internalIsDeletable() {
        return editable && SessionManager.getInstance().isConnected();
    }

    public boolean isEditable() {
        return editable && SessionManager.getInstance().isConnected();
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    protected String getConfirmDeleteMessage() {
        return null;
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

    /**
     * Used when searching inside the tree
     */
    @Override
    public boolean equals(Object o) {
        boolean same = this == o;
        if (!same && o instanceof AbstractAdapterBase) {
            Class<?> class1 = getClass();
            Class<?> class2 = o.getClass();
            if (class1.equals(class2)) {
                Integer id1 = getId();
                Integer id2 = ((AbstractAdapterBase) o).getId();
                return id1 != null && id2 != null && id1.equals(id2);
            }
        }
        return same;
    }

    /**
     * Used when searching inside the tree
     */
    @Override
    public int hashCode() {
        if (id != null)
            return id.hashCode();
        return super.hashCode();
    }

    public abstract void setValue(Object value);

}