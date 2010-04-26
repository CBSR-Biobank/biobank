package edu.ualberta.med.biobank.common.security;

import java.lang.reflect.Constructor;

import org.apache.log4j.Logger;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SecurityHelper {
    private static Logger LOGGER = Logger.getLogger(SecurityHelper.class
        .getName());

    /**
     * return true if the user can create an object of type hold by the
     * modelWrapperType
     */
    public static boolean canCreate(WritableApplicationService appService,
        Class<?> modelWrapperType) {
        try {
            Constructor<?> constructor = modelWrapperType
                .getConstructor(WritableApplicationService.class);
            ModelWrapper<?> wrapper = (ModelWrapper<?>) constructor
                .newInstance(appService);
            return ((BiobankApplicationService) appService)
                .canCreateObjects(wrapper.getWrappedClass());
        } catch (Exception e) {
            LOGGER.error("Error testing security authorization on "
                + modelWrapperType.getName(), e);
            return false;
        }
    }

    /**
     * return true if the user can delete an object of type hold by the
     * modelWrapperType
     */
    public static boolean canDelete(WritableApplicationService appService,
        Class<?> modelWrapperType) {
        try {
            Constructor<?> constructor = modelWrapperType
                .getConstructor(WritableApplicationService.class);
            ModelWrapper<?> wrapper = (ModelWrapper<?>) constructor
                .newInstance(appService);
            return ((BiobankApplicationService) appService)
                .canCreateObjects(wrapper.getWrappedClass());
        } catch (Exception e) {
            LOGGER.error("Error testing security authorization on "
                + modelWrapperType.getName(), e);
            return false;
        }
    }

    /**
     * return true if the user can view objects of type hold by the
     * modelWrapperType
     */
    public static boolean canView(WritableApplicationService appService,
        Class<?> modelWrapperType) {
        try {
            Constructor<?> constructor = modelWrapperType
                .getConstructor(WritableApplicationService.class);
            ModelWrapper<?> wrapper = (ModelWrapper<?>) constructor
                .newInstance(appService);
            return wrapper.canView();
        } catch (Exception e) {
            LOGGER.error("Error testing security authorization on "
                + modelWrapperType.getName(), e);
            return false;
        }
    }

    /**
     * return true if the user can view objects of type hold by the
     * modelWrapperType
     */
    public static boolean canUpdate(WritableApplicationService appService,
        Class<?> modelWrapperType) {
        try {
            Constructor<?> constructor = modelWrapperType
                .getConstructor(WritableApplicationService.class);
            ModelWrapper<?> wrapper = (ModelWrapper<?>) constructor
                .newInstance(appService);
            return wrapper.canEdit();
        } catch (Exception e) {
            LOGGER.error("Error testing security authorization on "
                + modelWrapperType.getName(), e);
            return false;
        }
    }

    public static boolean isContainerAdministrator(
        WritableApplicationService appService) {
        try {
            return ((BiobankApplicationService) appService)
                .isContainerAdministrator();
        } catch (ApplicationException e) {
            LOGGER.error("Error testing security isContainerAdministrator");
            return false;
        }
    }
}
