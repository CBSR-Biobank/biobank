package edu.ualberta.med.biobank.treeview.patient;

import org.eclipse.core.runtime.Assert;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.treeview.AbstractSearchedNode;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.admin.SiteAdapter;
import edu.ualberta.med.biobank.views.PatientAdministrationView;

public class PatientSearchedNode extends AbstractSearchedNode {

    public PatientSearchedNode(AdapterBase parent, int id) {
        super(parent, id, false);
    }

    @Override
    protected AdapterBase createChildNode(ModelWrapper<?> child) {
        Assert.isTrue(child instanceof SiteWrapper);
        return new SiteAdapter(this, (SiteWrapper) child);
    }

    @Override
    protected AdapterBase createChildNode() {
        return new SiteAdapter(this, null);
    }

    @Override
    protected boolean isParentTo(ModelWrapper<?> parent, ModelWrapper<?> child) {
        if (child instanceof PatientWrapper) {
            return parent.equals(((PatientWrapper) child).getStudy());
        }
        return false;
    }

    @Override
    public AdapterBase search(Object searchedObject) {
        if (searchedObject instanceof StudyWrapper) {
            return getChild((ModelWrapper<?>) searchedObject, true);
        }
        return searchChildren(searchedObject);
    }

    @Override
    protected void addNode(ModelWrapper<?> wrapper) {
        PatientAdministrationView.getCurrent().addToNode(this, wrapper);
    }
}
