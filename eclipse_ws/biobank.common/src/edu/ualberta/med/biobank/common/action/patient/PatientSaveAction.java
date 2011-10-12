package edu.ualberta.med.biobank.common.action.patient;

import java.util.Date;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionException;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;

public class PatientSaveAction implements Action<Patient> {

    private static final long serialVersionUID = 1L;

    private Integer patientId;
    private Integer studyId;
    private String pnumber;
    private Date createdAt;

    public PatientSaveAction(Integer patientId, Integer studyId,
        String pnumber, Date createdAt) {
        this.patientId = patientId;
        this.studyId = studyId;
        this.pnumber = pnumber;
        this.createdAt = createdAt;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public Patient doAction(Session session) throws ActionException {
        Patient patientToSave;
        if (patientId == null) {
            patientToSave = new Patient();
        } else {
            patientToSave = (Patient) session.get(Patient.class, patientId);
        }
        patientToSave.setPnumber(pnumber);
        patientToSave.setCreatedAt(createdAt);
        patientToSave.setStudy((Study) session.load(Study.class, studyId));

        session.saveOrUpdate(patientToSave);

        return patientToSave;
    }
}
