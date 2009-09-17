package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import edu.ualberta.med.biobank.common.DatabaseResult;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.model.Site;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

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

    public Collection<SampleType> getSampleTypes(boolean useChildrenRecursively) {
        List<SampleType> sampleTypes = new ArrayList<SampleType>();
        sampleTypes.addAll(getSampleTypeCollection());
        if (useChildrenRecursively) {
            for (ContainerType type : getChildContainerTypeCollection()) {
                sampleTypes.addAll(new ContainerTypeWrapper(appService, type)
                    .getSampleTypes(useChildrenRecursively));
            }
        }
        return sampleTypes;
    }

    private Collection<SampleType> getSampleTypeCollection() {
        return wrappedObject.getSampleTypeCollection();
    }

    /**
     * Get containers types defined in a site. if useStrictName is true, then
     * the container type name should be exactly containerName, otherwise, it
     * will contains containerName.
     */
    public static List<ContainerType> getContainerTypesInSite(
        WritableApplicationService appService, Site site, String containerName,
        boolean useStrictName) throws ApplicationException {
        String nameComparison = "=";
        String containerNameParameter = containerName;
        if (!useStrictName) {
            nameComparison = "like";
            containerNameParameter = "%" + containerName + "%";
        }
        String query = "from " + ContainerType.class.getName()
            + " where site = ? and name " + nameComparison + " ?";
        HQLCriteria criteria = new HQLCriteria(query, Arrays
            .asList(new Object[] { site, containerNameParameter }));
        return appService.query(criteria);
    }
}
