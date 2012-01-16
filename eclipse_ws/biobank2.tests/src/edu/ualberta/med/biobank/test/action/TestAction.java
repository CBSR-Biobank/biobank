package edu.ualberta.med.biobank.test.action;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.test.action.helper.SpecimenTypeHelper;

public class TestAction {

    protected MockActionExecutor actionExecutor;

    protected static Random r;

    protected User currentUser;

    protected Session session;

    /**
     * Done for each test of this class.
     */
    @Before
    public void setUp() throws Exception {
        r = new Random();
        actionExecutor = new MockActionExecutor(false);
    }

    /**
     * Done for each test of this class.
     */
    @After
    public void tearDown() throws Exception {
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
                + OriginInfo.class.getName() + " oi WHERE oi.center.id=?");
        q.setParameter(0, centerId);
        q.executeUpdate();
    }

    public Session getSession() {
        return actionExecutor.getSession();
    }

    public void openHibernateSession() {

    }

    public void closeHibernateSession() {

    }
}
