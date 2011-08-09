package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankDeleteException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.exception.BiobankFailedQueryException;
import edu.ualberta.med.biobank.common.exception.BiobankQueryResultSizeException;
import edu.ualberta.med.biobank.common.peer.ActivityStatusPeer;
import edu.ualberta.med.biobank.common.wrappers.base.ActivityStatusBaseWrapper;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.StudyEventAttr;
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
public class ActivityStatusWrapper extends ActivityStatusBaseWrapper {

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
    public void deleteChecks() throws BiobankException, ApplicationException {
        if (isUsed()) {
            throw new BiobankDeleteException(
                "Unable to delete activity status "
                    + getName()
                    + " since it is being used by other objects in the database.");
        }
    }

    public boolean isUsed() throws ApplicationException,
        BiobankQueryResultSizeException {
        Class<?>[] classes = new Class[] { StudyEventAttr.class, Study.class,
            Specimen.class, ProcessingEvent.class, Clinic.class,
            ContainerType.class, Container.class, CollectionEvent.class,
            AliquotedSpecimen.class, Center.class };

        long usedCount = 0;
        for (Class<?> clazz : classes) {
            StringBuilder sb = new StringBuilder("select count(x) from ")
                .append(clazz.getName()).append(
                    " as x where x.activityStatus=?");
            HQLCriteria c = new HQLCriteria(sb.toString(),
                Arrays.asList(new Object[] { wrappedObject }));
            usedCount += getCountResult(appService, c);
        }

        return usedCount > 0;
    }

    @Override
    protected void persistChecks() throws BiobankException,
        ApplicationException {
        checkNoDuplicates(ActivityStatus.class,
            ActivityStatusPeer.NAME.getName(), getName(),
            "An activity status with name");
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ActivityStatusWrapper)
            return ((ActivityStatusWrapper) object).getName().equals(
                this.getName());

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
        throws ApplicationException, BiobankFailedQueryException {

        HQLCriteria c = new HQLCriteria(ACTIVITY_STATUS_QRY,
            Arrays.asList(new Object[] { name }));

        List<ActivityStatus> result = appService.query(c);
        if (result.size() != 1) {
            throw new BiobankFailedQueryException(
                "unexpected results from query");
        }
        return new ActivityStatusWrapper(appService, result.get(0));

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
        BiobankFailedQueryException {
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

    /**
     * return true if this Activity status name is "Active". Facility method to
     * avoid using "Active" string everywhere
     */
    public boolean isActive() {
        String name = getName();
        return ((name != null) && name.equals(ACTIVE_STATUS_STRING));
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

    // TODO: convert this to use the transactions model? Probably not necessary
    // since this is mostly a read-only class.
}
