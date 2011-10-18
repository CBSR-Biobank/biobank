package edu.ualberta.med.biobank.common.action.site;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.site.GetSiteContainerTypeInfoAction.ContainerTypeInfo;
import edu.ualberta.med.biobank.common.action.site.GetSiteInfoAction.SiteInfo;
import edu.ualberta.med.biobank.common.action.site.GetSiteStudyInfoAction.StudyInfo;
import edu.ualberta.med.biobank.common.permission.SiteReadPermission;
import edu.ualberta.med.biobank.common.util.NotAProxy;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.User;

public class GetSiteInfoAction implements Action<SiteInfo> {
    private static final long serialVersionUID = 1L;
    // @formatter:off
    private static final String SITE_INFO_HQL = "SELECT site, COUNT(DISTINCT patients), COUNT(DISTINCT collectionEvents), COUNT(DISTINCT aliquotedSpecimens)"
        + " FROM " + Site.class.getName() + " site"
        + " INNER JOIN FETCH site.address address"
        + " INNER JOIN FETCH site.activityStatus activityStatus"
        + " LEFT JOIN site.studyCollection AS studies"
        + " LEFT JOIN studies.patientCollection AS patients"
        + " LEFT JOIN patients.collectionEventCollection AS collectionEvents"
        + " LEFT JOIN collectionEvents.allSpecimenCollection AS aliquotedSpecimens"
        + " WHERE site.id = ?"
        + " AND aliquotedSpecimens.parentSpecimen IS NULL" // count only aliquoted Specimen-s
        + " GROUP BY site";
    // @formatter:on

    private final Integer siteId;
    private final GetSiteTopContainersAction getTopContainers;
    private final GetSiteContainerTypeInfoAction getContainerTypeInfo;
    private final GetSiteStudyInfoAction getStudyInfo;

    public GetSiteInfoAction(Site site) {
        this(site.getId());
    }

    public GetSiteInfoAction(Integer siteId) {
        this.siteId = siteId;

        this.getTopContainers = new GetSiteTopContainersAction(siteId);
        this.getContainerTypeInfo = new GetSiteContainerTypeInfoAction(siteId);
        this.getStudyInfo = new GetSiteStudyInfoAction(siteId);
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        return new SiteReadPermission(siteId).isAllowed(user, session);
    }

    @Override
    public SiteInfo run(User user, Session session) throws ActionException {
        SiteInfo info = new SiteInfo();

        Query query = session.createQuery(SITE_INFO_HQL);
        query.setParameter(0, siteId);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.list();
        if (rows.size() == 1) {
            Object[] row = rows.get(0);

            Site site = (Site) row[0];

            info.site = site;
            info.patientCount = (Long) row[1];
            info.collectionEventCount = (Long) row[2];
            info.aliquotedSpecimenCount = (Long) row[3];

            info.topContainers = getTopContainers.run(null, session);
            info.containerTypes = getContainerTypeInfo.run(null, session);
            info.studies = getStudyInfo.run(null, session);
        } else {
            // TODO: throw exception?
        }

        return info;
    }

    public static class SiteInfo implements Serializable, NotAProxy {
        private static final long serialVersionUID = 1L;

        public Site site;
        public List<ContainerTypeInfo> containerTypes;
        public List<StudyInfo> studies;
        public List<Container> topContainers;
        public Long patientCount;
        public Long collectionEventCount;
        public Long aliquotedSpecimenCount;
    }
}
