package edu.ualberta.med.biobank.common.action.site;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.info.SiteInfo;
import edu.ualberta.med.biobank.common.permission.site.SiteReadPermission;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Specimen;

public class SiteGetInfoAction implements Action<SiteInfo> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String SITE_INFO_HQL =
        "SELECT site"
            + " FROM " + Site.class.getName() + " site"
            + " INNER JOIN FETCH site.address address"
            + " LEFT JOIN FETCH site.studies studies"
            + " LEFT JOIN FETCH site.comments comments"
            + " LEFT JOIN FETCH comments.user"
            + " WHERE site.id = ?";

    @SuppressWarnings("nls")
    private static final String CENTER_PROCESSING_EVENT_COUNT =
        "SELECT COUNT(pe)"
            + " FROM " + ProcessingEvent.class.getName() + " pe"
            + " WHERE pe.center.id = ?";

    @SuppressWarnings("nls")
    private static final String CENTER_SPECIMEN_AND_PATIENT_COUNT =
        "SELECT COUNT(specimen), COUNT(DISTINCT patient)"
            + " FROM " + Specimen.class.getName() + " specimen"
            + " INNER JOIN specimen.collectionEvent ce"
            + " INNER JOIN ce.patient patient"
            + " WHERE specimen.currentCenter.id = ?"
            + " AND specimen.activityStatus = ?";

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

        query =
            context.getSession().createQuery(CENTER_SPECIMEN_AND_PATIENT_COUNT);
        query.setParameter(0, siteId);
        query.setParameter(1, ActivityStatus.ACTIVE);

        Object[] items = (Object[]) query.uniqueResult();

        builder.setSpecimenCount((Long) items[0]);
        builder.setPatientCount((Long) items[1]);

        query = context.getSession().createQuery(CENTER_PROCESSING_EVENT_COUNT);
        query.setParameter(0, siteId);

        Long count = (Long) query.uniqueResult();
        builder.setProcessingEventCount(count);

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
