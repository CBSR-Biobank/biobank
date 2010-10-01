package edu.ualberta.med.biobank.common.wrappers.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ClinicShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.WrapperException;
import edu.ualberta.med.biobank.model.ClinicShipment;
import edu.ualberta.med.biobank.model.ClinicShipmentPatient;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PatientVisit;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class ClinicShipmentPatientWrapper extends
    ModelWrapper<ClinicShipmentPatient> {
    private static final String PROP_KEY_SHIPMENT = "shipment";
    private static final String PROP_KEY_PATIENT = "patient";
    private static final String PROP_KEY_PV_COLLECTION =
        "patientVisitCollection";

    public ClinicShipmentPatientWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public ClinicShipmentPatientWrapper(WritableApplicationService appService,
        ClinicShipmentPatient csp) {
        super(appService, csp);
    }

    @Override
    public Class<ClinicShipmentPatient> getWrappedClass() {
        return ClinicShipmentPatient.class;
    }

    public ClinicShipmentWrapper getShipment() {
        if (!propertiesMap.containsKey(PROP_KEY_SHIPMENT)) {
            ClinicShipmentWrapper wrappedShipment = null;
            ClinicShipment rawShipment = wrappedObject.getClinicShipment();

            if (rawShipment != null) {
                wrappedShipment =
                    new ClinicShipmentWrapper(appService, rawShipment);
            }

            propertiesMap.put(PROP_KEY_SHIPMENT, wrappedShipment);
        }
        return (ClinicShipmentWrapper) propertiesMap.get(PROP_KEY_SHIPMENT);
    }

    public void setShipment(ClinicShipmentWrapper shipment) {
        propertiesMap.put(PROP_KEY_SHIPMENT, shipment);
        ClinicShipment oldRawShipment = wrappedObject.getClinicShipment();
        ClinicShipment newRawShipment = null;
        if (shipment != null) {
            newRawShipment = shipment.getWrappedObject();
        }
        wrappedObject.setClinicShipment(newRawShipment);
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
            Collection<PatientVisitWrapper> wrappedPvs =
                new ArrayList<PatientVisitWrapper>();
            Collection<PatientVisit> rawPvs =
                wrappedObject.getPatientVisitCollection();

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
            visit.setClinicShipmentPatient(this);
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
            "ClinicShipmentPatientWrapper should not be persisted directly. Persist a ClinicShipment, Patient, or PatientVisit instead.");
    }

    @Override
    protected String[] getPropertyChangeNames() {
        // TODO: don't want anyone to listen to our properties?
        return new String[] {};
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException, WrapperException {
    }

    @Override
    protected void deleteChecks() throws Exception {
    }

    public static Collection<ClinicShipmentPatientWrapper> wrapClinicShipmentPatientCollection(
        WritableApplicationService appService,
        Collection<ClinicShipmentPatient> rawCsps) {
        Collection<ClinicShipmentPatientWrapper> wrappedCsps =
            new ArrayList<ClinicShipmentPatientWrapper>();
        for (ClinicShipmentPatient csp : rawCsps) {
            ClinicShipmentPatientWrapper wrappedCsp =
                new ClinicShipmentPatientWrapper(appService, csp);
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

    public boolean isSameShipmentAndPatient(ClinicShipmentPatientWrapper csp) {
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
