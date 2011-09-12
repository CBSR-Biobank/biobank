package edu.ualberta.med.biobank.common.wrappers.actions;

import java.io.Serializable;

import javax.transaction.Status;
import javax.transaction.Synchronization;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.server.applicationservice.BiobankSecurityUtil;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;
import gov.nih.nci.system.applicationservice.ApplicationException;

// TODO: this class is some totally broken example code. It's just an example for now. Fix up later.
public class ExamplePersistCsmUserAction extends WrapperAction<User> {
    private static final long serialVersionUID = 1L;

    private final edu.ualberta.med.biobank.common.security.User csmUser;

    protected ExamplePersistCsmUserAction(ModelWrapper<User> wrapper,
        edu.ualberta.med.biobank.common.security.User csmUser) {
        super(wrapper);
        this.csmUser = csmUser;
    }

    @Override
    public Object doAction(Session session) throws BiobankSessionException {
        persistCsmUser();

        Synchronization rollbackListener = new RollbackCsmUserPersist();
        session.getTransaction().registerSynchronization(rollbackListener);

        return null;
    }

    private void persistCsmUser() throws BiobankSessionException {
        try {
            // TODO: why does this take a current user argument and not just get
            // the current user? Shouldn't it just get this from the UPM?
            BiobankSecurityUtil.persistUser(csmUser, null);
        } catch (ApplicationException e) {
            throw new BiobankSessionException(e.getMessage(), e);
        }
    }

    /**
     * Listens to a {@link Transaction} in which a user is persisted and should
     * delete a freshly created user or undo the changes made, depending on
     * whether the user was inserted or updated, respectively.
     * 
     * @author jferland
     * 
     */
    private class RollbackCsmUserPersist implements Serializable,
        Synchronization {
        private static final long serialVersionUID = 1L;

        private final edu.ualberta.med.biobank.common.security.User oldCsmUser;

        public RollbackCsmUserPersist() {
            this.oldCsmUser = null;
        }

        @Override
        public void afterCompletion(int status) {
            if (status == Status.STATUS_ROLLEDBACK) {
                if (oldCsmUser == null) {
                    deleteCsmUser();
                } else {
                    revertCsmUser();
                }
            }
        }

        @Override
        public void beforeCompletion() {
            // nothing to do here
        }

        private void deleteCsmUser() {
        }

        private void revertCsmUser() {
        }
    }
}
