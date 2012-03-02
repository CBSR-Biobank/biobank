package edu.ualberta.med.biobank.common.action.site;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.info.SiteInfo;
import edu.ualberta.med.biobank.common.permission.site.SiteReadPermission;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Specimen;

public class SiteGetInfoAction implements Action<SiteInfo> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String SITE_INFO_HQL =
        "SELECT site"
            + " FROM " + Site.class.getName() + " site"
            + " INNER JOIN FETCH site.address address"
            + " LEFT JOIN FETCH site.studyCollection studies"
            + " LEFT JOIN FETCH site.commentCollection comments"
            + " LEFT JOIN FETCH comments.user"
            + " WHERE site.id = ?";

    @SuppressWarnings("nls")
    private static final String SITE_COUNT_INFO_HQL =
        "SELECT site, COUNT(DISTINCT patients), COUNT(DISTINCT pevents) "
            + " FROM " + Site.class.getName() + " site"
            + " LEFT JOIN site.processingEventCollection pevents"
            + " LEFT JOIN site.studyCollection studies"
            + " LEFT JOIN studies.patientCollection patients"
            + " LEFT JOIN patients.collectionEventCollection cevents"
            + " LEFT JOIN cevents.allSpecimenCollection allSpecimens"
            + " LEFT JOIN allSpecimens.currentCenter.id currentCenter"
            + " WITH currentCenter=site"
            + " WHERE site.id=?"
            + " AND allSpecimens.activityStatus=?"
            + " GROUP BY site";

    private static final String SITE_COUNT_INFO_2_HQL =
        "SELECT count(*) "
            + " FROM " + Specimen.class.getName() + " s"
            + " WHERE s.activityStatus=?"
            + " AND s.currentCenter.id=?";

    private final Integer siteId;

    public SiteGetInfoAction(Site site) {
        this(site.getId());
    }

    public SiteGetInfoAction(Integer siteId) {
        this.siteId = siteId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return new SiteReadPermission(siteId).isAllowed(context);
    }

    @Override
    public SiteInfo run(ActionContext context) throws ActionException {
        Query query = context.getSession().createQuery(SITE_INFO_HQL);
        query.setParameter(0, siteId);

        Site site = (Site) query.uniqueResult();

        SiteInfo.Builder builder = new SiteInfo.Builder();
        builder.setSite(site);

        query = context.getSession().createQuery(SITE_COUNT_INFO_HQL);
        query.setParameter(0, siteId);
        query.setParameter(1, ActivityStatus.ACTIVE);

        Object[] items = (Object[]) query.uniqueResult();

        builder.setPatientCount((Long) items[1]);
        builder.setCollectionEventCount((Long) items[2]);

        query = context.getSession().createQuery(SITE_COUNT_INFO_2_HQL);
        query.setParameter(0, siteId);
        query.setParameter(1, ActivityStatus.ACTIVE);

        Long l = (Long) query.uniqueResult();
        builder.setAliquotedSpecimenCount(l);

        builder.setTopContainers(
            new SiteGetTopContainersAction(siteId).run(context).getList());
        builder.setContainerTypes(
            new SiteGetContainerTypeInfoAction(siteId).run(context).getList());
        builder
            .setStudies(
            new SiteGetStudyInfoAction(siteId).run(context).getList());

        return builder.build();
    }
}
