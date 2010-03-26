package edu.ualberta.med.biobank.common.wrappers;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.model.ActivityStatus;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

/**
 * This wrapper does not allow new activity status objects to be added to the
 * database. Therefore, when either getActivityStatus() or
 * getAllActivityStatuses() are called the first time, a map of the existing
 * statuses in the database is created.
 * 
 */
public class ActivityStatusWrapper extends ModelWrapper<ActivityStatus> {

    private static Map<String, ActivityStatusWrapper> activityStatusMap = new HashMap<String, ActivityStatusWrapper>();

    public static final String ACTIVE_STATUS_STRING = "Active";

    public ActivityStatusWrapper(WritableApplicationService appService,
        ActivityStatus wrappedObject) {
        super(appService, wrappedObject);
    }

    public ActivityStatusWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected String[] getPropertyChangeNames() {
        return new String[] {};
    }

    public String getName() {
        return wrappedObject.getName();
    }

    @Override
    protected void deleteChecks() throws Exception {
        throw new BiobankCheckException("object of this type cannot be deleted");
    }

    @Override
    public Class<ActivityStatus> getWrappedClass() {
        return ActivityStatus.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException, WrapperException {
        throw new BiobankCheckException(
            "should not be adding objects of this type");
    }

    @Override
    public int compareTo(ModelWrapper<ActivityStatus> wrapper) {
        if (wrapper instanceof ActivityStatusWrapper) {
            String name1 = wrappedObject.getName();
            String name2 = wrapper.wrappedObject.getName();
            if (name1 != null && name2 != null) {
                return name1.compareTo(name2);
            }
        }
        return 0;
    }

    public static Collection<ActivityStatusWrapper> getAllActivityStatuses(
        WritableApplicationService appService) throws ApplicationException {
        if (activityStatusMap.size() > 0) {
            return activityStatusMap.values();
        }
        HQLCriteria c = new HQLCriteria("from "
            + ActivityStatus.class.getName());
        List<ActivityStatus> result = appService.query(c);
        for (ActivityStatus ac : result) {
            activityStatusMap.put(ac.getName(), new ActivityStatusWrapper(
                appService, ac));
        }
        return activityStatusMap.values();
    }

    public static ActivityStatusWrapper getActivityStatus(
        WritableApplicationService appService, String name) throws Exception {
        if (activityStatusMap.size() == 0) {
            getAllActivityStatuses(appService);
        }
        ActivityStatusWrapper activityStatus = activityStatusMap.get(name);
        if (activityStatus == null) {
            throw new BiobankCheckException("activity status \"" + name
                + "\" does not exist");
        }
        return activityStatus;
    }

    /**
     * return activity status "Active". Facility method to avoid using "Active"
     * string everywhere
     */
    public static ActivityStatusWrapper getActiveActivityStatus(
        WritableApplicationService appService) throws Exception {
        return getActivityStatus(appService, ACTIVE_STATUS_STRING);
    }

    /**
     * return true if this Activity status name is "Active". Facility method to
     * avoid using "Active" string everywhere
     */
    public boolean isActive() {
        return getName() != null && getName().equals(ACTIVE_STATUS_STRING);
    }

    @Override
    public String toString() {
        return getName();
    }

}
