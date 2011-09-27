package edu.ualberta.med.biobank.common.wrappers.actions;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.server.applicationservice.BiobankCSMSecurityUtil;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class DeleteCsmUserAction extends WrapperAction<User> {
    private static final long serialVersionUID = 1L;

    public DeleteCsmUserAction(ModelWrapper<User> wrapper) {
        super(wrapper);
    }

    @Override
    public Object doAction(Session session) throws BiobankSessionException {
        try {
            BiobankCSMSecurityUtil.deleteUser(getModel());
        } catch (ApplicationException e) {
            throw new BiobankSessionException("Error persisting csm user", e); //$NON-NLS-1$
        }
        return null;
    }

}
