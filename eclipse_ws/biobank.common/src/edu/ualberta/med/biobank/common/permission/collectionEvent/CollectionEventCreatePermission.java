package edu.ualberta.med.biobank.common.permission.collectionEvent;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.patient.PatientUpdatePermission;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PermissionEnum;

public class CollectionEventCreatePermission implements Permission {
    private static final long serialVersionUID = 1L;

    private final Integer patientId;

    public CollectionEventCreatePermission(Integer patientId) {
        this.patientId = patientId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        Patient patient = context.load(Patient.class, patientId);
        return PermissionEnum.COLLECTION_EVENT_CREATE
            .isAllowed(context.getUser(), patient.getStudy())
            && new PatientUpdatePermission(patientId).isAllowed(context);
    }
}
