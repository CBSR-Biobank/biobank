package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.peer.OriginInfoPeer;
import edu.ualberta.med.biobank.common.wrappers.base.OriginInfoBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.loggers.OriginInfoLogProvider;
import edu.ualberta.med.biobank.model.OriginInfo;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class OriginInfoWrapper extends OriginInfoBaseWrapper {
    private static final OriginInfoLogProvider LOG_PROVIDER =
        new OriginInfoLogProvider();

    public OriginInfoWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public OriginInfoWrapper(WritableApplicationService appService,
        OriginInfo originInfo) {
        super(appService, originInfo);
    }

    public List<SpecimenWrapper> getSpecimenCollection() {
        return getSpecimenCollection(false);
    }

    public List<PatientWrapper> getPatientCollection() {
        List<SpecimenWrapper> specimens = getSpecimenCollection();
        List<PatientWrapper> patients = new ArrayList<PatientWrapper>();

        for (SpecimenWrapper specimen : specimens) {
            PatientWrapper patient = specimen.getCollectionEvent().getPatient();

            if (!patients.contains(patient)) {
                patients.add(patient);
            }
        }

        return patients;
    }

    /**
     * security specific to the 2 centers involved in the shipment
     */
    @Override
    public List<? extends CenterWrapper<?>> getSecuritySpecificCenters() {
        List<CenterWrapper<?>> centers = new ArrayList<CenterWrapper<?>>();
        if (getCenter() != null)
            centers.add(getCenter());
        if (getReceiverCenter() != null)
            centers.add(getReceiverCenter());
        return centers;
    }

    @Override
    public OriginInfoLogProvider getLogProvider() {
        return LOG_PROVIDER;
    }

    /**
     * Should be addToSpecimenCollection most of the time. But can use this
     * method from tome to tome to reset the collection (used in saving
     * originInfo when want to try to re-add the specimens)
     */
    public void setSpecimenWrapperCollection(List<SpecimenWrapper> specs) {
        setWrapperCollection(OriginInfoPeer.SPECIMENS, specs);
    }

}
