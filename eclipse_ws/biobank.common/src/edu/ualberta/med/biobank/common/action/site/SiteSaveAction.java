package edu.ualberta.med.biobank.common.action.site;

import java.util.Map;
import java.util.Set;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.center.CenterSaveAction;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.exception.NullPropertyException;
import edu.ualberta.med.biobank.common.action.util.SessionUtil;
import edu.ualberta.med.biobank.common.peer.SitePeer;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.site.SiteCreatePermission;
import edu.ualberta.med.biobank.common.permission.site.SiteUpdatePermission;
import edu.ualberta.med.biobank.common.util.SetDifference;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;

public class SiteSaveAction extends CenterSaveAction {

    private static final long serialVersionUID = 1L;

    private Set<Integer> studyIds;

    public void setStudyIds(Set<Integer> studyIds) {
        this.studyIds = studyIds;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        // TODO
        Permission permission;
        if (centerId == null)
            permission = new SiteCreatePermission();
        else
            permission = new SiteUpdatePermission(centerId);
        return permission.isAllowed(user, session);
    }

    @Override
    public IdResult run(User user, Session session) throws ActionException {
        if (studyIds == null) {
            throw new NullPropertyException(Site.class,
                SitePeer.STUDY_COLLECTION);
        }

        SessionUtil sessionUtil = new SessionUtil(session);
        Site site = sessionUtil.get(Site.class, centerId, new Site());

        // TODO: check that the user has access to at least the studies they are
        // removing or adding?
        Map<Integer, Study> studies = sessionUtil.get(Study.class, studyIds);
        SetDifference<Study> sitesDiff = new SetDifference<Study>(
            site.getStudyCollection(), studies.values());
        site.setStudyCollection(sitesDiff.getNewSet());
        for (Study study : sitesDiff.getRemoveSet()) {
            session.delete(study);
        }

        return run(user, session, sessionUtil, site);
    }

}
