package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.ualberta.med.biobank.common.DatabaseResult;
import edu.ualberta.med.biobank.model.ContainerType;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class ContainerTypeWrapper extends ModelWrapper<ContainerType> {

    public ContainerTypeWrapper(WritableApplicationService appService,
        ContainerType wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    protected void firePropertyChanges(ContainerType oldWrappedObject,
        ContainerType newWrappedObject) {
        // TODO Auto-generated method stub

    }

    @Override
    protected DatabaseResult persistChecks() throws ApplicationException {
        return DatabaseResult.OK;
    }

    @Override
    protected Class<ContainerType> getWrappedClass() {
        return ContainerType.class;
    }

    public Collection<ContainerType> getChildContainerTypeCollection() {
        return wrappedObject.getChildContainerTypeCollection();
    }

    public Collection<ContainerType> getAllChildren() {
        List<ContainerType> allChildren = new ArrayList<ContainerType>();
        for (ContainerType type : getChildContainerTypeCollection()) {
            allChildren.addAll(new ContainerTypeWrapper(appService, type)
                .getAllChildren());
            allChildren.add(type);
        }
        return allChildren;
    }
}
