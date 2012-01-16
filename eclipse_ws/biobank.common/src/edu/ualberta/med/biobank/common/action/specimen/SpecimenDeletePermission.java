package edu.ualberta.med.biobank.common.action.specimen;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.User;

public class SpecimenDeletePermission implements Permission {
    private static final long serialVersionUID = 1L;

    private Integer specimenId;

    public SpecimenDeletePermission(Integer specimenId) {
        this.specimenId = specimenId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        ActionContext actionContext = new ActionContext(user, session);
        Specimen specimen = actionContext.load(Specimen.class, specimenId);
        return PermissionEnum.SPECIMEN_DELETE.isAllowed(user,
            specimen.getCurrentCenter());
    }

}
