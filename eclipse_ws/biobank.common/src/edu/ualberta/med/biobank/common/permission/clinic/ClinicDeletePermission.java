package edu.ualberta.med.biobank.common.permission.clinic;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.PermissionEnum;

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
