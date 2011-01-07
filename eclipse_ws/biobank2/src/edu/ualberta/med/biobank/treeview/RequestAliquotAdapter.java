package edu.ualberta.med.biobank.treeview;

import java.util.Collection;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestAliquotWrapper;

public class RequestAliquotAdapter extends AdapterBase {

    // variables are replicated here to avoid repeated calls to remote api

    public RequestAliquotAdapter(AdapterBase parent, RequestAliquotWrapper raw) {
        super(parent, raw);
    }

    @Override
    protected String getLabelInternal() {
        return ((RequestAliquotWrapper) getModelObject()).getAliquot()
            .getInventoryId();
    }

    @Override
    public String getTooltipText() {
        return null;
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {

    }

    @Override
    protected AdapterBase createChildNode() {
        return null;
    }

    @Override
    protected AdapterBase createChildNode(ModelWrapper<?> child) {
        return null;
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
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
    public String getEntryFormId() {
        return null;
    }

    public String getClaimedBy() {
        return ((RequestAliquotWrapper) getModelObject()).getClaimedBy();
    }

    public String getSampleType() {
        return ((RequestAliquotWrapper) getModelObject()).getAliquot()
            .getSampleType().getNameShort();
    }

    public String getPosition() {
        return ((RequestAliquotWrapper) getModelObject()).getAliquot()
            .getPositionString();
    }

}
