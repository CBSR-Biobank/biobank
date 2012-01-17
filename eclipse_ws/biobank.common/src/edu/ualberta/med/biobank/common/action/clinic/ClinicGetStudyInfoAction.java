package edu.ualberta.med.biobank.common.action.clinic;

import java.util.ArrayList;
import java.util.List;

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

    // @formatter:off
    @SuppressWarnings("nls")
    private static final String STUDY_INFO_HQL =    
        "SELECT clinics,studies,COUNT(DISTINCT patients),"
        + " COUNT(DISTINCT cevents)"
        + " FROM edu.ualberta.med.biobank.model.Clinic clinics"
        + " INNER JOIN clinics.originInfoCollection oi"        
        + " INNER JOIN oi.specimenCollection spcs"        
        + " INNER JOIN spcs.collectionEvent cevents"
        + " INNER JOIN cevents.patient patients"
        + " INNER JOIN patients.study studies"
        + " WHERE clinics.id=?"
        + " GROUP BY clinics,studies";
    // @formatter:on

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

    @Override
    public ListResult<StudyCountInfo> run(ActionContext context)
        throws ActionException {
        ArrayList<StudyCountInfo> infos = new ArrayList<StudyCountInfo>();

        Query query = context.getSession().createQuery(STUDY_INFO_HQL);
        query.setParameter(0, clinicId);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.list();
        for (Object[] row : results) {
            StudyCountInfo info =
                new StudyCountInfo((Study) row[1], (Long) row[2],
                    (Long) row[3]);
            infos.add(info);
        }

        return new ListResult<StudyCountInfo>(infos);
    }
}
