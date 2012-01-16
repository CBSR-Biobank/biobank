package edu.ualberta.med.biobank.common.permission.collectionEvent;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.common.permission.patient.PatientUpdatePermission;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.User;

public class CollectionEventCreatePermission implements Permission {
    private static final long serialVersionUID = 1L;

    private final Integer patientId;

    public CollectionEventCreatePermission(Integer patientId) {
        this.patientId = patientId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        Patient patient =
            new ActionContext(user, session).load(Patient.class, patientId);
        return PermissionEnum.COLLECTION_EVENT_CREATE
            .isAllowed(user, patient.getStudy())
            && new PatientUpdatePermission(patientId).isAllowed(user,
                session);
    }
}
