package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Log;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class CollectionEventWrapper extends
    AbstractShipmentWrapper<CollectionEvent> {

    public CollectionEventWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public CollectionEventWrapper(WritableApplicationService appService,
        CollectionEvent wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    protected void deleteChecks() throws BiobankException, ApplicationException {
        checkNoMoreSourceVessels();
    }

    private void checkNoMoreSourceVessels() throws BiobankCheckException {
        List<SourceVesselWrapper> sourceVessels = getSourceVesselCollection();
        if (sourceVessels != null && sourceVessels.size() > 0) {
            throw new BiobankCheckException(
                "Source Vessels are still linked to this Collection Event. Delete them before attempting to remove this Collection Event");
        }
    }

    @Override
    protected List<String> getPropertyChangeNames() {
        return CollectionEventPeer.PROP_NAMES;
    }

    @Override
    public Class<CollectionEvent> getWrappedClass() {
        return CollectionEvent.class;
    }

    @Override
    protected void persistChecks() throws BiobankException,
        ApplicationException {
        if (getSourceCenter() == null) {
            throw new BiobankCheckException("A Center should be set.");
        }
        checkAtLeastOneSouceVessel();
    }

    @Override
    protected void persistDependencies(CollectionEvent origObject)
        throws Exception {
    }

    public List<SourceVesselWrapper> getSourceVesselCollection() {
        return getWrapperCollection(
            CollectionEventPeer.SOURCE_VESSEL_COLLECTION,
            SourceVesselWrapper.class, false);
    }

    public void setSourceVesselCollection(
        Collection<SourceVesselWrapper> sourceVessels) {
        setWrapperCollection(CollectionEventPeer.SOURCE_VESSEL_COLLECTION,
            sourceVessels);
    }

    public void checkAtLeastOneSouceVessel() throws BiobankCheckException {
        List<SourceVesselWrapper> sourceVessels = getSourceVesselCollection();
        if (sourceVessels == null || sourceVessels.size() == 0) {
            throw new BiobankCheckException(
                "At least one Source Vessel should be added to this Collection Event.");
        }
    }

    public CenterWrapper<?> getSourceCenter() {
        return getWrappedProperty(CollectionEventPeer.SOURCE_CENTER, null);
    }

    public void setSourceCenter(CenterWrapper<?> center) {
        setWrappedProperty(CollectionEventPeer.SOURCE_CENTER, center);
    }

    @Override
    protected Log getLogMessage(String action, String site, String details) {
        Log log = new Log();
        log.setAction(action);
        if (site == null) {
            log.setSite(getSourceCenter().getNameShort());
        } else {
            log.setSite(site);
        }
        details += "Received:" + getFormattedDateReceived();
        String waybill = getWaybill();
        if (waybill != null) {
            details += " - Waybill:" + waybill;
        }
        log.setDetails(details);
        log.setType("Shipment");
        return log;
    }

    public Boolean needDeparted() {
        ShippingMethodWrapper shippingMethod = getShippingMethod();
        return shippingMethod == null || shippingMethod.needDate();
    }

    public List<PatientWrapper> getPatientCollection() {
        Collection<SourceVesselWrapper> sourceVessels = getSourceVesselCollection();
        Set<PatientWrapper> patients = new HashSet<PatientWrapper>();
        for (SourceVesselWrapper sourceVessel : sourceVessels) {
            PatientWrapper patient = sourceVessel.getPatient();
            patients.add(patient);
        }
        return new ArrayList<PatientWrapper>(patients);
    }

    public void setActivityStatus(ActivityStatusWrapper activityStatus) {
        setWrappedProperty(CollectionEventPeer.ACTIVITY_STATUS, activityStatus);
    }

    public void addSourceVessels(List<SourceVesselWrapper> svs) {
        addToWrapperCollection(CollectionEventPeer.SOURCE_VESSEL_COLLECTION,
            svs);
    }

    public void removeSourceVessels(
        List<SourceVesselWrapper> sourceVesselCollection) {
        removeFromWrapperCollection(
            CollectionEventPeer.SOURCE_VESSEL_COLLECTION,
            sourceVesselCollection);
    }
}
