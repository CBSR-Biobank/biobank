package edu.ualberta.med.biobank.common.action.patient;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.check.CollectionIsEmptyCheck;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.peer.PatientPeer;
import edu.ualberta.med.biobank.common.permission.patient.PatientDeletePermission;
import edu.ualberta.med.biobank.model.Patient;

public class PatientDeleteAction implements Action<IdResult> {

    private static final long serialVersionUID = 1L;

    private static final String HAS_COLLECTION_EVENTS_MSG = Messages
        .getString("PatientDeleteAction.has.collectionevents.msg"); //$NON-NLS-1$

    private Integer patientId;

    public PatientDeleteAction(Integer patientId) {
        this.patientId = patientId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return new PatientDeletePermission(patientId).isAllowed(null);
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        Patient patient = context.load(Patient.class, patientId);

        new CollectionIsEmptyCheck<Patient>(Patient.class, patient,
            PatientPeer.COLLECTION_EVENT_COLLECTION, patient.getPnumber(),
            HAS_COLLECTION_EVENTS_MSG).run(context);

        context.getSession().delete(patient);

        return new IdResult(patientId);
    }
}
