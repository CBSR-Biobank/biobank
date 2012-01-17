package edu.ualberta.med.biobank.common.action.study;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.peer.SitePeer;
import edu.ualberta.med.biobank.common.peer.StudyPeer;
import edu.ualberta.med.biobank.model.Study;

public class StudyGetListForSiteAction implements Action<ListResult<Study>> {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    // @formatter:off
    @SuppressWarnings("nls")
    private static final String STUDIES_QRY =
        "select study"
        + " from " + Study.class.getName() + " as study"
        + " left join study." + StudyPeer.SITE_COLLECTION.getName()
        + " as site"
        + " where site." + SitePeer.ID.getName() + " =?";

    // @formatter:on

    public StudyGetListForSiteAction(Integer siteId) {
        this.siteId = siteId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ListResult<Study> run(ActionContext context)
        throws ActionException {
        Query query = context.getSession().createQuery(STUDIES_QRY);
        query.setParameter(0, siteId);

        return new ListResult<Study>(query.list());
    }
}
