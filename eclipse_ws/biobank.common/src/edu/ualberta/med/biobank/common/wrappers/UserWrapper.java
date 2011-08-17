package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.peer.UserPeer;
import edu.ualberta.med.biobank.common.security.Privilege;
import edu.ualberta.med.biobank.common.security.SecurityFeature;
import edu.ualberta.med.biobank.common.wrappers.base.UserBaseWrapper;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class UserWrapper extends UserBaseWrapper {

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
    protected void persistDependencies(User origObject) throws Exception {
        Long csmId = ((BiobankApplicationService) appService).persistUser(
            getWrappedObject(), password);
        if (isNew())
            setCsmUserId(csmId);
    }

    @Override
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
    }

    /**
     * Should use group.addToUserCollection
     */
    @Override
    @Deprecated
    public void addToGroupCollection(List<BbGroupWrapper> groupCollection) {
    }

    /**
     * Should use group.removeFromUserCollection
     */
    @Override
    @Deprecated
    public void removeFromGroupCollection(List<BbGroupWrapper> groupCollection) {
    }

    /**
     * Should use group.removeFromUserCollectionWithCheck
     */
    @Override
    @Deprecated
    public void removeFromGroupCollectionWithCheck(
        List<BbGroupWrapper> groupCollection) throws BiobankCheckException {
    }

    @Override
    public void deleteDependencies() throws Exception {
        // should remove this user from its groups
        for (BbGroupWrapper group : getGroupCollection(false)) {
            group.removeFromUserCollection(Arrays.asList(this));
            group.persist();
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

    @Deprecated
    public CenterWrapper<?> getCurrentWorkingCenter() {
        // FIXME this method was used in old User object. What should be done
        // now ?
        currentWorkingCenter = null;
        return currentWorkingCenter;
    }

    @SuppressWarnings("unused")
    @Deprecated
    public List<CenterWrapper<?>> getWorkingCenters(
        WritableApplicationService appService) throws Exception {
        // FIXME this method was used in old User object. What should be done
        // now ?
        return null;
    }

    public boolean isSuperAdmin() {
        if (getIsSuperAdmin() == null)
            return false;
        return getIsSuperAdmin();
    }

    @SuppressWarnings("unused")
    @Deprecated
    public void setCurrentWorkingCenter(CenterWrapper<?> centerWrapper) {
        // FIXME this method was used in old User object. What should be done
        // now ?
    }

    @Deprecated
    public boolean canPerformActions(SecurityFeature... features) {
        return canPerformActions(Arrays.asList(features));
    }

    @SuppressWarnings("unused")
    @Deprecated
    public boolean canPerformActions(List<SecurityFeature> value) {
        // FIXME this method was used in old User object. What should be done
        // now ?
        return true;
    }

    @Deprecated
    public boolean isAdministratorForCurrentCenter() {
        // FIXME this method was used in old User object. What should be done
        // now ?
        return false;
    }

    @Deprecated
    public boolean hasPrivilegeOnObject(Privilege privilege,
        Class<?> objectClazz) {
        return hasPrivilegeOnObject(privilege, objectClazz, null);
    }

    @SuppressWarnings("unused")
    @Deprecated
    public boolean hasPrivilegeOnObject(Privilege privilege,
        Class<?> objectClazz, List<? extends CenterWrapper<?>> specificCenters) {
        // FIXME this method was used in old User object. What should be done
        // now ?
        return true;
    }

    public boolean isCBSRCenter() {
        CenterWrapper<?> center = getCurrentWorkingCenter();
        return center != null && center.getNameShort().equals("CBSR"); //$NON-NLS-1$
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
}
