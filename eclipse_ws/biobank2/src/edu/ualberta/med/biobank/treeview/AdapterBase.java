package edu.ualberta.med.biobank.treeview;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteAccessException;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.treeview.admin.ContainerAdapter;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

/**
 * Base class for all "Session" tree view nodes. Generally, most of the nodes in the tree are
 * adapters for classes in the ORM model.
 */
public abstract class AdapterBase extends AbstractAdapterBase {
    private static final I18n i18n = I18nFactory
        .getI18n(AbstractTodayNode.class);

    private static BgcLogger logger = BgcLogger.getLogger(AdapterBase.class
        .getName());

    @SuppressWarnings("nls")
    protected static final String BGR_LOADING_LABEL = i18n.tr("loading...");

    private Object modelObject;

    //OHSDEV
    // Specimen tree view implementation
    // In order to attach SpecimenTreeViewAdapter to CollectionEventAddapter those object
    // must be brought to the same parent class.

    public AdapterBase(AbstractAdapterBase parent, ModelWrapper<?> object) {
        super(parent, object == null ? null : object.getId(), null, null, false);
        setModelObject(object);
    }

    public AdapterBase(AdapterBase parent, Integer id, String label,
        boolean hasChildren) {
        super(parent, id, label, null, hasChildren);
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

    public void setModelObject(ModelWrapper<?> object) {
        this.modelObject = object;
        if (getId() != null) init();
    }

    @Override
    public Integer getId() {
        if (getModelObject() != null) {
            return getModelObject().getId();
        }
        return super.getId();
    }

    /**
     * Derived classes should not override this method. Instead they should implement
     * getNameInternal().
     * 
     * @return the name for the node.
     */
    @Override
    public final String getLabel() {
        if (getModelObject() != null) {
            return getLabelInternal();
        }
        return super.getLabel();
    }

    /**
     * Derived classses should implement this method instead of overriding getName().
     * 
     * @return the name of the node. The name is the label displayed in the treeview.
     */
    protected abstract String getLabelInternal();
    //OHSDEV
    // Specimen tree view implementation
    // In order to attach SpecimenTreeViewAdapter to CollectionEventAddapter those objects
    // must be brought to the same parent class.
    @Override
    public AbstractAdapterBase getParent() {
        return (AbstractAdapterBase) parent;
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
    public void loadChildren(final boolean updateNode) {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            @SuppressWarnings("nls")
            @Override
            public void run() {
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
                    logger.error(
                        "Error while loading children of node " + text, e);
                }
            }
        });
    }

    /**
     * get the list of this model object children that this node should have as children nodes.
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

    public static boolean closeEditor(FormInput input) {
        IWorkbenchPage page = PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getActivePage();
        if (page != null) {
            IEditorPart part = page.findEditor(input);
            if (part != null) {
                return page.closeEditor(part, true);
            }
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
            logger.error("Can't open form with id " + id, e);
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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected int internalCompareTo(AbstractAdapterBase o) {
        if (getModelObject() != null) {
            return getModelObject().compareTo((ModelWrapper) ((AdapterBase) o).getModelObject());
        }
        return 0;
    }

    @Override
    public void setValue(Object value) {

    }
}