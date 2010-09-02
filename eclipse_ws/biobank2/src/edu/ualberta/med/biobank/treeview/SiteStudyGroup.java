package edu.ualberta.med.biobank.treeview;

import java.util.Collection;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;

/**
 * Group of studies for a site
 */
public class SiteStudyGroup extends AbstractStudyGroup {

    public SiteStudyGroup(SiteAdapter parent, int id) {
        super(parent, id, "Studies");
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        SiteAdapter site = getParentFromClass(SiteAdapter.class);
        return site.getWrapper().getStudyCollection();
    }
}
