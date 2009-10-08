package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import edu.ualberta.med.biobank.model.ContainerType;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ContainerLabelingSchemeWrapper extends
    ModelWrapper<ContainerLabelingScheme> {

    public ContainerLabelingSchemeWrapper(
        WritableApplicationService appService,
        ContainerLabelingScheme wrappedObject) {
        super(appService, wrappedObject);
    }

    protected ContainerLabelingSchemeWrapper(
        WritableApplicationService appService) {
        super(appService);
    }

    @Override
    public Class<ContainerLabelingScheme> getWrappedClass() {
        return ContainerLabelingScheme.class;
    }

    @Override
    protected String[] getPropertyChangesNames() {
        return new String[] { "name" };
    }

    public void setName(String name) {
        String oldName = name;
        wrappedObject.setName(name);
        propertyChangeSupport.firePropertyChange("name", oldName, name);
    }

    public String getName() {
        return wrappedObject.getName();
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException, Exception {
        if (hasContainerTypes()) {
            throw new BiobankCheckException(
                "Can't delete this ContainerLabelingScheme: container types are using it.");
        }
    }

    @Override
    protected void persistChecks() throws BiobankCheckException, Exception {
    }

    private boolean hasContainerTypes() throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria("from "
            + ContainerType.class.getName() + " where childLabelingScheme=?",
            Arrays.asList(new Object[] { wrappedObject }));
        List<ContainerType> types = appService.query(criteria);
        return types.size() > 0;
    }

    public static List<ContainerLabelingSchemeWrapper> getAllLabelingSchemes(
        WritableApplicationService appService) throws ApplicationException {
        List<ContainerLabelingScheme> schemes = appService.search(
            ContainerLabelingScheme.class, new ContainerLabelingScheme());
        return transformToWrapperList(appService, schemes);
    }

    public static List<ContainerLabelingSchemeWrapper> transformToWrapperList(
        WritableApplicationService appService,
        List<ContainerLabelingScheme> schemes) {
        List<ContainerLabelingSchemeWrapper> list = new ArrayList<ContainerLabelingSchemeWrapper>();
        for (ContainerLabelingScheme scheme : schemes) {
            list.add(new ContainerLabelingSchemeWrapper(appService, scheme));
        }
        return list;
    }

}
