/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.List;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import edu.ualberta.med.biobank.model.Permission;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.peer.PermissionPeer;

public class PermissionBaseWrapper extends ModelWrapper<Permission> {

    public PermissionBaseWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public PermissionBaseWrapper(WritableApplicationService appService,
        Permission wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public final Class<Permission> getWrappedClass() {
        return Permission.class;
    }

    @Override
    public Property<Integer, ? super Permission> getIdProperty() {
        return PermissionPeer.ID;
    }

    @Override
    protected List<Property<?, ? super Permission>> getProperties() {
        return PermissionPeer.PROPERTIES;
    }

    public String getClassName() {
        return getProperty(PermissionPeer.CLASS_NAME);
    }

    public void setClassName(String className) {
        String trimmed = className == null ? null : className.trim();
        setProperty(PermissionPeer.CLASS_NAME, trimmed);
    }

}
