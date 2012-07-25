package edu.ualberta.med.biobank.action.security;

import java.util.Set;

import junit.framework.Assert;

import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.junit.Test;

import edu.ualberta.med.biobank.action.security.MembershipContext;
import edu.ualberta.med.biobank.action.security.MembershipContextGetAction;
import edu.ualberta.med.biobank.action.security.MembershipContextGetInput;
import edu.ualberta.med.biobank.action.security.UserSaveAction;
import edu.ualberta.med.biobank.action.security.UserSaveInput;
import edu.ualberta.med.biobank.action.security.UserSaveOutput;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Domain;
import edu.ualberta.med.biobank.model.Group;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.action.ActionTest;

public class TestUserSaveAction extends ActionTest {
    @Test
    public void insert() {
        Transaction tx = session.beginTransaction();
        Role role = factory.createRole();
        Center center = factory.createSite();
        Study study = factory.createStudy();
        Group group = factory.createGroup();
        tx.commit();

        Membership membership = new Membership();
        Domain domain = membership.getDomain();
        domain.getCenters().add(center);
        domain.getStudies().add(study);

        membership.getRoles().add(role);
        membership.getPermissions().add(PermissionEnum.CLINIC_CREATE);

        String name = factory.getName(User.class);

        User user = new User();
        user.setLogin(name);
        user.setEmail(name);
        user.setFullName(name);

        user.getMemberships().add(membership);
        membership.setPrincipal(user);

        user.getGroups().add(group);

        MembershipContext context = exec(new MembershipContextGetAction(
            new MembershipContextGetInput())).getContext();

        UserSaveOutput result =
            exec(new UserSaveAction(new UserSaveInput(user, context, "asdfa")));

        Object o = session.createCriteria(User.class)
            .add(Restrictions.idEq(result.getUserId()))
            .uniqueResult();

        Assert.assertTrue(o instanceof User);

        User saved = (User) o;

        Assert.assertEquals("login not inserted properly",
            user.getLogin(), saved.getLogin());

        Assert.assertEquals("email not inserted properly",
            user.getEmail(), saved.getEmail());

        Assert.assertEquals("full name not inserted properly",
            user.getFullName(), saved.getFullName());

        Set<Membership> savedMemberships = saved.getMemberships();

        Assert.assertEquals("memberships not inserted properly",
            user.getMemberships().size(), savedMemberships.size());

        Membership savedMembership = savedMemberships.iterator().next();

        Assert.assertEquals("membership permissions not inserted properly",
            membership.getPermissions(), savedMembership.getPermissions());

        Assert.assertEquals("membership permissions",
            membership.getPermissions(), savedMembership.getPermissions());

        Assert.assertEquals("membership roles",
            membership.getRoles(), savedMembership.getRoles());

        Assert.assertTrue("membership domain not inserted properly",
            domain.isEquivalent(savedMembership.getDomain()));

        Set<Group> savedGroups = user.getGroups();

        Assert.assertEquals("groups not inserted properly",
            user.getGroups(), savedGroups);
    }
}
