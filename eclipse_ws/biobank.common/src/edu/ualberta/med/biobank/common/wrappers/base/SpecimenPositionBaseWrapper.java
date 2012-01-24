/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.List;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import edu.ualberta.med.biobank.model.SpecimenPosition;
import edu.ualberta.med.biobank.common.wrappers.Property;
import java.util.ArrayList;
import edu.ualberta.med.biobank.common.peer.SpecimenPositionPeer;
import edu.ualberta.med.biobank.common.wrappers.internal.AbstractPositionWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.ContainerBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.SpecimenBaseWrapper;
import java.util.Arrays;

public abstract class SpecimenPositionBaseWrapper extends AbstractPositionWrapper<SpecimenPosition> {

    public SpecimenPositionBaseWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public SpecimenPositionBaseWrapper(WritableApplicationService appService,
        SpecimenPosition wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public final Class<SpecimenPosition> getWrappedClass() {
        return SpecimenPosition.class;
    }

    @Override
    public Property<Integer, ? super SpecimenPosition> getIdProperty() {
        return SpecimenPositionPeer.ID;
    }

    @Override
    protected List<Property<?, ? super SpecimenPosition>> getProperties() {
        List<Property<?, ? super SpecimenPosition>> superNames = super.getProperties();
        List<Property<?, ? super SpecimenPosition>> all = new ArrayList<Property<?, ? super SpecimenPosition>>();
        all.addAll(superNames);
        all.addAll(SpecimenPositionPeer.PROPERTIES);
        return all;
    }

    public ContainerWrapper getContainer() {
        boolean notCached = !isPropertyCached(SpecimenPositionPeer.CONTAINER);
        ContainerWrapper container = getWrappedProperty(SpecimenPositionPeer.CONTAINER, ContainerWrapper.class);
        if (container != null && notCached) ((ContainerBaseWrapper) container).addToSpecimenPositionCollectionInternal(Arrays.asList(this));
        return container;
    }

    public void setContainer(ContainerBaseWrapper container) {
        if (isInitialized(SpecimenPositionPeer.CONTAINER)) {
            ContainerBaseWrapper oldContainer = getContainer();
            if (oldContainer != null) oldContainer.removeFromSpecimenPositionCollectionInternal(Arrays.asList(this));
        }
        if (container != null) container.addToSpecimenPositionCollectionInternal(Arrays.asList(this));
        setWrappedProperty(SpecimenPositionPeer.CONTAINER, container);
    }

    void setContainerInternal(ContainerBaseWrapper container) {
        setWrappedProperty(SpecimenPositionPeer.CONTAINER, container);
    }

    public SpecimenWrapper getSpecimen() {
        boolean notCached = !isPropertyCached(SpecimenPositionPeer.SPECIMEN);
        SpecimenWrapper specimen = getWrappedProperty(SpecimenPositionPeer.SPECIMEN, SpecimenWrapper.class);
        if (specimen != null && notCached) ((SpecimenBaseWrapper) specimen).setSpecimenPositionInternal(this);
        return specimen;
    }

    public void setSpecimen(SpecimenBaseWrapper specimen) {
        if (isInitialized(SpecimenPositionPeer.SPECIMEN)) {
            SpecimenBaseWrapper oldSpecimen = getSpecimen();
            if (oldSpecimen != null) oldSpecimen.setSpecimenPositionInternal(null);
        }
        if (specimen != null) specimen.setSpecimenPositionInternal(this);
        setWrappedProperty(SpecimenPositionPeer.SPECIMEN, specimen);
    }

    void setSpecimenInternal(SpecimenBaseWrapper specimen) {
        setWrappedProperty(SpecimenPositionPeer.SPECIMEN, specimen);
    }

}
