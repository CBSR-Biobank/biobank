package edu.ualberta.med.biobank.common.permission.clinic;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.Clinic;

public class ClinicDeletePermission implements Permission {
    private static final long serialVersionUID = 1L;

    private Integer clinicId;

    public ClinicDeletePermission(Integer clinicId) {
        this.clinicId = clinicId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        Clinic clinic = context.load(Clinic.class, clinicId);
        return PermissionEnum.CLINIC_DELETE
            .isAllowed(context.getUser(), clinic);
    }

}
