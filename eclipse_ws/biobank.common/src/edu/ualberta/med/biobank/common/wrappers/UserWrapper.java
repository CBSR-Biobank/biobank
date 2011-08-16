package edu.ualberta.med.biobank.common.wrappers;

import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.base.UserBaseWrapper;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class UserWrapper extends UserBaseWrapper {

    private String password;

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
}
