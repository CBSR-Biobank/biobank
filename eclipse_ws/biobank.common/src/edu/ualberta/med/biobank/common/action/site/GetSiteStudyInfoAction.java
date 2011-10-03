package edu.ualberta.med.biobank.common.action.site;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.site.GetSiteStudyInfoAction.StudyInfo;
import edu.ualberta.med.biobank.common.util.NotAProxy;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;

public class GetSiteStudyInfoAction implements Action<List<StudyInfo>> {
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

    public GetSiteStudyInfoAction(Integer siteId) {
        this.siteId = siteId;
    }

    public GetSiteStudyInfoAction(Site site) {
        this(site.getId());
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<StudyInfo> run(User user, Session session) throws ActionException {
        List<StudyInfo> studies = new ArrayList<StudyInfo>();

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
