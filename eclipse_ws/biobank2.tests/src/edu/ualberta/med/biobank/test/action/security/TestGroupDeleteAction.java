package edu.ualberta.med.biobank.test.action.security;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.junit.Test;

import edu.ualberta.med.biobank.common.action.exception.AccessDeniedException;
import edu.ualberta.med.biobank.common.action.exception.ModelNotFoundException;
import edu.ualberta.med.biobank.common.action.security.GroupDeleteAction;
import edu.ualberta.med.biobank.common.action.security.GroupDeleteInput;
import edu.ualberta.med.biobank.model.Group;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Rank;
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.test.Factory.Domain;
import edu.ualberta.med.biobank.test.action.TestAction;
import edu.ualberta.med.biobank.test.action.security.util.TestCase;
import edu.ualberta.med.biobank.test.action.security.util.TestCase.IIterableBuilder;

public class TestGroupDeleteAction extends TestAction {
    TestCase<Scenario, Boolean> ADMIN = new TestCase<Scenario, Boolean>() {
        @Override
        public Boolean run(Scenario s) {
            Transaction tx = session.beginTransaction();
            Group g = factory.createGroup();
            Membership m = factory.createMembership(s.domain, s.rank);

            // all permissions and a role to make sure that an
            // administrator has power over all of them without
            // explicitly assigning one
            m.getPermissions().addAll(PermissionEnum.valuesList());

            Role role = factory.createRole();
            role.getPermissions().addAll(PermissionEnum.valuesList());
            session.update(role);

            m.getRoles().add(role);
            session.update(m);
            tx.commit();

            try {
                execAs(s.user, new GroupDeleteAction(new GroupDeleteInput(g)));
                return Boolean.TRUE;
            } catch (Throwable t) {
            }

            return Boolean.FALSE;
        }
    };

    @Test
    public void asGlobalAdmin() {
        Transaction tx = session.beginTransaction();
        User user = factory.createUser();
        // Membership userMembership =
        // factory.createMembership(Domain.GLOBAL, Rank.ADMINISTRATOR);
        tx.commit();

        Scenario.Builder b = new Scenario.Builder().user(user).allDomains();

        ADMIN.run(b.ranks(Rank.NORMAL, Rank.MANAGER), true);
        ADMIN.run(b.ranks(Rank.ADMINISTRATOR), false);
    }

    @Test
    public void asCenterAdmin() {
        Transaction tx = session.beginTransaction();
        User user = factory.createUser();
        // Membership userMembership =
        // factory.createMembership(Domain.CENTER, Rank.ADMINISTRATOR);
        tx.commit();

        Scenario.Builder b = new Scenario.Builder().user(user)
            .ranks(Rank.NORMAL, Rank.MANAGER);

        ADMIN.run(b.domains(Domain.CENTER, Domain.CENTER_STUDY), true);
        ADMIN.run(b.domains(Domain.STUDY, Domain.GLOBAL), false);
        ADMIN.run(b.allDomains().ranks(Rank.ADMINISTRATOR), false);
    }

    @Test
    public void asStudyAdmin() {
        Transaction tx = session.beginTransaction();
        User user = factory.createUser();
        // Membership userMembership =
        // factory.createMembership(Domain.STUDY, Rank.ADMINISTRATOR);
        tx.commit();

        Scenario.Builder b = new Scenario.Builder().user(user)
            .ranks(Rank.NORMAL, Rank.MANAGER);

        ADMIN.run(b.domains(Domain.STUDY, Domain.CENTER_STUDY), true);
        ADMIN.run(b.domains(Domain.CENTER, Domain.GLOBAL), false);
        ADMIN.run(b.allDomains().ranks(Rank.ADMINISTRATOR), false);
    }

    @Test
    public void asCenterStudyAdmin() {
        Transaction tx = session.beginTransaction();
        User user = factory.createUser();

        @SuppressWarnings("unused")
        Membership userMembership =
            factory.createMembership(Domain.CENTER_STUDY, Rank.ADMINISTRATOR);
        tx.commit();

        Scenario.Builder b = new Scenario.Builder().user(user)
            .ranks(Rank.NORMAL, Rank.MANAGER);

        ADMIN.run(b.domains(Domain.CENTER_STUDY), true);
        ADMIN.run(b.domains(Domain.CENTER, Domain.STUDY, Domain.GLOBAL), false);
        ADMIN.run(b.allDomains().ranks(Rank.ADMINISTRATOR), false);
    }

    @Test
    public void normalAccess() {
        Transaction tx = session.beginTransaction();
        Group group = factory.createGroup();
        User user = factory.createUser();
        factory.createMembership(Domain.GLOBAL, Rank.NORMAL);
        tx.commit();

        try {
            execAs(user, new GroupDeleteAction(new GroupDeleteInput(group)));
            Assert.fail();
        } catch (AccessDeniedException e) {
        }
    }

    @Test
    public void asGlobalManager() {
        Transaction tx = session.beginTransaction();
        Group group = factory.createGroup();
        User user = factory.createUser();
        factory.createMembership(Domain.CENTER_STUDY, Rank.MANAGER);
        tx.commit();

        execAs(user, new GroupDeleteAction(new GroupDeleteInput(group)));
    }

    @Test
    public void transientGroup() {
        Group group = new Group();
        group.setId(0);

        try {
            exec(new GroupDeleteAction(new GroupDeleteInput(group)));
            Assert.fail();
        } catch (ModelNotFoundException e) {
        }
    }

    @Test
    public void deleted() {
        Transaction tx = session.beginTransaction();
        Group group = factory.createGroup();
        tx.commit();

        exec(new GroupDeleteAction(new GroupDeleteInput(group)));

        List<?> results = session.createCriteria(Role.class)
            .add(Restrictions.idEq(group.getId()))
            .list();

        Assert.assertTrue("group not deleted", results.isEmpty());
    }

    static class Scenario {
        final User user;
        final Domain domain;
        final Rank rank;

        Scenario(User u, Domain d, Rank r) {
            this.user = u;
            this.domain = d;
            this.rank = r;
        }

        @Override
        public String toString() {
            return "Scenario [user=" + user + ", domain=" + domain + ", rank="
                + rank + "]";
        }

        public static class Builder implements IIterableBuilder<Scenario> {
            private User user;
            private Set<Domain> domains = new HashSet<Domain>();
            private Set<Rank> ranks = new HashSet<Rank>();

            Builder user(User user) {
                this.user = user;
                return this;
            }

            Builder domains(Domain... domains) {
                this.domains.clear();
                this.domains.addAll(Arrays.asList(domains));
                return this;
            }

            Builder allDomains() {
                return domains(Domain.values());
            }

            Builder ranks(Rank... ranks) {
                this.ranks.clear();
                this.ranks.addAll(Arrays.asList(ranks));
                return this;
            }

            Builder allRanks() {
                return ranks(Rank.values());
            }

            @Override
            public Set<Scenario> build() {
                Set<Scenario> scenarios = new HashSet<Scenario>();
                for (Domain domain : domains) {
                    for (Rank rank : ranks) {
                        Scenario scenario = new Scenario(user, domain, rank);
                        scenarios.add(scenario);
                    }
                }
                return scenarios;
            }
        }
    }
}
