package edu.ualberta.med.biobank.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import org.eclipse.core.runtime.Assert;

import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ModelUtils {

    public static List<StorageContainer> getTopContainersForSite(
        WritableApplicationService appService, Site site)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria("from "
            + StorageContainer.class.getName() + " where site.id = "
            + site.getId() + " and locatedAtPosition.parentContainer is null");
        return appService.query(criteria);
    }

    public static StorageContainer newStorageContainer(StorageContainer parent) {
        StorageContainer newStorageContainer = new StorageContainer();
        ContainerPosition position = new ContainerPosition();
        position.setParentContainer(parent);
        position.setOccupiedContainer(newStorageContainer);
        newStorageContainer.setLocatedAtPosition(position);
        return newStorageContainer;
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

    public static StorageType getCabinetType(
        WritableApplicationService appService) {
        StorageType type = new StorageType();
        type.setName("Cabinet");
        List<StorageType> types;
        try {
            types = appService.search(StorageType.class, type);
            if (types.size() == 1) {
                return types.get(0);
            }
        } catch (ApplicationException e) {
        }
        return null;
    }

    public static StorageContainer getStorageContainerWithBarcode(
        WritableApplicationService appService, String barcode)
        throws ApplicationException {
        StorageContainer container = new StorageContainer();
        container.setBarcode(barcode);
        List<StorageContainer> containers = appService.search(
            StorageContainer.class, container);
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
            String positionString = getPositionString(position);
            StorageContainer container = position.getStorageContainer();
            ContainerPosition containerPosition = container
                .getLocatedAtPosition();
            StorageContainer parent = containerPosition.getParentContainer();
            while (parent != null) {
                positionString = getPositionString(containerPosition) + ":"
                    + positionString;
                container = parent;
                containerPosition = parent.getLocatedAtPosition();
                parent = containerPosition.getParentContainer();
            }
            positionString = container.getBarcode() + ":" + positionString;
            return positionString;
        }
    }

    public static String getPositionString(AbstractPosition position) {
        int dim1 = position.getPositionDimensionOne();
        int dim2 = position.getPositionDimensionTwo();
        StorageType parentType = null;
        if (position instanceof SamplePosition) {
            parentType = ((SamplePosition) position).getStorageContainer()
                .getStorageType();
        } else {
            parentType = ((ContainerPosition) position).getParentContainer()
                .getStorageType();
        }
        String dim1String = String.valueOf(dim1);
        if (getBooleanValue(parentType.getDimensionOneIsLetter(), false)) {
            dim1String = String.valueOf((char) ('A' + dim1));
        }
        String dim2String = String.valueOf(dim2);
        if (getBooleanValue(parentType.getDimensionTwoIsLetter(), false)) {
            dim1String = String.valueOf((char) ('A' + dim2));
        }
        return dim1String + dim2String;
    }

    public static boolean getBooleanValue(Boolean value, boolean defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        return value.booleanValue();
    }
}
