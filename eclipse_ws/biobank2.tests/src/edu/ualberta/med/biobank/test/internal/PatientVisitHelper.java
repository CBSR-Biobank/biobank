package edu.ualberta.med.biobank.test.internal;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.test.wrappers.TestCommon;

public class PatientVisitHelper extends DbHelper {

    /**
     * Creates a new patient visit wrapper. It is not saved to the database.
     * 
     * @param patient The patient that the patient visit belongs to.
     * @param shipment The shipment that the samples where received in.
     * @param dateProcessed The date the aliquot was processed.
     * @return A new patient visit wrapper.
     */
    public static PatientVisitWrapper newPatientVisit(PatientWrapper patient,
        ShipmentWrapper shipment, Date dateProcessed, Date dateDrawn) {
        PatientVisitWrapper pv = new PatientVisitWrapper(appService);
        pv.setPatient(patient);
        pv.setDateProcessed(dateProcessed);
        pv.setDateDrawn(dateDrawn);
        pv.setShipment(shipment);
        return pv;
    }

    /**
     * Adds a new patient visit to the database.
     * 
     * @param patient The patient that the patient visit belongs to.
     * @param shipment The shipment that the samples where received in.
     * @param dateProcessed The date the aliquot was processed.
     * @return A new patient visit wrapper.
     * @throws Exception if the object could not be saved to the database.
     */
    public static PatientVisitWrapper addPatientVisit(PatientWrapper patient,
        ShipmentWrapper shipment, Date dateProcessed, Date dateDrawn)
        throws Exception {
        PatientVisitWrapper pv = newPatientVisit(patient, shipment,
            dateProcessed, dateDrawn);
        pv.persist();
        return pv;
    }

    /**
     * Adds a new patient visit to the database.
     * 
     * @param patient The patient that the patient visit belongs to.
     * @param shipment The shipment associated with the visit.
     * 
     * @return A new patient visit wrapper.
     * 
     * @throws Exception if the object could not be saved to the database.
     */
    public static List<PatientVisitWrapper> addPatientVisits(
        PatientWrapper patient, ShipmentWrapper shipment, int minimumNumber,
        int maxNumber) throws ParseException, Exception {
        int count = r.nextInt(maxNumber - minimumNumber + 1) + minimumNumber;
        List<PatientVisitWrapper> visits = new ArrayList<PatientVisitWrapper>();
        for (int i = 0; i < count; i++) {
            visits.add(addPatientVisit(patient, shipment, TestCommon
                .getUniqueDate(r), TestCommon.getUniqueDate(r)));
        }
        return visits;
    }

    public static List<PatientVisitWrapper> addPatientVisits(
        PatientWrapper patient, ShipmentWrapper shipment, int minimumNumber)
        throws ParseException, Exception {
        return addPatientVisits(patient, shipment, 1, 15);
    }

    public static List<PatientVisitWrapper> addPatientVisits(
        PatientWrapper patient, ShipmentWrapper shipment)
        throws ParseException, Exception {
        return addPatientVisits(patient, shipment, 1);

    }
}
