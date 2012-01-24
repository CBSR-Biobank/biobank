/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.List;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import edu.ualberta.med.biobank.model.GlobalEventAttr;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.peer.GlobalEventAttrPeer;
import edu.ualberta.med.biobank.common.wrappers.internal.EventAttrTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.EventAttrTypeBaseWrapper;

public class GlobalEventAttrBaseWrapper extends ModelWrapper<GlobalEventAttr> {

    public GlobalEventAttrBaseWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public GlobalEventAttrBaseWrapper(WritableApplicationService appService,
        GlobalEventAttr wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public final Class<GlobalEventAttr> getWrappedClass() {
        return GlobalEventAttr.class;
    }

    @Override
    public Property<Integer, ? super GlobalEventAttr> getIdProperty() {
        return GlobalEventAttrPeer.ID;
    }

    @Override
    protected List<Property<?, ? super GlobalEventAttr>> getProperties() {
        return GlobalEventAttrPeer.PROPERTIES;
    }

    public String getLabel() {
        return getProperty(GlobalEventAttrPeer.LABEL);
    }

    public void setLabel(String label) {
        String trimmed = label == null ? null : label.trim();
        setProperty(GlobalEventAttrPeer.LABEL, trimmed);
    }

    public EventAttrTypeWrapper getEventAttrType() {
        EventAttrTypeWrapper eventAttrType = getWrappedProperty(GlobalEventAttrPeer.EVENT_ATTR_TYPE, EventAttrTypeWrapper.class);
        return eventAttrType;
    }

    public void setEventAttrType(EventAttrTypeBaseWrapper eventAttrType) {
        setWrappedProperty(GlobalEventAttrPeer.EVENT_ATTR_TYPE, eventAttrType);
    }

    void setEventAttrTypeInternal(EventAttrTypeBaseWrapper eventAttrType) {
        setWrappedProperty(GlobalEventAttrPeer.EVENT_ATTR_TYPE, eventAttrType);
    }

}
