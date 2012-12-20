package edu.ualberta.med.biobank.permission.clinic;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.type.PermissionEnum;

public class ClinicReadPermission implements Permission {
    private static final long serialVersionUID = 1L;

    private final Integer clinicId;

    public ClinicReadPermission() {
        this.clinicId = null;
    }

    public ClinicReadPermission(Integer clinicId) {
        this.clinicId = clinicId;
    }

    public ClinicReadPermission(Clinic clinic) {
        this(clinic.getId());
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        if (clinicId != null) {
            Clinic clinic = context.load(Clinic.class, clinicId);
            return PermissionEnum.CLINIC_READ.isAllowed(context.getUser(),
                clinic);
        }

        return PermissionEnum.CLINIC_READ.isAllowed(context.getUser());
    }
}
