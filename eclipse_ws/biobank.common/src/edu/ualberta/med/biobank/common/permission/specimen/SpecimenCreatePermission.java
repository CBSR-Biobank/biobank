package edu.ualberta.med.biobank.common.permission.specimen;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.util.SessionUtil;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;

public class SpecimenCreatePermission implements Permission {
    private static final long serialVersionUID = 1L;

    private final Specimen specimen;

    public SpecimenCreatePermission(Specimen specimen) {
        this.specimen = specimen;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        SessionUtil sessionUtil = new SessionUtil(session);
        CollectionEvent collectionEvent = sessionUtil.get(
            CollectionEvent.class, specimen.getCollectionEvent().getId());
        Study study = collectionEvent.getPatient().getStudy();

        return PermissionEnum.SPECIMEN_CREATE.isAllowed(user, study);
    }
}
