package edu.ualberta.med.biobank.common.permission.specimen;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.Study;

public class SpecimenReadPermission implements Permission {
    private static final long serialVersionUID = 1L;

    private final Integer specimenId;

    public SpecimenReadPermission(Integer specimenId) {
        this.specimenId = specimenId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        Specimen specimen = context.get(Specimen.class, specimenId);

        Center center = specimen.getCurrentCenter();
        Study study = specimen.getCollectionEvent().getPatient().getStudy();

        return PermissionEnum.SPECIMEN_READ.isAllowed(context.getUser(),
            center, study);
    }
}
