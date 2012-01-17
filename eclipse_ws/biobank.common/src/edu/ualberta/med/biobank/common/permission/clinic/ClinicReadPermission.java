package edu.ualberta.med.biobank.common.permission.clinic;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.Clinic;

public class ClinicReadPermission implements Permission {
    private static final long serialVersionUID = 1L;

    private final Integer clinicId;

    public ClinicReadPermission(Integer clinicId) {
        this.clinicId = clinicId;
    }

    public ClinicReadPermission(Clinic clinic) {
        this(clinic.getId());
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        Clinic clinic = context.load(Clinic.class, clinicId);
        return PermissionEnum.CLINIC_READ.isAllowed(context.getUser(), clinic);
    }
}
