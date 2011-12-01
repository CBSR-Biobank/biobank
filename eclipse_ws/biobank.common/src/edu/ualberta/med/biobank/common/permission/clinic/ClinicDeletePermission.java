package edu.ualberta.med.biobank.common.permission.clinic;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.ActionUtil;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.User;

public class ClinicDeletePermission implements Permission {
    private static final long serialVersionUID = 1L;

    private Integer clinicId;

    public ClinicDeletePermission(Integer clinicId) {
        this.clinicId = clinicId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        Clinic clinic = ActionUtil.sessionGet(session, Clinic.class, clinicId);
        return PermissionEnum.CLINIC_DELETE.isAllowed(user, clinic);
    }

}
