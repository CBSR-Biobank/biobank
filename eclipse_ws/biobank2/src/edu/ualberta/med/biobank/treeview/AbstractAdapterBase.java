package edu.ualberta.med.biobank.treeview;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintViolationException;

import org.apache.commons.lang.StringUtils;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.web.bindery.event.shared.EventBus;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.forms.BiobankFormBase;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.treeview.listeners.AdapterChangedEvent;
import edu.ualberta.med.biobank.treeview.listeners.AdapterChangedListener;
import edu.ualberta.med.biobank.treeview.util.DeltaEvent;
import edu.ualberta.med.biobank.treeview.util.IDeltaListener;
import edu.ualberta.med.biobank.treeview.util.NullDeltaListener;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * Base class for all "Session" tree view nodes. Generally, most of the nodes in the tree are
 * adapters for classes in the ORM model.
 */
public abstract class AbstractAdapterBase implements
    Comparable<AbstractAdapterBase> {
    private static final I18n i18n = I18nFactory.getI18n(AbstractAdapterBase.class);

    private static Logger log = LoggerFactory.getLogger(AbstractAdapterBase.class.getName());

    @SuppressWarnings("nls")
    // dialog title.
    private static final String DELETE_FAILED = i18n.tr("Delete failed");

    private static BgcLogger LOGGER = BgcLogger
        .getLogger(AbstractAdapterBase.class.getName());

    private Integer id;

    private String label;

    private final String tooltip;

    protected AbstractAdapterBase parent;

    protected boolean hasChildren;

    protected List<AbstractAdapterBase> children;

    protected EventBus eventBus;

    // used when add or remove children. Initialised to a listener that does
    // nothing. See NodeContentProvider for an implementation
    //OHSDEV
    // Specimen tree view implementation - Make accessible from SpecimenTreeViewAdapter to create child
    protected IDeltaListener deltaListener = NullDeltaListener.getSoleInstance();

    // FIXME can we merge this list of listeners with the DeltaListener ?
    private final List<AdapterChangedListener> listeners;

    protected boolean isDeletable = false;

    protected boolean isEditable = false;

    protected boolean isReadable = false;

    @SuppressWarnings("nls")
    protected static boolean isAllowed(Permission p) {
        try {
            return SessionManager.getAppService().isAllowed(p);
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError(
                // dialog title.
                i18n.tr("Permission Error"),
                // dialog message.
                i18n.tr("Unable to retrieve user permissions"));
        }
        return false;
    }

    public AbstractAdapterBase(AbstractAdapterBase parent, Integer id,
        String label, String tooltip, boolean hasChildren) {
        this.parent = parent;
        children = new ArrayList<AbstractAdapterBase>();
        this.id = id;
        this.label = label;
        this.tooltip = tooltip;
        this.hasChildren = hasChildren;
        listeners = new ArrayList<AdapterChangedListener>();

        // TODO: this class should be injected, not inject itself. Worst case,
        // have some AdapterBase super-class with an EventBus that injects
        // itself upon instantiation?
        Injector injector = BiobankPlugin.getInjector();
        injector.injectMembers(this);
    }

    @Inject
    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void init() {
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

    @SuppressWarnings("nls")
    protected String getTooltipText(String string) {
        String label = getLabel();
        if (label == null) {

            String newObjectLabel = i18n.tr("New {0}", string);

            return newObjectLabel;
        }
        return new StringBuilder(string).append(" ").append(label).toString();
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

    @SuppressWarnings("nls")
    public void insertAfter(AbstractAdapterBase existingNode,
        AbstractAdapterBase newNode) {
        int pos = children.indexOf(existingNode);
        Assert.isTrue(pos >= 0,
            "existing node not found: " + existingNode.getLabel());
        newNode.setParent(this);
        children.add(pos + 1, newNode);
        newNode.addListener(deltaListener);
    }

    public void removeChild(AbstractAdapterBase item) {
        removeChild(item, true, true);
    }

    public void removeChild(AbstractAdapterBase item, boolean nodeOnly) {
        removeChild(item, true, nodeOnly);
    }

    public void removeChild(AbstractAdapterBase item, boolean closeForm,
        boolean nodeOnly) {
        if (children.size() == 0)
            return;
        AbstractAdapterBase itemToRemove = null;
        for (AbstractAdapterBase child : children) {
            if ((child.getId() == null && item.getId() == null)
                || ((child.getId() != null)
                    && child.getId().equals(item.getId())
                    && (child.getLabel() != null)
                    && child.getLabel().equals(item.getLabel())))
                itemToRemove = child;
        }
        if (itemToRemove != null) {
            if (closeForm) {
                closeEditor(new FormInput(itemToRemove));
            }
            children.remove(itemToRemove);
            fireRemove(itemToRemove);
        }

        if (!nodeOnly)
            // node might need to remove completely the information from inside
            // (not only node child)
            removeChildInternal(itemToRemove.getId());
    }

    @SuppressWarnings("unused")
    protected void removeChildInternal(Integer id) {
        // do mothing by default
    }

    public void removeAll() {
        for (AbstractAdapterBase child : new ArrayList<AbstractAdapterBase>(
            getChildren())) {
            removeChild(child);
        }
        this.hasChildren = false;
    }

    public AbstractAdapterBase contains(AbstractAdapterBase item) {
        if (children.size() == 0)
            return null;

        for (AbstractAdapterBase child : children) {
            if ((child.getId() != null && child.getId().equals(item.getId()))
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
        if (isReadable()) executeDoubleClick();
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

            @SuppressWarnings("nls")
            String editObjectLabel = i18n.tr("Edit {0}", objectName);

            mi.setText(editObjectLabel);
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    AbstractAdapterBase.this.openEntryForm();
                }
            });
        }
    }

    protected void addViewMenu(Menu menu, String objectName) {
        if (isReadable()) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);

            @SuppressWarnings("nls")
            String viewObjectLabel = i18n.tr("View {0}", objectName);

            mi.setText(viewObjectLabel);
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    AbstractAdapterBase.this.openViewForm();
                }
            });
        }
    }

    protected void addDeleteMenu(Menu menu, String objectName) {
        if (isDeletable()) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);

            @SuppressWarnings("nls")
            String deleteObjectLabel = i18n.tr("Delete {0}", objectName);

            mi.setText(deleteObjectLabel);
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    deleteWithConfirm();
                }
            });
        }
    }

    @SuppressWarnings("nls")
    public void deleteWithConfirm() {
        String msg = getConfirmDeleteMessage();
        if (msg == null) {
            throw new RuntimeException("adapter has no confirm delete msg: "
                + getClass().getName());
        }
        boolean doDelete = true;
        doDelete = BgcPlugin.openConfirm(
            // dialog
            i18n.tr("Confirm Delete"), msg);
        if (doDelete) {
            BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
                @Override
                public void run() {
                    if (getId() == null) return;

                    // the order is very important
                    IWorkbenchPage page = PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow().getActivePage();
                    IEditorPart part = page.findEditor(
                        new FormInput(AbstractAdapterBase.this));
                    getParent().removeChild(AbstractAdapterBase.this,
                        false, false);
                    try {
                        runDelete();
                        page.closeEditor(part, true);
                    } catch (ApplicationException e) {
                        if (e.getCause() instanceof javax.validation.ConstraintViolationException) {
                            List<String> msgs = BiobankFormBase
                                .getConstraintViolationsMsgs(
                                (ConstraintViolationException) e.getCause());
                            BgcPlugin.openAsyncError(
                                DELETE_FAILED,
                                StringUtils.join(msgs, "\n"));
                        } else if (e.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
                            BgcPlugin.openAsyncError(
                                DELETE_FAILED,
                                // dialog message.
                                i18n.tr("delete not allowed"));
                        } else {
                            BgcPlugin.openAsyncError(
                                DELETE_FAILED,
                                e.getLocalizedMessage());
                        }
                    } catch (Exception e) {
                        BgcPlugin.openAsyncError(
                            DELETE_FAILED, e);
                        getParent().addChild(AbstractAdapterBase.this);
                        return;
                    }
                    getParent().rebuild();
                    getParent().notifyListeners();
                    notifyListeners();
                    additionalRefreshAfterDelete();
                }
            });
        }
    }

    @SuppressWarnings("nls")
    protected void runDelete() throws Exception {
        BgcPlugin
            .openAsyncError(
                i18n.tr("Programming Error"),
                i18n.tr("This adapter is missing its implementation for runDelete()"));
    }

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
     * get the list of this model object children that this node should have as children nodes.
     * 
     * @throws Exception
     */
    protected abstract Map<Integer, ?> getChildrenObjects() throws Exception;

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

    @SuppressWarnings("nls")
    public static IEditorPart openForm(FormInput input, String id,
        boolean focusOnEditor) {
        closeEditor(input);
        try {
            IEditorPart part = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getActivePage()
                .openEditor(input, id, focusOnEditor);
            return part;
        } catch (PartInitException e) {
            LOGGER.error("Can't open form with id " + id, e);
            return null;
        }

    }

    @SuppressWarnings("unchecked")
    public <E> E getParentFromClass(Class<E> parentClass) {
        AbstractAdapterBase node = this;
        while (node != null) {
            if (node.getClass().equals(parentClass)) {
                return (E) node;
            }
            node = node.getParent();
        }
        return null;
    }

    public void openViewForm() {
        if (getViewFormId() != null && isReadable) {
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

    @SuppressWarnings("nls")
    protected List<AbstractAdapterBase> searchChildren(Class<?> searchedClass, Integer objectId) {
        log.trace("searchChildren: class:{}, id:{}", searchedClass.getName(), objectId);
        loadChildren(false);
        List<AbstractAdapterBase> result = new ArrayList<AbstractAdapterBase>();
        for (AbstractAdapterBase child : getChildren()) {
            List<AbstractAdapterBase> tmpRes = child.search(searchedClass, objectId);
            //OHSDEV checking for null first
            if (tmpRes!=null && !tmpRes.isEmpty()) {
                result.addAll(tmpRes);
            }
        }
        return result;
    }

    protected List<AbstractAdapterBase> findChildFromClass(
        Class<?> searchedClass, Integer objectId, Class<?>... clazzList) {
        for (Class<?> clazz : clazzList) {
            if (clazz.isAssignableFrom(searchedClass)) {
                List<AbstractAdapterBase> res =
                    new ArrayList<AbstractAdapterBase>();
                AbstractAdapterBase child = getChild(objectId, true);
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
        return isDeletable;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public boolean isReadable() {
        return isReadable;
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