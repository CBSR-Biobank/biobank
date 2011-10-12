package edu.ualberta.med.biobank.treeview;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
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
import gov.nih.nci.system.applicationservice.WritableApplicationService;

/**
 * Base class for all "Session" tree view nodes. Generally, most of the nodes in
 * the tree are adapters for classes in the ORM model.
 */
public abstract class AdapterBase extends AbstractAdapterBase {

    private static BgcLogger logger = BgcLogger.getLogger(AdapterBase.class
        .getName());

    protected static final String BGR_LOADING_LABEL = Messages.AdapterBase_loading;

    private boolean loadChildrenInBackground;

    private Thread childUpdateThread;

    private Semaphore loadChildrenSemaphore;

    private Object modelObject;

    public AdapterBase(AdapterBase parent, ModelWrapper<?> object,
        boolean loadChildrenInBackground) {
        super(parent, object == null ? null : object.getId(), null, null, false);
        this.modelObject = object;
        this.loadChildrenInBackground = loadChildrenInBackground;
    }

    public AdapterBase(AdapterBase parent, ModelWrapper<?> object) {
        this(parent, object, true);
    }

    public AdapterBase(AdapterBase parent, Integer id, String label,
        boolean hasChildren, boolean loadChildrenInBackground) {
        super(parent, id, label, null, hasChildren);
        this.loadChildrenInBackground = loadChildrenInBackground;
    }

    @Override
    protected void init() {
        loadChildrenSemaphore = new Semaphore(10, true);
    }

    public ModelWrapper<?> getModelObject() {
        return (ModelWrapper<?>) modelObject;
    }

    public ModelWrapper<?> getModelObjectClone() throws Exception {
        return getModelObject().getDatabaseClone();
    }

    public void setParent(AdapterBase parent) {
        this.parent = parent;
    }

    @Override
    public Integer getId() {
        if (getModelObject() != null) {
            return getModelObject().getId();
        }
        return super.getId();
    }

    /**
     * Derived classes should not override this method. Instead they should
     * implement getNameInternal().
     * 
     * @return the name for the node.
     */
    @Override
    public final String getLabel() {
        if (getModelObject() != null) {
            return getLabelInternal();
        } else if (parent != null
            && ((AdapterBase) parent).loadChildrenInBackground) {
            return BGR_LOADING_LABEL;
        }
        return super.getLabel();
    }

    /**
     * Derived classses should implement this method instead of overriding
     * getName().
     * 
     * @return the name of the node. The name is the label displayed in the
     *         treeview.
     */
    protected abstract String getLabelInternal();

    @Override
    public AdapterBase getParent() {
        return (AdapterBase) parent;
    }

    /*
     * Used when updating tree nodes from a background thread.
     */
    protected void setModelObject(Object modelObject) {
        this.modelObject = modelObject;
    }

    @Override
    public void addChild(AbstractAdapterBase child) {
        super.addChild(child);
        fireAdd(child);
    }

    @Override
    public void insertAfter(AbstractAdapterBase existingNode,
        AbstractAdapterBase newNode) {
        super.insertAfter(existingNode, newNode);
        fireAdd(newNode);
    }

