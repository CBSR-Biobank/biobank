/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.List;

import edu.ualberta.med.biobank.common.peer.RolePeer;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.Role;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

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
}
