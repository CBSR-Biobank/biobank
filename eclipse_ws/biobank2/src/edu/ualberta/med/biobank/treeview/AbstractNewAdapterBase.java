package edu.ualberta.med.biobank.treeview;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.swt.widgets.Display;
import org.springframework.remoting.RemoteAccessException;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.model.IBiobankModel;

/**
 * Base class for all "Session" tree view nodes. Generally, most of the nodes in
 * the tree are adapters for classes in the ORM model.
 */
public abstract class AbstractNewAdapterBase extends AbstractAdapterBase {

    private static BgcLogger logger = BgcLogger
        .getLogger(AbstractNewAdapterBase.class.getName());

    public AbstractNewAdapterBase(AbstractAdapterBase parent,
        IBiobankModel model, String label) {
        super(parent, model, label);
    }

    public AbstractNewAdapterBase(AbstractAdapterBase parent,
        IBiobankModel model) {
        this(parent, model, null);
    }

    public AbstractNewAdapterBase(AbstractAdapterBase parent, int id,
        String label, boolean hasChildren) {
        super(parent, id, label, hasChildren);
    }

    @Override
    public Integer getId() {
        if (getModelObject() != null)
            return getModelObject().getId();
        return super.getId();
    }

    @Override
    public String getLabel() {
        if (super.getLabel() == null && getModelObject() != null) {
            return getLabelInternal();
        }
        return super.getLabel();
    }

    protected abstract String getLabelInternal();

    @Override
    public IBiobankModel getModelObject() {
        return (IBiobankModel) super.getModelObject();
    }

    @Override
    public void performExpand() {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                loadChildren(true);
                NewRootNode root = getRootNode();
                if (root != null) {
                    root.expandChild(AbstractNewAdapterBase.this);
                }
            }
        });
    }

    public NewRootNode getRootNode() {
        return getParentFromClass(NewRootNode.class);
    }

    /**
     * Called to load it's children;
     * 
     * @param updateNode If not null, the node in the treeview to update.
     */
    @Override
    public void loadChildren(boolean updateNode) {
        try {
            Collection<?> children = getChildrenObjects();
            if (children != null) {
                for (Object child : children) {
                    AbstractAdapterBase node = getChild(child);
                    if (node == null) {
                        node = createChildNode(child);
                        addChild(node);
                    }
                    if (updateNode) {
                        SessionManager.updateAdapterTreeNode(node);
                    }
                }
                SessionManager.refreshTreeNode(AbstractNewAdapterBase.this);
            }
        } catch (final RemoteAccessException exp) {
            BgcPlugin.openRemoteAccessErrorMessage(exp);
        } catch (Exception e) {
            String text = getClass().getName();
            if (getModelObject() != null) {
                text = getModelObject().toString();
            }
            logger.error("Error while loading children of node " + text, e); //$NON-NLS-1$
        }
    }

    @Override
    public void deleteWithConfirm() {
        // FIXME action ?
    }

    @Override
    public List<AbstractAdapterBase> search(Object searchedObject) {
        if (getModelObject() != null && getModelObject().equals(searchedObject))
            return Arrays.asList(new AbstractAdapterBase[] { this });
        return new ArrayList<AbstractAdapterBase>();
    }

}