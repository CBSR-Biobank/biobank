package edu.ualberta.med.biobank.common.permission.specimen;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Study;

public class SpecimenStudyCenterReadPermission implements Permission {

    private static final long serialVersionUID = 1L;

    private final Integer studyId;

    private final Integer centerId;

    public SpecimenStudyCenterReadPermission(Integer studyId, Integer centerId) {
        this.studyId = studyId;
        this.centerId = centerId;
    }

    public SpecimenStudyCenterReadPermission(Integer centerId) {
        this.centerId = centerId;
        this.studyId = null;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        Study study = null;
        if (studyId != null) {
            study = context.load(Study.class, studyId);
        }
        Center center = context.load(Center.class, centerId);
        return PermissionEnum.SPECIMEN_READ.isAllowed(context.getUser(), center, study);
    }

}
