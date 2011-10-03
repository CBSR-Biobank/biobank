package edu.ualberta.med.biobank.treeview.patient;

import java.util.List;

import org.eclipse.core.runtime.Assert;

import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AbstractSearchedNode;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.admin.SiteAdapter;
import edu.ualberta.med.biobank.views.CollectionView;

public class PatientSearchedNode extends AbstractSearchedNode {

    public PatientSearchedNode(AdapterBase parent, int id) {
        super(parent, id, false);
    }

    @Override
    protected AdapterBase createChildNode(Object child) {
        Assert.isTrue(child instanceof SiteWrapper);
        return new SiteAdapter(this, (SiteWrapper) child);
    }

    @Override
    protected AdapterBase createChildNode() {
        return new SiteAdapter(this, null);
    }

    @Override
    protected boolean isParentTo(Object parent, Object child) {
        return false;
    }

    @Override
    public List<AbstractAdapterBase> search(Object searchedObject) {
        return findChildFromClass(searchedObject, StudyWrapper.class);
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
