package edu.ualberta.med.biobank.common.permission.specimen;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.User;

public class SpecimenAssignPermission implements Permission {
    private static final long serialVersionUID = 1L;

    private final Integer centerId;

    public SpecimenAssignPermission(Integer centerId) {
        this.centerId = centerId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        Center center =
            new ActionContext(user, session).load(Center.class, centerId);
        // FIXME check also permission to create/update containers?
        return PermissionEnum.SPECIMEN_ASSIGN.isAllowed(user, center);
    }
}
