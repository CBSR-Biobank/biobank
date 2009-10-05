package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.model.SampleType;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SampleTypeWrapper extends ModelWrapper<SampleType> {

    public SampleTypeWrapper(WritableApplicationService appService,
        SampleType wrappedObject) {
        super(appService, wrappedObject);
    }

    public SampleTypeWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected String[] getPropertyChangesNames() {
        return null;

    }

    @Override
    public Class<SampleType> getWrappedClass() {
        return SampleType.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException, Exception {
    }

    /**
     * get all sample types in a site for containers which type name contains
     * "typeNameContains"
     */
    public static List<SampleTypeWrapper> getSampleTypeForContainerTypes(
        WritableApplicationService appService, SiteWrapper siteWrapper,
        String typeNameContains) throws ApplicationException {
        List<ContainerTypeWrapper> types = ContainerTypeWrapper
            .getContainerTypesInSite(appService, siteWrapper, typeNameContains,
                false);
        List<SampleTypeWrapper> sampleTypes = new ArrayList<SampleTypeWrapper>();
        for (ContainerTypeWrapper type : types) {
            sampleTypes.addAll(type.getSampleTypeCollectionRecursively());
        }
        return sampleTypes;
    }

    @Override
    public boolean checkIntegrity() {
        return true;
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException, Exception {
        // TODO Auto-generated method stub
    }

    public String getNameShort() {
        return wrappedObject.getNameShort();
    }

    public String getName() {
        return wrappedObject.getName();
    }

    public static List<SampleTypeWrapper> transformToWrapperList(
        WritableApplicationService appService, List<SampleType> sampleTypes) {
        List<SampleTypeWrapper> list = new ArrayList<SampleTypeWrapper>();
        for (SampleType type : sampleTypes) {
            list.add(new SampleTypeWrapper(appService, type));
        }
        return list;
    }

    public static List<SampleTypeWrapper> getAllWrappers(
        WritableApplicationService appService) throws ApplicationException {
        List<SampleType> sampleTypes = appService.search(SampleType.class,
            new SampleType());
        return transformToWrapperList(appService, sampleTypes);
    }
}
