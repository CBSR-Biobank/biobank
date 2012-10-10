package edu.ualberta.med.biobank.common.wrappers;

import gov.nih.nci.system.applicationservice.WritableApplicationService;

import java.util.List;

@SuppressWarnings("unused")
@Deprecated
public class SourceVesselTypeWrapper {

    private static class SourceVesselType {

    }

    public SourceVesselTypeWrapper(WritableApplicationService appService) {
    }

    public SourceVesselTypeWrapper(WritableApplicationService appService,
        SourceVesselType sourceVesselType) {
    }

    public int compareTo(ModelWrapper<SourceVesselType> o) {
        return 0;
    }

    protected void deleteChecks() {
    }

    protected void persistChecks() {
    }

    public void checkUnique() {
    }

    public boolean isUsed() {
        return false;
    }

    public static void persistSourceVesselTypes(
        List<SourceVesselTypeWrapper> addedOrModifiedTypes,
        List<SourceVesselTypeWrapper> typesToDelete) throws Exception {
    }
}
