package edu.ualberta.med.biobank.action;

import java.lang.annotation.Annotation;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.hibernate.Query;
import org.hibernate.Transaction;
import org.junit.Assert;
import org.junit.Before;

import edu.ualberta.med.biobank.DbTest;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.ShippingMethod;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.security.User;

public class ActionTest extends DbTest {
    private static final LocalActionExecutor EXECUTOR;

    static {
        EXECUTOR = new LocalActionExecutor(DbTest.getSessionProvider());
        EXECUTOR.setUserId(getGlobalAdmin().getId());
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        // let action tests manager their own transactions.
        Transaction tx = session.getTransaction();
        if (tx != null && tx.isActive()) {
            tx.rollback();
        }

        // by default, always execute as the super user
        getExecutor().setUserId(getGlobalAdmin().getId());
    }

    protected static IActionExecutor getExecutor() {
        return EXECUTOR;
    }

    protected static <T extends ActionResult> T exec(Action<T> action)
        throws ActionException {
        return getExecutor().exec(action);
    }

    protected static <T extends ActionResult> T execAs(User user,
        Action<T> action) throws ActionException {
        Integer oldUserId = getExecutor().getUserId();
        try {
            getExecutor().setUserId(user.getId());
            return getExecutor().exec(action);
        } finally {
            getExecutor().setUserId(oldUserId);
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

    /**
     * REQUIRED WHEN TESTS ARE RUN REMOTELY
     */
    public static boolean compareDateInHibernate(Date localDate,
        Date hibernateDate) {
        Date convertdate = convertToGmt(localDate);
        return convertdate.equals(hibernateDate);
    }

    public static boolean compareDouble(Double d1, Double d2) {
        return Math.abs((d1 - d2)) < 0.0001;
    }

    protected List<SpecimenType> getSpecimenTypes() {
        Query q = session.createQuery("from " + SpecimenType.class.getName());
        @SuppressWarnings("unchecked")
        List<SpecimenType> spcTypes = q.list();
        Assert.assertTrue("specimen types not found in database",
            !spcTypes.isEmpty());
        return spcTypes;
    }

    protected Map<String, ContainerLabelingScheme> getContainerLabelingSchemes() {
        Map<String, ContainerLabelingScheme> result =
            new HashMap<String, ContainerLabelingScheme>();
        Query q =
            session.createQuery("from "
                + ContainerLabelingScheme.class.getName());
        @SuppressWarnings("unchecked")
        List<ContainerLabelingScheme> labelingSchemes = q.list();
        Assert.assertTrue("container labeling schemes not found in database",
            !labelingSchemes.isEmpty());
        for (ContainerLabelingScheme scheme : labelingSchemes) {
            result.put(scheme.getName(), scheme);
        }
        return result;
    }

    protected List<ShippingMethod> getShippingMethods() {
        Query q =
            session.createQuery("from " + ShippingMethod.class.getName());
        @SuppressWarnings("unchecked")
        List<ShippingMethod> labelingSchemes = q.list();
        Assert.assertTrue("shipping methods not found in database",
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

    public static boolean contains(ConstraintViolationException e,
        Class<? extends Annotation> annotationKlazz, Class<?> klazz) {
        Annotation annotation = klazz.getAnnotation(annotationKlazz);
        return contains(e, annotation);
    }

    public static boolean contains(ConstraintViolationException e,
        Class<? extends Annotation> annotationKlazz, Class<?> klazz,
        String methodName) {
        Annotation annotation;
        try {
            annotation = klazz.getMethod(methodName)
                .getAnnotation(annotationKlazz);
        } catch (Throwable caught) {
            throw new RuntimeException(caught);
        }
        return contains(e, annotation);
    }

    public static boolean contains(ConstraintViolationException e, Annotation a) {
        if (a == null) {
            throw new NullPointerException("annotation cannot be null");
        }
        for (ConstraintViolation<?> cv : e.getConstraintViolations()) {
            if (cv.getConstraintDescriptor().getAnnotation().equals(a))
                return true;
        }
        return false;
    }
}
