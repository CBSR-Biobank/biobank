package edu.ualberta.med.biobank.common.action.patient;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.common.permission.patient.OriginInfoReadPermission;
import edu.ualberta.med.biobank.model.User;

public class ShipmentGetSpecimenInfosAction implements Action<SpecimenInfo> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Integer oiId;

    public ShipmentGetSpecimenInfosAction(Integer oiId) {
        this.oiId = oiId;
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        return new OriginInfoReadPermission(oiId).isAllowed(user, session);
    }

    @Override
    public SpecimenInfo run(User user, Session session) throws ActionException {
        return null;
    }
}
