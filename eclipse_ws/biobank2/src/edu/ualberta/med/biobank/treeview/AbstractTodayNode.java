package edu.ualberta.med.biobank.treeview;

import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.springframework.remoting.RemoteAccessException;

import edu.ualberta.med.biobank.gui.common.BiobankGuiCommonPlugin;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.gui.common.BiobankLogger;
import gov.nih.nci.system.applicationservice.ApplicationException;

public abstract class AbstractTodayNode<E extends ModelWrapper<?>> extends
    AdapterBase {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(AbstractTodayNode.class.getName());

    private List<E> currentTodayElements;

    public AbstractTodayNode(AdapterBase parent, int id) {
        super(parent, id, "Today", true, false);
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
    }

    @Override
    protected void executeDoubleClick() {
        performExpand();
    }

    @Override
    protected Collection<E> getWrapperChildren() throws Exception {
        return null;
    }

    @Override
    protected int getWrapperChildCount() throws Exception {
        return 0;
    }

    @Override
    public String getViewFormId() {
        return null;
    }

    @Override
    public String getTooltipText() {
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

    @Override
    public void performExpand() {
        try {
            currentTodayElements = getTodayElements();

            // remove elements that are not in today list
            for (AdapterBase child : getChildren()) {
                ModelWrapper<?> childWrapper = child.getModelObject();
                childWrapper.reload();

                for (AdapterBase grandchild : child.getChildren()) {
                    ModelWrapper<?> grandchildWrapper = grandchild
                        .getModelObject();
                    grandchildWrapper.reload();
                    if (!currentTodayElements.contains(grandchildWrapper)
                        || !isParentTo(childWrapper, grandchildWrapper)) {
                        grandchild.getParent().removeChild(grandchild);
                    }
                }
            }

            // add today elements is not yet there
            if (currentTodayElements != null) {
                for (E wrapper : currentTodayElements) {
                    addChild(wrapper);
                }
            }

            // remove sub children without any children
            for (AdapterBase child : getChildren()) {
                if (child.getChildren().isEmpty()) {
                    removeChild(child);
                }
            }
        } catch (final RemoteAccessException exp) {
            BiobankGuiCommonPlugin.openRemoteAccessErrorMessage(exp);
        } catch (Exception e) {
            logger.error("Error while getting " + getLabel(), e);
        }
    }

    protected abstract boolean isParentTo(ModelWrapper<?> parent,
        ModelWrapper<?> child);

    protected abstract List<E> getTodayElements() throws ApplicationException;

    protected abstract void addChild(E child);

    @Override
    public List<AdapterBase> search(Object searchedObject) {
        return searchChildren(searchedObject);
    }

    public List<E> getCurrentTodayElements() {
        return currentTodayElements;
    }
}
