package edu.ualberta.med.biobank.test;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.junit.After;
import org.junit.Before;

import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.Rank;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.test.action.SessionProvider;
import edu.ualberta.med.biobank.test.action.SessionProvider.Mode;

public class TestDb extends TestBase {
    private static final String SUPER_USER_LOGIN = "superadmin";
    private static final User SUPER_USER;
    private static final SessionProvider SESSION_PROVIDER;

    static {
        SESSION_PROVIDER = new SessionProvider(Mode.RUN);
        SUPER_USER = getOrCreateSuperUser();
    }

    protected Session session;
    protected Factory factory;

    @Before
    public void setUp() throws Exception {
        session = getSessionProvider().openSession();
        factory = new Factory(session, getMethodNameR());
    }

    @After
    public void tearDown() throws Exception {
        factory = null;
        session.close();
    }

    protected static SessionProvider getSessionProvider() {
        return SESSION_PROVIDER;
    }

    protected static Session openSession() {
        return SESSION_PROVIDER.openSession();
    }

    protected static User getSuperUser() {
        return SUPER_USER;
    }

    private static User getOrCreateSuperUser() {
        Session session = getSessionProvider().openSession();

        // check if user already exists
        @SuppressWarnings("unchecked")
        List<User> users = session.createCriteria(User.class)
            .add(Restrictions.eq("login", SUPER_USER_LOGIN))
            .list();

        if (users.size() >= 1) return users.get(0);

        Transaction tx = session.beginTransaction();

        User superUser = new User();
        superUser.setLogin(SUPER_USER_LOGIN);
        superUser.setCsmUserId(-1L);
        superUser.setRecvBulkEmails(false);
        superUser.setFullName("super admin");
        superUser.setEmail(Utils.getRandomString(5, 10));
        superUser.setNeedPwdChange(false);
        superUser.setNeedPwdChange(false);
        superUser.setActivityStatus(ActivityStatus.ACTIVE);

        session.save(superUser);

        Membership membership = new Membership();
        membership.setRank(Rank.ADMINISTRATOR);
        membership.setPrincipal(superUser);
        superUser.getMemberships().add(membership);

        session.save(membership);

        tx.commit();
        session.close();

        return superUser;
    }
}
