package edu.ualberta.med.biobank.common.action.site;

import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.exception.ModelNotFoundException;
import edu.ualberta.med.biobank.common.action.info.SiteInfo;
import edu.ualberta.med.biobank.common.permission.site.SiteReadPermission;
import edu.ualberta.med.biobank.model.Site;

public class SiteGetInfoAction implements Action<SiteInfo> {
    private static final long serialVersionUID = 1L;
    // @formatter:off
    @SuppressWarnings("nls")
    private static final String SITE_INFO_HQL = 
        "SELECT site, COUNT(DISTINCT patients), " 
        + "COUNT(DISTINCT collectionEvents), " 
        + "COUNT(DISTINCT aliquotedSpecimens)"
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
        SiteInfo.Builder builder = new SiteInfo.Builder();

        Query query = context.getSession().createQuery(SITE_INFO_HQL);
        query.setParameter(0, siteId);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.list();
        if (rows.size() != 1) {
            throw new ModelNotFoundException(Site.class, siteId);
        }

        Object[] row = rows.get(0);

        builder.setSite((Site) row[0]);
        builder.setPatientCount((Long) row[1]);
        builder.setCollectionEventCount((Long) row[2]);
        builder.setAliquotedSpecimenCount((Long) row[3]);

        builder.setTopContainers(
            new SiteGetTopContainersAction(siteId).run(context)
                .getTopContainers());
        builder.setContainerTypes(
            new SiteGetContainerTypeInfoAction(siteId).run(context)
                .getContainerTypeInfoCollection());
        builder
            .setStudies(
            new SiteGetStudyInfoAction(siteId).run(context).getList());

        return builder.build();
    }
}
