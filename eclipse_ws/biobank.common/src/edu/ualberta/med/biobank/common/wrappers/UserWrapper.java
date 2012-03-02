package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.peer.UserPeer;
import edu.ualberta.med.biobank.common.wrappers.WrapperTransaction.TaskList;
import edu.ualberta.med.biobank.common.wrappers.actions.DeleteCsmUserAction;
import edu.ualberta.med.biobank.common.wrappers.actions.PersistCsmUserAction;
import edu.ualberta.med.biobank.common.wrappers.base.UserBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.tasks.QueryTask;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
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

    @Deprecated
    @Override
    protected void addPersistTasks(TaskList tasks) {
        // TODO problem= if persist fail, then the csm user is created anyway
        tasks.add(new QueryTask() {
            @Override
            public SDKQuery getSDKQuery() {
                return new PersistCsmUserAction(UserWrapper.this,
                    UserWrapper.this.getPassword());
            }

            @Override
            public void afterExecute(SDKQueryResult result) {
                // TODO Auto-generated method stub
            }
        });
        super.addPersistTasks(tasks);
        // tasks.persistAdded(this, UserPeer.GROUP_COLLECTION);
    }

    @Deprecated
    @Override
    protected void addDeleteTasks(TaskList tasks) {
        // should remove this user from its groups
        for (BbGroupWrapper group : getGroupCollection(false)) {
            group.removeFromUserCollection(Arrays.asList(this));
            group.addPersistTasks(tasks);
        }
        super.addDeleteTasks(tasks);
        tasks.add(new QueryTask() {
            @Override
            public SDKQuery getSDKQuery() {
                return new DeleteCsmUserAction(UserWrapper.this);
            }

            @Override
            public void afterExecute(SDKQueryResult result) {
                // TODO Auto-generated method stub
            }
        });
    }

    public void setPassword(String password) {
        String old = this.password;
        this.password = password;
        propertyChangeSupport.firePropertyChange("password", old, password); //$NON-NLS-1$

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

    private static final String GET_USER_QRY = "from " + User.class.getName() //$NON-NLS-1$
        + " where " + UserPeer.LOGIN.getName() + " = ?"; //$NON-NLS-1$ //$NON-NLS-2$

    public static UserWrapper getUser(BiobankApplicationService appService,
        String userName) throws BiobankCheckException, ApplicationException {
        HQLCriteria criteria = new HQLCriteria(GET_USER_QRY,
            Arrays.asList(new Object[] { userName }));
        List<User> users = appService.query(criteria);
        if (users == null || users.size() == 0)
            return null;
        if (users.size() == 1)
            return new UserWrapper(appService, users.get(0));
        throw new BiobankCheckException("Error retrieving users: found " //$NON-NLS-1$
            + users.size() + " results."); //$NON-NLS-1$
    }

    public boolean isInSuperAdminMode() {
        return inSuperAdminMode;
    }

    public void setInSuperAdminMode(boolean inSuperAdminMode) {
        this.inSuperAdminMode = inSuperAdminMode && isSuperAdmin();
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

    public boolean isSuperAdmin() {
        // try {
        // return super.hasPrivilegeOnKeyDesc(
        // PrivilegeWrapper.getAllowedPrivilege(appService), null, null,
        // ADMIN_KEY_DESC);
        // } catch (Exception e) {
        // throw new RuntimeException(e);
        // }
        return true;
    }

    public boolean needChangePassword() {
        if (getNeedPwdChange() == null)
            return false;
        return getNeedPwdChange();
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

    private static final String ALL_USERS_QRY = " from " + User.class.getName(); //$NON-NLS-1$

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
    public void modifyPassword(String oldPassword, String newPassword,
        Boolean bulkEmails) throws Exception {
        ((BiobankApplicationService) appService).executeModifyPassword(
            getCsmUserId(), oldPassword, newPassword, bulkEmails);
    }

    /**
     * Duplicate a user: create a new one that will have the exact same
     * relations. This duplicated user is not yet saved into the DB. Login,
     * email and csmUserId, user specific info are not copied
     */
    @Override
    public UserWrapper createDuplicate() {
        UserWrapper newUser = new UserWrapper(appService);
        newUser.setRecvBulkEmails(getRecvBulkEmails());
        return newUser;
    }

    @Override
    public UserWrapper duplicate() {
        return (UserWrapper) super.duplicate();
    }

    @Override
    protected Set<CenterWrapper<?>> getWorkingCentersInternal() {
        if (isInSuperAdminMode()) {
            try {
                return new HashSet<CenterWrapper<?>>(
                    CenterWrapper.getCenters(appService));
            } catch (ApplicationException e) {
                throw new RuntimeException(e);
            }
        }
        Set<CenterWrapper<?>> setOfWorkingCenter = super
            .getWorkingCentersInternal();
        for (BbGroupWrapper g : getGroupCollection(false)) {
            setOfWorkingCenter.addAll(g.getWorkingCentersInternal());
        }
        return setOfWorkingCenter;
    }

    @Override
    public String toString() {
        return getLogin();
    }

}
