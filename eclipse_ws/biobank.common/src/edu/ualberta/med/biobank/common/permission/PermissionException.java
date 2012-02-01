package edu.ualberta.med.biobank.common.permission;

import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Study;

// TODO: find a better home?
public class PermissionException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final PermissionEnum permission;
    private final Center center;
    private final Study study;

    public PermissionException(PermissionEnum permission, Center center,
        Study study) {
        this.permission = permission;
        this.center = center;
        this.study = study;
    }

    public PermissionEnum getPermission() {
        return permission;
    }

    public Center getCenter() {
        return center;
    }

    public Study getStudy() {
        return study;
    }
}
