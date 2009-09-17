package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.DatabaseResult;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.model.Site;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SampleTypeWrapper extends ModelWrapper<SampleType> {

    public SampleTypeWrapper(WritableApplicationService appService,
        SampleType wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    protected void firePropertyChanges(SampleType oldWrappedObject,
        SampleType newWrappedObject) {
        // TODO Auto-generated method stub

    }

    @Override
    protected Class<SampleType> getWrappedClass() {
        return SampleType.class;
    }

    @Override
    protected DatabaseResult persistChecks() throws ApplicationException {
        return DatabaseResult.OK;
    }

    public static List<SampleType> getSampleTypeForContainerTypes(
        WritableApplicationService appService, Site site,
        String typeNameContains) throws ApplicationException {
        List<ContainerType> types = ContainerTypeWrapper
            .getContainerTypesInSite(appService, site, typeNameContains, false);
        List<SampleType> sampleTypes = new ArrayList<SampleType>();
        for (ContainerType type : types) {
            sampleTypes.addAll(new ContainerTypeWrapper(appService, type)
                .getSampleTypes(true));
        }
        return sampleTypes;
    }
}
