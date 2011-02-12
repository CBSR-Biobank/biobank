package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.base.SampleStorageBaseWrapper;
import edu.ualberta.med.biobank.model.SampleStorage;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SampleStorageWrapper extends SampleStorageBaseWrapper {

    public SampleStorageWrapper(WritableApplicationService appService,
        SampleStorage wrappedObject) {
        super(appService, wrappedObject);
    }

    public SampleStorageWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException,
        ApplicationException {
    }

    @Override
    public int compareTo(ModelWrapper<SampleStorage> wrapper) {
        if (wrapper instanceof SampleStorageWrapper) {
            String name1 = wrappedObject.getSampleType().getName();
            String name2 = wrapper.wrappedObject.getSampleType().getName();
            if (name1 != null && name2 != null) {
                return name1.compareTo(name2);
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        return new StringBuilder(getSampleType().getName()).append("/")
            .append(getQuantity()).append("/").append(getActivityStatus())
            .toString();
    }

}
