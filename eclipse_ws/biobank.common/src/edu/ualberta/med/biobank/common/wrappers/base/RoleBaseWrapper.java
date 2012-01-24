/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.List;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.peer.RolePeer;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.PermissionWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.PermissionBaseWrapper;

public class RoleBaseWrapper extends ModelWrapper<Role> {

    public RoleBaseWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public RoleBaseWrapper(WritableApplicationService appService,
        Role wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public final Class<Role> getWrappedClass() {
        return Role.class;
    }

    @Override
    public Property<Integer, ? super Role> getIdProperty() {
        return RolePeer.ID;
    }

    @Override
    protected List<Property<?, ? super Role>> getProperties() {
        return RolePeer.PROPERTIES;
    }

    public String getName() {
        return getProperty(RolePeer.NAME);
    }

    public void setName(String name) {
        String trimmed = name == null ? null : name.trim();
        setProperty(RolePeer.NAME, trimmed);
    }

    public List<PermissionWrapper> getPermissionCollection(boolean sort) {
        List<PermissionWrapper> permissionCollection = getWrapperCollection(RolePeer.PERMISSION_COLLECTION, PermissionWrapper.class, sort);
        return permissionCollection;
    }

    public void addToPermissionCollection(List<? extends PermissionBaseWrapper> permissionCollection) {
        addToWrapperCollection(RolePeer.PERMISSION_COLLECTION, permissionCollection);
    }

    void addToPermissionCollectionInternal(List<? extends PermissionBaseWrapper> permissionCollection) {
        if (isInitialized(RolePeer.PERMISSION_COLLECTION)) {
            addToWrapperCollection(RolePeer.PERMISSION_COLLECTION, permissionCollection);
        } else {
            getElementQueue().add(RolePeer.PERMISSION_COLLECTION, permissionCollection);
        }
    }

    public void removeFromPermissionCollection(List<? extends PermissionBaseWrapper> permissionCollection) {
        removeFromWrapperCollection(RolePeer.PERMISSION_COLLECTION, permissionCollection);
    }

    void removeFromPermissionCollectionInternal(List<? extends PermissionBaseWrapper> permissionCollection) {
        if (isPropertyCached(RolePeer.PERMISSION_COLLECTION)) {
            removeFromWrapperCollection(RolePeer.PERMISSION_COLLECTION, permissionCollection);
        } else {
            getElementQueue().remove(RolePeer.PERMISSION_COLLECTION, permissionCollection);
        }
    }

    public void removeFromPermissionCollectionWithCheck(List<? extends PermissionBaseWrapper> permissionCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(RolePeer.PERMISSION_COLLECTION, permissionCollection);
    }

    void removeFromPermissionCollectionWithCheckInternal(List<? extends PermissionBaseWrapper> permissionCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(RolePeer.PERMISSION_COLLECTION, permissionCollection);
    }

}
