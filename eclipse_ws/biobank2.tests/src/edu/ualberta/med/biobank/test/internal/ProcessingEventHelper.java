package edu.ualberta.med.biobank.test.internal;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceVesselWrapper;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.wrappers.TestCommon;

public class ProcessingEventHelper extends DbHelper {
    private static ActivityStatusWrapper activeActivityStatus = null;

    /**
     * Creates a new patient pevent wrapper. It is not saved to the database.
     * 
     * @param patient The patient that the patient pevent belongs to.
     * @param shipment The shipment that the samples where received in.
     * @param dateProcessed The date the aliquot was processed.
     * @return A new patient pevent wrapper.
     */
    public static ProcessingEventWrapper newProcessingEvent(
        CenterWrapper<?> center, PatientWrapper patient, Date dateProcessed,
        Date dateDrawn) throws Exception {
        ProcessingEventWrapper pv = new ProcessingEventWrapper(appService);
        pv.setCenter(center);
        pv.setPatient(patient);
        pv.setDateProcessed(dateProcessed);
        pv.setDateDrawn(dateDrawn);
        pv.setActivityStatus(getActiveActivityStatus());

        return pv;
    }

    private static ActivityStatusWrapper getActiveActivityStatus()
        throws Exception {
        if (activeActivityStatus == null) {
            activeActivityStatus = ActivityStatusWrapper.getActivityStatus(
                appService, ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        }

        return activeActivityStatus;
    }

    /**
     * Adds a new patient pevent to the database.
     * 
     * @param patient The patient that the patient pevent belongs to.
     * @param shipment The shipment that the samples where received in.
     * @param dateProcessed The date the aliquot was processed.
     * @return A new patient pevent wrapper.
     * @throws Exception if the object could not be saved to the database.
     */
    public static ProcessingEventWrapper addProcessingEvent(
        CenterWrapper<?> center, PatientWrapper patient, Date dateProcessed,
        Date dateDrawn, boolean includeSourceVessels) throws Exception {
        ProcessingEventWrapper pv = newProcessingEvent(center, patient,
            dateProcessed, dateDrawn);
        pv.persist();
        if (includeSourceVessels) {
            List<ClinicWrapper> clinics = patient.getStudy()
                .getClinicCollection();
            if ((clinics == null) || clinics.isEmpty())
                throw new Exception(
                    "Cannot add source vessels: no clinic link to the study to "
                        + "create a collection event");
            SourceVesselWrapper sv = SourceVesselHelper.newSourceVessel(
                patient, Utils.getRandomDate(), 0.1);
            sv.setProcessingEvent(pv);
            CollectionEventWrapper ce = CollectionEventHelper
                .addCollectionEventNoWaybill(
                    clinics.get(0),
                    ShippingMethodWrapper.getShippingMethods(appService).get(0),
                    sv);
        }
        return pv;
    }

    public static ProcessingEventWrapper addProcessingEvent(
        CenterWrapper<?> center, PatientWrapper patient, Date dateProcessed,
        Date dateDrawn) throws Exception {
        return addProcessingEvent(center, patient, dateProcessed, dateDrawn,
            false);
    }

    /**
     * Adds a new patient pevent to the database.
     * 
     * @param patient The patient that the patient pevent belongs to.
     * @param shipment The shipment associated with the pevent.
     * 
     * @return A new patient pevent wrapper.
     * 
     * @throws Exception if the object could not be saved to the database.
     */
    public static List<ProcessingEventWrapper> addProcessingEvents(
        CenterWrapper<?> center, PatientWrapper patient, int minimumNumber,
        int maxNumber, boolean includeSourceVessels) throws ParseException,
        Exception {
        int count = r.nextInt(maxNumber - minimumNumber + 1) + minimumNumber;
        List<ProcessingEventWrapper> pevents = new ArrayList<ProcessingEventWrapper>();
        for (int i = 0; i < count; i++) {
            pevents.add(addProcessingEvent(center, patient,
                TestCommon.getUniqueDate(r), TestCommon.getUniqueDate(r),
                includeSourceVessels));
        }
        return pevents;
    }

    public static List<ProcessingEventWrapper> addProcessingEvents(
        CenterWrapper<?> center, PatientWrapper patient, int minimumNumber,
        boolean includeSourceVessels) throws ParseException, Exception {
        return addProcessingEvents(center, patient, minimumNumber, 15,
            includeSourceVessels);
    }

    public static List<ProcessingEventWrapper> addProcessingEvents(
        CenterWrapper<?> center, PatientWrapper patient,
        boolean includeSourceVessels) throws ParseException, Exception {
        return addProcessingEvents(center, patient, 1, includeSourceVessels);
    }

    public static List<ProcessingEventWrapper> addProcessingEvents(
        CenterWrapper<?> center, PatientWrapper patient) throws ParseException,
        Exception {
        return addProcessingEvents(center, patient, false);

    }

}
