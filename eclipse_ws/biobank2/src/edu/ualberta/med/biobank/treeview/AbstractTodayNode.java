package edu.ualberta.med.biobank.treeview;

import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.springframework.remoting.RemoteAccessException;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.model.IBiobankModel;
import gov.nih.nci.system.applicationservice.ApplicationException;

public abstract class AbstractTodayNode<E extends IBiobankModel> extends
    AdapterBase {
    private static final I18n i18n = I18nFactory
        .getI18n(AbstractTodayNode.class);

    private static BgcLogger logger = BgcLogger
        .getLogger(AbstractTodayNode.class.getName());

    private List<E> currentTodayElements;

    @SuppressWarnings("nls")
    public AbstractTodayNode(AdapterBase parent, int id) {
        super(parent, id, i18n.tr("Today"), true);
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        //
    }

    @Override
    protected void executeDoubleClick() {
        performExpand();
    }

    @Override
    protected List<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        return null;
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
    protected String getLabelInternal() {
        return null;
    }

    @Override
    public String getEntryFormId() {
        return null;
    }

    @SuppressWarnings("nls")
    @Override
    public void performExpand() {
        try {
            currentTodayElements = (List<E>) getTodayElements();

            // remove elements that are not in today list
            for (AbstractAdapterBase child : getChildren()) {
                ModelWrapper<?> childWrapper = ((AdapterBase) child)
                    .getModelObject();
                childWrapper.reload();

                for (AbstractAdapterBase grandchild : child.getChildren()) {
                    ModelWrapper<?> grandchildWrapper =
                        ((AdapterBase) grandchild)
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
                    if (wrapper instanceof OriginInfoWrapper)
                        addChild(wrapper);
                }
            }

            // remove sub children without any children
            for (AbstractAdapterBase child : getChildren()) {
                if (child.getChildren().isEmpty()) {
                    removeChild(child);
                }
            }
        } catch (final RemoteAccessException exp) {
            BgcPlugin.openRemoteAccessErrorMessage(exp);
        } catch (Exception e) {
            logger.error("Error while getting " + getLabel(), e);
        }
    }

    protected abstract boolean isParentTo(ModelWrapper<?> parent,
        ModelWrapper<?> child);

    protected abstract List<IBiobankModel> getTodayElements()
        throws ApplicationException;

    protected abstract void addChild(E child);

    @Override
    public List<AbstractAdapterBase> search(Class<?> searchedClass,
        Integer objectId) {
        return searchChildren(searchedClass, objectId);
    }

    public List<E> getCurrentTodayElements() {
        return currentTodayElements;
    }

    @Override
    public int compareTo(AbstractAdapterBase o) {
        return 0;
    }
}
