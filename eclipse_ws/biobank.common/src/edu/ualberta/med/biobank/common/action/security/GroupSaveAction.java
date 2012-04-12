package edu.ualberta.med.biobank.common.action.security;

import java.util.Set;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.security.UserManagerPermission;
import edu.ualberta.med.biobank.i18n.S;
import edu.ualberta.med.biobank.model.Domain;
import edu.ualberta.med.biobank.model.Group;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.util.SetDiff;
import edu.ualberta.med.biobank.util.SetDiff.Pair;

public class GroupSaveAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;
    private static final Permission PERMISSION = new UserManagerPermission();

    private final GroupSaveInput input;

    public GroupSaveAction(GroupSaveInput input) {
        this.input = input;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return PERMISSION.isAllowed(context);
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        User executingUser = context.getUser();
        Group group = context.get(Group.class, input.getGroupId(), new Group());

        checkFullyManageable(group, executingUser);

        group.setName(input.getName());
        group.setDescription(input.getDescription());

        Set<User> users = context.load(User.class, input.getUserIds());
        group.getUsers().clear();
        group.getUsers().addAll(users);

        setMemberships(context, group);

        checkFullyManageable(group, executingUser);

        context.getSession().saveOrUpdate(group);

        return new IdResult(group.getId());
    }

    @SuppressWarnings("nls")
    private void checkFullyManageable(Group group, User executingUser) {
        if (!group.isFullyManageable(executingUser)) {
            throw new ActionException(
                S.tr("You do not have adequate permissions to save this group."));
        }
    }

    private void setMemberships(ActionContext context, Group group) {
        SetDiff<Membership> diff = new SetDiff<Membership>(
            group.getMemberships(), input.getMemberships());

        for (Membership m : diff.getRemovals()) {
            group.getMemberships().remove(m);
            context.getSession().delete(m);
        }

        for (Membership m : diff.getAdditions()) {
            group.getMemberships().add(m);
            m.setPrincipal(group);
        }

        for (Pair<Membership> pair : diff.getIntersection()) {
            Membership oldM = pair.getOld();
            Membership newM = pair.getNew();

            oldM.getPermissions().clear();
            oldM.getPermissions().addAll(newM.getPermissions());

            oldM.getRoles().clear();
            oldM.getRoles().addAll(newM.getRoles());

            oldM.setUserManager(newM.isUserManager());
            oldM.setEveryPermission(newM.isEveryPermission());

            // TODO: throw away old domain, copy into new? Shorter.
            Domain newD = newM.getDomain();
            Domain oldD = oldM.getDomain();

            oldD.getCenters().clear();
            oldD.getCenters().addAll(newD.getCenters());
            oldD.setAllCenters(newD.isAllCenters());

            oldD.getStudies().clear();
            oldD.getStudies().addAll(newD.getStudies());
            oldD.setAllStudies(newD.isAllStudies());
        }

        for (Membership m : group.getMemberships()) {
            m.reducePermissions();
        }
    }
}
