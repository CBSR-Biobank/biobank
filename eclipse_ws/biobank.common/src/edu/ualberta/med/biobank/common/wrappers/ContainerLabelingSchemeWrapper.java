package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.DatabaseResult;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

//FIXME todo by delphine
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
    protected DatabaseResult persistChecks() throws ApplicationException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void firePropertyChanges(
        ContainerLabelingScheme oldWrappedObject,
        ContainerLabelingScheme newWrappedObject) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean checkIntegrity() {
        return true;
    }

    @Override
    protected DatabaseResult deleteChecks() throws ApplicationException {
        // TODO Auto-generated method stub
        return null;
    }

}
