package edu.ualberta.med.biobank.test.reports;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.common.util.BiobankListProxy;
import edu.ualberta.med.biobank.common.util.Predicate;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.server.reports.AbstractReport;
import edu.ualberta.med.biobank.server.reports.ReportFactory;
import edu.ualberta.med.biobank.test.AllTests;
import edu.ualberta.med.biobank.test.internal.AliquotHelper;
import edu.ualberta.med.biobank.test.internal.ClinicHelper;
import edu.ualberta.med.biobank.test.internal.ContactHelper;
import edu.ualberta.med.biobank.test.internal.ContainerHelper;
import edu.ualberta.med.biobank.test.internal.ContainerTypeHelper;
import edu.ualberta.med.biobank.test.internal.PatientHelper;
import edu.ualberta.med.biobank.test.internal.PatientVisitHelper;
import edu.ualberta.med.biobank.test.internal.SampleTypeHelper;
import edu.ualberta.med.biobank.test.internal.ShipmentHelper;
import edu.ualberta.med.biobank.test.internal.ShippingMethodHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import edu.ualberta.med.biobank.test.internal.SourceVesselHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

@RunWith(Suite.class)
@SuiteClasses({ AliquotCountTest.class, ContainerCapacityTest.class,
    ContainerEmptyLocationsTest.class, PatientVisitSummaryTest.class })
public final class TestReports {
    public static final Predicate<ContainerWrapper> CAN_STORE_SAMPLES_PREDICATE = new Predicate<ContainerWrapper>() {
        public boolean evaluate(ContainerWrapper container) {
            return (container.getContainerType().getSampleTypeCollection() != null)
                && (container.getContainerType().getSampleTypeCollection()
                    .size() > 0);
        }
    };

    private static TestReports INSTANCE = null;

    private static final int NUM_SITES = 1;
    private static final int CONTAINER_DEPTH = 3;
    private static final int CONTAINERS_PER_SITE = 4;
    private static final int SKIP_ALIQUOT = 7; // leave every xth aliquot empty
    private static final int NUM_SAMPLE_TYPES = 5;
    private static final int NUM_CLINICS = 7;
    private static final int[] NUM_CONTACTS_PER_CLINIC = { 1, 2, 3 };
    private static final int[] NUM_CONTACTS_PER_STUDY = { 2, 2, 3 };
    private static final int NUM_STUDIES = 11;
    private static final int CONTAINER_ROWS = 2;
    private static final int CONTAINER_COLS = 3;
    private static final int PATIENTS_PER_STUDY = 17;
    private static final int SHIPMENTS_PER_SITE = 11; // TODO: up to 23, but
                                                      // limit number of
                                                      // patients added to
                                                      // shipment.
    private static final int[] PATIENTS_PER_SHIPMENT = { 5, 2, 3 };

    // TODO: generate data to ensure patient visits WITHOUT aliquots, etc.?

    private final WritableApplicationService appService;
    private final Random random = new Random(1); // I want "repeatable"
                                                 // tests
    private final List<SiteWrapper> sites = new ArrayList<SiteWrapper>();
    private final List<SampleTypeWrapper> sampleTypes = new ArrayList<SampleTypeWrapper>();
    private final List<AliquotWrapper> aliquots = new ArrayList<AliquotWrapper>();
    private final List<ContainerWrapper> containers = new ArrayList<ContainerWrapper>();
    private final List<ClinicWrapper> clinics = new ArrayList<ClinicWrapper>();
    private final List<StudyWrapper> studies = new ArrayList<StudyWrapper>();
    private final List<ContactWrapper> contacts = new ArrayList<ContactWrapper>();
    private final List<PatientVisitWrapper> patientVisits = new ArrayList<PatientVisitWrapper>();
    private final List<PatientWrapper> patients = new ArrayList<PatientWrapper>();
    private final List<ShipmentWrapper> shipments = new ArrayList<ShipmentWrapper>();

    private TestReports() {
        try {
            AllTests.setUp();
        } catch (Exception e) {
            e.printStackTrace();
        }
        appService = AllTests.appService;
        Assert.assertNotNull("setUp: appService is null", appService);
    }

