package edu.ualberta.med.biobank.treeview.patient;

import java.util.List;

import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AbstractSearchedNode;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.views.CollectionView;

public class PatientSearchedNode extends AbstractSearchedNode {

    public PatientSearchedNode(AdapterBase parent, int id) {
        super(parent, id, false);
    }

    @Override
    protected AdapterBase createChildNode(Object child) {
        return null;
    }

    @Override
    protected AdapterBase createChildNode() {
        return null;
    }

    @Override
    protected boolean isParentTo(Object parent, Object child) {
        return false;
    }

    @Override
    public List<AbstractAdapterBase> search(Class<?> searchedClass,
        Integer objectId) {
        return findChildFromClass(searchedClass, objectId, Study.class);
    }

    @Override
    protected void addNode(Object obj) {
        CollectionView.addToNode(this, obj);
    }

    @Override
    public void rebuild() {
        performExpand();
    }

}
