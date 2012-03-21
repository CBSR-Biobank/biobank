package edu.ualberta.med.biobank.test.action.security;

import java.util.Set;

import junit.framework.Assert;

import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.junit.Test;

import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.security.GroupSaveAction;
import edu.ualberta.med.biobank.common.action.security.GroupSaveInput;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Group;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.test.action.TestAction;

public class TestGroupSaveAction extends TestAction {
    @Test
    public void insert() {
        Transaction tx = session.beginTransaction();
        Role role = factory.createRole();
        Center center = factory.createSite();
        Study study = factory.createStudy();
        tx.commit();

        Membership membership = new Membership();
        membership.setCenter(center);
        membership.setStudy(study);
        membership.getRoles().add(role);
        membership.getPermissions().add(PermissionEnum.CLINIC_CREATE);

        Group group = new Group();
        group.setName(factory.getName(Group.class));
        group.setDescription("nothing to see here");

        group.getMemberships().add(membership);
        membership.setPrincipal(group);

        IdResult result = exec(new GroupSaveAction(new GroupSaveInput(group)));

        Object o = session.createCriteria(Group.class)
            .add(Restrictions.idEq(result.getId()))
            .uniqueResult();

        Assert.assertTrue(o instanceof Group);

        Group saved = (Group) o;

        Assert.assertEquals("name not inserted properly",
            group.getName(), saved.getName());

        Assert.assertEquals("description not inserted properly",
            group.getDescription(), saved.getDescription());

        Set<Membership> savedMemberships = saved.getMemberships();

        Assert.assertEquals("memberships not inserted properly",
            group.getMemberships().size(), savedMemberships.size());

        Membership savedMembership = savedMemberships.iterator().next();

        Assert.assertEquals("membership permissions not inserted properly",
            membership.getPermissions(), savedMembership.getPermissions());

        Assert.assertEquals("membership permissions",
            membership.getPermissions(), savedMembership.getPermissions());

        Assert.assertEquals("membership roles",
            membership.getRoles(), savedMembership.getRoles());

        Assert.assertEquals("membership center",
            membership.getCenter(), savedMembership.getCenter());

        Assert.assertEquals("membership study",
            membership.getStudy(), savedMembership.getStudy());
    }
}
