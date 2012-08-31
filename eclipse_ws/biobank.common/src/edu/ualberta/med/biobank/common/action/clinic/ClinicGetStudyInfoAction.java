package edu.ualberta.med.biobank.common.action.clinic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.info.StudyCountInfo;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Study;

public class ClinicGetStudyInfoAction implements
    Action<ListResult<StudyCountInfo>> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String CLINIC_STUDIES_HQL =
        "SELECT studies"
            + " FROM " + Clinic.class.getName() + " clinic"
            + " INNER JOIN clinic.originInfos oi"
            + " INNER JOIN oi.specimens spcs"
            + " INNER JOIN spcs.collectionEvent cevents"
            + " INNER JOIN cevents.patient patients"
            + " INNER JOIN patients.study studies"
            + " WHERE clinic.id=?";

    @SuppressWarnings("nls")
    private static final String STUDY_INFO_HQL =
        "SELECT clinics.id,studies.id,COUNT(DISTINCT patients),"
            + " COUNT(DISTINCT cevents)"
            + " FROM " + Clinic.class.getName() + " clinics"
            + " INNER JOIN clinics.originInfos oi"
            + " INNER JOIN oi.specimens spcs"
            + " INNER JOIN spcs.collectionEvent cevents"
            + " INNER JOIN cevents.patient patients"
            + " INNER JOIN patients.study studies"
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

    @SuppressWarnings("unchecked")
    @Override
    public ListResult<StudyCountInfo> run(ActionContext context)
        throws ActionException {
        ArrayList<StudyCountInfo> infos = new ArrayList<StudyCountInfo>();

        Map<Integer, Study> studiesById = new HashMap<Integer, Study>();

        Query query = context.getSession().createQuery(CLINIC_STUDIES_HQL);
        query.setParameter(0, clinicId);

        List<Study> studies = query.list();
        for (Study study : studies) {
            studiesById.put(study.getId(), study);
        }

        query = context.getSession().createQuery(STUDY_INFO_HQL);
        query.setParameter(0, clinicId);

        List<Object[]> results = query.list();
        for (Object[] row : results) {
            Study study = studiesById.get(row[1]);

            if (study == null) {
                throw new NullPointerException(
                    "study not found in query result"); //$NON-NLS-1$
            }

            StudyCountInfo info =
                new StudyCountInfo(study, (Long) row[2], (Long) row[3]);
            infos.add(info);
        }

        return new ListResult<StudyCountInfo>(infos);
    }
}
