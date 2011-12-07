package edu.ualberta.med.biobank.common.action.security;

import java.util.Map;
import java.util.Set;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.exception.NullPropertyException;
import edu.ualberta.med.biobank.common.action.util.SessionUtil;
import edu.ualberta.med.biobank.common.util.SetDifference;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.Principal;
import edu.ualberta.med.biobank.model.User;

public abstract class PrincipalSaveAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;

    protected Integer principalId = null;

    private Set<Integer> membershipIds;

    protected SessionUtil sessionUtil;

    public void setId(Integer id) {
        this.principalId = id;
    }

    public void setMembershipIds(Set<Integer> membershipIds) {
        this.membershipIds = membershipIds;
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        // TODO what should be here?
        return true;
    }

    public IdResult run(User user, Session session, Principal principal)
        throws ActionException {
        if (membershipIds == null) {
            throw new NullPropertyException(Principal.class,
                "membership ids cannot be null");
        }

        principal.setId(principalId);

        if (sessionUtil == null) {
            sessionUtil = new SessionUtil(session);
        }

        Map<Integer, Membership> memberships =
            sessionUtil.load(Membership.class, membershipIds);

        SetDifference<Membership> sitesDiff =
            new SetDifference<Membership>(principal.getMembershipCollection(),
                memberships.values());
        principal.setMembershipCollection(sitesDiff.getNewSet());

        // remove memberships no longer used
        for (Membership membership : sitesDiff.getRemoveSet()) {
            session.delete(membership);
        }

        session.saveOrUpdate(principal);
        session.flush();

        return new IdResult(principal.getId());
    }

}
