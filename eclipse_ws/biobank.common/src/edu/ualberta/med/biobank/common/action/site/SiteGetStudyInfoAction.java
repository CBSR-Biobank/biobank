package edu.ualberta.med.biobank.common.action.site;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.site.SiteGetStudyInfoAction.StudyInfo;
import edu.ualberta.med.biobank.common.util.NotAProxy;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;

public class SiteGetStudyInfoAction implements Action<ArrayList<StudyInfo>> {
    public static class StudyInfo implements Serializable, NotAProxy {
        private static final long serialVersionUID = 1L;

        private final Study study;
        private final Long patientCount;
        private final Long collectionEventCount;

        public StudyInfo(Study study, Long patientCount,
            Long collectionEventCount) {
            this.study = study;
            this.patientCount = patientCount;
            this.collectionEventCount = collectionEventCount;
        }

        public Study getStudy() {
            return study;
        }

        public Long getPatientCount() {
            return patientCount;
        }

        public Long getCollectionEventCount() {
            return collectionEventCount;
        }
    }

    private static final long serialVersionUID = 1L;
    // @formatter:off
    @SuppressWarnings("nls")
    private static final String STUDY_INFO_HQL = "SELECT studies, COUNT(DISTINCT patients), COUNT(DISTINCT collectionEvents)"
        + " FROM " + Site.class.getName() + " site"
        + " INNER JOIN site.studyCollection AS studies"
        + " INNER JOIN FETCH studies.activityStatus aStatus"
        + " LEFT JOIN studies.patientCollection AS patients"
        + " LEFT JOIN patients.collectionEventCollection AS collectionEvents"
        + " WHERE site.id = ?"
        + " GROUP BY studies"
        + " ORDER BY studies.nameShort";
    // @formatter:on

    private final Integer siteId;

    public SiteGetStudyInfoAction(Integer siteId) {
        this.siteId = siteId;
    }

    public SiteGetStudyInfoAction(Site site) {
        this(site.getId());
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        return true;
    }

    @Override
    public ArrayList<StudyInfo> run(User user, Session session)
        throws ActionException {
        ArrayList<StudyInfo> studies = new ArrayList<StudyInfo>();

        Query query = session.createQuery(STUDY_INFO_HQL);
        query.setParameter(0, siteId);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.list();
        for (Object[] row : results) {
            StudyInfo studyInfo = new StudyInfo((Study) row[0], (Long) row[1],
                (Long) row[2]);

            studies.add(studyInfo);
        }

        return studies;
    }
}
