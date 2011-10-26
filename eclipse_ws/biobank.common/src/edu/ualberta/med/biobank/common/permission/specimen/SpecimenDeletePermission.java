package edu.ualberta.med.biobank.common.permission.specimen;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.util.SessionUtil;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;

public class SpecimenDeletePermission implements Permission {
    private static final long serialVersionUID = 1L;

    private final Integer specimenId;

    public SpecimenDeletePermission(Integer specimenId) {
        this.specimenId = specimenId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        SessionUtil sessionUtil = new SessionUtil(session);
        Specimen specimen = sessionUtil.get(Specimen.class, specimenId);

        Center center = specimen.getCurrentCenter();
        Study study = specimen.getCollectionEvent().getPatient().getStudy();

        return PermissionEnum.SPECIMEN_DELETE.isAllowed(user, center, study);
    }
}
