package edu.ualberta.med.biobank.common.action.patient;

import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.patient.PatientGetCollectionEventInfosAction.PatientCEventInfo;
import edu.ualberta.med.biobank.common.action.patient.PatientGetInfoAction.PatientInfo;
import edu.ualberta.med.biobank.common.permission.patient.PatientReadPermission;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Patient;

/**
 * Retrieve a patient information using a patient id
 * 
 * @author delphine
 * 
 */
public class PatientGetInfoAction implements Action<PatientInfo> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String PATIENT_INFO_HQL =
        "SELECT patient,COUNT(DISTINCT sourceSpecs),"
            + "COUNT(DISTINCT allSpecs) - COUNT(DISTINCT sourceSpecs)"
            + " FROM " + Patient.class.getName() + " patient"
            + " INNER JOIN FETCH patient.study study"
            + " LEFT JOIN patient.collectionEvents cevents"
            + " LEFT JOIN cevents.originalSpecimens sourceSpecs"
            + " LEFT JOIN cevents.allSpecimens allSpecs"
            + " WHERE patient.id = ?"
            + " GROUP BY patient";

    private final Integer patientId;

    public static class PatientInfo implements ActionResult {
        private static final long serialVersionUID = 1L;

        public Patient patient;
        public List<PatientCEventInfo> ceventInfos;
        public Long sourceSpecimenCount;
        public Long aliquotedSpecimenCount;

    }

    public PatientGetInfoAction(Integer patientId) {
        this.patientId = patientId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return new PatientReadPermission(patientId).isAllowed(context);
    }

    @Override
    public PatientInfo run(ActionContext context) throws ActionException {
        PatientInfo pInfo = new PatientInfo();

        Query query = context.getSession().createQuery(PATIENT_INFO_HQL);
        query.setParameter(0, patientId);

        Object[] results = (Object[]) query.uniqueResult();

        pInfo.patient = (Patient) results[0];
        pInfo.sourceSpecimenCount = (Long) results[1];
        pInfo.aliquotedSpecimenCount = (Long) results[2];
        pInfo.ceventInfos = new PatientGetCollectionEventInfosAction(patientId)
            .run(context).getList();

        // get all comments
        for (Comment c : pInfo.patient.getComments()) {
            c.getUser().getLogin();
        }

        return pInfo;
    }

}
