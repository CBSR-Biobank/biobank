package edu.ualberta.med.biobank.common.permission.specimenType;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.User;

public class SpecimenTypeUpdatePermission implements Permission {
    private static final long serialVersionUID = 1L;

    private final Integer specimenId;

    public SpecimenTypeUpdatePermission(Integer specimenId) {
        this.specimenId = specimenId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        return PermissionEnum.SPECIMEN_TYPE_UPDATE.isAllowed(user);
    }
}
