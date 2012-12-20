package edu.ualberta.med.biobank.permission.specimen;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.model.center.Center;
import edu.ualberta.med.biobank.model.study.Study;
import edu.ualberta.med.biobank.model.type.PermissionEnum;

public class SpecimenListPermission implements Permission {
    private static final long serialVersionUID = 1L;

    private final Integer centerId;
    private final Integer studyId;

    public SpecimenListPermission(Integer centerId, Integer studyId) {
        this.centerId = centerId;
        this.studyId = studyId;
    }

    public SpecimenListPermission(Integer centerId) {
        this.centerId = centerId;
        this.studyId = null;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        Center center = context.get(Center.class, centerId);
        Study study = context.get(Study.class, studyId);

        return PermissionEnum.SPECIMEN_READ.isAllowed(context.getUser(),
            center, study);
    }
}
