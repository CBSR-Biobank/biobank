package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.wrappers.base.CollectionEventBaseWrapper;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Log;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class CollectionEventWrapper extends CollectionEventBaseWrapper {

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
        List<SourceVesselWrapper> sourceVessels = getSourceVesselCollection(false);
        if (sourceVessels != null && sourceVessels.size() > 0) {
            throw new BiobankCheckException(
                "Source Vessels are still linked to this Collection Event. Delete them before attempting to remove this Collection Event");
        }
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

    public void checkAtLeastOneSouceVessel() throws BiobankCheckException {
        List<SourceVesselWrapper> sourceVessels = getSourceVesselCollection(false);
        if (sourceVessels == null || sourceVessels.size() == 0) {
            throw new BiobankCheckException(
                "At least one Source Vessel should be added to this Collection Event.");
        }
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
        Collection<SourceVesselWrapper> sourceVessels = getSourceVesselCollection(false);
        Set<PatientWrapper> patients = new HashSet<PatientWrapper>();
        for (SourceVesselWrapper sourceVessel : sourceVessels) {
            PatientWrapper patient = sourceVessel.getPatient();
            patients.add(patient);
        }
        return new ArrayList<PatientWrapper>(patients);
    }

    public static List<CollectionEventWrapper> getCollectionEvents(
        WritableApplicationService appService, String waybill)
        throws ApplicationException {
        return appService.query(new HQLCriteria("from "
            + CollectionEvent.class.getName() + " ce where ce.waybill=?",
            Arrays.asList(new Object[] { waybill })));
    }

    public static List<CollectionEventWrapper> getCollectionEvents(
        WritableApplicationService appService, Date dateReceived)
        throws ApplicationException {
        return appService.query(new HQLCriteria("from "
            + CollectionEvent.class.getName() + " ce where ce.dateReceived=?",
            Arrays.asList(new Object[] { dateReceived })));
    }

    public boolean hasPatient(String pnum) {
        List<SourceVesselWrapper> svs = getSourceVesselCollection(false);
        for (SourceVesselWrapper sv : svs)
            if (sv.getPatient().getPnumber().equals(pnum))
                return true;
        return false;
    }

    public static List<CollectionEventWrapper> getTodayCollectionEvents(
        WritableApplicationService appService) throws ApplicationException {
        return appService.query(new HQLCriteria("from "
            + CollectionEvent.class.getName() + " ce where ce.dateReceived=?",
            Arrays.asList(new Object[] { new Date() })));
    }
}
