package edu.ualberta.med.biobank.common.security;

import java.lang.reflect.Constructor;

import org.apache.log4j.Logger;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.server.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SecurityHelper {
    private static Logger LOGGER = Logger.getLogger(SecurityHelper.class
        .getName());

    /**
     * return true if the user can create an object of type hold by the
     * modelWrapperType
     */
    public static boolean canCreate(WritableApplicationService appService,
        Class<?> modelWrapperType, String user) {
        try {
            Constructor<?> constructor = modelWrapperType
                .getConstructor(WritableApplicationService.class);
            ModelWrapper<?> wrapper = (ModelWrapper<?>) constructor
                .newInstance(appService);
            return ((BiobankApplicationService) appService).canCreateObjects(
                user, wrapper.getWrappedClass());
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
        Class<?> modelWrapperType, String user) {
        try {
            Constructor<?> constructor = modelWrapperType
                .getConstructor(WritableApplicationService.class);
            ModelWrapper<?> wrapper = (ModelWrapper<?>) constructor
                .newInstance(appService);
            return wrapper.canView(user);
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
    public static boolean canEdit(WritableApplicationService appService,
        Class<?> modelWrapperType, String user) {
        try {
            Constructor<?> constructor = modelWrapperType
                .getConstructor(WritableApplicationService.class);
            ModelWrapper<?> wrapper = (ModelWrapper<?>) constructor
                .newInstance(appService);
            return wrapper.canEdit(user);
        } catch (Exception e) {
            LOGGER.error("Error testing security authorization on "
                + modelWrapperType.getName(), e);
            return false;
        }
    }
}