    // TODO: better enforce singleton (e.g. prevent clone, multi-thread, etc.)
    public static TestReports getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TestReports();
        }

        return INSTANCE;
    }

    @BeforeClass
    public static void setUp() throws Exception {
        // generate sites
        for (int i = 0; i < NUM_SITES; i++) {
            getInstance().sites.add(SiteHelper.addSite(getInstance()
                .getRandString()));
        }

        for (int siteIndex = 0, numSites = getInstance().sites.size(); siteIndex < numSites; siteIndex++) {
            SiteWrapper site = getInstance().sites.get(siteIndex);
            SampleTypeWrapper sampleType;

            // TODO: vary container types?
            ContainerTypeWrapper topContainerType = ContainerTypeHelper
                .addContainerType(site, getInstance().getRandString(),
                    getInstance().getRandString(), 1, CONTAINER_ROWS,
                    CONTAINER_COLS, true);

            ContainerTypeWrapper containerType = ContainerTypeHelper
                .addContainerType(site, getInstance().getRandString(),
                    getInstance().getRandString(), 1, CONTAINER_ROWS,
                    CONTAINER_COLS, false);
            topContainerType.addChildContainerTypes(Arrays
                .asList(containerType));
            containerType.addChildContainerTypes(Arrays.asList(containerType));

            // generate sample types
            for (int i = 0; i < NUM_SAMPLE_TYPES; i++) {
                sampleType = SampleTypeHelper.addSampleType(getInstance()
                    .getRandString());
                containerType.addSampleTypes(Arrays.asList(sampleType));
                getInstance().sampleTypes.add(sampleType);
            }
            topContainerType.persist();
            containerType.persist();

            // generate containers
            ContainerWrapper parentContainer, container;
            for (int i = 0; i < CONTAINERS_PER_SITE; i++) {
                // start some containers' label with "SS" (Sample Storage) as
                // these containers are to be ignored by some queries
                String label = (i == 0 ? "SS" : "")
                    + getInstance().getRandString();
                parentContainer = ContainerHelper.addContainer(label, null,
                    null, site, topContainerType);
                getInstance().containers.add(parentContainer);

                for (int j = 1; j < CONTAINER_DEPTH; j++) {
                    container = ContainerHelper.addContainer(label, null,
                        parentContainer, site, containerType, 0, 0);

                    getInstance().containers.add(container);

                    parentContainer.persist();
                    parentContainer.reload();

                    parentContainer = container;
                }
            }

            // generate clinics
            // TODO: move this up a level when clinics go global.
            for (int i = 0; i < NUM_CLINICS; i++) {
                ClinicWrapper clinic = ClinicHelper.addClinic(site,
                    getInstance().getRandString());
                getInstance().clinics.add(clinic);

                ContactWrapper contact;
                for (int j = 0; j < NUM_CONTACTS_PER_CLINIC[i
                    % NUM_CONTACTS_PER_CLINIC.length]; j++) {
                    contact = ContactHelper.addContact(clinic, getInstance()
                        .getRandString());
                    getInstance().contacts.add(contact);
                }

                // need to reload clinic-contact relationship
                clinic.reload();
            }

            // generate studies
            // TODO: move this up a level when studies go global.
            int contactIndex = 0;
            for (int i = 0; i < NUM_STUDIES; i++) {
                StudyWrapper study = StudyHelper.addStudy(getInstance()
                    .getRandString());

                Assert.assertTrue(getInstance().contacts.size() > 0);

                // cycle through different numbers of contacts to add per study.
                for (int j = 0; j < NUM_CONTACTS_PER_STUDY[i
                    % NUM_CONTACTS_PER_STUDY.length]; j++) {
                    ContactWrapper contact = getInstance().contacts
                        .get(contactIndex++ % getInstance().contacts.size());

                    study.addContacts(Arrays.asList(contact));

                    // need to reload contact-study relationship
                    contact.reload();
                }

                // need to reload contact-study relationship
                study.persist();
                study.reload();

                getInstance().studies.add(study);
            }

            // generate patients
            PatientVisitWrapper patientVisit;
            for (int i = 0; i < PATIENTS_PER_STUDY; i++) {
                for (StudyWrapper study : getInstance().studies) {
                    // add 1 patient to each study, then another to each study,
                    // etc. so that the list of patients is not grouped by study
                    PatientWrapper patient = PatientHelper.addPatient(
                        getInstance().getRandString(), study);
                    getInstance().patients.add(patient);
                }
            }

            // generate shipments with associated patient visits
            int millisecondsPastEpoch = 0;
            for (int i = 0; i < SHIPMENTS_PER_SITE; i++) {
                ClinicWrapper clinic = getInstance().clinics.get(i
                    % getInstance().clinics.size());

                for (StudyWrapper study : clinic.getStudyCollection()) {
                    ShipmentWrapper shipment = null;

                    // TODO: geez, always add all patients to a shipment?
                    // probably should cycle through blocks of em.
                    for (PatientWrapper patient : study.getPatientCollection()) {
                        if (shipment == null) {
                            shipment = ShipmentHelper.addShipment(site, clinic,
                                patient);
                        } else {
                            shipment.addPatients(Arrays.asList(patient));
                        }

                        // TODO: appropriate dates
                        patientVisit = PatientVisitHelper.addPatientVisit(
                            patient, shipment, new Date(millisecondsPastEpoch),
                            new Date(millisecondsPastEpoch
                                + (60 * 60 * 24 * 1000)));

                        getInstance().patientVisits.add(patientVisit);

                        // note: remember to use seconds since the database
                        // will only support that resolution
                        millisecondsPastEpoch += 1000;
                    }

                    shipment.persist();
                    shipment.reload();

                    getInstance().shipments.add(shipment);
                }
            }

            // reload patients to get patient-patientvisit info
            for (PatientWrapper patient : getInstance().patients) {
                patient.reload();
            }

            // populate the containers with aliquots
            for (int containerIndex = 0, numContainers = getInstance().containers
                .size(); containerIndex < numContainers; containerIndex++) {
                container = getInstance().containers.get(containerIndex);

                if ((container.getContainerType().getSampleTypeCollection() != null)
                    && (container.getContainerType().getSampleTypeCollection()
                        .size() > 0)) {
                    for (int row = 0, numRows = container.getRowCapacity(); row < numRows; row++) {
                        for (int col = 0, numCols = container.getColCapacity(); col < numCols; col++) {
                            int aliquotNumber = (siteIndex * numSites)
                                + (containerIndex * numContainers)
                                + (row * numRows) + col;

                            // cycle through sample types
                            sampleType = getInstance().sampleTypes
                                .get(aliquotNumber
                                    % getInstance().sampleTypes.size());

                            // do not add aliquots all the time
                            if (aliquotNumber % SKIP_ALIQUOT == 0) {
                                break;
                            }

                            // cycle through patient visits
                            patientVisit = getInstance().patientVisits
                                .get(aliquotNumber
                                    % getInstance().patientVisits.size());

                            AliquotWrapper aliquot = AliquotHelper.addAliquot(
                                sampleType, container, patientVisit, row, col);

                            // base the link date on the date the patient visit
                            // is processed
                            aliquot
                                .setLinkDate(new Date(
                                    (patientVisit.getDateProcessed()
                                        .getSeconds() + 10 * 60) * 1000));
                            aliquot.persist();

                            getInstance().aliquots.add(aliquot);
                        }
                    }
                }

                container.reload();
            }
        }
    }

    @AfterClass
    public static void tearDown() throws Exception {
        try {
            SiteHelper.deleteCreatedStudies();
            SiteHelper.deleteCreatedSites();
            SampleTypeHelper.deleteCreatedSampleTypes();
            SourceVesselHelper.deleteCreatedSourceVessels();
            ShippingMethodHelper.deleteCreateShippingMethods();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            Assert.fail();
        }
    }

    public WritableApplicationService getAppService() {
        return appService;
    }

    public final String getRandString() {
        return new BigInteger(130, random).toString(32);
    }

    public final Random getRandom() {
        return random;
    }

    public List<SiteWrapper> getSites() {
        return sites;
    }

    public List<SampleTypeWrapper> getSampleTypes() {
        return sampleTypes;
    }

    public List<AliquotWrapper> getAliquots() {
        return aliquots;
    }

    public List<ContainerWrapper> getContainers() {
        return containers;
    }

    public List<ClinicWrapper> getClinics() {
        return clinics;
    }

    public List<StudyWrapper> getStudies() {
        return studies;
    }

    public List<ContactWrapper> getContacts() {
        return contacts;
    }

    public List<PatientVisitWrapper> getPatientVisits() {
        return patientVisits;
    }

    public List<PatientWrapper> getPatients() {
        return patients;
    }

    public List<Object> checkReport(BiobankReport report,
        Collection<Object> expectedResults) throws ApplicationException {
        List<Object> actualResults = report.generate(getAppService());

        int actualResultsSize = actualResults.size();
        if (actualResults instanceof BiobankListProxy) {
            actualResultsSize = ((BiobankListProxy) actualResults)
                .getRealSize();
        }

        AbstractReport abstractReport = ReportFactory.createReport(report);
        Collection<Object> postProcessedExpectedResults = abstractReport
            .postProcess(getAppService(),
                new ArrayList<Object>(expectedResults));

        if (actualResultsSize != postProcessedExpectedResults.size()) {
            Assert.fail();
        }

        // order independent comparison of results
        for (Object expectedRow : postProcessedExpectedResults) {
            Object postProcessedExpectedRow = expectedRow;
            if (abstractReport.getRowPostProcess() != null) {
                postProcessedExpectedRow = abstractReport.getRowPostProcess()
                    .rowPostProcess(expectedRow);
            }

            boolean isFound = false;
            for (Object actualRow : actualResults) {
                if (Arrays.equals((Object[]) postProcessedExpectedRow,
                    (Object[]) actualRow)) {
                    isFound = true;
                    break;
                }
            }
            if (!isFound) {
                Assert.fail("expected output row not found: "
                    + Arrays.toString((Object[]) postProcessedExpectedRow));
            } else {
                System.out.println("Found: "
                    + Arrays.toString((Object[]) postProcessedExpectedRow));
            }
        }

        return actualResults;
    }
}
