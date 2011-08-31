package edu.ualberta.med.biobank.common.wrappers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.NoRightForKeyDescException;
import edu.ualberta.med.biobank.common.peer.PrincipalPeer;
import edu.ualberta.med.biobank.common.wrappers.WrapperTransaction.TaskList;
import edu.ualberta.med.biobank.common.wrappers.base.PrincipalBaseWrapper;
import edu.ualberta.med.biobank.model.Principal;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public abstract class PrincipalWrapper<T extends Principal> extends
    PrincipalBaseWrapper<T> {

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
        List<MembershipWrapper<?>> msList = new ArrayList<MembershipWrapper<?>>();
        for (MembershipWrapper<?> ms : getMembershipCollection(false)) {
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
        BbRightWrapper right, CenterWrapper<?> center, StudyWrapper study)
        throws ApplicationException {
        List<PrivilegeWrapper> privileges = new ArrayList<PrivilegeWrapper>();
        for (MembershipWrapper<?> ms : getMembershipCollection(false)) {
            privileges.addAll(ms.getPrivilegesForRight(right, center, study));
        }
        return privileges;
    }

}
