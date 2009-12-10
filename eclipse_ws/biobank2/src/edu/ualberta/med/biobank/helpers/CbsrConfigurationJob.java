package edu.ualberta.med.biobank.helpers;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.IProgressConstants;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.cbsr.CbsrClinics;
import edu.ualberta.med.biobank.common.cbsr.CbsrContainerTypes;
import edu.ualberta.med.biobank.common.cbsr.CbsrContainers;
import edu.ualberta.med.biobank.common.cbsr.CbsrSite;
import edu.ualberta.med.biobank.common.cbsr.CbsrStudies;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingCompanyWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

/**
 * Accessed via the "**Debug**" main menu item. Invoked by the
 * CbsrConfigurationHandler to populate the database with CBSR configuration and
 * sample objects.
 */
public class CbsrConfigurationJob {

    private static Logger LOGGER = Logger.getLogger(CbsrConfigurationJob.class
        .getName());

    private WritableApplicationService appService;

    private SiteWrapper cbsrSite;

    private List<SampleTypeWrapper> sampleTypesList;

    private List<ShippingCompanyWrapper> shippingCompaniesList;

    private Random r = new Random();

    // methods that add objects to database, plus a message to display in job
    // dialog
    private static Map<String, String> addMethodMap;
    static {
        Map<String, String> aMap = new LinkedHashMap<String, String>();

        // insert methods are listed here and order is important
        aMap.put("addSite", "Adding sites");
        aMap.put("addClinicsToSite", "Adding clinics");
        aMap.put("addStudiesToSite", "Adding studies");
        aMap.put("addContainerTypesInSite", "Adding container types");
        aMap.put("addContainers", "Adding containers");
        aMap.put("addPatientsToStudies", "Adding patients to studies");
        aMap.put("addShipmentsInClinics", "Adding shipments to clinics");
        aMap.put("addPatientVisitsInPatient", "Adding patient visits");
        addMethodMap = Collections.unmodifiableMap(aMap);
    };

    public CbsrConfigurationJob() {
        appService = SessionManager.getInstance().getSession().getAppService();

        try {
            sampleTypesList = SampleTypeWrapper.getGlobalSampleTypes(
                appService, false);
            shippingCompaniesList = ShippingCompanyWrapper
                .getShippingCompanies(appService);
        } catch (Exception e) {
            BioBankPlugin.openError("Init Examples",
                "Error encounted when adding init examples");
            return;
        }

        Job job = new Job("CBSR Configuration") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    int taskNber = addMethodMap.size() + 1;

                    monitor.beginTask("Adding new objects to database...",
                        taskNber);

                    monitor.subTask("Removing previous CBSR configuration...");
                    deleteConfiguration();
                    monitor.worked(1);

                    for (String methodName : addMethodMap.keySet()) {
                        monitor.subTask(addMethodMap.get(methodName) + "...");
                        Method method = CbsrConfigurationJob.class
                            .getDeclaredMethod(methodName, new Class<?>[] {});
                        method.setAccessible(true);
                        method.invoke(CbsrConfigurationJob.this,
                            new Object[] {});
                        monitor.worked(1);
                        method.setAccessible(false);
                        if (monitor.isCanceled())
                            throw new OperationCanceledException();
                    }
                } catch (Exception e) {
                    LOGGER.error("initialization error", e);
                    return Status.CANCEL_STATUS;
                } finally {
                    monitor.done();
                }

