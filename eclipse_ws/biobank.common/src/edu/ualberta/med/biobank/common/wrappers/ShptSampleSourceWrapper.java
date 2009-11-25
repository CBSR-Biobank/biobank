package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.SampleSource;
import edu.ualberta.med.biobank.model.Shipment;
import edu.ualberta.med.biobank.model.ShptSampleSource;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class ShptSampleSourceWrapper extends ModelWrapper<ShptSampleSource> {

    public ShptSampleSourceWrapper(WritableApplicationService appService,
        ShptSampleSource wrappedObject) {
        super(appService, wrappedObject);
    }

    public ShptSampleSourceWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected String[] getPropertyChangeNames() {
        return new String[] { "quantity", "shipment", "sampleSource",
            "patientCollection" };
    }

    @Override
    public Class<ShptSampleSource> getWrappedClass() {
        return ShptSampleSource.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException {
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException,
        ApplicationException {
    }

    public Integer getQuantity() {
        return wrappedObject.getQuantity();
    }

    public void setQuantity(Integer quantity) {
        Integer oldQuantity = getQuantity();
        wrappedObject.setQuantity(quantity);
        propertyChangeSupport.firePropertyChange("quantity", oldQuantity,
            quantity);
    }

    public ShipmentWrapper getShipment() {
        Shipment s = wrappedObject.getShipment();
        if (s == null) {
            return null;
        }
        return new ShipmentWrapper(appService, s);
    }

    public void setShipment(Shipment s) {
        ShipmentWrapper oldShipment = getShipment();
        wrappedObject.setShipment(s);
        propertyChangeSupport.firePropertyChange("shipment", oldShipment, s);
    }

    public void setShipment(ShipmentWrapper s) {
        setShipment(s.wrappedObject);
    }

    public SampleSourceWrapper getSampleSource() {
        SampleSource ss = wrappedObject.getSampleSource();
        if (ss == null) {
            return null;
        }
        return new SampleSourceWrapper(appService, ss);
    }

    public void setSampleSource(SampleSource ss) {
        SampleSource oldSs = wrappedObject.getSampleSource();
        wrappedObject.setSampleSource(ss);
        propertyChangeSupport.firePropertyChange("sampleSource", oldSs, ss);
    }

    public void setSampleSource(SampleSourceWrapper ss) {
        if (ss == null) {
            setSampleSource((SampleSource) null);
        } else {
            setSampleSource(ss.getWrappedObject());
        }
    }

    @SuppressWarnings("unchecked")
    public List<PatientWrapper> getPatientCollection() {
        List<PatientWrapper> patientCollection = (List<PatientWrapper>) propertiesMap
            .get("patientCollection");
        if (patientCollection == null) {
            Collection<Patient> children = wrappedObject.getPatientCollection();
            if (children != null) {
                patientCollection = new ArrayList<PatientWrapper>();
                for (Patient p : children) {
                    patientCollection.add(new PatientWrapper(appService, p));
                }
                propertiesMap.put("patientCollection", patientCollection);
            }
        }
        return patientCollection;
    }

    public void setPatientCollection(Collection<Patient> patientCollection,
        boolean setNull) {
        Collection<Patient> oldCollection = wrappedObject
            .getPatientCollection();
        wrappedObject.setPatientCollection(patientCollection);
        propertyChangeSupport.firePropertyChange("patientCollection",
            oldCollection, patientCollection);
        if (setNull) {
            propertiesMap.put("patientCollection", null);
        }
    }

    public void setPatientCollection(
        Collection<PatientWrapper> patientCollection) {
        Collection<Patient> pCollection = new HashSet<Patient>();
        for (PatientWrapper p : patientCollection) {
            pCollection.add(p.getWrappedObject());
        }
        setPatientCollection(pCollection, false);
        propertiesMap.put("patientCollection", patientCollection);
    }

    @Override
    public int compareTo(ModelWrapper<ShptSampleSource> o) {
        return getSampleSource().compareTo(
            ((ShptSampleSourceWrapper) o).getSampleSource());
    }

    public String getPatientsAsString() {
        List<PatientWrapper> patients = getPatientCollection();
        if (patients == null || patients.size() == 0) {
            return "";
        }
        if (patients.size() == 1) {
            return patients.get(0).getNumber();
        }
        StringBuffer sb = new StringBuffer();
        for (PatientWrapper patient : patients) {
            sb.append(patient.getNumber()).append("; ");
        }
        return sb.toString();
    }

    public void setPatientsFromString(String text, SiteWrapper site)
        throws ApplicationException {
        if (!text.equals(getPatientsAsString())) {
            List<PatientWrapper> patients = new ArrayList<PatientWrapper>();
            String[] numbers = text.split(";");
            for (String number : numbers) {
                number = number.trim();
                patients.add(PatientWrapper.getPatientInSite(appService,
                    number, site));
            }
            setPatientCollection(patients);
        }
    }
}
