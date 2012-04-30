package edu.ualberta.med.biobank.common.wrappers.checks;

import java.text.MessageFormat;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.peer.MembershipPeer;
import edu.ualberta.med.biobank.common.util.HibernateUtil;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.actions.UncachedAction;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;

public class RolePreDeleteChecks extends UncachedAction<Role> {
    private static final long serialVersionUID = 1L;

    private static final String HAS_MBERSHIP_MSG = Messages.getString("RolePreDeleteChecks.has.membership.msg"); //$NON-NLS-1$

    // @formatter:off
    private static final String USED_IN_MEMBERSHIP_QRY = "select count(ms) from " //$NON-NLS-1$
        + Membership.class.getName()
        + " as ms join ms." //$NON-NLS-1$
        + MembershipPeer.ROLES.getName()
        + " as roles where roles=?"; //$NON-NLS-1$
    // @formatter:on

    public RolePreDeleteChecks(ModelWrapper<Role> wrapper) {
        super(wrapper);
    }

    @Override
    public void doUncachedAction(Session session)
        throws BiobankSessionException {
        checkNotUsedInMembership(session);
    }

    private void checkNotUsedInMembership(Session session)
        throws BiobankSessionException {
        Query query = session.createQuery(USED_IN_MEMBERSHIP_QRY);
        query.setParameter(0, getModel());

        Long mbershipCount = HibernateUtil.getCountFromQuery(query);

        if (mbershipCount != 0) {
            String hasMbershipsMsg = MessageFormat.format(HAS_MBERSHIP_MSG,
                getModel().getName());

            throw new BiobankSessionException(hasMbershipsMsg);
        }
    }
}
