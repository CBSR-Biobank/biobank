package edu.ualberta.med.biobank.action.site;

import java.util.Set;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.IdResult;
import edu.ualberta.med.biobank.action.center.CenterSaveAction;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.permission.site.SiteCreatePermission;
import edu.ualberta.med.biobank.permission.site.SiteUpdatePermission;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.study.Study;

public class SiteSaveAction extends CenterSaveAction {

    private static final long serialVersionUID = 1L;

    private Set<Integer> studyIds;

    public void setStudyIds(Set<Integer> studyIds) {
        if (studyIds == null) {
            throw new IllegalArgumentException();
        }

        this.studyIds = studyIds;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
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

        Set<Study> studies = context.load(Study.class, studyIds);
        site.getStudies().clear();
        site.getStudies().addAll(studies);

        return run(context, site);
    }

}
