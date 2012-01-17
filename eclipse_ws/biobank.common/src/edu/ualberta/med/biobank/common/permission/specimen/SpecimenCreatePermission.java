package edu.ualberta.med.biobank.common.permission.specimen;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Study;

public class SpecimenCreatePermission implements Permission {
    private static final long serialVersionUID = 1L;

    private final Integer centerId;
    private final Integer studyId;

    public SpecimenCreatePermission(Integer centerId, Integer studyId) {
        this.centerId = centerId;
        this.studyId = studyId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        Study study = context.load(Study.class, studyId);
        Center center = context.load(Center.class, centerId);
        return PermissionEnum.SPECIMEN_CREATE.isAllowed(context.getUser(),
            center, study);
    }
}
