package edu.ualberta.med.biobank.common.action.site;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.site.SiteViewAction.SiteInfo;
import edu.ualberta.med.biobank.common.util.NotAProxy;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;

public class SiteViewAction implements Action<SiteInfo> {
    private static final long serialVersionUID = 1L;
    // @formatter:off
    @SuppressWarnings("nls")
    private static final String SITE_INFO_HQL = "SELECT site, COUNT(DISTINCT patients), COUNT(DISTINCT collectionEvents), COUNT(DISTINCT aliquotedSpecimens)"
        + " FROM "
        + Site.class.getName()
        + " site"
        + " INNER JOIN FETCH site.address address"
        + " INNER JOIN FETCH site.activityStatus activityStatus"
        + " LEFT JOIN site.studyCollection AS studies"
        + " LEFT JOIN studies.patientCollection AS patients"
        + " LEFT JOIN patients.collectionEventCollection AS collectionEvents"
        + " LEFT JOIN collectionEvents.allSpecimenCollection AS aliquotedSpecimens"
        + " WHERE site.id = ?"
        + " AND aliquotedSpecimens.parentSpecimen IS NULL" // count only
                                                           // aliquoted
                                                           // Specimen-s
        + " GROUP BY site";
    @SuppressWarnings("nls")
    private static final String TOP_CONTAINER_HQL = "SELECT container"
        + " FROM " + Container.class.getName() + " container"
        + " INNER JOIN FETCH container.containerType containerType"
        + " INNER JOIN FETCH container.activityStatus activityStatus"
        + " WHERE container.site.id = ?"
        + " AND containerType.topLevel IS TRUE"; // only select top-level
                                                 // Container-s
    @SuppressWarnings("nls")
    private static final String CONTAINER_TYPE_INFO_HQL = "SELECT containerType, (SELECT COUNT(*) FROM "
        + Container.class.getName()
        + " c WHERE c.containerType = containerType)"
        + " FROM " + ContainerType.class.getName() + " containerType"
        + " INNER JOIN FETCH containerType.activityStatus AS activityStatus"
        + " INNER JOIN FETCH containerType.capacity capacity"
        + " WHERE containerType.site.id = ?"
        + " ORDER BY containerType.nameShort";
    @SuppressWarnings("nls")
    private static final String STUDY_INFO_HQL = "SELECT studies, COUNT(DISTINCT patients), COUNT(DISTINCT collectionEvents)"
        + " FROM " + Site.class.getName() + " site"
        + " INNER JOIN site.studyCollection AS studies"
        + " INNER JOIN studies.patientCollection AS patients"
        + " INNER JOIN patients.collectionEventCollection AS collectionEvents"
        + " INNER JOIN FETCH studies.activityStatus aStatus"
        + " WHERE site.id = ?"
        + " GROUP BY studies"
        + " ORDER BY studies.nameShort";
    // @formatter:on

    private final Integer siteId;

    public SiteViewAction(Site site) {
        this(site.getId());
    }

    public SiteViewAction(Integer siteId) {
        this.siteId = siteId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        return true; 
    }

    @Override
    public SiteInfo run(User user, Session session) throws ActionException {
        SiteInfo siteInfo = new SiteInfo();

        Query query = session.createQuery(SITE_INFO_HQL);
        query.setParameter(0, siteId);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.list();
        if (rows.size() == 1) {
            Object[] row = rows.get(0);

            siteInfo.site = (Site) row[0];
            siteInfo.patientCount = (Long) row[1];
            siteInfo.collectionEventCount = (Long) row[2];
            siteInfo.aliquotedSpecimenCount = (Long) row[3];

            siteInfo.topContainers = getTopContainers(session);
            siteInfo.containerTypes = getContainerTypes(session);
            siteInfo.studies = getStudies(session);
        } else {
            // TODO: throw exception?
        }

        return siteInfo;
    }

    private List<ContainerTypeInfo> getContainerTypes(Session session) {
        List<ContainerTypeInfo> containerTypes = new ArrayList<ContainerTypeInfo>();

        Query query = session.createQuery(CONTAINER_TYPE_INFO_HQL);
        query.setParameter(0, siteId);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.list();
        for (Object[] row : results) {
            ContainerTypeInfo containerTypeInfo = new ContainerTypeInfo();
            containerTypeInfo.containerType = (ContainerType) row[0];
            containerTypeInfo.containerCount = (Long) row[1];

            containerTypes.add(containerTypeInfo);
        }

        return containerTypes;
    }

    public List<StudyInfo> getStudies(Session session) {
        List<StudyInfo> studies = new ArrayList<StudyInfo>();

        Query query = session.createQuery(STUDY_INFO_HQL);
        query.setParameter(0, siteId);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.list();
        for (Object[] row : results) {
            StudyInfo studyInfo = new StudyInfo();
            studyInfo.study = (Study) row[0];
            studyInfo.patientCount = (Long) row[1];
            studyInfo.collectionEventCount = (Long) row[2];

            studies.add(studyInfo);
        }

        return studies;
    }

    private List<Container> getTopContainers(Session session) {
        List<Container> topContainers = new ArrayList<Container>();

        Query query = session.createQuery(TOP_CONTAINER_HQL);
        query.setParameter(0, siteId);

        @SuppressWarnings("unchecked")
        List<Container> results = query.list();
        if (results != null) {
            topContainers.addAll(results);
        }

        return topContainers;
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

    public static class ContainerTypeInfo implements Serializable {
        private static final long serialVersionUID = 1L;

        public ContainerType containerType;
        public Long containerCount;
    }

    public static class StudyInfo implements Serializable {
        private static final long serialVersionUID = 1L;

        public Study study;
        public Long patientCount;
        public Long collectionEventCount;
    }
}
