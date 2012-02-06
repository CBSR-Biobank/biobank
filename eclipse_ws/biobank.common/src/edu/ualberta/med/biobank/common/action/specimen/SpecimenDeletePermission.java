package edu.ualberta.med.biobank.common.action.specimen;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Specimen;

public class SpecimenDeletePermission implements Permission {
    private static final long serialVersionUID = 1L;

    private Integer specimenId;

    public SpecimenDeletePermission(Integer specimenId) {
        this.specimenId = specimenId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        Specimen specimen = context.load(Specimen.class, specimenId);
        return PermissionEnum.SPECIMEN_DELETE.isAllowed(context.getUser(),
            specimen.getCurrentCenter());
    }

}
