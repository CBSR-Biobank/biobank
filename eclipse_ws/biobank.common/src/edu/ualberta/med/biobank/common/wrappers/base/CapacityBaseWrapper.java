/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.List;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.peer.CapacityPeer;

public class CapacityBaseWrapper extends ModelWrapper<Capacity> {

    public CapacityBaseWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public CapacityBaseWrapper(WritableApplicationService appService,
        Capacity wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public final Class<Capacity> getWrappedClass() {
        return Capacity.class;
    }

    @Override
    public Property<Integer, ? super Capacity> getIdProperty() {
        return CapacityPeer.ID;
    }

    @Override
    protected List<Property<?, ? super Capacity>> getProperties() {
        return CapacityPeer.PROPERTIES;
    }

    public Integer getColCapacity() {
        return getProperty(CapacityPeer.COL_CAPACITY);
    }

    public void setColCapacity(Integer colCapacity) {
        setProperty(CapacityPeer.COL_CAPACITY, colCapacity);
    }

    public Integer getRowCapacity() {
        return getProperty(CapacityPeer.ROW_CAPACITY);
    }

    public void setRowCapacity(Integer rowCapacity) {
        setProperty(CapacityPeer.ROW_CAPACITY, rowCapacity);
    }

}
