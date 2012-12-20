package edu.ualberta.med.biobank.permission;

import edu.ualberta.med.biobank.model.center.Center;
import edu.ualberta.med.biobank.model.study.Study;
import edu.ualberta.med.biobank.model.type.PermissionEnum;

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
