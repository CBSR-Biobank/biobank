/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.peer.ContainerPositionPeer;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.internal.AbstractPositionWrapper;
import edu.ualberta.med.biobank.model.ContainerPosition;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public abstract class ContainerPositionBaseWrapper extends AbstractPositionWrapper<ContainerPosition> {

    public ContainerPositionBaseWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public ContainerPositionBaseWrapper(WritableApplicationService appService,
        ContainerPosition wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public final Class<ContainerPosition> getWrappedClass() {
        return ContainerPosition.class;
    }

    @Override
    public Property<Integer, ? super ContainerPosition> getIdProperty() {
        return ContainerPositionPeer.ID;
    }

    @Override
    protected List<Property<?, ? super ContainerPosition>> getProperties() {
        List<Property<?, ? super ContainerPosition>> superNames = super.getProperties();
        List<Property<?, ? super ContainerPosition>> all = new ArrayList<Property<?, ? super ContainerPosition>>();
        all.addAll(superNames);
        all.addAll(ContainerPositionPeer.PROPERTIES);
        return all;
    }

    public ContainerWrapper getContainer() {
        boolean notCached = !isPropertyCached(ContainerPositionPeer.CONTAINER);
        ContainerWrapper container = getWrappedProperty(ContainerPositionPeer.CONTAINER, ContainerWrapper.class);
        if (container != null && notCached) ((ContainerBaseWrapper) container).setPositionInternal(this);
        return container;
    }

    public void setContainer(ContainerBaseWrapper container) {
        if (isInitialized(ContainerPositionPeer.CONTAINER)) {
            ContainerBaseWrapper oldContainer = getContainer();
            if (oldContainer != null) oldContainer.setPositionInternal(null);
        }
        if (container != null) container.setPositionInternal(this);
        setWrappedProperty(ContainerPositionPeer.CONTAINER, container);
    }

    void setContainerInternal(ContainerBaseWrapper container) {
        setWrappedProperty(ContainerPositionPeer.CONTAINER, container);
    }

    public ContainerWrapper getParentContainer() {
        boolean notCached = !isPropertyCached(ContainerPositionPeer.PARENT_CONTAINER);
        ContainerWrapper parentContainer = getWrappedProperty(ContainerPositionPeer.PARENT_CONTAINER, ContainerWrapper.class);
        if (parentContainer != null && notCached) ((ContainerBaseWrapper) parentContainer).addToChildPositionCollectionInternal(Arrays.asList(this));
        return parentContainer;
    }

    public void setParentContainer(ContainerBaseWrapper parentContainer) {
        if (isInitialized(ContainerPositionPeer.PARENT_CONTAINER)) {
            ContainerBaseWrapper oldParentContainer = getParentContainer();
            if (oldParentContainer != null) oldParentContainer.removeFromChildPositionCollectionInternal(Arrays.asList(this));
        }
        if (parentContainer != null) parentContainer.addToChildPositionCollectionInternal(Arrays.asList(this));
        setWrappedProperty(ContainerPositionPeer.PARENT_CONTAINER, parentContainer);
    }

    void setParentContainerInternal(ContainerBaseWrapper parentContainer) {
        setWrappedProperty(ContainerPositionPeer.PARENT_CONTAINER, parentContainer);
    }

}
