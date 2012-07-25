package edu.ualberta.med.biobank.action.patient;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.IdResult;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.permission.patient.PatientDeletePermission;
import edu.ualberta.med.biobank.model.Patient;

public class PatientDeleteAction implements Action<IdResult> {

    private static final long serialVersionUID = 1L;

    private final Integer patientId;

    public PatientDeleteAction(Patient patient) {
        if (patient == null) {
            throw new IllegalArgumentException();
        }
        this.patientId = patient.getId();
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return new PatientDeletePermission(patientId).isAllowed(context);
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        Patient patient = context.load(Patient.class, patientId);

        context.getSession().delete(patient);

        return new IdResult(patientId);
    }
}
