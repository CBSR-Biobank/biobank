package test.ualberta.med.biobank.internal;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import test.ualberta.med.biobank.Utils;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;

public class PatientVisitHelper extends DbHelper {

    /**
     * Creates a new patient visit wrapper. It is not saved to the database.
     * 
     * @param patient The patient that the patient visit belongs to.
     * @param clinic The clinic that the patient belongs to.
     * @param dateDrawn The date the sample was drawn.
     * @param dateProcessed The date the sample was processed.
     * @param dateReceived The date the sample was received.
     * @return A new patient visit wrapper.
     */
    public static PatientVisitWrapper newPatientVisit(PatientWrapper patient,
        ShipmentWrapper shipment, Date dateDrawn, Date dateProcessed,
        Date dateReceived) {
        PatientVisitWrapper pv = new PatientVisitWrapper(appService);
        pv.setPatient(patient);
        pv.setDateProcessed(dateProcessed);
        pv.setShipment(shipment);
        return pv;
    }

    /**
     * Adds a new patient visit to the database.
     * 
     * @param patient The patient that the patient visit belongs to.
     * @param clinic The clinic that the patient belongs to.
     * @param dateDrawn The date the sample was drawn.
     * @param dateProcessed The date the sample was processed.
     * @param dateReceived The date the sample was received.
     * @return A new patient visit wrapper.
     * @throws Exception if the object could not be saved to the database.
     */
    public static PatientVisitWrapper addPatientVisit(PatientWrapper patient,
        ShipmentWrapper shipment, Date dateDrawn, Date dateProcessed,
        Date dateReceived) throws Exception {
        PatientVisitWrapper pv = newPatientVisit(patient, shipment, dateDrawn,
            dateProcessed, dateReceived);
        pv.persist();
        return pv;
    }

    /**
     * Adds a new patient visit to the database.
     * 
     * @param patient The patient that the patient visit belongs to.
     * @param clinic The clinic that the patient belongs to.
     * @param shipment The shipment associated with the visit.
     * 
     * @return A new patient visit wrapper.
     * 
     * @throws Exception if the object could not be saved to the database.
     */
    public static List<PatientVisitWrapper> addPatientVisits(
        PatientWrapper patient, ShipmentWrapper shipment)
        throws ParseException, Exception {
        int count = r.nextInt(15) + 1;
        List<PatientVisitWrapper> visits = new ArrayList<PatientVisitWrapper>();
        for (int i = 0; i < count; i++) {
            visits.add(addPatientVisit(patient, shipment,
                Utils.getRandomDate(), null, null));
        }
        return visits;
    }
}
