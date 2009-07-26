package edu.ualberta.med.biobank.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Assert;

import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ModelUtils {

    public static List<Container> getTopContainersForSite(
        WritableApplicationService appService, Site site)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria("from "
            + Container.class.getName() + " where site.id = " + site.getId()
            + " and position.parentContainer is null");
        return appService.query(criteria);
    }

    public static Container newContainer(Container parent) {
        Container newContainer = new Container();
        ContainerPosition position = new ContainerPosition();
        position.setParentContainer(parent);
        position.setContainer(newContainer);
        newContainer.setPosition(position);
        return newContainer;
    }

    public static Object getObjectWithId(WritableApplicationService appService,
        Class<?> classType, Integer id) throws Exception {
        Constructor<?> constructor = classType.getConstructor();
        Object instance = constructor.newInstance();
        Method setIdMethod = classType.getMethod("setId", Integer.class);
        setIdMethod.invoke(instance, id);

        List<?> list = appService.search(classType, instance);
        Assert.isTrue(list.size() == 1);
        return list.get(0);
    }

    public static ContainerType getCabinetType(
        WritableApplicationService appService) {
        ContainerType type = new ContainerType();
        type.setName("Cabinet");
        List<ContainerType> types;
        try {
            types = appService.search(ContainerType.class, type);
            if (types.size() == 1) {
                return types.get(0);
            }
        } catch (ApplicationException e) {
        }
        return null;
    }

    public static Container getContainerWithBarcode(
        WritableApplicationService appService, String barcode)
        throws ApplicationException {
        Container container = new Container();
        container.setBarcode(barcode);
        List<Container> containers = appService.search(Container.class,
            container);
        if (containers.size() == 1) {
            return containers.get(0);
        }
        return null;
    }

    public static String getSamplePosition(Sample sample) {
        SamplePosition position = sample.getSamplePosition();
        if (position == null) {
            return "none";
        } else {
            // String positionString = getPositionString(position);
            // Container container = position.getContainer();
            // ContainerPosition containerPosition = container.getPosition();
            // Container parent = containerPosition.getParentContainer();
            // while (parent != null) {
            // positionString = getPositionString(containerPosition) + ":"
            // + positionString;
            // container = parent;
            // containerPosition = parent.getPosition();
            // parent = containerPosition.getParentContainer();
            // }
            // positionString = container.getBarcode() + ":" + positionString;
            // return positionString;
            String containerPositionBarcode = position.getContainer().getName();
            return containerPositionBarcode + getPositionString(position);
        }
    }

    public static String getPositionString(AbstractPosition position) {
        int dim1 = position.getPositionDimensionOne();
        int dim2 = position.getPositionDimensionTwo();
        String dim1String = String.valueOf(dim1);
        String dim2String = String.valueOf(dim2);
        return dim1String + dim2String;
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
        if (strict) {
            query += " = '" + text + "'";
        } else {
            query += " like '%" + text + "%'";
        }
        return appService.query(new HQLCriteria(query));
    }

    public static SampleStorage[] toArray(Collection<SampleStorage> collection) {
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
}
