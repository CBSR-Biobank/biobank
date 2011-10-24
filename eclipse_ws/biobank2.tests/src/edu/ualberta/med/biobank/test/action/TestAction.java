package edu.ualberta.med.biobank.test.action;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;

import edu.ualberta.med.biobank.test.TestDatabase;

public class TestAction extends TestDatabase {
    protected SessionFactory sessionFactory;

    @Override
    @Before
    public void setUp() throws Exception {
        // A SessionFactory is set up once for an application
        sessionFactory = new Configuration().configure() // configures
                                                         // settings
            // from
            // hibernate.cfg.xml
            .buildSessionFactory();
        super.setUp();
    }

    @Override
    @After
    public void tearDown() throws Exception {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
        super.tearDown();
    }

    public abstract class HibernateCheck {

        public void run() throws Exception {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            try {
                check(session);
            } finally {
                session.getTransaction().commit();
                session.close();
            }
        }

        public abstract void check(Session session) throws Exception;
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
}
