package edu.ualberta.med.biobank.treeview.admin;

import java.util.Collection;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.treeview.AbstractStudyGroup;

/**
 * Group of studies for a site
 */
public class SiteStudyGroup extends AbstractStudyGroup {

    public SiteStudyGroup(SiteAdapter parent, int id) {
        super(parent, id, Messages.SiteStudyGroup_studies_node_label);
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        SiteAdapter site = getParentFromClass(SiteAdapter.class);
        return site.getWrapper().getStudyCollection();
    }

    @Override
    protected int getWrapperChildCount() throws Exception {
        SiteAdapter site = getParentFromClass(SiteAdapter.class);
        return site.getWrapper().getStudyCollection().size();
    }
}
