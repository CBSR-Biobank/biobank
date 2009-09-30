package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class ContainerLabelingSchemeWrapper extends
    ModelWrapper<ContainerLabelingScheme> {

    public ContainerLabelingSchemeWrapper(
        WritableApplicationService appService,
        ContainerLabelingScheme wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    protected Class<ContainerLabelingScheme> getWrappedClass() {
        return ContainerLabelingScheme.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException, Exception {
    }

    @Override
    protected void firePropertyChanges(
        ContainerLabelingScheme oldWrappedObject,
        ContainerLabelingScheme newWrappedObject) {
        propertyChangeSupport.firePropertyChange("name", oldWrappedObject,
            newWrappedObject);
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException, Exception {
    }

    public void setName(String name) {
        String oldName = name;
        wrappedObject.setName(name);
        propertyChangeSupport.firePropertyChange("name", oldName, name);
    }

    public String getName() {
        return wrappedObject.getName();
    }
}
