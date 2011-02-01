package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.exception.BiobankQueryResultSizeException;
import edu.ualberta.med.biobank.common.peer.ActivityStatusPeer;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.SampleStorage;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.StudyPvAttr;
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

    public static final String FLAGGED_STATUS_STRING = "Flagged";

    public ActivityStatusWrapper(WritableApplicationService appService,
        ActivityStatus wrappedObject) {
        super(appService, wrappedObject);
    }

    public ActivityStatusWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected List<String> getPropertyChangeNames() {
        return ActivityStatusPeer.PROP_NAMES;
    }

    public String getName() {
        return wrappedObject.getName();
    }

    @Override
    public void deleteChecks() throws BiobankException, ApplicationException {
        if (isUsed()) {
            throw new BiobankCheckException("Unable to delete activity status "
                + getName()
                + " since it is being used by other objects in the database.");
        }
    }

    public boolean isUsed() throws ApplicationException, BiobankException {
        long usedCount = 0;

        Class<?>[] classes = new Class[] { Aliquot.class, Clinic.class,
            Container.class, ContainerType.class, SampleStorage.class,
            Site.class, Study.class, StudyPvAttr.class, PatientVisit.class };

        for (Class<?> clazz : classes) {
            StringBuilder sb = new StringBuilder("select count(x) from ")
                .append(clazz.getName()).append(
                    " as x where x.activityStatus=?");
            HQLCriteria c = new HQLCriteria(sb.toString(),
                Arrays.asList(new Object[] { wrappedObject }));
            List<Long> results = appService.query(c);
            if (results.size() != 1) {
                throw new BiobankQueryResultSizeException();
            }
            usedCount += results.get(0);
        }

        return usedCount > 0;
    }

    @Override
    public Class<ActivityStatus> getWrappedClass() {
        return ActivityStatus.class;
    }

    @Override
    protected void persistChecks() throws BiobankException,
        ApplicationException {
        checkNoDuplicates(ActivityStatus.class,
            ActivityStatusPeer.NAME.getName(), getName(),
            ActivityStatusPeer.NAME.getName());
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

    private static final String ALL_ACTIVITY_STATUSES_QRY = "from "
        + ActivityStatus.class.getName();

    public static List<ActivityStatusWrapper> getAllActivityStatuses(
        WritableApplicationService appService) throws ApplicationException {

        List<ActivityStatusWrapper> activities = new ArrayList<ActivityStatusWrapper>();

        HQLCriteria c = new HQLCriteria(ALL_ACTIVITY_STATUSES_QRY);
        List<ActivityStatus> result = appService.query(c);
        for (ActivityStatus ac : result) {
            activities.add(new ActivityStatusWrapper(appService, ac));
        }
        Collections.sort(activities);
        return activities;
    }

    private static final String ACTIVITY_STATUS_QRY = "from "
        + ActivityStatus.class.getName() + " where name = ?";

    public static ActivityStatusWrapper getActivityStatus(
        WritableApplicationService appService, String name)
        throws ApplicationException, BiobankCheckException {

        HQLCriteria c = new HQLCriteria(ACTIVITY_STATUS_QRY,
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
     * 
     * @throws BiobankCheckException
     * @throws ApplicationException
     */
    public static ActivityStatusWrapper getActiveActivityStatus(
        WritableApplicationService appService) throws ApplicationException,
        BiobankCheckException {
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

    public boolean isClosed() {
        String name = getName();
        return name != null && name.equals(CLOSED_STATUS_STRING);
    }

    public boolean isFlagged() {
        String name = getName();
        return name != null && name.equals(FLAGGED_STATUS_STRING);
    }

    @Override
    public String toString() {
        return getName();
    }

}
