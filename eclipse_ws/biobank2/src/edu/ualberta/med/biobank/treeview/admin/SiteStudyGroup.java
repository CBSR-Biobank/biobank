package edu.ualberta.med.biobank.treeview.admin;

import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.treeview.AbstractStudyGroup;

/**
 * Group of studies for a site
 */
public class SiteStudyGroup extends AbstractStudyGroup {

    public SiteStudyGroup(SiteAdapter parent, int id) {
        super(parent, id, Messages.SiteStudyGroup_studies_node_label);
    }

    @Override
    protected List<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        SiteAdapter site = getParentFromClass(SiteAdapter.class);
        return ((SiteWrapper) site.getModelObject()).getStudyCollection();
    }
}
