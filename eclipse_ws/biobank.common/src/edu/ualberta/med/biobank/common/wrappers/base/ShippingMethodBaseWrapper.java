/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.List;

import edu.ualberta.med.biobank.common.peer.ShippingMethodPeer;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.ShippingMethod;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class ShippingMethodBaseWrapper extends ModelWrapper<ShippingMethod> {

    public ShippingMethodBaseWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public ShippingMethodBaseWrapper(WritableApplicationService appService,
        ShippingMethod wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public final Class<ShippingMethod> getWrappedClass() {
        return ShippingMethod.class;
    }

    @Override
    public Property<Integer, ? super ShippingMethod> getIdProperty() {
        return ShippingMethodPeer.ID;
    }

    @Override
    protected List<Property<?, ? super ShippingMethod>> getProperties() {
        return ShippingMethodPeer.PROPERTIES;
    }

    public String getName() {
        return getProperty(ShippingMethodPeer.NAME);
    }

    public void setName(String name) {
        String trimmed = name == null ? null : name.trim();
        setProperty(ShippingMethodPeer.NAME, trimmed);
    }

}
