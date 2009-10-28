package edu.ualberta.med.biobank.helpers;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Assert;
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
import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.LabelingScheme;
import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.common.wrappers.WrapperException;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import edu.ualberta.med.biobank.model.SampleStorage;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

/**
 * Accessed via the "**Debug**" main menu item. Invoked by the
 * InitExamplesHandler to populate the database with sample objects.
 * 
 * After re-generation : init some storage type and storage containers for one
 * site
 */
public class DebugInitializationHelper {

    private static Logger LOGGER = Logger
        .getLogger(DebugInitializationHelper.class.getName());

    private static final int MAX_CLINICS = 2;

    private WritableApplicationService appService;

    private SiteWrapper site;
    private StudyWrapper study;
    private ClinicWrapper[] clinics;

    private ContainerTypeWrapper palletType;
    private ContainerTypeWrapper hotel19Type;
    private ContainerTypeWrapper hotel13Type;
    private ContainerTypeWrapper freezerType;

    private Collection<PatientWrapper> patients;

    private Collection<PatientVisitWrapper> patientVisits;

    private ContainerTypeWrapper binType;

    private ContainerTypeWrapper drawerType;

    private HashMap<String, ContainerLabelingScheme> numSchemeMap;

    public DebugInitializationHelper() {
        Job job = new Job("Init Examples") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    monitor.beginTask("Adding new objects to database...", 16);
                    clinics = new ClinicWrapper[MAX_CLINICS];
                    numSchemeMap = new HashMap<String, ContainerLabelingScheme>();

                    appService = SessionManager.getInstance().getSession()
                        .getAppService();

                    deleteSites(monitor);

                    // insert methods are listed here and order is important
                    String[] insertMethodNames = new String[] { "insertSite",
                        "insertClinicsInSite", "insertStudyInSite",
                        "insertPatientInStudy", "insertPatientVisitsInPatient",
                        "insertSampleInPatientVisit",
                        "insertContainerTypesInSite", "insertContainers",
                        "insertSampleStorage" };

                    // invokes all methods starting with "insert"
                    for (String methodName : insertMethodNames) {
                        monitor.subTask("invoking " + methodName);
                        Method method = DebugInitializationHelper.class
                            .getDeclaredMethod(methodName, new Class<?>[] {});
                        method.setAccessible(true);
                        method.invoke(DebugInitializationHelper.this,
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
    private void insertSite() throws ApplicationException,
        BiobankCheckException, WrapperException {
        site = new SiteWrapper(appService);
        site.setName("Site Edmonton Test");
        site.setCity("Edmonton");
        site.persist();
    }

    @SuppressWarnings("unused")
    private void insertClinicsInSite() throws ApplicationException,
        BiobankCheckException, WrapperException {
        for (int i = 0; i < 2; ++i) {
            ClinicWrapper clinic = new ClinicWrapper(appService);
            clinic.setName("Clinic " + (i + 1));
            clinic.setSite(site);
            clinic.setCity("Edmonton");
            clinic.persist();
            clinics[i] = clinic;

            for (int j = 0; j < 2; ++j) {
                ContactWrapper contact = new ContactWrapper(appService);
                contact.setName("Contact " + (i + 1) + "-" + (j + 1));
                contact.setClinicWrapper(clinics[i]);
                contact.persist();
            }
        }
    }

    @SuppressWarnings("unused")
    private void insertStudyInSite() throws Exception {
        study = new StudyWrapper(appService);
        study.setName("Blood Borne Pathogens");
        study.setNameShort("BBP");
        study.setSite(site);
        clinics[0].reload();
        study.setContactCollection(Arrays
            .asList(new ContactWrapper[] { clinics[0].getContactCollection()
                .get(0) }));
        study.persist();
    }

    @SuppressWarnings("unused")
    private void insertSampleInPatientVisit() throws ApplicationException,
        BiobankCheckException, WrapperException {
        for (PatientVisitWrapper patientVisit : patientVisits) {
            SampleWrapper sample = new SampleWrapper(appService);
            sample.setInventoryId(Integer.valueOf(new Random().nextInt(10000))
                .toString());
            sample.setPatientVisit(patientVisit);
            sample.setLinkDate(new Date());
            sample.setSampleType(getSampleType());
            sample.persist();
        }
    }

    private SampleType getSampleType() throws ApplicationException {
        return (SampleType) appService.search(SampleType.class,
            new SampleType()).get(0);
    }

    @SuppressWarnings("unused")
    private void insertPatientVisitsInPatient() throws Exception {
        patientVisits = new ArrayList<PatientVisitWrapper>();
        Random r = new Random();
        for (PatientWrapper patient : patients) {
            createPatientVisit(r, patient);
            createPatientVisit(r, patient);
            createPatientVisit(r, patient);
        }
    }

    private void createPatientVisit(Random r, PatientWrapper patient)
        throws ApplicationException, BiobankCheckException, WrapperException {
        PatientVisitWrapper patientVisit = new PatientVisitWrapper(appService);
        patientVisit.setClinic(clinics[0]);
        String dateStr = String.format("2009-%02d-25 %02d:%02d",
            r.nextInt(12) + 1, r.nextInt(24), r.nextInt(60));
        patientVisit.setDateDrawn(DateFormatter.parseToDateTime(dateStr));

        patientVisit.setPatient(patient);
        patientVisit.persist();
        patientVisits.add(patientVisit);
    }

    @SuppressWarnings("unused")
    private void insertPatientInStudy() throws ApplicationException,
        BiobankCheckException, WrapperException {
        patients = new ArrayList<PatientWrapper>();
        for (int i = 0; i < 100; i++) {
            PatientWrapper patient = new PatientWrapper(appService);
            patient.setNumber(Integer.toString(i));
            patient.setStudy(study);

            patient.persist();
            patients.add(patient);
        }
    }

    @SuppressWarnings("unused")
    private void insertContainerTypesInSite() throws ApplicationException,
        BiobankCheckException, WrapperException {
        List<ContainerLabelingScheme> numSchemes = appService.search(
            ContainerLabelingScheme.class, new ContainerLabelingScheme());
        Assert.isNotNull(numSchemes);
        for (ContainerLabelingScheme scheme : numSchemes) {
            numSchemeMap.put(scheme.getName(), scheme);
        }

        // Freezer Types
        palletType = insertContainerTypeInSite("Pallet-96", "P96", false, 8,
            12, null, numSchemeMap.get("SBS Standard").getId());
        hotel13Type = insertContainerTypeInSite("Hotel-13", "H13", false, 13,
            1, Arrays.asList(new ContainerTypeWrapper[] { palletType }),
            numSchemeMap.get("2 char numeric").getId());
        hotel19Type = insertContainerTypeInSite("Hotel-19", "H19", false, 19,
            1, Arrays.asList(new ContainerTypeWrapper[] { palletType }),
            numSchemeMap.get("2 char numeric").getId());
        freezerType = insertContainerTypeInSite(
            "Freezer",
            "FR",
            true,
            3,
            10,
            Arrays
                .asList(new ContainerTypeWrapper[] { hotel13Type, hotel19Type }),
            numSchemeMap.get("CBSR 2 char alphabetic").getId());

        // Cabinet Types
        binType = insertContainerTypeInSite("Bin", "Bin", false, 120, 1, null,
            numSchemeMap.get("CBSR 2 char alphabetic").getId());
        drawerType = insertContainerTypeInSite("Drawer", "Dr", false, 36, 1,
            Arrays.asList(new ContainerTypeWrapper[] { binType }), numSchemeMap
                .get("2 char numeric").getId());
        insertContainerTypeInSite("Cabinet", "Cab", true, 4, 1, Arrays
            .asList(new ContainerTypeWrapper[] { drawerType }), numSchemeMap
            .get("CBSR 2 char alphabetic").getId());
    }

    private ContainerTypeWrapper insertContainerTypeInSite(String name,
        String shortName, boolean topLevel, int dim1, int dim2,
        List<ContainerTypeWrapper> children, Integer childLabelingScheme)
        throws ApplicationException, BiobankCheckException, WrapperException {
        ContainerTypeWrapper ct = new ContainerTypeWrapper(appService);
        ct.setName(name);
        ct.setNameShort(shortName);
        ct.setTopLevel(topLevel);
        ct.setSite(site);
        if (childLabelingScheme != null) {
            ct.setChildLabelingScheme(childLabelingScheme);
        }
        ct.setRowCapacity(dim1);
        ct.setColCapacity(dim2);
        if (children != null) {
            ct.setChildContainerTypeCollection(children);
        }
        ct.persist();
        return ct;
    }

    private ContainerWrapper insertContainer(String label,
        ContainerTypeWrapper ct, ContainerWrapper parent) throws Exception {
        ContainerWrapper container = new ContainerWrapper(appService);
        container.setLabel(label);
        container.setProductBarcode(label);
        container.setSite(site);
        container.setContainerType(ct);
        if (parent != null) {
            RowColPos rc = LabelingScheme.getRowColFromPositionString(container
                .getLabel(), parent.getContainerType());
            if (rc == null) {
                throw new Exception(
                    "error while getting the position from string");
            }
            container.setParent(parent);
            container.setPosition(rc);
        }
        container.persist();
        return container;
    }

    @SuppressWarnings("unused")
    private void insertContainers() throws Exception {
        ContainerWrapper freezer = insertContainer("01", freezerType, null);
        ContainerWrapper hotel1 = insertContainer("01AA", hotel19Type, freezer);
        insertContainer("01AA01", palletType, hotel1);
        insertContainer("01AA03", palletType, hotel1);
        ContainerWrapper hotel2 = insertContainer("01AE", hotel13Type, freezer);
        insertContainer("01AE01", palletType, hotel2);
        insertContainer("01AE05", palletType, hotel2);
    }

    @SuppressWarnings("unused")
    private void insertSampleStorage() throws Exception {
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

            appService.executeQuery(new InsertExampleQuery(ss));
        }
    }

    public void deleteSites(IProgressMonitor monitor) throws Exception {
        for (SiteWrapper site : SiteWrapper.getAllSites(appService)) {
            monitor.subTask("deleting site " + site);
            site.reload();
            deleteContainers(site.getTopContainerCollection(), monitor);
            // in case containers with no top level type has been created
            // without a
            // parent :
            // TODO check if still need this with last modifications
            site.reload();
            deleteContainers(site.getContainerCollection(), monitor);
            deleteStudies(site.getStudyCollection(), monitor);
            deleteClinics(site.getClinicCollection(), monitor);
            deleteFromList(site.getContainerTypeCollection(), monitor);
            site.reload();
            site.delete();
            monitor.worked(1);
        }
    }

    public void deleteContainers(Collection<ContainerWrapper> containers,
        IProgressMonitor monitor) throws Exception {
        if ((containers == null) || (containers.size() == 0))
            return;

        for (ContainerWrapper container : containers) {
            monitor.subTask("deleting container " + container);
            container.reload();
            if (container.hasChildren()) {
                deleteContainers(container.getChildren().values(), monitor);
            }
            if (container.hasSamples()) {
                deleteFromList(container.getSamples().values(), monitor);
            }
            container.reload();
            container.delete();
            monitor.worked(1);
        }
    }

    public void deleteStudies(List<StudyWrapper> studies,
        IProgressMonitor monitor) throws Exception {
        if (studies == null)
            return;

        for (StudyWrapper study : studies) {
            monitor.subTask("deleting study " + study);
            deletePatients(study.getPatientCollection(), monitor);
            study.reload();
            study.delete();
            monitor.worked(1);
        }
    }

    public void deletePatients(List<PatientWrapper> patients,
        IProgressMonitor monitor) throws Exception {
        if (patients == null)
            return;

        for (PatientWrapper patient : patients) {
            monitor.subTask("deleting patient " + patient);
            deleteFromList(patient.getPatientVisitCollection(), monitor);
            patient.reload();
            patient.delete();
            monitor.worked(1);
        }
    }

    public void deleteClinics(List<ClinicWrapper> clinics,
        IProgressMonitor monitor) throws Exception {
        for (ClinicWrapper clinic : clinics) {
            monitor.subTask("deleting clinic " + clinic);
            clinic.reload();
            deleteFromList(clinic.getContactCollection(), monitor);
            clinic.reload();
            clinic.delete();
            monitor.worked(1);
        }
    }

    public void deleteFromList(Collection<? extends ModelWrapper<?>> list,
        IProgressMonitor monitor) throws Exception {
        if (list == null)
            return;

        for (ModelWrapper<?> object : list) {
            monitor.subTask("deleting object " + object);
            object.reload();
            object.delete();
            monitor.worked(1);
        }
    }

}
