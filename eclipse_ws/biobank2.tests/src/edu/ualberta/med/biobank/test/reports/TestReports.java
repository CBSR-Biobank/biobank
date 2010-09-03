package edu.ualberta.med.biobank.test.reports;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.common.util.AbstractRowPostProcess;
import edu.ualberta.med.biobank.common.util.Predicate;
import edu.ualberta.med.biobank.common.util.PredicateUtil;
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
@SuiteClasses({ AliquotCountTest.class, AliquotInvoiceByClinicTest.class,
    AliquotInvoiceByPatientTest.class, AliquotRequestTest.class,
    AliquotsByPalletTest.class, AliquotSCountTest.class, CAliquotsTest.class,
    ContainerCapacityTest.class, ContainerEmptyLocationsTest.class,
    DAliquotsTest.class, FTAReportTest.class, FvLPatientVisitsTest.class,
    InvoicingReportTest.class, NewPsByStudyClinicTest.class,
    NewPVsByStudyClinicTest.class, PatientVisitSummaryTest.class })
public final class TestReports implements ReportDataSource {
    public static final Predicate<ContainerWrapper> CONTAINER_CAN_STORE_SAMPLES_PREDICATE = new Predicate<ContainerWrapper>() {
        public boolean evaluate(ContainerWrapper container) {
            return (container.getContainerType().getSampleTypeCollection() != null)
                && (container.getContainerType().getSampleTypeCollection()
                    .size() > 0);
        }
    };
    public static final Predicate<AliquotWrapper> ALIQUOT_NOT_IN_SENT_SAMPLE_CONTAINER = new Predicate<AliquotWrapper>() {
        public boolean evaluate(AliquotWrapper aliquot) {
            return (aliquot.getParent() == null)
                || !aliquot.getParent().getLabel().startsWith("SS");
        }
    };
    public static final Predicate<AliquotWrapper> ALIQUOT_HAS_POSITION = new Predicate<AliquotWrapper>() {
        public boolean evaluate(AliquotWrapper aliquot) {
            return aliquot.getParent() != null;
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

    public static Predicate<AliquotWrapper> aliquotLinkedBetween(
        final Date after, final Date before) {
        return new Predicate<AliquotWrapper>() {
            public boolean evaluate(AliquotWrapper aliquot) {
                return (aliquot.getLinkDate().after(after) || aliquot
                    .getLinkDate().equals(after))
                    && (aliquot.getLinkDate().before(before) || aliquot
                        .getLinkDate().equals(before));
            }
        };
    }

    public static Predicate<AliquotWrapper> aliquotDrawnSameDay(final Date date) {
        final Calendar wanted = Calendar.getInstance();
        wanted.setTime(date);

        return new Predicate<AliquotWrapper>() {
            private Calendar drawn = Calendar.getInstance();

            public boolean evaluate(AliquotWrapper aliquot) {
                drawn.setTime(aliquot.getPatientVisit().getDateDrawn());
                int drawnDayOfYear = drawn.get(Calendar.DAY_OF_YEAR);
                int wantedDayOfYear = wanted.get(Calendar.DAY_OF_YEAR);
                int drawnYear = drawn.get(Calendar.YEAR);
                int wantedYear = wanted.get(Calendar.YEAR);
                return (drawnDayOfYear == wantedDayOfYear)
                    && (drawnYear == wantedYear);
            }
        };
    }

    public static void generateSites() throws Exception {
        for (int i = 0; i < NUM_SITES; i++) {
            getInstance().sites.add(SiteHelper.addSite(getInstance()
                .getRandString()));
        }
    }

    public static void generateSampleTypes() throws Exception {
        SampleTypeWrapper sampleType;
        for (int i = 0; i < NUM_SAMPLE_TYPES; i++) {
            sampleType = SampleTypeHelper.addSampleType(getInstance()
                .getRandString());
            getInstance().sampleTypes.add(sampleType);
        }

        try {
            // add a sample type with the name
            // "AbstractReport.FTA_CARD_SAMPLE_TYPE_NAME" as some reports query
            // for this specific sample type; however, this sample type may
            // already exist
            SampleTypeHelper
                .addSampleType(AbstractReport.FTA_CARD_SAMPLE_TYPE_NAME);
        } catch (Exception e) {
        }

        for (SampleTypeWrapper s : SampleTypeWrapper.getAllSampleTypes(
            getInstance().getAppService(), true)) {
            if (s.getName().equals(AbstractReport.FTA_CARD_SAMPLE_TYPE_NAME)) {
                getInstance().sampleTypes.add(s);
            }
        }

        // ensure there is one sample type named
        // "AbstractReport.FTA_CARD_SAMPLE_TYPE_NAME"
        Assert.assertTrue(PredicateUtil.filter(getInstance().sampleTypes,
            new Predicate<SampleTypeWrapper>() {
                public boolean evaluate(SampleTypeWrapper sampleType) {
                    return sampleType.getName().equals(
                        AbstractReport.FTA_CARD_SAMPLE_TYPE_NAME);
                }

            }).size() == 1);
    }

    @BeforeClass
    public static void setUp() throws Exception {
        generateSites();
        generateSampleTypes();

        Calendar calendar = Calendar.getInstance();

        for (int siteIndex = 0, numSites = getInstance().sites.size(); siteIndex < numSites; siteIndex++) {
            SiteWrapper site = getInstance().sites.get(siteIndex);

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

            // TODO: don't add every sample type to every container type?
            for (SampleTypeWrapper sampleType : getInstance().getSampleTypes()) {
                containerType.addSampleTypes(Arrays.asList(sampleType));
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
            Calendar c = Calendar.getInstance();
            c.setTime(new Date(0));
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
                        Date drawn = c.getTime();
                        c.add(Calendar.DAY_OF_YEAR, 1);
                        Date processed = c.getTime();

                        patientVisit = PatientVisitHelper.addPatientVisit(
                            patient, shipment, drawn, processed);

                        getInstance().patientVisits.add(patientVisit);

                        c.add(Calendar.HOUR_OF_DAY, 2);
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
            int aliquotNumber = 0;
            for (int containerIndex = 0, numContainers = getInstance().containers
                .size(); containerIndex < numContainers; containerIndex++) {
                container = getInstance().containers.get(containerIndex);

                if ((container.getContainerType().getSampleTypeCollection() != null)
                    && (container.getContainerType().getSampleTypeCollection()
                        .size() > 0)) {
                    for (int row = 0, numRows = container.getRowCapacity(); row < numRows; row++) {
                        for (int col = 0, numCols = container.getColCapacity(); col < numCols; col++) {
                            aliquotNumber++;

                            // cycle through sample types
                            SampleTypeWrapper sampleType = getInstance().sampleTypes
                                .get(aliquotNumber
                                    % getInstance().sampleTypes.size());

                            // cycle through patient visits
                            patientVisit = getInstance().patientVisits
                                .get(aliquotNumber
                                    % getInstance().patientVisits.size());
                            AliquotWrapper aliquot = AliquotHelper
                                .newAliquot(sampleType);

                            // leave some aliquots without a parent container
                            if (aliquotNumber % SKIP_ALIQUOT != 0) {
                                aliquot.setParent(container);
                                aliquot.setPosition(row, col);
                            }

                            aliquot.setPatientVisit(patientVisit);
                            aliquot.setInventoryId(getInstance()
                                .getRandString());

                            // base the link date on the date the patient visit
                            // is processed
                            calendar.setTime(patientVisit.getDateProcessed());
                            calendar.add(Calendar.MINUTE, 10);

                            aliquot.setLinkDate(calendar.getTime());
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

    public Collection<Object> checkReport(BiobankReport report,
        Collection<Object> expectedResults) throws ApplicationException {
        return checkReport(report, expectedResults,
            EnumSet.of(CompareResult.Size));
    }

    public static enum CompareResult {
        Order, Size
    };

    public Collection<Object> checkReport(BiobankReport report,
        Collection<Object> expectedResults, EnumSet<CompareResult> cmpOptions)
        throws ApplicationException {
        List<Object> actualResults = report.generate(getAppService());

        // post process individual rows BEFORE post processing the entire
        // collection, if necessary
        List<Object> postProcessedExpectedResults = new ArrayList<Object>(
            expectedResults);
        AbstractReport abstractReport = ReportFactory.createReport(report);
        AbstractRowPostProcess rowPostProcessor = abstractReport
            .getRowPostProcess();

        if (rowPostProcessor != null) {
            Object processedRow;
            for (int i = 0, numRows = postProcessedExpectedResults.size(); i < numRows; i++) {
                processedRow = rowPostProcessor
                    .rowPostProcess(postProcessedExpectedResults.get(i));
                postProcessedExpectedResults.set(i, processedRow);
            }
        }

        // post process the entire expected collection AFTER individual
        // rows have been processed
        postProcessedExpectedResults = abstractReport.postProcess(
            getAppService(), postProcessedExpectedResults);

        // we may only require the actual results to be a subset of
        // the expected results, so the actual results must be iterated in an
        // outer loop.
        Iterator<Object> it = postProcessedExpectedResults.iterator();
        int actualResultsSize = 0;
        for (Object actualRow : actualResults) {
            boolean isFound = false;
            if (cmpOptions.contains(CompareResult.Order)) {
                if (it.hasNext()) {
                    Object[] next = (Object[]) it.next();

                    // the order of arguments to Arrays.equals() matters, e.g.:
                    //
                    // java.util.Date date = new java.util.Date();
                    // java.util.Date stamp =
                    // new java.sql.Timestamp(date.getTime());
                    // assertTrue(date.equals(stamp));
                    // assertTrue(date.compareTo(stamp) == 0);
                    // assertTrue(stamp.compareTo(date) == 0);
                    // assertTrue(stamp.equals(date)); // <-- FAILS
                    if (Arrays.equals(next, (Object[]) actualRow)) {
                        isFound = true;
                    }
                }
            } else {
                for (Object expectedRow : postProcessedExpectedResults) {
                    if (Arrays.equals((Object[]) expectedRow,
                        (Object[]) actualRow)) {
                        isFound = true;
                        break;
                    }
                }
            }

            if (!isFound) {
                Assert.fail("did not expect this row in actual results: "
                    + Arrays.toString((Object[]) actualRow));
            } else {
                System.out.println("found: "
                    + Arrays.toString((Object[]) actualRow));
            }

            actualResultsSize++;
        }

        it = null; // done with this iterator.

        // cannot accurately know the size of actual results until they have all
        // been run through once, so, do not compare actual size to expected
        // size

        if (cmpOptions.contains(CompareResult.Size)
            && (postProcessedExpectedResults.size() != actualResultsSize)) {
            Assert.fail("expected " + postProcessedExpectedResults.size()
                + " results, got " + actualResultsSize);
        }

        return postProcessedExpectedResults;
    }
}
