package edu.ualberta.med.biobank.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

public class CbsrConfigWithDataJob extends CbsrConfigJob {

    private static Set<String> withDataSubTasks;
    static {
        Set<String> aSet = new LinkedHashSet<String>();

        // insert methods are listed here and order is important
        aSet.addAll(CbsrConfigJob.defaultSubTasks);
        aSet.add("Adding patients to studies");
        aSet.add("Adding shipments to clinics");
        aSet.add("Adding patient visits");
        withDataSubTasks = Collections.unmodifiableSet(aSet);
    };

    public CbsrConfigWithDataJob() {
        super(withDataSubTasks);
    }

    @Override
    protected void performSubTask(int subTaskNumber) throws Exception {
        switch (subTaskNumber) {
        case 0:
        case 1:
        case 2:
        case 3:
        case 4:
        case 5:
            super.performSubTask(subTaskNumber);
            break;
        case 6:
            addPatientsToStudies();
            break;
        case 7:
            addShipmentsInClinics();
            break;
        case 8:
            addPatientVisitsInPatient();
            break;
        default:
            throw new Exception("sub task number " + subTaskNumber
                + " is invalid");
        }
    }

    private void addPatientsToStudies() throws Exception {
        List<StudyWrapper> studies = cbsrSite.getStudyCollection();
        if ((studies == null) || (studies.size() == 0)) {
            throw new Exception("cannot add patients: no studies in CBSR site");
        }

        List<PatientWrapper> studyPatients;
        int patientCount = 0;

        for (StudyWrapper study : studies) {
            studyPatients = new ArrayList<PatientWrapper>();
            for (int i = 0, n = 5 + r.nextInt(15); i < n; i++) {
                PatientWrapper patient = new PatientWrapper(appService);
                patient.setPnumber(Integer.toString(patientCount));
                patient.setStudy(study);
                patient.persist();
                studyPatients.add(patient);
                ++patientCount;
            }
            study.addPatients(studyPatients);
            study.persist();
            study.reload();
        }
    }

    private void addShipmentsInClinics() throws Exception {
        int numShippingCompanies = shippingCompaniesList.size();

        List<PatientWrapper> patients;
        ClinicWrapper clinic;
        StudyWrapper study;
        PatientWrapper patient;

        for (int i = 0; i < 100; i++) {

            /*
             * comment out for now, uncomment when all studies have a contact
             * 
             * clinic = CbsrClinics.getClinic(clinicNames.get(r
             * .nextInt(numClinics))); contacts = clinic.getContactCollection();
             * studies = contacts.get(r.nextInt(contacts.size()))
             * .getStudyCollection(); study =
             * studies.get(r.nextInt(studies.size()));
             */

            study = configStudies.getStudy("BBPSP");
            study.reload();
            clinic = study.getContactCollection().get(0).getClinic();

            /*
         * 
         */

            patients = study.getPatientCollection();
            if (patients.size() == 0) {
                throw new Exception("study " + study.getNameShort()
                    + " does not have patients");
            }

            patient = patients.get(r.nextInt(patients.size()));

            ShipmentWrapper shipment = new ShipmentWrapper(appService);
            String dateStr = String.format("2009-%02d-%02d %02d:%02d", r
                .nextInt(12) + 1, r.nextInt(28), r.nextInt(24), r.nextInt(60));
            shipment.setDateShipped(DateFormatter.parseToDateTime(dateStr));
            dateStr = String.format("2009-%02d-%02d %02d:%02d",
                r.nextInt(12) + 1, r.nextInt(28), r.nextInt(24), r.nextInt(60));
            shipment.setDateReceived(DateFormatter.parseToDateTime(dateStr));
            shipment.setWaybill(r.nextInt(2000) + getRandomString(10));
            shipment.setClinic(clinic);
            shipment.addPatients(Arrays.asList(patient));
            shipment.setShippingMethod(shippingCompaniesList.get(r
                .nextInt(numShippingCompanies)));
            shipment.persist();
            clinic.reload();
        }
    }

    private void addPatientVisitsInPatient() throws Exception {
        List<StudyWrapper> studies;
        List<PatientWrapper> patients;

        studies = cbsrSite.getStudyCollection();

        if (studies.size() == 0) {
            throw new Exception(
                "cannot add patients visits: no studies in CBSR site");
        }

        for (StudyWrapper study : studies) {
            patients = study.getPatientCollection();
            if (patients.size() == 0) {
                throw new Exception(
                    "cannot add patients visits: no patients in study "
                        + study.getNameShort() + " in CBSR site");
            }

            for (PatientWrapper patient : patients) {
                List<PatientVisitWrapper> visits = new ArrayList<PatientVisitWrapper>();

                for (int j = 0, n = 1 + r.nextInt(5); j < n; ++j) {
                    PatientVisitWrapper visit = addPatientVisit(patient);
                    if (visit == null)
                        continue;
                    visits.add(visit);
                }
                patient.addPatientVisits(visits);
                patient.persist();
            }
        }
    }

    private PatientVisitWrapper addPatientVisit(PatientWrapper patient) {
        List<ShipmentWrapper> shipments = patient.getShipmentCollection();
        if (shipments.size() == 0) {
            return null;
        }

        PatientVisitWrapper patientVisit = new PatientVisitWrapper(appService);
        String dateStr = String.format("2009-%02d-25 %02d:%02d",
            r.nextInt(12) + 1, r.nextInt(24), r.nextInt(60));
        patientVisit.setDateProcessed(DateFormatter.parseToDateTime(dateStr));
        patientVisit.setPatient(patient);
        AliquotWrapper aliquot = addAliquot(patientVisit);
        aliquot.setPatientVisit(patientVisit);
        patientVisit.setShipment(shipments.get(r.nextInt(shipments.size())));
        return patientVisit;
    }

    private AliquotWrapper addAliquot(PatientVisitWrapper patientVisit) {
        AliquotWrapper aliquot = new AliquotWrapper(appService);
        aliquot.setInventoryId(Integer.valueOf(r.nextInt(10000)).toString());
        aliquot.setPatientVisit(patientVisit);
        aliquot.setLinkDate(new Date());
        aliquot.setSampleType(sampleTypesList.get(r.nextInt(sampleTypesList
            .size())));
        return aliquot;
    }

    public String getRandomString(int maxlen) {
        String str = new String();
        for (int j = 0, n = r.nextInt(maxlen) + 1; j < n; ++j) {
            str += (char) ('A' + r.nextInt(26));
        }
        return str;
    }
}
