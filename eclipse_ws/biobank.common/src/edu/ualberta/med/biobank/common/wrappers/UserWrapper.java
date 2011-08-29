package edu.ualberta.med.biobank.common.wrappers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankFailedQueryException;
import edu.ualberta.med.biobank.common.peer.UserPeer;
import edu.ualberta.med.biobank.common.wrappers.WrapperTransaction.TaskList;
import edu.ualberta.med.biobank.common.wrappers.base.UserBaseWrapper;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class UserWrapper extends UserBaseWrapper {

    private static final String WORKING_CENTERS_KEY = "workingCenters";

    private String password;

    private boolean inSuperAdminMode;
    private transient CenterWrapper<?> currentWorkingCenter;
    private Boolean lockedOut;

    public UserWrapper(WritableApplicationService appService, User wrappedObject) {
        super(appService, wrappedObject);
    }

    public UserWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    // FIXME can do something better. Or event can remove CSM ?
    public void persist() throws Exception {
        Long csmId = ((BiobankApplicationService) appService).persistUser(
            getWrappedObject(), password);
        if (isNew())
            setCsmUserId(csmId);
        super.persist();
    }

    @Override
    // FIXME can do something better. Or event can remove CSM ?
    public void delete() throws Exception {
        User userMiniCopy = new User();
        userMiniCopy.setCsmUserId(getCsmUserId());
        super.delete();
        // should delete in csm only if the wrapper delete succeedes
        ((BiobankApplicationService) appService).deleteUser(userMiniCopy);
    }

    public void setPassword(String password) {
        String old = this.password;
        this.password = password;
        propertyChangeSupport.firePropertyChange("password", old, password);

    }

    public String getPassword() {
        if (password == null && !isNew())
            try {
                password = ((BiobankApplicationService) appService)
                    .getUserPassword(getLogin());
            } catch (ApplicationException e) {
                return null;
            }
        return password;
    }

    @Override
    protected void resetInternalFields() {
        super.resetInternalFields();
        password = null;
        lockedOut = null;
    }

    @Override
    protected void addDeleteTasks(TaskList tasks) {
        removeThisuserFromGroups(tasks);

        super.addDeleteTasks(tasks);
    }

    private void removeThisuserFromGroups(TaskList tasks) {
        // should remove this user from its groups
        for (BbGroupWrapper group : getGroupCollection(false)) {
            group.removeFromUserCollection(Arrays.asList(this));
            group.addPersistTasks(tasks);
        }
    }

    private static final String GET_USER_QRY = "from " + User.class.getName()
        + " where " + UserPeer.LOGIN.getName() + " = ?";

    public static UserWrapper getUser(BiobankApplicationService appService,
        String userName) throws BiobankCheckException, ApplicationException {
        HQLCriteria criteria = new HQLCriteria(GET_USER_QRY,
            Arrays.asList(new Object[] { userName }));
        List<User> users = appService.query(criteria);
        if (users == null || users.size() == 0)
            return null;
        if (users.size() == 1)
            return new UserWrapper(appService, users.get(0));
        throw new BiobankCheckException("Error retrieving users: found "
            + users.size() + " results.");
    }

    public boolean isInSuperAdminMode() {
        return inSuperAdminMode;
    }

    public void setInSuperAdminMode(boolean inSuperAdminMode) {
        this.inSuperAdminMode = inSuperAdminMode && (getIsSuperAdmin() != null)
            && getIsSuperAdmin();
    }

    public SiteWrapper getCurrentWorkingSite() {
        if (currentWorkingCenter instanceof SiteWrapper)
            return (SiteWrapper) currentWorkingCenter;
        return null;
    }

    public CenterWrapper<?> getCurrentWorkingCenter() {
        return currentWorkingCenter;
    }

    public void setCurrentWorkingCenter(CenterWrapper<?> currentWorkingCenter) {
        this.currentWorkingCenter = currentWorkingCenter;
    }

    @SuppressWarnings("unchecked")
    public List<CenterWrapper<?>> getWorkingCenters() {
        List<CenterWrapper<?>> workingCenters = (List<CenterWrapper<?>>) cache
            .get(WORKING_CENTERS_KEY);
        if (workingCenters == null) {
            workingCenters = new ArrayList<CenterWrapper<?>>();
            Set<CenterWrapper<?>> setOfWorkingCenter = new HashSet<CenterWrapper<?>>();
            for (MembershipWrapper<?> ms : getMembershipCollection(false)) {
                if (ms.getCenter() != null)
                    setOfWorkingCenter.add(ms.getCenter());
            }
            workingCenters.addAll(setOfWorkingCenter);
            cache.put(WORKING_CENTERS_KEY, workingCenters);
        }
        return workingCenters;
    }

    public boolean isSuperAdmin() {
        if (getIsSuperAdmin() == null)
            return false;
        return getIsSuperAdmin();
    }

    public boolean hasPrivilegesOnKeyDesc(PrivilegeWrapper privilege,
        String... rightsKeyDescs) throws BiobankFailedQueryException,
        ApplicationException {
        for (String keyDesc : rightsKeyDescs) {
            boolean ok = hasPrivilegeOnKeyDesc(privilege, keyDesc);
            if (ok)
                return ok;
        }
        return false;
    }

    // FIXME should check study and/or center ?
    public boolean hasPrivilegeOnKeyDesc(PrivilegeWrapper privilege,
        String keyDesc) throws BiobankFailedQueryException,
        ApplicationException {
        BbRightWrapper right = BbRightWrapper.getRightWithKeyDesc(appService,
            keyDesc);
        List<PrivilegeWrapper> userPrivileges = getPrivilegesForRight(right);
        return userPrivileges.contains(privilege);
    }

    public boolean hasPrivilegeOnClassObject(PrivilegeWrapper privilege,
        Class<?> objectClazz) throws BiobankFailedQueryException,
        ApplicationException {
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
            return hasPrivilegeOnKeyDesc(privilege, type);
        }
        return false;
    }

    private List<PrivilegeWrapper> getPrivilegesForRight(BbRightWrapper right)
        throws ApplicationException {
        List<PrivilegeWrapper> privileges = new ArrayList<PrivilegeWrapper>();
        for (MembershipWrapper<?> ms : getMembershipCollection(false)) {
            privileges.addAll(ms.getPrivilegesForRight(right));
        }
        return privileges;
    }

    public boolean needChangePassword() {
        if (getNeedChangePwd() == null)
            return false;
        return getNeedChangePwd();
    }

    /**
     * if center is the current center, then current center is reset to be sure
     * it has latest modifications
     * 
     * @throws Exception
     */
    public void updateCurrentCenter(CenterWrapper<?> center) throws Exception {
        if (center != null && center.equals(currentWorkingCenter)) {
            currentWorkingCenter.reset();
        }
    }

    @Override
    public int compareTo(ModelWrapper<User> user2) {
        if (user2 instanceof UserWrapper) {
            String login1 = getLogin();
            String login2 = ((UserWrapper) user2).getLogin();

            if (login1 == null || login2 == null)
                return 0;
            return login1.compareTo(login2);
        }
        return 0;
    }

    public void setLockedOut(boolean lockedOut) {
        this.lockedOut = lockedOut;
    }

    public boolean isLockedOut() {
        if (lockedOut == null && getCsmUserId() != null)
            try {
                lockedOut = ((BiobankApplicationService) appService)
                    .isUserLockedOut(getCsmUserId());
            } catch (ApplicationException e) {
                // TODO log error ?
                lockedOut = false;
            }
        if (lockedOut == null)
            return false;
        return lockedOut;
    }

    private static final String ALL_USERS_QRY = " from " + User.class.getName();

    public static final List<UserWrapper> getAllUsers(
        WritableApplicationService appService) throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria(ALL_USERS_QRY,
            new ArrayList<Object>());

        List<User> users = appService.query(criteria);
        return ModelWrapper.wrapModelCollection(appService, users,
            UserWrapper.class);
    }

    /**
     * This method should be called by the user itself. If another user is
     * connected to the server, the method will fail
     */
    public void modifyPassword(String oldPassword, String newPassword)
        throws Exception {
        ((BiobankApplicationService) appService).executeModifyPassword(
            getCsmUserId(), oldPassword, newPassword);
    }

}
