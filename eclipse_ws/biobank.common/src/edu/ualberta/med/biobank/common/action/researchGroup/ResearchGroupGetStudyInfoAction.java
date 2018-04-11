package edu.ualberta.med.biobank.common.action.researchGroup;

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
import edu.ualberta.med.biobank.model.ResearchGroup;
import edu.ualberta.med.biobank.model.Study;

/**
 *
 * Action object that loads the Studies as associated with the Research Group from the database
 *
 * Code Changes -
 * 		1> New class similar to SiteGetStudyInfoAction
 * 		2> Get the list of Study IDs from Research Group and reads the Study for each of them
 *
 * @author OHSDEV
 *
 */
public class ResearchGroupGetStudyInfoAction implements Action<ListResult<StudyCountInfo>> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String STUDY_INFO_HQL =
        "SELECT studies.id, studies.nameShort"
            + " FROM " + ResearchGroup.class.getName() + " researchGroup"
            + " INNER JOIN researchGroup.studies AS studies"
            + " WHERE researchGroup.id = ?"
            + " GROUP BY studies.id"
            + " ORDER BY studies.nameShort";

    private final Integer rgId;

    public ResearchGroupGetStudyInfoAction(Integer rgId) {
        this.rgId = rgId;
    }

    public ResearchGroupGetStudyInfoAction(ResearchGroup researchGroup) {
        this(researchGroup.getId());
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return true;
    }

    @Override
    public ListResult<StudyCountInfo> run(ActionContext context) throws ActionException {
        ArrayList<StudyCountInfo> studies = new ArrayList<StudyCountInfo>();

        Map<Integer, Study> studyByIds = new HashMap<Integer, Study>();

        @SuppressWarnings("nls")
        Criteria criteria = context.getSession().createCriteria(ResearchGroup.class, "s").add(Restrictions.eq("id", rgId));

        ResearchGroup researchGroup = (ResearchGroup) criteria.uniqueResult();

        if (researchGroup == null) {
            throw new NullPointerException("site not found in DB"); //$NON-NLS-1$
        }

        for (Study study : researchGroup.getStudies()) {
            studyByIds.put(study.getId(), study);
        }

        Query query = context.getSession().createQuery(STUDY_INFO_HQL);
        query.setParameter(0, rgId);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.list();
        for (Object[] row : results) {
            Study study = studyByIds.get(row[0]);

            if (study == null) {
                throw new NullPointerException("study not found in query result"); //$NON-NLS-1$
            }

            StudyCountInfo studyInfo = new StudyCountInfo(study, 0L, 0L);

            studies.add(studyInfo);
        }

        return new ListResult<StudyCountInfo>(studies);
    }
}