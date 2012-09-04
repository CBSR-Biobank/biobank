package edu.ualberta.med.biobank.common.action.site;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.info.StudyCountInfo;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;

public class SiteGetStudyInfoAction implements
    Action<ListResult<StudyCountInfo>> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String STUDY_INFO_HQL =
        "SELECT studies.id, COUNT(DISTINCT patients), COUNT(DISTINCT collectionEvents)"
            + " FROM " + Site.class.getName() + " site"
            + " INNER JOIN site.studies AS studies"
            + " LEFT JOIN studies.patients AS patients"
            + " LEFT JOIN patients.collectionEvents AS collectionEvents"
            + " WHERE site.id = ?"
            + " GROUP BY studies.id"
            + " ORDER BY studies.nameShort";

    private final Integer siteId;

    public SiteGetStudyInfoAction(Integer siteId) {
        this.siteId = siteId;
    }

    public SiteGetStudyInfoAction(Site site) {
        this(site.getId());
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return true;
    }

    @Override
    public ListResult<StudyCountInfo> run(ActionContext context)
        throws ActionException {
        ArrayList<StudyCountInfo> studies = new ArrayList<StudyCountInfo>();

        Map<Integer, Study> studyByIds = new HashMap<Integer, Study>();

        @SuppressWarnings("nls")
        Criteria criteria = context.getSession()
            .createCriteria(Site.class, "s")
            .add(Restrictions.eq("id", siteId));

        Site site = (Site) criteria.uniqueResult();

        if (site == null) {
            throw new NullPointerException("site not found in DB"); //$NON-NLS-1$
        }

        for (Study study : site.getStudies()) {
            studyByIds.put(study.getId(), study);
        }

        Query query = context.getSession().createQuery(STUDY_INFO_HQL);
        query.setParameter(0, siteId);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.list();
        for (Object[] row : results) {
            Study study = studyByIds.get(row[0]);

            if (study == null) {
                throw new NullPointerException(
                    "study not found in query result"); //$NON-NLS-1$
            }

            StudyCountInfo studyInfo =
                new StudyCountInfo(study, (Long) row[1], (Long) row[2]);

            studies.add(studyInfo);
        }

        return new ListResult<StudyCountInfo>(studies);
    }
}
