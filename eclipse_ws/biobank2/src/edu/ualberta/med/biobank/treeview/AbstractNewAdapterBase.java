package edu.ualberta.med.biobank.treeview;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.springframework.remoting.RemoteAccessException;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;

/**
 * Base class for all "Session" tree view nodes. Generally, most of the nodes in
 * the tree are adapters for classes in the ORM model.
 */
public abstract class AbstractNewAdapterBase extends AbstractAdapterBase {

    private static BgcLogger logger = BgcLogger
        .getLogger(AbstractNewAdapterBase.class.getName());

    public AbstractNewAdapterBase(AbstractAdapterBase parent, Integer id,
        String label, String tooltip, boolean hasChildren) {
        super(parent, id, label, tooltip, hasChildren);
    }

    @Override
    public String getLabel() {
        if (super.getLabel() == null) {
            return getLabelInternal();
        }
        return super.getLabel();
    }

    protected abstract String getLabelInternal();

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
    public void loadChildren(final boolean updateNode) {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            @Override
            public void run() {
                try {
                    Map<Integer, ?> children = getChildrenObjects();
                    if (children != null) {
                        for (Entry<Integer, ?> entry : children.entrySet()) {
                            AbstractAdapterBase node = getChild(entry.getKey());
                            if (node == null) {
                                node = createChildNode(entry.getValue());
                                addChild(node);
                            }
                            if (updateNode) {
                                node.setValue(entry.getValue());
                                SessionManager.updateAdapterTreeNode(node);
                            }
                        }
                        SessionManager
                            .refreshTreeNode(AbstractNewAdapterBase.this);
                    }
                } catch (final RemoteAccessException exp) {
                    BgcPlugin.openRemoteAccessErrorMessage(exp);
                } catch (Exception e) {
                    String text = getClass().getName();
                    text += " id=" + getId(); //$NON-NLS-1$
                    logger.error(
                        "Error while loading children of node " + text, e); //$NON-NLS-1$
                }
            }
        });
    }

    @Override
    public List<AbstractAdapterBase> search(Class<?> searchedClass,
        Integer objectId) {
        if (getClass().equals(searchedClass) && getId().equals(objectId))
            return Arrays.asList(new AbstractAdapterBase[] { this });
        return new ArrayList<AbstractAdapterBase>();
    }

}