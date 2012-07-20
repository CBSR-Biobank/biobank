/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.List;

import edu.ualberta.med.biobank.common.peer.EventAttrTypePeer;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.EventAttrType;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class EventAttrTypeBaseWrapper extends ModelWrapper<EventAttrType> {

    public EventAttrTypeBaseWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public EventAttrTypeBaseWrapper(WritableApplicationService appService,
        EventAttrType wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public final Class<EventAttrType> getWrappedClass() {
        return EventAttrType.class;
    }

    @Override
    public Property<Integer, ? super EventAttrType> getIdProperty() {
        return EventAttrTypePeer.ID;
    }

    @Override
    protected List<Property<?, ? super EventAttrType>> getProperties() {
        return EventAttrTypePeer.PROPERTIES;
    }

    public String getName() {
        return getProperty(EventAttrTypePeer.NAME);
    }

    public void setName(String name) {
        String trimmed = name == null ? null : name.trim();
        setProperty(EventAttrTypePeer.NAME, trimmed);
    }

}
