package edu.ualberta.med.biobank.common.permission;

import org.hibernate.Session;

import edu.ualberta.med.biobank.model.Center;
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
        // TODO Auto-generated method stub

        Study study = specimen.getCollectionEvent().getPatient().getStudy();
        Center center = specimen.getOriginInfo().getCenter();

        boolean isAllowed = false;

        return isAllowed;
    }
}
