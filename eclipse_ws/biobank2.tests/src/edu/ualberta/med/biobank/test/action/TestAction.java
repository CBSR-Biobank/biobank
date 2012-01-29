package edu.ualberta.med.biobank.test.action;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;

import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.test.action.SessionProvider.Mode;
import edu.ualberta.med.biobank.test.action.helper.SpecimenTypeHelper;

public class TestAction {
    @Rule
    public final TestName testName = new TestName();

    protected static final String SUPER_ADMIN_LOGIN = "superadmin";
    protected static final Random R = new Random();
    protected static final SessionProvider SESSION_PROVIDER;
    protected static final LocalActionExecutor EXECUTOR;

    protected Session session;

    static {
        SESSION_PROVIDER = new SessionProvider(Mode.RUN);
        EXECUTOR = new LocalActionExecutor(SESSION_PROVIDER);

        User user = getSuperAdminUser();
        EXECUTOR.setUserId(user.getId());
    }

    /**
     * Done for each test of this class.
     */
    @Before
    public void setUp() throws Exception {
        session = SESSION_PROVIDER.openSession();
    }

    /**
     * Done for each test of this class.
     */
    @After
    public void tearDown() throws Exception {
        session.close();
    }

    public static User getSuperAdminUser() {
        Session session = SESSION_PROVIDER.openSession();

        // check if user already exists
        @SuppressWarnings("unchecked")
        List<User> users = session.createCriteria(User.class)
            .add(Restrictions.eq("login", SUPER_ADMIN_LOGIN))
            .list();

        if (users.size() >= 1) return users.get(0);

        session.beginTransaction();

        ActivityStatus active = (ActivityStatus) session
            .createCriteria(ActivityStatus.class)
            .add(Restrictions.eq("name", "Active"))
            .list().iterator().next();

        User superAdmin = new User();
        superAdmin.setLogin(SUPER_ADMIN_LOGIN);
        superAdmin.setCsmUserId(0L);
        superAdmin.setRecvBulkEmails(false);
        superAdmin.setFullName("super admin");
        superAdmin.setEmail(randString());
        superAdmin.setNeedPwdChange(false);
        superAdmin.setNeedPwdChange(false);
        superAdmin.setActivityStatus(active);

        session.save(superAdmin);

        Membership membership = new Membership();
        membership.setPrincipal(superAdmin);
        membership.getPermissionCollection().add(PermissionEnum.ADMINISTRATION);

        session.save(membership);

        session.getTransaction().commit();
        session.close();

        return superAdmin;
    }

    private static Date convertToGmt(Date localDate) {
        // create a new local calendar
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        // Returns the number of milliseconds since January 1, 1970, 00:00:00
        // GMT
        long msFromEpochGmt = localDate.getTime();

        // gives you the current offset in ms from GMT at the current date
        int offsetFromUTC = tz.getOffset(msFromEpochGmt);

        // create a new calendar in GMT timezone, set to this date and remove
        // the offset
        Calendar gmtCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        gmtCal.setTime(localDate);
        gmtCal.add(Calendar.MILLISECOND, -offsetFromUTC);
        return gmtCal.getTime();
    }

    /**
     * NOT REQUIRED ANYMORE SINCE TESTS ARE RUN LOCALLY
     */
    @Deprecated
    public static boolean compareDateInHibernate(Date localDate,
        Date hibernateDate) {
        Date convertdate = convertToGmt(localDate);
        return convertdate.equals(hibernateDate);
    }

    public static boolean compareDouble(Double d1, Double d2) {
        return Math.abs((d1 - d2)) < 0.0001;
    }

    protected List<SpecimenType> getSpecimenTypes() {
        List<SpecimenType> spcTypes =
            SpecimenTypeHelper.getSpecimenTypes(session);
        return spcTypes;
    }

    protected List<ContainerLabelingScheme> getContainerLabelingSchemes() {
        Query q =
            session.createQuery("from "
                + ContainerLabelingScheme.class.getName());
        @SuppressWarnings("unchecked")
        List<ContainerLabelingScheme> labelingSchemes = q.list();
        Assert.assertTrue("container labeling schemes not found in database",
            !labelingSchemes.isEmpty());
        return labelingSchemes;
    }

    protected void deleteOriginInfos(Integer centerId) {
        // delete origin infos
        session.clear();
        session.beginTransaction();
        Query q = session.createQuery("DELETE FROM "
            + OriginInfo.class.getName() + " oi WHERE oi.center.id=?");
        q.setParameter(0, centerId);
        q.executeUpdate();
        session.getTransaction().commit();
    }

    protected String getMethodName() {
        return testName.getMethodName();
    }

    protected String getMethodNameR() {
        return testName.getMethodName() + R.nextInt();
    }

    protected static String randString() {
        return new BigInteger(130, R).toString(32);
    }
}
