/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.List;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.peer.ActivityStatusPeer;

public class ActivityStatusBaseWrapper extends ModelWrapper<ActivityStatus> {

    public ActivityStatusBaseWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public ActivityStatusBaseWrapper(WritableApplicationService appService,
        ActivityStatus wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public final Class<ActivityStatus> getWrappedClass() {
        return ActivityStatus.class;
    }

    @Override
    public Property<Integer, ? super ActivityStatus> getIdProperty() {
        return ActivityStatusPeer.ID;
    }

    @Override
    protected List<Property<?, ? super ActivityStatus>> getProperties() {
        return ActivityStatusPeer.PROPERTIES;
    }

    public String getName() {
        return getProperty(ActivityStatusPeer.NAME);
    }

    public void setName(String name) {
        String trimmed = name == null ? null : name.trim();
        setProperty(ActivityStatusPeer.NAME, trimmed);
    }

}