    @Override
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
            // override because of fireRemove
            fireRemove(itemToRemove);
        }
    }

    @Override
    public void removeAll() {
        super.removeAll();
        notifyListeners();
    }

    @Deprecated
    public WritableApplicationService getAppService() {
        if (getModelObject() != null) {
            return getModelObject().getAppService();
        }
        if (parent != null)
            return ((AdapterBase) parent).getAppService();
        return null;
    }

    /**
     * Called to load it's children;
     * 
     * @param updateNode If not null, the node in the treeview to update.
     */
    @Override
    public void loadChildren(boolean updateNode) {
        try {
            loadChildrenSemaphore.acquire();
        } catch (InterruptedException e) {
            BgcPlugin.openAsyncError(Messages.AdapterBase_load_error_title, e);
        }

        if (loadChildrenInBackground) {
            loadChildrenBackground(true);
            return;
        }

        try {
            Collection<? extends ModelWrapper<?>> children = getWrapperChildren();
            if (children != null) {
                for (ModelWrapper<?> child : children) {
                    AbstractAdapterBase node = getChild(child.getId());
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
            if (getModelObject() != null) {
                text = getModelObject().toString();
            }
            logger.error("Error while loading children of node " + text, e); //$NON-NLS-1$
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
            } else
                setHasChildren(true);
            final List<AbstractAdapterBase> newNodes = new ArrayList<AbstractAdapterBase>();
            for (int i = 0, n = childCount - children.size(); i < n; ++i) {
                final AbstractAdapterBase node = createChildNode(-i);
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
                                AbstractAdapterBase node = getChild(child
                                    .getId());
                                if (node == null) {
                                    Assert.isTrue(newNodes.size() > 0);
                                    node = newNodes.get(0);
                                    newNodes.remove(0);
                                }
                                Assert.isNotNull(node);
                                ((AdapterBase) node).setModelObject(child);
                                final AbstractAdapterBase nodeToUpdate = node;
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
                        String modelString = Messages.AdapterBase_unknow;
                        if (getModelObject() != null) {
                            modelString = getModelObject().toString();
                        }
                        logger.error("Error while loading children of node " //$NON-NLS-1$
                            + modelString + " in background", e); //$NON-NLS-1$
                    } finally {
                        loadChildrenSemaphore.release();
                    }
                }
            };
            childUpdateThread.start();
        } catch (Exception e) {
            String nodeString = "null"; //$NON-NLS-1$
            if (getModelObject() != null) {
                nodeString = getModelObject().toString();
            }
            logger.error(
                "Error while expanding children of node " + nodeString, e); //$NON-NLS-1$
            loadChildrenSemaphore.release();
        }
    }

    /**
     * get the list of this model object children that this node should have as
     * children nodes.
     * 
     * @throws Exception
     */
    protected abstract List<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception;

    @Override
    protected Map<Integer, ?> getChildrenObjects() throws Exception {
        Map<Integer, Object> map = new HashMap<Integer, Object>();
        for (ModelWrapper<?> model : getWrapperChildren()) {
            map.put(model.getId(), model);
        }
        return map;
    }

    protected abstract int getWrapperChildCount() throws Exception;

    @Override
    protected int getChildrenCount() throws Exception {
        return getWrapperChildCount();
    }

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

    @Override
    public void openViewForm() {
        if (getViewFormId() != null && getModelObject() != null
            && getModelObject().getWrappedObject() != null) {
            openForm(new FormInput(this), getViewFormId());
        }
    }

    public Class<?> getObjectClazz() {
        if (modelObject != null)
            return modelObject.getClass();
        return null;
    }

    @Override
    public List<AbstractAdapterBase> search(Class<?> searchedClass,
        Integer objectId) {
        if (getObjectClazz() != null && getObjectClazz().equals(searchedClass))
            return Arrays.asList(new AbstractAdapterBase[] { this });
        return new ArrayList<AbstractAdapterBase>();
    }

    public void resetObject() throws Exception {
        if (getModelObject() != null) {
            getModelObject().reset();
        }
    }

    @Override
    public void deleteWithConfirm() {
        String msg = getConfirmDeleteMessage();
        if (msg == null) {
            throw new RuntimeException("adapter has no confirm delete msg: " //$NON-NLS-1$
                + getClass().getName());
        }
        boolean doDelete = true;
        if (msg != null)
            doDelete = BgcPlugin.openConfirm(
                Messages.AdapterBase_confirm_delete_title, msg);
        if (doDelete) {
            BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
                @Override
                public void run() {
                    // the order is very important
                    if (getModelObject() != null) {
                        IWorkbenchPage page = PlatformUI.getWorkbench()
                            .getActiveWorkbenchWindow().getActivePage();
                        IEditorPart part = page.findEditor(new FormInput(
                            AdapterBase.this));
                        getParent().removeChild(AdapterBase.this, false);
                        try {
                            getModelObject().delete();
                            page.closeEditor(part, true);
                        } catch (Exception e) {
                            BgcPlugin.openAsyncError(
                                Messages.AdapterBase_delete_error_title, e);
                            getParent().addChild(AdapterBase.this);
                            return;
                        }
                        getParent().rebuild();
                        getParent().notifyListeners();
                        notifyListeners();
                        additionalRefreshAfterDelete();
                    }
                }
            });
        }
    }

    @Override
    protected boolean internalIsDeletable() {
        return super.internalIsDeletable() && getModelObject() != null
            && SessionManager.canDelete(getModelObject());
    }

    @Override
    public boolean isEditable() {
        return super.isEditable() && SessionManager.canUpdate(getModelObject());
    }

    public void setLoadChildrenInBackground(boolean loadChildrenInBackground) {
        this.loadChildrenInBackground = loadChildrenInBackground;
    }

    protected List<AbstractAdapterBase> searchChildContainers(
        Class<?> searchedClass, Integer objectId, ContainerAdapter container,
        final List<ContainerWrapper> parents) {
        List<AbstractAdapterBase> res = new ArrayList<AbstractAdapterBase>();
        if (parents.contains(container.getModelObject())) {
            AbstractAdapterBase child = container.getChild(objectId, true);
            if (child == null) {
                for (AbstractAdapterBase childContainer : container
                    .getChildren()) {
                    if (childContainer instanceof ContainerAdapter) {
                        res = searchChildContainers(searchedClass, objectId,
                            (ContainerAdapter) childContainer, parents);
                    } else {
                        res = childContainer.search(searchedClass, objectId);
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

    public RootNode getRootNode() {
        return getParentFromClass(RootNode.class);
    }

    @Override
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

}