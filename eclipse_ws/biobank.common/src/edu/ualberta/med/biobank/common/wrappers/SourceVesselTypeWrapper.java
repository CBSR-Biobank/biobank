package edu.ualberta.med.biobank.common.wrappers;

import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.exception.BiobankQueryResultSizeException;
import edu.ualberta.med.biobank.common.peer.SourceVesselPeer;
import edu.ualberta.med.biobank.common.peer.SourceVesselTypePeer;
import edu.ualberta.med.biobank.common.peer.StudySourceVesselPeer;
import edu.ualberta.med.biobank.common.wrappers.base.SourceVesselTypeBaseWrapper;
import edu.ualberta.med.biobank.model.SourceVessel;
import edu.ualberta.med.biobank.model.SourceVesselType;
import edu.ualberta.med.biobank.model.StudySourceVessel;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class SourceVesselTypeWrapper extends SourceVesselTypeBaseWrapper {

    public SourceVesselTypeWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public SourceVesselTypeWrapper(WritableApplicationService appService,
        SourceVesselType sourceVesselType) {
        super(appService, sourceVesselType);
    }

    @Override
    protected void deleteChecks() throws BiobankException, ApplicationException {
        if (isUsed()) {
            throw new BiobankCheckException(
                "Source vessel is in use. Please remove from all corresponding studies and patient visits before deleting.");
        }
    }

    @Override
    protected void persistChecks() throws BiobankException,
        ApplicationException {
        checkUnique();
    }

    public void checkUnique() throws ApplicationException, BiobankException {
        checkNoDuplicates(SourceVesselType.class,
            SourceVesselTypePeer.NAME.getName(), getName(),
            "A Source Vessel Type with name");
    }

    private static final String IS_USED_BY_STUDY_QRY = "select count(s) from "
        + StudySourceVessel.class.getName() + " as s where s."
        + StudySourceVesselPeer.SOURCE_VESSEL_TYPE.getName() + "=?)";

    private static final String IS_USED_BY_SV_QRY = "select count(s) from "
        + SourceVessel.class.getName() + " as s where s."
        + SourceVesselPeer.SOURCE_VESSEL_TYPE + "=?)";

    public boolean isUsed() throws ApplicationException, BiobankException {
        // is this used by any Study-s?
        HQLCriteria c = new HQLCriteria(IS_USED_BY_STUDY_QRY,
            Arrays.asList(new Object[] { wrappedObject }));
        List<Long> results = appService.query(c);
        if (results.size() != 1) {
            throw new BiobankQueryResultSizeException();
        }
        if (results.get(0) > 0) {
            return true;
        }

        // is this used by any SourceVessel-s?
        c = new HQLCriteria(IS_USED_BY_SV_QRY,
            Arrays.asList(new Object[] { wrappedObject }));
        results = appService.query(c);
        if (results.size() != 1) {
            throw new BiobankQueryResultSizeException();
        }
        return results.get(0) > 0;
    }
}
