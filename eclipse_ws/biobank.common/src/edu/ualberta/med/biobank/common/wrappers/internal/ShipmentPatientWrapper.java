package edu.ualberta.med.biobank.common.wrappers.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.peer.ShipmentPatientPeer;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.WrapperException;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.Shipment;
import edu.ualberta.med.biobank.model.ShipmentPatient;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class ShipmentPatientWrapper extends ModelWrapper<ShipmentPatient> {
    private static final String PROP_KEY_SHIPMENT = "shipment";
    private static final String PROP_KEY_PATIENT = "patient";
    private static final String PROP_KEY_PV_COLLECTION = "patientVisitCollection";

    public ShipmentPatientWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public ShipmentPatientWrapper(WritableApplicationService appService,
        ShipmentPatient csp) {
        super(appService, csp);
    }

    @Override
    public Class<ShipmentPatient> getWrappedClass() {
        return ShipmentPatient.class;
    }

    public ShipmentWrapper getShipment() {
        if (!propertiesMap.containsKey(PROP_KEY_SHIPMENT)) {
            ShipmentWrapper wrappedShipment = null;
            Shipment rawShipment = wrappedObject.getShipment();

            if (rawShipment != null) {
                wrappedShipment = new ShipmentWrapper(appService, rawShipment);
            }

            propertiesMap.put(PROP_KEY_SHIPMENT, wrappedShipment);
        }
        return (ShipmentWrapper) propertiesMap.get(PROP_KEY_SHIPMENT);
    }

    public void setShipment(ShipmentWrapper shipment) {
        propertiesMap.put(PROP_KEY_SHIPMENT, shipment);
        Shipment oldRawShipment = wrappedObject.getShipment();
        Shipment newRawShipment = null;
        if (shipment != null) {
            newRawShipment = shipment.getWrappedObject();
        }
        wrappedObject.setShipment(newRawShipment);
        propertyChangeSupport.firePropertyChange(PROP_KEY_SHIPMENT,
            oldRawShipment, newRawShipment);
    }

    public PatientWrapper getPatient() {
        if (!propertiesMap.containsKey(PROP_KEY_PATIENT)) {
            PatientWrapper wrappedPatient = null;
            Patient rawPatient = wrappedObject.getPatient();

            if (rawPatient != null) {
                wrappedPatient = new PatientWrapper(appService, rawPatient);
            }

            propertiesMap.put(PROP_KEY_PATIENT, wrappedPatient);
        }
        return (PatientWrapper) propertiesMap.get(PROP_KEY_PATIENT);
    }

    public void setPatient(PatientWrapper patient) {
        propertiesMap.put(PROP_KEY_PATIENT, patient);
        Patient oldRawPatient = wrappedObject.getPatient();
        Patient newRawPatient = patient.getWrappedObject();
        wrappedObject.setPatient(newRawPatient);
        propertyChangeSupport.firePropertyChange(PROP_KEY_PATIENT,
            oldRawPatient, newRawPatient);
    }

    @SuppressWarnings("unchecked")
    public Collection<PatientVisitWrapper> getPatientVisitCollection() {
        if (!propertiesMap.containsKey(PROP_KEY_PV_COLLECTION)) {
            Collection<PatientVisitWrapper> wrappedPvs = new ArrayList<PatientVisitWrapper>();
            Collection<PatientVisit> rawPvs = wrappedObject
                .getPatientVisitCollection();

            if (rawPvs != null) {
                PatientVisitWrapper wrappedPv;
                for (PatientVisit visit : rawPvs) {
                    wrappedPv = new PatientVisitWrapper(appService, visit);
                    wrappedPvs.add(wrappedPv);
                }
            }

            propertiesMap.put(PROP_KEY_PV_COLLECTION, wrappedPvs);
        }
        return (Collection<PatientVisitWrapper>) propertiesMap
            .get(PROP_KEY_PV_COLLECTION);
    }

    public void setPatientVisitCollection(
        Collection<PatientVisitWrapper> newWrappedPvs) {

        Collection<PatientVisit> oldRawPvs = new ArrayList<PatientVisit>();
        for (PatientVisitWrapper visit : getPatientVisitCollection()) {
            oldRawPvs.add(visit.getWrappedObject());
        }

        Collection<PatientVisit> newRawPvs = new HashSet<PatientVisit>();
        for (PatientVisitWrapper visit : newWrappedPvs) {
            visit.setShipmentPatient(this);
            newRawPvs.add(visit.getWrappedObject());
        }

        propertiesMap.put(PROP_KEY_PV_COLLECTION, newWrappedPvs);
        wrappedObject.setPatientVisitCollection(newRawPvs);
        propertyChangeSupport.firePropertyChange(PROP_KEY_PV_COLLECTION,
            oldRawPvs, newRawPvs);
    }

    @Override
    public void persist() throws Exception {
        throw new BiobankCheckException(
            "ShipmentPatientWrapper should not be persisted directly. Persist a Shipment, Patient, or PatientVisit instead.");
    }

    @Override
    protected List<String> getPropertyChangeNames() {
        return ShipmentPatientPeer.PROP_NAMES;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException, WrapperException {
    }

    @Override
    protected void deleteChecks() throws Exception {
    }

    public static Collection<ShipmentPatientWrapper> wrapShipmentPatientCollection(
        WritableApplicationService appService,
        Collection<ShipmentPatient> rawCsps) {
        Collection<ShipmentPatientWrapper> wrappedCsps = new ArrayList<ShipmentPatientWrapper>();
        for (ShipmentPatient csp : rawCsps) {
            ShipmentPatientWrapper wrappedCsp = new ShipmentPatientWrapper(
                appService, csp);
            wrappedCsps.add(wrappedCsp);
        }
        return wrappedCsps;
    }

    @Override
    public String toString() {
        // TODO: couldn't this slow a lot down (b/c of a lot of "unnecessary"
        // fetching)
        return "(" + getShipment().getId() + ", " + getPatient().getId() + ")";
    }

    public boolean isSameShipmentAndPatient(ShipmentPatientWrapper csp) {
        if (csp == null) {
            return false;
        }

        if (getId().equals(csp.getId())) {
            return true;
        }

        if (getShipment() != null && getShipment().getId() != null
            && csp.getShipment() != null
            && getShipment().getId().equals(csp.getShipment().getId())) {

            if (csp.getPatient() != null && csp.getPatient().getId() != null
                && csp.getPatient() != null
                && csp.getPatient().getId().equals(csp.getPatient().getId())) {
                return true;
            }
        }

        return false;
    }
}
