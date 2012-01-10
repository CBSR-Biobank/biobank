package edu.ualberta.med.biobank.common.action.patient;

import java.util.Arrays;
import java.util.Date;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.check.UniquePreCheck;
import edu.ualberta.med.biobank.common.action.check.ValueProperty;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.peer.PatientPeer;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.patient.PatientCreatePermission;
import edu.ualberta.med.biobank.common.permission.patient.PatientUpdatePermission;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;

public class PatientSaveAction implements Action<IdResult> {

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
        Permission permission;
        if (patientId == null) {
            permission = new PatientCreatePermission(studyId);
        } else {
            permission = new PatientUpdatePermission(patientId);
        }
        return permission.isAllowed(user, session);
    }

    @SuppressWarnings("unchecked")
    @Override
    public IdResult run(User user, Session session) throws ActionException {
        // checks pnumber unique to send a proper message:
        new UniquePreCheck<Patient>(Patient.class, patientId,
            Arrays.asList(new ValueProperty<Patient>(PatientPeer.PNUMBER,
                pnumber))).run(user, session);

        Patient patientToSave;
        ActionContext actionContext = new ActionContext(user, session);

        if (patientId == null) {
            patientToSave = new Patient();
        } else {
            patientToSave = actionContext.load(Patient.class, patientId);
        }

        patientToSave.setPnumber(pnumber);
        patientToSave.setCreatedAt(createdAt);
        patientToSave.setStudy((Study) session.load(Study.class, studyId));

        session.saveOrUpdate(patientToSave);

        return new IdResult(patientToSave.getId());
    }
}
