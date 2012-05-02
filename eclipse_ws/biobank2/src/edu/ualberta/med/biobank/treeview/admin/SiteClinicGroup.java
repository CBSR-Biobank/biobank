package edu.ualberta.med.biobank.treeview.admin;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.treeview.AbstractClinicGroup;

public class SiteClinicGroup extends AbstractClinicGroup {

    public SiteClinicGroup(SiteAdapter parent, int id) {
        super(parent, id, Clinic.NAME.plural().toString());
    }

    @Override
    protected List<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        SiteAdapter site = getParentFromClass(SiteAdapter.class);
        return new ArrayList<ModelWrapper<?>>(
            ((SiteWrapper) site.getModelObject()).getWorkingClinicCollection());
    }
}
