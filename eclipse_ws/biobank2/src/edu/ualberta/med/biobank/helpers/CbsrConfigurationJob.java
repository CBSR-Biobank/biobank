package edu.ualberta.med.biobank.helpers;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
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
import edu.ualberta.med.biobank.common.cbsr.CbsrSite;
import edu.ualberta.med.biobank.common.cbsr.CbsrStudies;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingCompanyWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.SampleStorage;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

/**
 * Accessed via the "**Debug**" main menu item. Invoked by the
 * InitExamplesHandler to populate the database with sample objects.
 */
public class CbsrConfigurationJob {

    private static Logger LOGGER = Logger.getLogger(CbsrConfigurationJob.class
        .getName());

    private WritableApplicationService appService;

    private SiteWrapper cbsrSite;

    private List<PatientWrapper> patients;

    private List<SampleTypeWrapper> sampleTypesList;

    private List<ShippingCompanyWrapper> shippingCompaniesList;

    private Random r = new Random();

    public CbsrConfigurationJob() {

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

        Job job = new Job("Init Examples") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    appService = SessionManager.getInstance().getSession()
                        .getAppService();

                    // insert methods are listed here and order is important
                    String[] addMethodNames = new String[] { "addSite",
                        "addClinicsInSite", "addStudyInSite",
                        "addPatientInStudy", "addShipmentsInClinics",
                        "addPatientVisitsInPatient", "addContainerTypesInSite",
                        "addContainers", "addSampleStorage" };
                    int taskNber = addMethodNames.length
                        + (5 * SiteWrapper.getSites(appService).size());

                    monitor.beginTask("Adding new objects to database...",
                        taskNber);

                    deleteSites(monitor);

                    for (String methodName : addMethodNames) {
                        monitor.subTask("invoking " + methodName);
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
    private void addClinicsInSite() throws Exception {
        CbsrClinics.createClinics(cbsrSite);
    }

    @SuppressWarnings("unused")
    private void addShipmentsInClinics() throws Exception {
        List<String> clinicNames = CbsrClinics.getClinicNames();
        int numClinics = clinicNames.size();
        int numShippingCompanies = shippingCompaniesList.size();

        for (int i = 0; i < 100; i++) {
            ClinicWrapper clinic = CbsrClinics.getClinic(clinicNames.get(r
                .nextInt(numClinics)));
            ShipmentWrapper shipment = new ShipmentWrapper(appService);
            String dateStr = String.format("2009-%02d-%02d %02d:%02d", r
                .nextInt(12) + 1, r.nextInt(28), r.nextInt(24), r.nextInt(60));
            shipment.setDateShipped(DateFormatter.parseToDateTime(dateStr));
            dateStr = String.format("2009-%02d-%02d %02d:%02d",
                r.nextInt(12) + 1, r.nextInt(28), r.nextInt(24), r.nextInt(60));
            shipment.setDateReceived(DateFormatter.parseToDateTime(dateStr));
            shipment.setWaybill(r.nextInt() + getRandomString(10));
            shipment.setClinic(clinic);
            shipment.setPatientCollection(Arrays
                .asList(new PatientWrapper[] { patients.get(i) }));
            shipment.setShippingCompany(shippingCompaniesList.get(r
                .nextInt(numShippingCompanies)));
            shipment.persist();
            clinic.reload();
        }
    }

    @SuppressWarnings("unused")
    private void addStudyInSite() throws Exception {
        CbsrStudies.createStudies(cbsrSite);
    }

    @SuppressWarnings("unused")
    private void addPatientVisitsInPatient() throws Exception {
        for (int i = 0; i < 100; i++) {
            PatientWrapper patient = patients.get(i);
            List<PatientVisitWrapper> visits = new ArrayList<PatientVisitWrapper>();
            visits.add(createPatientVisit(patient));
            visits.add(createPatientVisit(patient));
            visits.add(createPatientVisit(patient));
            patient.setPatientVisitCollection(visits);
            patient.persist();
        }
    }

    private PatientVisitWrapper createPatientVisit(PatientWrapper patient) {
        PatientVisitWrapper patientVisit = new PatientVisitWrapper(appService);
        String dateStr = String.format("2009-%02d-25 %02d:%02d",
            r.nextInt(12) + 1, r.nextInt(24), r.nextInt(60));
        patientVisit.setDateProcessed(DateFormatter.parseToDateTime(dateStr));
        patientVisit.setPatient(patient);
        List<ShipmentWrapper> shipments = patient.getShipmentCollection();
        if (shipments.size() > 0) {
            patientVisit.setShipment(shipments.get(0));
        }
        SampleWrapper sample = createSample(patientVisit);
        sample.setPatientVisit(patientVisit);
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

    @SuppressWarnings("unused")
    private void addPatientInStudy() throws Exception {
        List<PatientWrapper> studyPatients = new ArrayList<PatientWrapper>();
        for (int i = 0; i < 100; i++) {
            PatientWrapper patient = new PatientWrapper(appService);
            patient.setNumber(Integer.toString(i));
            patient.setStudy(study);

            patient.persist();
            studyPatients.add(patient);
        }
        study.setPatientCollection(studyPatients);
        study.persist();
        study.reload();
        patients = study.getPatientCollection();
    }

    @SuppressWarnings("unused")
    private void addContainerTypesInSite() throws Exception {
    }

    @SuppressWarnings("unused")
    private void addSampleStorage() throws Exception {
        HQLCriteria c = new HQLCriteria("from " + Study.class.getName()
            + " as study " + "where study.nameShort=?");
        c.setParameters(Arrays.asList(new Object[] { "BBP" }));
        List<Study> studies = appService.query(c);
        if (studies.size() != 1) {
            throw new Exception("BBP study not found");
        }

        Study bbpStudy = studies.get(0);

        c = new HQLCriteria("from " + SampleType.class.getName());
        List<SampleType> results = appService.query(c);
        if (results.size() == 0) {
            throw new Exception("not sample types in database");
        }

        SampleStorage ss;

        for (SampleType type : results) {
            if (type.getName().equals("DNA (Blood)")) {
                ss = new SampleStorage();
                ss.setQuantity(2);
                ss.setVolume(0.4);
            } else if (type.getName().equals("Plasma (Na Heparin) - DAD")) {
                ss = new SampleStorage();
                ss.setQuantity(15);
                ss.setVolume(0.2);
                ss.setSampleType(type);
            } else if (type.getName().equals("Duodenum")) {
                ss = new SampleStorage();
                ss.setQuantity(10);
                ss.setVolume(0.6);
                ss.setSampleType(type);
            } else {
                continue;
            }

            ss.setSampleType(type);
            ss.setStudy(bbpStudy);

            appService.executeQuery(new addExampleQuery(ss));
        }
    }

    public void deleteSites(IProgressMonitor monitor) throws Exception {
    }

    public void deleteContainers(Collection<ContainerWrapper> containers,
        IProgressMonitor monitor) throws Exception {
    }

    public void deleteStudies(List<StudyWrapper> studies,
        IProgressMonitor monitor) throws Exception {
    }

    public void deletePatients(List<PatientWrapper> patients,
        IProgressMonitor monitor) throws Exception {
        if (patients != null) {
            for (PatientWrapper patient : patients) {
                monitor.subTask("deleting patient " + patient);
                deleteFromList(patient.getPatientVisitCollection(), monitor,
                    "PatientVisit");
                patient.reload();
                patient.delete();
            }
        }
    }

    public void deleteClinics(List<ClinicWrapper> clinics,
        IProgressMonitor monitor) throws Exception {
    }

    public void deleteFromList(Collection<? extends ModelWrapper<?>> list,
        IProgressMonitor monitor, String objectName) throws Exception {
        if (list == null)
            return;

        for (ModelWrapper<?> object : list) {
            monitor.subTask("deleting " + objectName + " " + object);
            object.reload();
            object.delete();
        }
    }

    public String getRandomString(int maxlen) {
        String str = new String();
        for (int j = 0, n = r.nextInt(maxlen) + 1; j < n; ++j) {
            str += (char) ('A' + r.nextInt(26));
        }
        return str;
    }
}
