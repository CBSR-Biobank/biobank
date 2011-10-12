package edu.ualberta.med.biobank.common.action.study;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionException;
import edu.ualberta.med.biobank.common.peer.SitePeer;
import edu.ualberta.med.biobank.common.peer.StudyPeer;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;

public class GetStudyListForSiteAction implements Action<List<Study>> {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    // @formatter:off
    @SuppressWarnings("nls")
    private static final String STUDIES_QRY = 
        "select study"
        + " from " + Study.class.getName() + " as study"
        + " left join study." + StudyPeer.SITE_COLLECTION.getName() + " as site"
        + " where site." + SitePeer.ID.getName() + " =?";
    // @formatter:on

    public GetStudyListForSiteAction(Integer siteId) {
        this.siteId = siteId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<Study> doAction(Session session) throws ActionException {
        Query query = session.createQuery(STUDIES_QRY);
        query.setParameter(0, siteId);

        @SuppressWarnings("unchecked")
        List<Study> rows = query.list();
        return rows;
    }
}
