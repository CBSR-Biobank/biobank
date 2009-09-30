package edu.ualberta.med.biobank.common.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.SampleStorage;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ModelUtils {

    private static final Logger logger = Logger.getLogger(ModelUtils.class
        .getName());

    public static <E> E getObjectWithId(WritableApplicationService appService,
        Class<E> classType, Integer id) throws Exception {
        Constructor<?> constructor = classType.getConstructor();
        Object instance = constructor.newInstance();
        Method setIdMethod = classType.getMethod("setId", Integer.class);
        setIdMethod.invoke(instance, id);

        List<E> list = appService.search(classType, instance);
        if (list.size() == 0)
            return null;
        Assert.isTrue(list.size() == 1);
        return list.get(0);
    }

    /**
     * Return the object from the database with the given class type and
     * attribute value
     */
    public static <E> E getObjectWithAttr(
        WritableApplicationService appService, Class<E> classType, String attr,
        Class<?> attrType, Object value) {
        try {
            Constructor<?> constructor = classType.getConstructor();
            Object instance = constructor.newInstance();
            attr = "set" + attr.substring(0, 1).toUpperCase()
                + attr.substring(1);
            Method setMethod = classType.getMethod(attr, attrType);
            setMethod.invoke(instance, value);

            List<E> list = appService.search(classType, instance);
            if (list.isEmpty()) {
                return null;
            }
            Assert.isTrue(list.size() == 1);
            return list.get(0);
        } catch (Exception ex) {
            logger.error("Error in getObjectWithAttr method", ex);
            return null;
        }
    }

    /**
     * Return a list of objects from the database a given classType and
     * attribute value
     */
    public static <E> List<E> getObjectsWithAttr(
        WritableApplicationService appService, Class<E> classType, String attr,
        Class<?> attrType, Object value) {
        try {
            Constructor<?> constructor = classType.getConstructor();
            Object instance = constructor.newInstance();
            attr = "set" + attr.substring(0, 1).toUpperCase()
                + attr.substring(1);
            Method setMethod = classType.getMethod(attr, attrType);
            setMethod.invoke(instance, value);

            return appService.search(classType, instance);
        } catch (Exception ex) {
            logger.error("Error in getObjectsWithAttr method", ex);
            return null;
        }
    }

    public static List<Clinic> getStudyClinicCollection(
        WritableApplicationService appService, Study study)
        throws ApplicationException {
        HQLCriteria c = new HQLCriteria("select distinct clinics from "
            + Contact.class.getName() + " as contacts"
            + " inner join contacts.clinic as clinics"
            + " where contacts.studyCollection.id = ?", Arrays
            .asList(new Object[] { study.getId() }));

        return appService.query(c);
    }

    public static List<Study> getClinicStudyCollection(
        WritableApplicationService appService, Clinic clinic)
        throws ApplicationException {
        HQLCriteria c = new HQLCriteria("select distinct studies from "
            + Contact.class.getName() + " as contacts"
            + " inner join contacts.studyCollection as studies"
            + " where contacts.clinic = ?", Arrays
            .asList(new Object[] { clinic }));

        return appService.query(c);
    }

    public static boolean getBooleanValue(Boolean value, boolean defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        return value.booleanValue();
    }

    /**
     * Search an object of type clazz with the specific property :
     * <ul>
     * <li>if strict is true, then the property should be exactly equals to the
     * text</li>
     * <li>if strict is fault then, the property should contain the text</li>
     * <ul>
     * 
     * @throws ApplicationException
     */
    public static <E> List<E> queryProperty(
        WritableApplicationService appService, Class<E> clazz, String property,
        String text, boolean strict) throws ApplicationException {
        String query = "from " + clazz.getName() + " as o ";
        query += " where o." + property;
        String textParam = text;
        if (strict) {
            query += " = ?";
        } else {
            query += " like ?";
            textParam = "%" + text + "%";
        }
        return appService.query(new HQLCriteria(query, Arrays
            .asList(new Object[] { textParam })));
    }

    public static SampleStorage[] toArray(Collection<SampleStorage> collection) {
        if (collection != null) {
            // hack required here because xxx.getXxxxCollection().toArray(new
            // Xxx[0])
            // returns Object[].
            if ((collection != null) && (collection.size() == 0))
                return null;

            int count = 0;
            SampleStorage[] arr = new SampleStorage[collection.size()];
            for (SampleStorage ss : collection) {
                arr[count] = ss;
                ++count;
            }
            return arr;
        }
        return null;
    }
}
