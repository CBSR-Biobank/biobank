package edu.ualberta.med.biobank.permission.specimen;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.type.PermissionEnum;

public class SpecimenLinkPermission implements Permission {
    private static final long serialVersionUID = 1L;

    private final Integer centerId;
    private final Integer studyId;

    public SpecimenLinkPermission(Integer centerId, Integer studyId) {
        this.centerId = centerId;
        this.studyId = studyId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        Center center = context.load(Center.class, centerId);
        // get is intended
        Study study = context.get(Study.class, studyId);
        return PermissionEnum.SPECIMEN_LINK.isAllowed(context.getUser(),
            center, study)
            && new SpecimenCreatePermission(centerId, studyId)
                .isAllowed(context);
    }
}
