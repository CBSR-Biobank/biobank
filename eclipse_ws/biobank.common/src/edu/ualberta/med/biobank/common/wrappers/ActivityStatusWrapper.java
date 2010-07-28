package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
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

    public static final String ACTIVE_STATUS_STRING = "Active";

    public static final String CLOSED_STATUS_STRING = "Closed";

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
    }

    @Override
    public Class<ActivityStatus> getWrappedClass() {
        return ActivityStatus.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException, WrapperException {
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ActivityStatusWrapper)
            return ((ActivityStatusWrapper) object).getName().equals(
                this.getName());
        else

            return false;
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

    public static List<ActivityStatusWrapper> getAllActivityStatuses(
        WritableApplicationService appService) throws ApplicationException {

        Map<String, ActivityStatusWrapper> activityStatusMap = new HashMap<String, ActivityStatusWrapper>();

        HQLCriteria c = new HQLCriteria("from "
            + ActivityStatus.class.getName());
        List<ActivityStatus> result = appService.query(c);
        for (ActivityStatus ac : result) {
            activityStatusMap.put(ac.getName(), new ActivityStatusWrapper(
                appService, ac));
        }
        return new ArrayList<ActivityStatusWrapper>(activityStatusMap.values());
    }

    // TODO test getActivityStatus
    public static ActivityStatusWrapper getActivityStatus(
        WritableApplicationService appService, String name) throws Exception {

        HQLCriteria c = new HQLCriteria("from "
            + ActivityStatus.class.getName() + " where name = ?",
            Arrays.asList(new Object[] { name }));

        List<ActivityStatus> result = appService.query(c);

        if (result.size() == 1) {
            return new ActivityStatusWrapper(appService, result.get(0));

        } else if (result.size() == 0) {
            throw new BiobankCheckException("activity status \"" + name
                + "\" does not exist");

        } else if (result.size() > 1) {
            throw new BiobankCheckException(" Too many instances of \"" + name
                + "\"");
        }
        return null;

    }

    /**
     * return activity status "Active". Facility method to avoid using "Active"
     * string everywhere
     */
    public static ActivityStatusWrapper getActiveActivityStatus(
        WritableApplicationService appService) throws Exception {
        return getActivityStatus(appService, ACTIVE_STATUS_STRING);
    }

    public static void persistActivityStatuses(
        List<ActivityStatusWrapper> addedOrModifiedTypes,
        List<ActivityStatusWrapper> typesToDelete)
        throws BiobankCheckException, Exception {
        if (addedOrModifiedTypes != null) {
            for (ActivityStatusWrapper ss : addedOrModifiedTypes) {
                ss.persist();
            }
        }
        if (typesToDelete != null) {
            for (ActivityStatusWrapper ss : typesToDelete) {
                ss.delete();
            }
        }
    }

    public void setName(String name) {
        String old = getName();
        wrappedObject.setName(name);
        propertyChangeSupport.firePropertyChange("name", old, name);
    }

    /**
     * return true if this Activity status name is "Active". Facility method to
     * avoid using "Active" string everywhere
     */
    public boolean isActive() {
        String name = getName();
        return name != null && name.equals(ACTIVE_STATUS_STRING);
    }

    @Override
    public String toString() {
        return getName();
    }

}
