package edu.ualberta.med.biobank.common.action.site;

import java.util.Map;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.center.CenterSaveAction;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.site.SiteCreatePermission;
import edu.ualberta.med.biobank.common.permission.site.SiteUpdatePermission;
import edu.ualberta.med.biobank.common.util.SetDifference;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;

public class SiteSaveAction extends CenterSaveAction {

    private static final long serialVersionUID = 1L;

    private Set<Integer> studyIds;

    public void setStudyIds(Set<Integer> studyIds) {
        this.studyIds = studyIds;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        // TODO
        Permission permission;
        if (centerId == null)
            permission = new SiteCreatePermission();
        else
            permission = new SiteUpdatePermission(centerId);
        return permission.isAllowed(context);
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        Site site = context.load(Site.class, centerId, new Site());

        // TODO: check that the user has access to at least the studies they are
        // removing or adding?
        Map<Integer, Study> studies = context.load(Study.class, studyIds);
        SetDifference<Study> sitesDiff = new SetDifference<Study>(
            site.getStudyCollection(), studies.values());
        site.setStudyCollection(sitesDiff.getNewSet());
        for (Study study : sitesDiff.getRemoveSet()) {
            context.getSession().delete(study);
        }

        return run(context, site);
    }

}
