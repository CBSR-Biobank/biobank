package edu.ualberta.med.biobank.treeview.admin;

import java.util.Collection;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.helpers.SiteQuery;
import edu.ualberta.med.biobank.treeview.AbstractClinicGroup;

public class SiteClinicGroup extends AbstractClinicGroup {

    public SiteClinicGroup(SiteAdapter parent, int id) {
        super(parent, id, Messages.SiteClinicGroup_clinics_node_label);
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        SiteAdapter site = getParentFromClass(SiteAdapter.class);
        return ((SiteWrapper) site.getModelObject())
            .getWorkingClinicCollection();
    }

    @Override
    protected int getWrapperChildCount() throws Exception {
        SiteAdapter site = getParentFromClass(SiteAdapter.class);
        return SiteQuery.getWorkingClinicCollectionSize((SiteWrapper) site
            .getModelObject());
    }
}
