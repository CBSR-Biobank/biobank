package edu.ualberta.med.biobank;

import edu.ualberta.med.biobank.common.LabelingScheme;
import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.SampleStorage;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.DeleteExampleQuery;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

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

/**
 * Accessed via the "**Debug**" main menu item. Invoked by the
 * InitExamplesHandler to populate the database with sample objects.
 * 
 * After re-generation : init some storagetype and storage containers for one
 * site
 */
public class InitExamples {

    private static final int MAX_CLINICS = 2;

    private static WritableApplicationService appService;

    private Site site;
    private Study study;
    private Clinic[] clinics;

    private ContainerType palletType;
    private ContainerType hotel19Type;
    private ContainerType hotel13Type;
    private ContainerType freezerType;

    private Collection<Patient> patients;

    private Collection<PatientVisit> patientVisits;

    private ContainerType binType;

    private ContainerType drawerType;

    private HashMap<String, ContainerLabelingScheme> numSchemeMap;

    public InitExamples() {
        Job job = new Job("Init Examples") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    monitor.beginTask("Adding new objects to database...", 16);
                    clinics = new Clinic[MAX_CLINICS];
                    numSchemeMap = new HashMap<String, ContainerLabelingScheme>();

                    appService = SessionManager.getInstance().getSession()
                        .getAppService();

                    // new classes should be added here
                    Class<?>[] classTypes = new Class<?>[] { Container.class,
                        ContainerType.class, PatientVisit.class, Patient.class,
                        Study.class, Clinic.class, Site.class };

                    for (Class<?> classType : classTypes) {
                        monitor.subTask("deleting all " + classType.getName()
                            + " objects");
                        deleteAll(classType);
                        monitor.worked(1);
                        if (monitor.isCanceled())
                            throw new OperationCanceledException();
                    }

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
                        Method method = InitExamples.class.getDeclaredMethod(
                            methodName, new Class<?>[] {});
                        method.setAccessible(true);
                        method.invoke(InitExamples.this, new Object[] {});
                        monitor.worked(1);
                        method.setAccessible(false);
                        if (monitor.isCanceled())
                            throw new OperationCanceledException();
                    }
                } catch (Exception e) {
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
                        SessionManager.getInstance().getSession()
                            .performExpand();
                        if (event.getResult().isOK()) {
                            if ((Boolean) event.getJob().getProperty(
                                IProgressConstants.PROPERTY_IN_DIALOG))
                                return;
                            BioBankPlugin.openMessage("Init Examples",
                                "successfully added all init examples");
                        } else
                            BioBankPlugin.openError("Init Examples",
                                "Error encounted when adding init examples");
                    }
                });
            }
        });
        job.setUser(true);
        job.schedule();
    }

    @SuppressWarnings("unused")
    private void insertSite() throws ApplicationException {
        site = new Site();
        site.setName("Site Edmonton Test");
        Address address = new Address();
        address.setCity("Edmonton");
        site.setAddress(address);
        SDKQueryResult res = appService.executeQuery(new InsertExampleQuery(
            site));
        site = (Site) res.getObjectResult();
    }

    @SuppressWarnings("unused")
    private void insertClinicsInSite() throws ApplicationException {
        for (int i = 0; i < 2; ++i) {
            Clinic clinic = new Clinic();
            clinic.setName("Clinic " + i);
            clinic.setSite(site);

            Address address = new Address();
            address.setCity("Edmonton");
            clinic.setAddress(address);

            SDKQueryResult res = appService
                .executeQuery(new InsertExampleQuery(clinic));
            clinics[i] = (Clinic) res.getObjectResult();
        }
    }

    @SuppressWarnings("unused")
    private void insertStudyInSite() throws ApplicationException {
        study = new Study();
        study.setName("Blood Borne Pathogens");
        study.setNameShort("BBP");
        study.setSite(site);
        SDKQueryResult res = appService.executeQuery(new InsertExampleQuery(
            study));
        study = (Study) res.getObjectResult();
    }

    @SuppressWarnings("unused")
    private void insertSampleInPatientVisit() throws ApplicationException {
        for (PatientVisit patientVisit : patientVisits) {
            Sample sample = new Sample();
            sample.setInventoryId(Integer.valueOf(new Random().nextInt(10000))
                .toString());
            sample.setPatientVisit(patientVisit);
            sample.setProcessDate(new Date());
            sample.setSampleType(getSampleType());
            SDKQueryResult res = appService
                .executeQuery(new InsertExampleQuery(sample));
            sample = (Sample) res.getObjectResult();
        }
    }

    private SampleType getSampleType() throws ApplicationException {
        return (SampleType) appService.search(SampleType.class,
            new SampleType()).get(0);
    }

    @SuppressWarnings("unused")
    private void insertPatientVisitsInPatient() throws Exception {
        patientVisits = new ArrayList<PatientVisit>();
        Random r = new Random();
        for (Patient patient : patients) {
            createPatientVisit(r, patient);
            createPatientVisit(r, patient);
            createPatientVisit(r, patient);
        }
    }

    private void createPatientVisit(Random r, Patient patient)
        throws ParseException, ApplicationException {
        PatientVisit patientVisit = new PatientVisit();
        patientVisit.setClinic(clinics[0]);
        String dateStr = String.format("2009-%02d-25 %02d:%02d",
            r.nextInt(12) + 1, r.nextInt(24), r.nextInt(60));
        SimpleDateFormat sdf = new SimpleDateFormat(
            BioBankPlugin.DATE_TIME_FORMAT);
        patientVisit.setDateDrawn(sdf.parse(dateStr));

        patientVisit.setPatient(patient);
        SDKQueryResult res = appService.executeQuery(new InsertExampleQuery(
            patientVisit));
        patientVisit = (PatientVisit) res.getObjectResult();
        patientVisits.add(patientVisit);
    }

    @SuppressWarnings("unused")
    private void insertPatientInStudy() throws ApplicationException {
        patients = new ArrayList<Patient>();
        for (int i = 0; i < 100; i++) {
            Patient patient = new Patient();
            patient.setNumber(Integer.toString(i));
            patient.setStudy(study);

            SDKQueryResult res = appService
                .executeQuery(new InsertExampleQuery(patient));
            patient = (Patient) res.getObjectResult();
            patients.add(patient);
        }
    }

    private void deleteAll(Class<?> classType) throws Exception {
        Constructor<?> constructor = classType.getConstructor();
        Object instance = constructor.newInstance();
        List<?> list = appService.search(classType, instance);
        for (Object o : list) {
            appService.executeQuery(new DeleteExampleQuery(o));
        }

    }

    @SuppressWarnings("unused")
    private void insertContainerTypesInSite() throws ApplicationException {
        List<ContainerLabelingScheme> numSchemes = appService.search(
            ContainerLabelingScheme.class, new ContainerLabelingScheme());
        Assert.isNotNull(numSchemes);
        for (ContainerLabelingScheme scheme : numSchemes) {
            numSchemeMap.put(scheme.getName(), scheme);
        }

        // Freezer Types
        palletType = insertContainerTypeInSite("Pallet-96", "P96", false, 8,
            12, null, numSchemeMap.get("SBS Standard"));
        hotel13Type = insertContainerTypeInSite("Hotel-13", "H13", false, 13,
            1, Arrays.asList(new ContainerType[] { palletType }), numSchemeMap
                .get("2 char numeric"));
        hotel19Type = insertContainerTypeInSite("Hotel-19", "H19", false, 19,
            1, Arrays.asList(new ContainerType[] { palletType }), numSchemeMap
                .get("2 char numeric"));
        freezerType = insertContainerTypeInSite("Freezer", "FR", true, 3, 10,
            Arrays.asList(new ContainerType[] { hotel13Type, hotel19Type }),
            numSchemeMap.get("CBSR 2 char alphabetic"));

        // Cabinet Types
        binType = insertContainerTypeInSite("Bin", "Bin", false, 120, 1, null,
            numSchemeMap.get("CBSR 2 char alphabetic"));
        drawerType = insertContainerTypeInSite("Drawer", "Dr", false, 36, 1,
            Arrays.asList(new ContainerType[] { binType }), numSchemeMap
                .get("2 char numeric"));
        insertContainerTypeInSite("Cabinet", "Cab", true, 4, 1, Arrays
            .asList(new ContainerType[] { drawerType }), numSchemeMap
            .get("CBSR 2 char alphabetic"));
    }

    private ContainerType insertContainerTypeInSite(String name,
        String shortName, boolean topLevel, int dim1, int dim2,
        List<ContainerType> children,
        ContainerLabelingScheme childLabelingScheme)
        throws ApplicationException {
        ContainerType ct = new ContainerType();
        ct.setName(name);
        ct.setNameShort(shortName);
        ct.setTopLevel(topLevel);
        ct.setSite(site);
        if (childLabelingScheme != null) {
            ct.setChildLabelingScheme(childLabelingScheme);
        }
        Capacity capacity = new Capacity();
        capacity.setDimensionOneCapacity(dim1);
        capacity.setDimensionTwoCapacity(dim2);
        ct.setCapacity(capacity);
        if (children != null) {
            ct.setChildContainerTypeCollection(new HashSet<ContainerType>(
                children));
        }
        SDKQueryResult res = appService
            .executeQuery(new InsertExampleQuery(ct));
        return (ContainerType) res.getObjectResult();
    }

    private Container insertContainer(String label, ContainerType ct,
        Container parent) throws Exception {
        Container container = new Container();
        container.setLabel(label);
        container.setProductBarcode(label);
        container.setSite(site);
        container.setContainerType(ct);
        if (parent != null) {
            String labelingScheme = parent.getContainerType()
                .getChildLabelingScheme().getName();

            RowColPos rc;
            if (labelingScheme.equals("2 char numeric")) {
                rc = LabelingScheme.twoCharNumericToRowCol(parent
                    .getContainerType(), container.getLabel());
            } else if (labelingScheme.equals("CBSR 2 char alphabetic")) {
                rc = LabelingScheme.twoCharAlphaToRowCol(parent
                    .getContainerType(), container.getLabel());
            } else {
                throw new Exception("Invalid child labeling scheme: "
                    + labelingScheme);
            }

            ContainerPosition pos = new ContainerPosition();
            pos.setContainer(container);
            pos.setParentContainer(parent);
            pos.setPositionDimensionOne(rc.row);
            pos.setPositionDimensionTwo(rc.col);
            container.setPosition(pos);
        }
        SDKQueryResult res = appService.executeQuery(new InsertExampleQuery(
            container));
        return (Container) res.getObjectResult();
    }

    @SuppressWarnings("unused")
    private void insertContainers() throws Exception {
        Container freezer = insertContainer("01", freezerType, null);
        Container hotel1 = insertContainer("01AA", hotel19Type, freezer);
        insertContainer("01AA01", palletType, hotel1);
        insertContainer("01AA03", palletType, hotel1);
        Container hotel2 = insertContainer("01AE", hotel13Type, freezer);
        insertContainer("01AE01", palletType, hotel2);
        insertContainer("01AE05", palletType, hotel2);
    }

    @SuppressWarnings("unused")
    private void insertSampleStorage() throws Exception {
        HQLCriteria c = new HQLCriteria(
            "from edu.ualberta.med.biobank.model.Study as study "
                + "where study.nameShort=?");
        c.setParameters(Arrays.asList(new Object[] { "BBP" }));
        List<Study> studies = appService.query(c);
        if (studies.size() != 1) {
            throw new Exception("BBP study not found");
        }

        Study bbpStudy = studies.get(0);

        c = new HQLCriteria("from edu.ualberta.med.biobank.model.SampleType");
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

}
