package edu.ualberta.med.biobank.permission.clinic;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.PermissionEnum;

public class ClinicUpdatePermission implements Permission {
    private static final long serialVersionUID = 1L;
    private Integer clinicId;

    public ClinicUpdatePermission(Integer clinicId) {
        this.clinicId = clinicId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        Clinic clinic = context.load(Clinic.class, clinicId);
        return PermissionEnum.CLINIC_UPDATE
            .isAllowed(context.getUser(), clinic);
    }

}
