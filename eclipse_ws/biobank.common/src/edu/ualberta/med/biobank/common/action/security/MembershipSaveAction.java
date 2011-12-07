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
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.model.User;

public class MembershipSaveAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;

    private Set<Integer> roleIds;
    private Set<Integer> permissionIds;
    private Set<Integer> centerIds;
    private Set<Integer> studyIds;
    private Session session = null;
    private SessionUtil sessionUtil = null;
    private Membership membership = null;

    public void setRoleIds(Set<Integer> roleIds) {
        this.roleIds = roleIds;
    }

    public void setPermissionIds(Set<Integer> permissionIds) {
        this.permissionIds = permissionIds;
    }

    public void setCenterIds(Set<Integer> centerIds) {
        this.centerIds = centerIds;
    }

    public void setStudyIds(Set<Integer> studyIds) {
        this.studyIds = studyIds;
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        return true;
    }

    @Override
    public IdResult run(User user, Session session) throws ActionException {
        if (roleIds == null) {
            throw new NullPropertyException(Membership.class,
                "role ids cannot be null");
        }
        if (permissionIds == null) {
            throw new NullPropertyException(Membership.class,
                "permission ids cannot be null");
        }
        if (centerIds == null) {
            throw new NullPropertyException(Membership.class,
                "center ids cannot be null");
        }
        if (studyIds == null) {
            throw new NullPropertyException(Membership.class,
                "study ids cannot be null");
        }

        this.session = session;
        sessionUtil = new SessionUtil(session);

        return null;
    }

    private saveRoles() {
        // delete source specimens no longer in use
        Map<Integer, Role> roles = sessionUtil.load(Role.class, roleIds);

        SetDifference<Role> rolesDiff = new SetDifference<Role>(
            membership.getRoleCollection(), roles.values());
        membership.setRoleCollection(rolesDiff.getAddSet());
        for (Role srcSpc : rolesDiff.getRemoveSet()) {
            session.delete(srcSpc);
        }

    }
}
