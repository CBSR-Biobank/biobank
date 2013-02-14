package edu.ualberta.med.biobank.test;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.junit.After;
import org.junit.Before;

import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.tools.SessionProvider;
import edu.ualberta.med.biobank.tools.SessionProvider.Mode;

public class TestDb extends BaseTest {
    private static final String GLOBAL_ADMIN_LOGIN = "globaladmin";
    private static final User GLOBAL_ADMIN;
    private static final SessionProvider SESSION_PROVIDER;

    static {
        SESSION_PROVIDER = new SessionProvider(Mode.RUN);
        // SESSION_PROVIDER = new SessionProvider(Mode.DEBUG);

        GLOBAL_ADMIN = getOrCreateSuperUser();
    }

    protected Session session;
    protected Factory factory;

    @Before
    public void setUp() throws Exception {
        session = getSessionProvider().openSession();
        factory = new Factory(session, getMethodNameR());
        session.beginTransaction();
    }

    @After
    public void tearDown() throws Exception {
        Transaction tx = session.getTransaction();
        if (tx != null && tx.isActive()) {
            tx.rollback();
        }

        factory = null;
        session.close();
    }

    protected static SessionProvider getSessionProvider() {
        return SESSION_PROVIDER;
    }

    protected static Session openSession() {
        return SESSION_PROVIDER.openSession();
    }

    protected static User getGlobalAdmin() {
        return GLOBAL_ADMIN;
    }

    private static User getOrCreateSuperUser() {
        Session session = getSessionProvider().openSession();

        // check if user already exists
        @SuppressWarnings("unchecked")
        List<User> users = session.createCriteria(User.class)
            .add(Restrictions.eq("login", GLOBAL_ADMIN_LOGIN))
            .list();

        if (users.size() >= 1) return users.get(0);

        Transaction tx = session.beginTransaction();

        User globalAdmin = new User();
        globalAdmin.setLogin(GLOBAL_ADMIN_LOGIN);
        globalAdmin.setCsmUserId(-1L);
        globalAdmin.setRecvBulkEmails(false);
        globalAdmin.setFullName(GLOBAL_ADMIN_LOGIN);
        globalAdmin.setEmail(GLOBAL_ADMIN_LOGIN);
        globalAdmin.setNeedPwdChange(false);
        globalAdmin.setNeedPwdChange(false);
        globalAdmin.setActivityStatus(ActivityStatus.ACTIVE);

        session.save(globalAdmin);

        Membership membership = new Membership();
        membership.getDomain().setAllCenters(true);
        membership.getDomain().setAllStudies(true);

        membership.setUserManager(true);
        membership.setEveryPermission(true);
        membership.setPrincipal(globalAdmin);
        globalAdmin.getMemberships().add(membership);

        session.save(membership);

        tx.commit();
        session.close();

        return globalAdmin;
    }
}
