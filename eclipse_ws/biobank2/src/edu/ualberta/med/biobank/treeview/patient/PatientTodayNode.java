package edu.ualberta.med.biobank.treeview.patient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Assert;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.treeview.AbstractTodayNode;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.admin.StudyAdapter;
import edu.ualberta.med.biobank.views.CollectionView;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class PatientTodayNode extends AbstractTodayNode<PatientWrapper> {

    public PatientTodayNode(AdapterBase parent, int id) {
        super(parent, id);
        setName("From today's shipments");
    }

    @Override
    protected AdapterBase createChildNode(ModelWrapper<?> child) {
        Assert.isTrue(child instanceof StudyWrapper);
        return new StudyAdapter(this, (StudyWrapper) child);
    }

    @Override
    protected AdapterBase createChildNode() {
        return new StudyAdapter(this, null);
    }

    @Override
    protected Collection<PatientWrapper> getWrapperChildren() throws Exception {
        return null;
    }

    @Override
    protected int getWrapperChildCount() throws Exception {
        return 0;
    }

    @Override
    protected List<PatientWrapper> getTodayElements()
        throws ApplicationException {
        return new ArrayList<PatientWrapper>();
    }

    @Override
    protected boolean isParentTo(ModelWrapper<?> parent, ModelWrapper<?> child) {
        if (child instanceof PatientWrapper) {
            return parent.equals(((PatientWrapper) child).getStudy());
        }
        return false;
    }

    @Override
    public List<AdapterBase> search(Object searchedObject) {
        return findChildFromClass(searchedObject, StudyWrapper.class);
    }

    @Override
    protected void addChild(PatientWrapper child) {
        CollectionView.addToNode(this, child);
    }
}
