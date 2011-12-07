package edu.ualberta.med.biobank.test.action;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;

import edu.ualberta.med.biobank.common.action.security.UserGetAction;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.test.AllTests;
import edu.ualberta.med.biobank.test.TestDatabase;

public class TestAction extends TestDatabase {
    private SessionFactory sessionFactory;
    protected Session session;

    protected User currentUser;

    /**
     * Done for each test of this class.
     */
    @Override
    @Before
    public void setUp() throws Exception {
        // configure() configures settings from hibernate.cfg.xml found into the
        // biobank-orm jar
        sessionFactory = new Configuration().configure().buildSessionFactory();
        super.setUp();
        currentUser = appService.doAction(
            new UserGetAction(AllTests.userLogin)).getUser();
    }

    /**
     * Done for each test of this class.
     */
    @Override
    @After
    public void tearDown() throws Exception {
        closeHibernateSession();
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    public void openHibernateSession() {
        session = sessionFactory.openSession();
        session.setFlushMode(FlushMode.MANUAL);
        session.beginTransaction();
    }

    public void closeHibernateSession() {
        if ((session != null) && session.isConnected()) {
            session.getTransaction().commit();
            session.close();
            session = null;
        }
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

    public static boolean compareDateInHibernate(Date localDate,
        Date hibernateDate) {
        Date convertdate = convertToGmt(localDate);
        return convertdate.equals(hibernateDate);
    }

    public static boolean compareDouble(Double d1, Double d2) {
        return Math.abs((d1 - d2)) < 0.0001;
    }
}
