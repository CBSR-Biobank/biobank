package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import gov.nih.nci.system.applicationservice.ApplicationException;
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

    protected void deleteChecks() throws BiobankException, ApplicationException {
    }

    protected void persistChecks() throws BiobankException,
        ApplicationException {
    }

    public void checkUnique() throws ApplicationException, BiobankException {
    }

    public boolean isUsed() throws ApplicationException, BiobankException {
        return false;
    }

    public static void persistSourceVesselTypes(
        List<SourceVesselTypeWrapper> addedOrModifiedTypes,
        List<SourceVesselTypeWrapper> typesToDelete)
        throws BiobankCheckException, Exception {
    }
}
