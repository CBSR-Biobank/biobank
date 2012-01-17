package edu.ualberta.med.biobank.test.action;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;

import edu.ualberta.med.biobank.common.action.activityStatus.ActivityStatusEnum;
import edu.ualberta.med.biobank.common.action.security.MembershipSaveAction;
import edu.ualberta.med.biobank.common.action.security.UserSaveAction;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.test.action.helper.SpecimenTypeHelper;

public class TestAction {

    protected static Random r;

    protected static MockActionExecutor actionExecutor;

    protected static Session session;

    protected static final String SUPER_ADMIN_LOGIN = "superadmin";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        r = new Random();
        actionExecutor = new MockActionExecutor(false);
        session = actionExecutor.getSession();
        User user = createSuperAdminUser();
        actionExecutor.setUser(user);
    }

    @AfterClass
    public static void tearDownBeforeClass() throws Exception {
    }

    /**
     * Done for each test of this class.
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * Done for each test of this class.
     */
    @After
    public void tearDown() throws Exception {
    }

    public void setUser(User user) {
        actionExecutor.setUser(user);
    }

    public User getUser() {
        return actionExecutor.getUser();
    }

    public static User createSuperAdminUser() {
        // check if user already exists
        Query q = actionExecutor.getSession().createQuery("FROM "
            + User.class.getName() + " WHERE login=?");
        q.setParameter(0, SUPER_ADMIN_LOGIN);
        @SuppressWarnings("unchecked")
        List<User> users = q.list();

        if (users.size() >= 1) return users.get(0);

        UserSaveAction userSaveAction = new UserSaveAction();
        userSaveAction.setLogin(SUPER_ADMIN_LOGIN);
        userSaveAction.setCsmUserId(0L);
        userSaveAction.setRecvBulkEmails(false);
        userSaveAction.setFullName("super admin");
        userSaveAction.setEmail("");
        userSaveAction.setNeedPwdChange(false);
        userSaveAction.setActivityStatusId(ActivityStatusEnum.ACTIVE.getId());
        userSaveAction.setGroupIds(new HashSet<Integer>());
        userSaveAction.setMembershipIds(new HashSet<Integer>());
        Integer userId = actionExecutor.exec(userSaveAction).getId();

        // set up a super admin user
        MembershipSaveAction membershipSaveAction = new MembershipSaveAction();
        membershipSaveAction.setPermissionIds(new HashSet<Integer>(Arrays
            .asList(PermissionEnum.ADMINISTRATION.getId())));
        membershipSaveAction.setRoleIds(new HashSet<Integer>());
        membershipSaveAction.setPrincipalId(userId);
        actionExecutor.exec(membershipSaveAction).getId();

        return (User) session.load(User.class, userId);
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
            SpecimenTypeHelper.getSpecimenTypes(getSession());
        return spcTypes;
    }

    protected List<ContainerLabelingScheme> getContainerLabelingSchemes() {
        Query q =
            getSession().createQuery("from "
                + ContainerLabelingScheme.class.getName());
        @SuppressWarnings("unchecked")
        List<ContainerLabelingScheme> labelingSchemes = q.list();
        Assert.assertTrue("container labeling schemes not found in database",
            !labelingSchemes.isEmpty());
        return labelingSchemes;
    }

    protected void deleteOriginInfos(Integer centerId) {
        // delete origin infos
        Query q =
            getSession().createQuery("DELETE FROM "
                + OriginInfo.class.getName() + " oi WHERE oi.center.id=?"
                + " AND oi.specimenCollection.size = 0");
        q.setParameter(0, centerId);
        q.executeUpdate();
    }

    public Session getSession() {
        return actionExecutor.getSession();
    }
}
