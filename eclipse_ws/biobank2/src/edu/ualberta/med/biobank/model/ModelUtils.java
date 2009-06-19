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
				+ site.getId()
				+ " and locatedAtPosition.parentContainer is null");
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
}
