package edu.ualberta.med.biobank.common.action.clinic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.info.StudyCountInfo;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Study;

public class ClinicGetStudyInfoAction implements Action<ListResult<StudyCountInfo>> {
    private static final long serialVersionUID = 1L;

    // private static Logger log = LoggerFactory.getLogger(ClinicGetStudyInfoAction.class);

    @SuppressWarnings("nls")
    private static final String STUDY_INFO_HQL =
        "SELECT clinics.id,studies,COUNT(DISTINCT patients),"
            + " COUNT(DISTINCT cevents)"
            + " FROM " + Clinic.class.getName() + " clinics"
            + " LEFT JOIN clinics.originInfos oi"
            + " LEFT JOIN oi.specimens spcs"
            + " LEFT JOIN spcs.collectionEvent cevents"
            + " LEFT JOIN cevents.patient patients"
            + " LEFT JOIN patients.study studies"
            + " WHERE clinics.id=?"
            + " GROUP BY clinics.id,studies.id";

    private final Integer clinicId;

    public ClinicGetStudyInfoAction(Integer clinicId) {
        this.clinicId = clinicId;
    }

    public ClinicGetStudyInfoAction(Clinic clinic) {
        this(clinic.getId());
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return true;
    }

    /**
     * Clinics can be associated by either:
     * <ul>
     * <li>shipments that contain patients from a study</li>
     * <li>clinic contact to a study</li>
     * </ul>
     * Both are valid.
     */
    @SuppressWarnings({ "unchecked", "nls" })
    @Override
    public ListResult<StudyCountInfo> run(ActionContext context) throws ActionException {
        List<Study> studies = context.getSession().createCriteria(Study.class, "study")
            .createAlias("study.contacts", "contacts")
            .createAlias("contacts.clinic", "clinic")
            .setFetchMode("contacts", FetchMode.JOIN)
            .setFetchMode("clinic", FetchMode.JOIN)
            .add(Restrictions.eq("clinic.id", clinicId)).list();

        Set<Study> studiesForClinic = new HashSet<Study>();
        for (Study study : studies) {
            studiesForClinic.add(study);
        }

        Map<Study, StudyCountInfo> countInfoById = new HashMap<Study, StudyCountInfo>();

        Query query = context.getSession().createQuery(STUDY_INFO_HQL);
        query.setParameter(0, clinicId);

        List<Object[]> results = query.list();
        for (Object[] row : results) {
            if (row[1] == null) continue;

            Study study = (Study) row[1];
            if (!studiesForClinic.contains(study)) {
                throw new IllegalStateException("study not associated with clinic");
            }
            countInfoById.put(study, new StudyCountInfo(study, (Long) row[2], (Long) row[3]));
        }

        // set counts for studies with no patients and collection events
        for (Study study : studiesForClinic) {
            if (!countInfoById.containsKey(study)) {
                countInfoById.put(study, new StudyCountInfo(study, 0L, 0L));
            }
        }

        return new ListResult<StudyCountInfo>(countInfoById.values());
    }
}
