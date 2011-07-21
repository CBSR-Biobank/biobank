package edu.ualberta.med.biobank.views;

import java.util.List;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.treeview.AbstractSearchedNode;
import edu.ualberta.med.biobank.treeview.AbstractTodayNode;

public abstract class AbstractTodaySearchAdministrationView extends
    AbstractAdministrationView {

    protected AbstractTodayNode<?> todayNode;

    protected AbstractSearchedNode searchedNode;

    protected abstract String getString();

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);

        searchedNode = createSearchedNode();
        searchedNode.setParent(rootNode);
        rootNode.addChild(searchedNode);
    }

    protected abstract AbstractTodayNode<?> createTodayNode();

    protected abstract AbstractSearchedNode createSearchedNode();

    public AbstractTodayNode<?> getTodayNode() {
        return todayNode;
    }

    public AbstractSearchedNode getSearchedNode() {
        return searchedNode;
    }

    @Override
    protected void internalSearch() {
        String text = treeText.getText();
        try {
            List<? extends ModelWrapper<?>> searchedObject = search(text);
            if (searchedObject == null || searchedObject.size() == 0) {
                notFound(text);
            } else {
                showSearchedObjectsInTree(searchedObject, true);
                getTreeViewer().expandToLevel(searchedNode, 3);
            }
        } catch (Exception e) {
            BgcPlugin
                .openAsyncError(
                    Messages.AbstractTodaySearchAdministrationView_search_error_title,
                    e);
        }
    }

    protected abstract void showSearchedObjectsInTree(
        List<? extends ModelWrapper<?>> searchedObject, boolean b);

    protected abstract List<? extends ModelWrapper<?>> search(String text)
        throws Exception;

    protected abstract void notFound(String text);

    @Override
    public void reload() {
        if (todayNode != null)
            todayNode.performExpand();
        if (searchedNode != null)
            searchedNode.performExpand();
        super.reload();
    }

    @Override
    public void clear() {
        todayNode.removeAll();
        searchedNode.clear();
    }

}