                return Status.OK_STATUS;
            }
        };

        job.addJobChangeListener(new JobChangeAdapter() {
            @Override
            public void done(final IJobChangeEvent event) {
                Display.getDefault().asyncExec(new Runnable() {
                    public void run() {
                        try {
                            SessionManager.getInstance().updateSites();
                            SessionManager.getInstance().getSession().rebuild();
                            SessionManager.getInstance().getSession()
                                .performExpand();
                            if (event.getResult().isOK()) {
                                if ((Boolean) event.getJob().getProperty(
                                    IProgressConstants.PROPERTY_IN_DIALOG))
                                    return;
                                BioBankPlugin.openMessage("Init Examples",
                                    "successfully added all init examples");
                            } else
                                BioBankPlugin
                                    .openError("Init Examples",
                                        "Error encounted when adding init examples");
                        } catch (Exception e) {
                            LOGGER.error("Init Examples error", e);
                        }
                    }
                });
            }
        });
        job.setUser(true);
        job.schedule();
    }

    @SuppressWarnings("unused")
    private void addSite() throws Exception {
        cbsrSite = CbsrSite.addSite(appService);
    }

    @SuppressWarnings("unused")
    private void addClinicsToSite() throws Exception {
        CbsrClinics.createClinics(cbsrSite);
    }

    @SuppressWarnings("unused")
    private void addStudiesToSite() throws Exception {
        CbsrStudies.createStudies(cbsrSite);
    }

    @SuppressWarnings("unused")
    private void addContainerTypesInSite() throws Exception {
        CbsrContainerTypes.createContainerTypes(cbsrSite);
    }

    @SuppressWarnings("unused")
    private void addContainers() throws Exception {
        CbsrContainers.createContainers(cbsrSite);
    }

    @SuppressWarnings("unused")
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
                patient.setNumber(Integer.toString(patientCount));
                patient.setStudy(study);
                patient.persist();
                studyPatients.add(patient);
                ++patientCount;
            }
            study.setPatientCollection(studyPatients);
            study.persist();
            study.reload();
        }
    }

    @SuppressWarnings("unused")
    private void addShipmentsInClinics() throws Exception {
        List<String> clinicNames = CbsrClinics.getClinicNames();
        int numClinics = clinicNames.size();
        int numShippingCompanies = shippingCompaniesList.size();

        List<ContactWrapper> contacts;
        List<StudyWrapper> studies;
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

            study = CbsrStudies.getStudy("BBPSP");
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
            shipment.setPatientCollection(Arrays
                .asList(new PatientWrapper[] { patient }));
            shipment.setShippingCompany(shippingCompaniesList.get(r
                .nextInt(numShippingCompanies)));
            shipment.persist();
            clinic.reload();
        }
    }

    @SuppressWarnings("unused")
    private void addPatientVisitsInPatient() throws Exception {
        List<StudyWrapper> studies;
        List<PatientWrapper> patients;

        studies = cbsrSite.getStudyCollection();
        int numStudies = studies.size();

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
                    PatientVisitWrapper visit = createPatientVisit(patient);
                    if (visit == null)
                        continue;
                    visits.add(visit);
                }
                patient.setPatientVisitCollection(visits);
                patient.persist();
            }
        }
    }

    private PatientVisitWrapper createPatientVisit(PatientWrapper patient) {
        List<ShipmentWrapper> shipments = patient.getShipmentCollection();
        if (shipments.size() == 0) {
            return null;
        }

        PatientVisitWrapper patientVisit = new PatientVisitWrapper(appService);
        String dateStr = String.format("2009-%02d-25 %02d:%02d",
            r.nextInt(12) + 1, r.nextInt(24), r.nextInt(60));
        patientVisit.setDateProcessed(DateFormatter.parseToDateTime(dateStr));
        patientVisit.setPatient(patient);
        SampleWrapper sample = createSample(patientVisit);
        sample.setPatientVisit(patientVisit);
        patientVisit.setShipment(shipments.get(r.nextInt(shipments.size())));
        return patientVisit;
    }

    private SampleWrapper createSample(PatientVisitWrapper patientVisit) {
        SampleWrapper sample = new SampleWrapper(appService);
        sample.setInventoryId(Integer.valueOf(r.nextInt(10000)).toString());
        sample.setPatientVisit(patientVisit);
        sample.setLinkDate(new Date());
        sample.setSampleType(sampleTypesList.get(r.nextInt(sampleTypesList
            .size())));
        return sample;
    }

    public void deleteConfiguration() throws Exception {
        CbsrSite.deleteConfiguration(appService);
    }

    public String getRandomString(int maxlen) {
        String str = new String();
        for (int j = 0, n = r.nextInt(maxlen) + 1; j < n; ++j) {
            str += (char) ('A' + r.nextInt(26));
        }
        return str;
    }
}
