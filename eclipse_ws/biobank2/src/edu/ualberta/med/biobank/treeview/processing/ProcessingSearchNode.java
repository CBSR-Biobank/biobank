package edu.ualberta.med.biobank.treeview.processing;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.treeview.AbstractSearchedNode;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class ProcessingSearchNode extends AbstractSearchedNode {

    public ProcessingSearchNode(AdapterBase parent, int id) {
        super(parent, id, false);
    }

    @Override
    protected void addNode(ModelWrapper<?> wrapper) {
        // TODO Auto-generated method stub

    }

    @Override
    protected boolean isParentTo(ModelWrapper<?> parent, ModelWrapper<?> child) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected AdapterBase createChildNode() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected AdapterBase createChildNode(ModelWrapper<?> child) {
        // TODO Auto-generated method stub
        return null;
    }

}
