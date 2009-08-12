package edu.ualberta.med.biobank.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Assert;

import edu.ualberta.med.biobank.LabelingScheme;
import edu.ualberta.med.biobank.RowColPos;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ModelUtils {

    public static List<Container> getTopContainersForSite(
        WritableApplicationService appService, Site site)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria("from "
            + Container.class.getName()
            + " where site.id=? and position is null");

        criteria.setParameters(Arrays.asList(new Object[] { site.getId() }));
        return appService.query(criteria);
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

    public static <E> E getObjectWithAttr(
        WritableApplicationService appService, Class<E> classType, String attr,
        Class<?> attrType, Object value) throws Exception {
        Constructor<?> constructor = classType.getConstructor();
        Object instance = constructor.newInstance();
        attr = "set" + attr.substring(0, 1).toUpperCase() + attr.substring(1);
        Method setMethod = classType.getMethod(attr, attrType);
        setMethod.invoke(instance, value);

        List<E> list = appService.search(classType, instance);
        Assert.isTrue(list.size() == 1);
        return list.get(0);
    }

    public static ContainerType getBinType(WritableApplicationService appService) {
        ContainerType type = new ContainerType();
        type.setName("Bin");
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

    public static Container getContainerWithLabel(
        WritableApplicationService appService, String barcode, String type)
        throws Exception {
        Container container = new Container();
        container.setLabel(barcode);
        List<Container> containers = appService.search(Container.class,
            container);
        if (containers.size() == 1) {
            return containers.get(0);
        } else {
            if (type != null) {
                List<ContainerType> cTypes = ModelUtils.queryProperty(
                    appService, ContainerType.class, "name", type, true);
                if (cTypes.size() > 0) {
                    for (Container c : containers) {
                        if (c.getContainerType().getId().equals(
                            cTypes.get(0).getId())) {
                            return c;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static String getSamplePosition(Sample sample) {
        SamplePosition position = sample.getSamplePosition();
        if (position == null) {
            return "none";
        } else {
            int dim1 = position.getPositionDimensionOne();
            int dim2 = position.getPositionDimensionTwo();
            String dim1String = String.valueOf((char) ('A' + dim1));
            String dim2String = String.valueOf(dim2);
            Container container = position.getContainer();
            ContainerType type = container.getContainerType();
            // FIXME use the labelling of the container !
            if (type.getName().equals("Bin")) {
                String binPosition = LabelingScheme.rowColToTwoCharAlpha(
                    new RowColPos(dim1, dim2), type);
                return position.getContainer().getLabel() + binPosition;
            } else if (type.getName().equals("Pallet")) {
                return position.getContainer().getLabel() + dim1String
                    + dim2String;
            }
            return "error in types";
        }
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
