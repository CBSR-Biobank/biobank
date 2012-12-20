package edu.ualberta.med.biobank.permission.specimen;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.model.center.Center;
import edu.ualberta.med.biobank.model.study.Specimen;
import edu.ualberta.med.biobank.model.study.Study;
import edu.ualberta.med.biobank.model.type.PermissionEnum;

public class SpecimenDeletePermission implements Permission {
    private static final long serialVersionUID = 1L;

    private final Integer specimenId;

    public SpecimenDeletePermission(Integer specimenId) {
        this.specimenId = specimenId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        Specimen specimen = context.get(Specimen.class, specimenId);

        Center center = specimen.getCurrentCenter();
        Study study = specimen.getCollectionEvent().getPatient().getStudy();

        return PermissionEnum.SPECIMEN_DELETE.isAllowed(context.getUser(),
            center, study);
    }
}
