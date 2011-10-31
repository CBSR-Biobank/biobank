package edu.ualberta.med.biobank.forms;

import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class CommentAdapter extends AdapterBase {

    public CommentAdapter(AdapterBase parent, ModelWrapper<?> object) {
        super(parent, object);
    }

    @Override
    public int compareTo(AbstractAdapterBase o) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected String getLabelInternal() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected int getWrapperChildCount() throws Exception {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getTooltipTextInternal() {
        return "test";
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        // TODO Auto-generated method stub

    }

    @Override
    protected AbstractAdapterBase createChildNode() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected AbstractAdapterBase createChildNode(Object child) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getViewFormId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getEntryFormId() {
        // TODO Auto-generated method stub
        return null;
    }

}
