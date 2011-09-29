package edu.ualberta.med.biobank.common.wrappers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.common.exception.NoRightForKeyDescException;
import edu.ualberta.med.biobank.common.peer.PrincipalPeer;
import edu.ualberta.med.biobank.common.wrappers.WrapperTransaction.TaskList;
import edu.ualberta.med.biobank.common.wrappers.base.PrincipalBaseWrapper;
import edu.ualberta.med.biobank.model.Principal;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public abstract class PrincipalWrapper<T extends Principal> extends
    PrincipalBaseWrapper<T> {

    private static final String WORKING_CENTERS_KEY = "workingCenters"; //$NON-NLS-1$

    public PrincipalWrapper(WritableApplicationService appService,
        T wrappedObject) {
        super(appService, wrappedObject);
    }

    public PrincipalWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected void addPersistTasks(TaskList tasks) {
        tasks.deleteRemoved(this, PrincipalPeer.MEMBERSHIP_COLLECTION);
        super.addPersistTasks(tasks);
    }

    /**
     * Duplicate a principal: create a new one that will have the exact same
     * relations. This duplicated principal is not yet saved into the DB.
     */
    public PrincipalWrapper<T> duplicate() {
        PrincipalWrapper<T> newPrincipal = createDuplicate();
        List<MembershipWrapper> msList = new ArrayList<MembershipWrapper>();
        for (MembershipWrapper ms : getMembershipCollection(false)) {
            msList.add(ms.duplicate());
        }
        newPrincipal.addToMembershipCollection(msList);
        return newPrincipal;
    }

    protected abstract PrincipalWrapper<T> createDuplicate();

    public boolean hasPrivilegesOnKeyDesc(PrivilegeWrapper privilege,
        CenterWrapper<?> center, StudyWrapper study, String... rightsKeyDescs)
        throws NoRightForKeyDescException, ApplicationException {
        for (String keyDesc : rightsKeyDescs) {
            boolean ok = hasPrivilegeOnKeyDesc(privilege, center, study,
                keyDesc);
            if (ok)
                return ok;
        }
        return false;
    }

    public boolean hasPrivilegeOnKeyDesc(PrivilegeWrapper privilege,
        CenterWrapper<?> center, StudyWrapper study, String keyDesc)
        throws ApplicationException, NoRightForKeyDescException {
        BbRightWrapper right = BbRightWrapper.getRightWithKeyDesc(appService,
            keyDesc);
        if (right == null)
            throw new NoRightForKeyDescException(keyDesc);
        List<PrivilegeWrapper> privileges = getPrivilegesForRight(right,
            center, study);
        return privileges.contains(privilege);
    }

    public boolean hasPrivilegeOnClassObject(PrivilegeWrapper privilege,
        CenterWrapper<?> center, StudyWrapper study, Class<?> objectClazz)
        throws NoRightForKeyDescException, ApplicationException {
        if (ModelWrapper.class.isAssignableFrom(objectClazz)) {
            ModelWrapper<?> wrapper = null;
            try {
                Constructor<?> constructor = objectClazz
                    .getConstructor(WritableApplicationService.class);
                wrapper = (ModelWrapper<?>) constructor
                    .newInstance((WritableApplicationService) null);
            } catch (NoSuchMethodException e) {
                return false;
            } catch (InvocationTargetException e) {
                return false;
            } catch (IllegalAccessException e) {
                return false;
            } catch (InstantiationException e) {
                return false;
            }
            String type = wrapper.getWrappedClass().getSimpleName();
            return hasPrivilegeOnKeyDesc(privilege, center, study, type);
        }
        return false;
    }

    protected List<PrivilegeWrapper> getPrivilegesForRight(
        BbRightWrapper right, CenterWrapper<?> center, StudyWrapper study) {
        List<PrivilegeWrapper> privileges = new ArrayList<PrivilegeWrapper>();
        for (MembershipWrapper ms : getMembershipCollection(false)) {
            privileges.addAll(ms.getPrivilegesForRight(right, center, study));
        }
        return privileges;
    }

    protected List<CenterWrapper<?>> getAllCentersInvolved() {
        List<CenterWrapper<?>> centers = new ArrayList<CenterWrapper<?>>();
        for (MembershipWrapper ms : getMembershipCollection(false)) {
            centers.add(ms.getCenter());
        }
        return centers;
    }

    protected List<CenterWrapper<?>> getAllCentersForRight(String keyDesc)
        throws ApplicationException {
        BbRightWrapper right = BbRightWrapper.getRightWithKeyDesc(appService,
            keyDesc);
        List<CenterWrapper<?>> centers = new ArrayList<CenterWrapper<?>>();
        for (MembershipWrapper ms : getMembershipCollection(false)) {
            if (ms.isUsingRight(right))
                centers.add(ms.getCenter());
        }
        return centers;
    }

    @SuppressWarnings("unchecked")
    public List<CenterWrapper<?>> getWorkingCenters() {
        List<CenterWrapper<?>> workingCenters = (List<CenterWrapper<?>>) cache
            .get(WORKING_CENTERS_KEY);
        if (workingCenters == null) {
            workingCenters = new ArrayList<CenterWrapper<?>>();
            Set<CenterWrapper<?>> setOfWorkingCenter = getWorkingCentersInternal();
            workingCenters.addAll(setOfWorkingCenter);
            cache.put(WORKING_CENTERS_KEY, workingCenters);
        }
        return workingCenters;
    }

    protected Set<CenterWrapper<?>> getWorkingCentersInternal() {
        Set<CenterWrapper<?>> setOfWorkingCenter = new HashSet<CenterWrapper<?>>();
        for (MembershipWrapper ms : getMembershipCollection(false)) {
            if (ms.getCenter() != null)
                setOfWorkingCenter.add(ms.getCenter());
        }
        return setOfWorkingCenter;
    }
}
