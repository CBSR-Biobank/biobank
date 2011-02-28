package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

import java.util.List;

@SuppressWarnings("unused")
@Deprecated
public class SourceVesselWrapper {

    private static class SourceVessel {
    }

    @Deprecated
    public SourceVesselWrapper(WritableApplicationService appService,
        SourceVessel wrappedObject) {
    }

    @Deprecated
    public SourceVesselWrapper(WritableApplicationService appService) {
    }

    @Deprecated
    public int compareTo(ModelWrapper<SourceVessel> wrapper) {
        return 0;
    }

    @Deprecated
    protected void deleteChecks() throws BiobankException, ApplicationException {
    }

    @Deprecated
    private void checkProcessingEvent() throws BiobankCheckException {
    }

    @Deprecated
    public static List<SourceVesselWrapper> getAllSourceVessels(
        WritableApplicationService appService) throws ApplicationException {
        return null;
    }

    @Deprecated
    public String getName() {
        return null;
    }

    @Deprecated
    public void checkUnique() throws ApplicationException {
    }

    @Deprecated
    public boolean isUsed() {
        return false;
    }

    @Deprecated
    public void setName(String name) {
    }

    @Deprecated
    public static void persistSourceVessels(
        List<SourceVesselWrapper> addedOrModifiedSampleTypes,
        List<SourceVesselWrapper> deletedSampleTypes) {

    }

}
