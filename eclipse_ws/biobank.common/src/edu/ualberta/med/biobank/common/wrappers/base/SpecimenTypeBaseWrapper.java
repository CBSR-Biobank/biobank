/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.List;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.peer.SpecimenTypePeer;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.ContainerTypeBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.SpecimenTypeBaseWrapper;
import java.util.Arrays;

public class SpecimenTypeBaseWrapper extends ModelWrapper<SpecimenType> {

    public SpecimenTypeBaseWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public SpecimenTypeBaseWrapper(WritableApplicationService appService,
        SpecimenType wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public final Class<SpecimenType> getWrappedClass() {
        return SpecimenType.class;
    }

    @Override
    public Property<Integer, ? super SpecimenType> getIdProperty() {
        return SpecimenTypePeer.ID;
    }

    @Override
    protected List<Property<?, ? super SpecimenType>> getProperties() {
        return SpecimenTypePeer.PROPERTIES;
    }

    public String getName() {
        return getProperty(SpecimenTypePeer.NAME);
    }

    public void setName(String name) {
        String trimmed = name == null ? null : name.trim();
        setProperty(SpecimenTypePeer.NAME, trimmed);
    }

    public String getNameShort() {
        return getProperty(SpecimenTypePeer.NAME_SHORT);
    }

    public void setNameShort(String nameShort) {
        String trimmed = nameShort == null ? null : nameShort.trim();
        setProperty(SpecimenTypePeer.NAME_SHORT, trimmed);
    }

    public List<ContainerTypeWrapper> getContainerTypeCollection(boolean sort) {
        boolean notCached = !isPropertyCached(SpecimenTypePeer.CONTAINER_TYPES);
        List<ContainerTypeWrapper> containerTypeCollection = getWrapperCollection(SpecimenTypePeer.CONTAINER_TYPES, ContainerTypeWrapper.class, sort);
        if (notCached) {
            for (ContainerTypeBaseWrapper e : containerTypeCollection) {
                e.addToSpecimenTypeCollectionInternal(Arrays.asList(this));
            }
        }
        return containerTypeCollection;
    }

    public void addToContainerTypeCollection(List<? extends ContainerTypeBaseWrapper> containerTypeCollection) {
        addToWrapperCollection(SpecimenTypePeer.CONTAINER_TYPES, containerTypeCollection);
        for (ContainerTypeBaseWrapper e : containerTypeCollection) {
            e.addToSpecimenTypeCollectionInternal(Arrays.asList(this));
        }
    }

    void addToContainerTypeCollectionInternal(List<? extends ContainerTypeBaseWrapper> containerTypeCollection) {
        if (isInitialized(SpecimenTypePeer.CONTAINER_TYPES)) {
            addToWrapperCollection(SpecimenTypePeer.CONTAINER_TYPES, containerTypeCollection);
        } else {
            getElementQueue().add(SpecimenTypePeer.CONTAINER_TYPES, containerTypeCollection);
        }
    }

    public void removeFromContainerTypeCollection(List<? extends ContainerTypeBaseWrapper> containerTypeCollection) {
        removeFromWrapperCollection(SpecimenTypePeer.CONTAINER_TYPES, containerTypeCollection);
        for (ContainerTypeBaseWrapper e : containerTypeCollection) {
            e.removeFromSpecimenTypeCollectionInternal(Arrays.asList(this));
        }
    }

    void removeFromContainerTypeCollectionInternal(List<? extends ContainerTypeBaseWrapper> containerTypeCollection) {
        if (isPropertyCached(SpecimenTypePeer.CONTAINER_TYPES)) {
            removeFromWrapperCollection(SpecimenTypePeer.CONTAINER_TYPES, containerTypeCollection);
        } else {
            getElementQueue().remove(SpecimenTypePeer.CONTAINER_TYPES, containerTypeCollection);
        }
    }

    public void removeFromContainerTypeCollectionWithCheck(List<? extends ContainerTypeBaseWrapper> containerTypeCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(SpecimenTypePeer.CONTAINER_TYPES, containerTypeCollection);
        for (ContainerTypeBaseWrapper e : containerTypeCollection) {
            e.removeFromSpecimenTypeCollectionInternal(Arrays.asList(this));
        }
    }

    void removeFromContainerTypeCollectionWithCheckInternal(List<? extends ContainerTypeBaseWrapper> containerTypeCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(SpecimenTypePeer.CONTAINER_TYPES, containerTypeCollection);
    }

    public List<SpecimenTypeWrapper> getParentSpecimenTypeCollection(boolean sort) {
        boolean notCached = !isPropertyCached(SpecimenTypePeer.PARENT_SPECIMEN_TYPES);
        List<SpecimenTypeWrapper> parentSpecimenTypeCollection = getWrapperCollection(SpecimenTypePeer.PARENT_SPECIMEN_TYPES, SpecimenTypeWrapper.class, sort);
        if (notCached) {
            for (SpecimenTypeBaseWrapper e : parentSpecimenTypeCollection) {
                e.addToChildSpecimenTypeCollectionInternal(Arrays.asList(this));
            }
        }
        return parentSpecimenTypeCollection;
    }

    public void addToParentSpecimenTypeCollection(List<? extends SpecimenTypeBaseWrapper> parentSpecimenTypeCollection) {
        addToWrapperCollection(SpecimenTypePeer.PARENT_SPECIMEN_TYPES, parentSpecimenTypeCollection);
        for (SpecimenTypeBaseWrapper e : parentSpecimenTypeCollection) {
            e.addToChildSpecimenTypeCollectionInternal(Arrays.asList(this));
        }
    }

    void addToParentSpecimenTypeCollectionInternal(List<? extends SpecimenTypeBaseWrapper> parentSpecimenTypeCollection) {
        if (isInitialized(SpecimenTypePeer.PARENT_SPECIMEN_TYPES)) {
            addToWrapperCollection(SpecimenTypePeer.PARENT_SPECIMEN_TYPES, parentSpecimenTypeCollection);
        } else {
            getElementQueue().add(SpecimenTypePeer.PARENT_SPECIMEN_TYPES, parentSpecimenTypeCollection);
        }
    }

    public void removeFromParentSpecimenTypeCollection(List<? extends SpecimenTypeBaseWrapper> parentSpecimenTypeCollection) {
        removeFromWrapperCollection(SpecimenTypePeer.PARENT_SPECIMEN_TYPES, parentSpecimenTypeCollection);
        for (SpecimenTypeBaseWrapper e : parentSpecimenTypeCollection) {
            e.removeFromChildSpecimenTypeCollectionInternal(Arrays.asList(this));
        }
    }

    void removeFromParentSpecimenTypeCollectionInternal(List<? extends SpecimenTypeBaseWrapper> parentSpecimenTypeCollection) {
        if (isPropertyCached(SpecimenTypePeer.PARENT_SPECIMEN_TYPES)) {
            removeFromWrapperCollection(SpecimenTypePeer.PARENT_SPECIMEN_TYPES, parentSpecimenTypeCollection);
        } else {
            getElementQueue().remove(SpecimenTypePeer.PARENT_SPECIMEN_TYPES, parentSpecimenTypeCollection);
        }
    }

    public void removeFromParentSpecimenTypeCollectionWithCheck(List<? extends SpecimenTypeBaseWrapper> parentSpecimenTypeCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(SpecimenTypePeer.PARENT_SPECIMEN_TYPES, parentSpecimenTypeCollection);
        for (SpecimenTypeBaseWrapper e : parentSpecimenTypeCollection) {
            e.removeFromChildSpecimenTypeCollectionInternal(Arrays.asList(this));
        }
    }

    void removeFromParentSpecimenTypeCollectionWithCheckInternal(List<? extends SpecimenTypeBaseWrapper> parentSpecimenTypeCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(SpecimenTypePeer.PARENT_SPECIMEN_TYPES, parentSpecimenTypeCollection);
    }

    public List<SpecimenTypeWrapper> getChildSpecimenTypeCollection(boolean sort) {
        boolean notCached = !isPropertyCached(SpecimenTypePeer.CHILD_SPECIMEN_TYPES);
        List<SpecimenTypeWrapper> childSpecimenTypeCollection = getWrapperCollection(SpecimenTypePeer.CHILD_SPECIMEN_TYPES, SpecimenTypeWrapper.class, sort);
        if (notCached) {
            for (SpecimenTypeBaseWrapper e : childSpecimenTypeCollection) {
                e.addToParentSpecimenTypeCollectionInternal(Arrays.asList(this));
            }
        }
        return childSpecimenTypeCollection;
    }

    public void addToChildSpecimenTypeCollection(List<? extends SpecimenTypeBaseWrapper> childSpecimenTypeCollection) {
        addToWrapperCollection(SpecimenTypePeer.CHILD_SPECIMEN_TYPES, childSpecimenTypeCollection);
        for (SpecimenTypeBaseWrapper e : childSpecimenTypeCollection) {
            e.addToParentSpecimenTypeCollectionInternal(Arrays.asList(this));
        }
    }

    void addToChildSpecimenTypeCollectionInternal(List<? extends SpecimenTypeBaseWrapper> childSpecimenTypeCollection) {
        if (isInitialized(SpecimenTypePeer.CHILD_SPECIMEN_TYPES)) {
            addToWrapperCollection(SpecimenTypePeer.CHILD_SPECIMEN_TYPES, childSpecimenTypeCollection);
        } else {
            getElementQueue().add(SpecimenTypePeer.CHILD_SPECIMEN_TYPES, childSpecimenTypeCollection);
        }
    }

    public void removeFromChildSpecimenTypeCollection(List<? extends SpecimenTypeBaseWrapper> childSpecimenTypeCollection) {
        removeFromWrapperCollection(SpecimenTypePeer.CHILD_SPECIMEN_TYPES, childSpecimenTypeCollection);
        for (SpecimenTypeBaseWrapper e : childSpecimenTypeCollection) {
            e.removeFromParentSpecimenTypeCollectionInternal(Arrays.asList(this));
        }
    }

    void removeFromChildSpecimenTypeCollectionInternal(List<? extends SpecimenTypeBaseWrapper> childSpecimenTypeCollection) {
        if (isPropertyCached(SpecimenTypePeer.CHILD_SPECIMEN_TYPES)) {
            removeFromWrapperCollection(SpecimenTypePeer.CHILD_SPECIMEN_TYPES, childSpecimenTypeCollection);
        } else {
            getElementQueue().remove(SpecimenTypePeer.CHILD_SPECIMEN_TYPES, childSpecimenTypeCollection);
        }
    }

    public void removeFromChildSpecimenTypeCollectionWithCheck(List<? extends SpecimenTypeBaseWrapper> childSpecimenTypeCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(SpecimenTypePeer.CHILD_SPECIMEN_TYPES, childSpecimenTypeCollection);
        for (SpecimenTypeBaseWrapper e : childSpecimenTypeCollection) {
            e.removeFromParentSpecimenTypeCollectionInternal(Arrays.asList(this));
        }
    }

    void removeFromChildSpecimenTypeCollectionWithCheckInternal(List<? extends SpecimenTypeBaseWrapper> childSpecimenTypeCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(SpecimenTypePeer.CHILD_SPECIMEN_TYPES, childSpecimenTypeCollection);
    }

}
