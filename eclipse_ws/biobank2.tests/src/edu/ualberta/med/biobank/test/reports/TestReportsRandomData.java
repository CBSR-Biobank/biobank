package edu.ualberta.med.biobank.test.reports;

import edu.ualberta.med.biobank.common.util.Predicate;
import edu.ualberta.med.biobank.common.util.PredicateUtil;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleStorageWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.server.reports.AbstractReport;
import edu.ualberta.med.biobank.test.AllTests;
import edu.ualberta.med.biobank.test.internal.AliquotHelper;
import edu.ualberta.med.biobank.test.internal.ClinicHelper;
import edu.ualberta.med.biobank.test.internal.ContactHelper;
import edu.ualberta.med.biobank.test.internal.ContainerHelper;
import edu.ualberta.med.biobank.test.internal.ContainerTypeHelper;
import edu.ualberta.med.biobank.test.internal.PatientHelper;
import edu.ualberta.med.biobank.test.internal.PatientVisitHelper;
import edu.ualberta.med.biobank.test.internal.SampleStorageHelper;
import edu.ualberta.med.biobank.test.internal.SampleTypeHelper;
import edu.ualberta.med.biobank.test.internal.ShipmentHelper;
import edu.ualberta.med.biobank.test.internal.ShippingMethodHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import edu.ualberta.med.biobank.test.internal.SourceVesselHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AliquotCountTest.class, AliquotInvoiceByClinicTest.class,
    AliquotInvoiceByPatientTest.class, AliquotRequestTest.class,
    AliquotsByPalletTest.class, AliquotSCountTest.class, CAliquotsTest.class,
    ContainerCapacityTest.class, ContainerEmptyLocationsTest.class,
    DAliquotsTest.class, FTAReportTest.class, FvLPatientVisitsTest.class,
    InvoicingReportTest.class, NewPsByStudyClinicTest.class,
    NewPVsByStudyClinicTest.class, PatientVisitSummaryTest.class,
    PatientWBCTest.class, PsByStudyTest.class, PVsByStudyTest.class,
    QAAliquotsTest.class, SAliquotsTest.class, SampleTypePvCountTest.class,
    SampleTypeSUsageTest.class })
public final class TestReportsRandomData implements ReportDataSource {
    private static TestReportsRandomData INSTANCE = null;

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
    private final List<SampleStorageWrapper> sampleStorages = new ArrayList<SampleStorageWrapper>();
    private final List<AliquotWrapper> aliquots = new ArrayList<AliquotWrapper>();
    private final List<ContainerWrapper> containers = new ArrayList<ContainerWrapper>();
    private final List<ClinicWrapper> clinics = new ArrayList<ClinicWrapper>();
    private final List<StudyWrapper> studies = new ArrayList<StudyWrapper>();
    private final List<ContactWrapper> contacts = new ArrayList<ContactWrapper>();
    private final List<PatientVisitWrapper> patientVisits = new ArrayList<PatientVisitWrapper>();
    private final List<PatientWrapper> patients = new ArrayList<PatientWrapper>();
    private final List<ShipmentWrapper> shipments = new ArrayList<ShipmentWrapper>();

    private TestReportsRandomData() {
        try {
            AllTests.setUp();
        } catch (Exception e) {
            e.printStackTrace();
        }
        appService = AllTests.appService;
        Assert.assertNotNull("setUp: appService is null", appService);
    }

    // TODO: better enforce singleton (e.g. prevent clone, multi-thread, etc.)
    public static TestReportsRandomData getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TestReportsRandomData();
        }

        return INSTANCE;
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

    // TODO: refactor this setup into methods that make sense.

    @BeforeClass
    public static void setUp() throws Exception {
        // make sure AbstractReportTest classes use this class as the their
        // source of data
        AbstractReportTest.setReportDataSource(getInstance());

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

            List<ContainerTypeWrapper> containerTypes = new ArrayList<ContainerTypeWrapper>();
            ContainerTypeWrapper containerType = ContainerTypeHelper
                .addContainerType(site, getInstance().getRandString(),
                    getInstance().getRandString(), 1, CONTAINER_ROWS,
                    CONTAINER_COLS, false);
            containerTypes.add(containerType);

            // add a "CabinetXXX" type (required for certain reports)
            containerType = ContainerTypeHelper.addContainerType(site,
                getInstance().getRandString(), "Cabinet"
                    + getInstance().getRandString(), 1, CONTAINER_ROWS,
                CONTAINER_COLS, false);
            containerTypes.add(containerType);

            // for simplicity, allow every (non-top) container type to be the
            // child of any container type
            // TODO: don't add every sample type to every container type?
            for (ContainerTypeWrapper c : containerTypes) {
                c.addSampleTypes(getInstance().getSampleTypes());
                c.addChildContainerTypes(containerTypes);
                c.persist();
            }
            topContainerType.addChildContainerTypes(containerTypes);
            topContainerType.persist();

            // generate containers
            ContainerWrapper parentContainer, container;
            for (int i = 0; i < CONTAINERS_PER_SITE; i++) {
                String label = "";
                if (i == 0) {
                    // start some containers' label with "SS" (sent samples) as
                    // these containers are to be ignored by some queries
                    label = "SS";
                }
                label += getInstance().getRandString();

                parentContainer = ContainerHelper.addContainer(label, null,
                    null, site, topContainerType);
                getInstance().containers.add(parentContainer);

                // cycle through container types
                containerType = containerTypes.get(i % containerTypes.size());

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

                // TODO: add sample storages?
                // leave the first sample type unassociated with any study via
                // SampleStorage since at least one report checks for these
                for (int j = 1, numSampleTypes = getInstance().getSampleTypes()
                    .size(); j < numSampleTypes; j++) {
                    SampleTypeWrapper type = getInstance().getSampleTypes()
                        .get(j);
                    SampleStorageWrapper sampleStorage = SampleStorageHelper
                        .addSampleStorage(study, type);
                    getInstance().getSampleStorages().add(sampleStorage);
                }

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

    public List<SampleStorageWrapper> getSampleStorages() {
        return sampleStorages;
    }
}
