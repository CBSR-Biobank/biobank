package edu.ualberta.med.biobank.tools.testconfig;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.model.type.LabelingLayout;
import edu.ualberta.med.biobank.tools.GenericAppArgs;
import edu.ualberta.med.biobank.tools.SessionProvider;
import edu.ualberta.med.biobank.tools.SessionProvider.Mode;

/**
 * Used to create a configuration to easily test with. Creates the following
 * 
 * - One clinic, named "Clinic1". Has one contact.
 * 
 * - One study, named "Study1". Has contact from Clinic1.
 * 
 * - One patient is created, "1100", which has one collection event and one parent specimen with
 * inventory ID "A100".
 * 
 * - Two site, names "Site1" and "Site2".
 * 
 * @author Nelson Loyola
 * 
 */
public class TestConfigCreate {

    private static String USAGE = "Usage: testconfigcreate\n\n"
        + "\tReads options from db.properties file.";

    private static final Logger log = LoggerFactory.getLogger(TestConfigCreate.class);

    private static final String SOURCE_SPC_TYPE_NAME = "10mL lavender top EDTA tube";

    private static final String ALQ_SPC_TYPE_NAME = "Cells500";

    private final SessionProvider sessionProvider;

    private final Session session;

    private final String globalAdminUserLogin = "testuser";

    private final User globalAdminUser;

    private Map<String, ContainerType> containerTypes;

    public static void main(String[] argv) {
        try {
            GenericAppArgs args = new GenericAppArgs();
            args.parse(argv);
            if (args.help) {
                System.out.println(USAGE);
                System.exit(0);
            } else if (args.error) {
                System.out.println(args.errorMsg + "\n" + USAGE);
                System.exit(-1);
            }
            new TestConfigCreate(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TestConfigCreate(GenericAppArgs appArgs) throws Exception {
        sessionProvider = new SessionProvider(Mode.RUN);
        session = sessionProvider.openSession();

        globalAdminUser = (User) session.createCriteria(User.class)
            .add(Restrictions.eq("login", globalAdminUserLogin)).uniqueResult();

        if (globalAdminUser == null) {
            throw new RuntimeException(globalAdminUserLogin + " user not found");
        }

        log.debug("username: {}", appArgs.username);

        Clinic clinic = createClinic("Clinic1", "CL1");

        Set<Study> studies = new HashSet<Study>();

        for (int i = 1; i <= 2; ++i) {
            Study study = createStudy("Study" + i, "ST" + i, clinic.getContacts().iterator().next());
            studies.add(study);
        }
        Site site1 = createSite("Site1", "Site1", studies);
        createSite("Site2", "Site2", studies);

        int studyCount = 0;
        for (Study study : studies) {
            ++studyCount;
            for (int j = 0; j <= 2; ++j) {
                Integer patientNumber = 1000 + 100 * studyCount + j;
                String sourceSpecimenInventoryId = "A" + patientNumber;
                createPatientWithProcessingEvent(
                    study,
                    patientNumber.toString(),
                    sourceSpecimenInventoryId,
                    site1);
            }
        }

        createFreezer(site1);
        log.info("testing configuration created");
    }

    private Clinic createClinic(String name, String nameShort) {
        if (session == null) {
            throw new IllegalStateException("session not initialized");
        }

        session.beginTransaction();

        Address address = new Address();
        address.setCity(name);

        Clinic clinic = new Clinic();
        clinic.setName(name);
        clinic.setNameShort(nameShort);
        clinic.setActivityStatus(ActivityStatus.ACTIVE);
        clinic.setSendsShipments(true);
        clinic.setAddress(address);
        session.save(clinic);

        Contact contact = new Contact();
        contact.setName("CL1-CT1");
        contact.setClinic(clinic);
        session.save(contact);

        clinic.getContacts().add(contact);
        session.update(clinic);

        session.getTransaction().commit();

        return clinic;
    }

    private Study createStudy(String name, String nameShort, Contact contact) {
        if (session == null) {
            throw new IllegalStateException("session not initialized");
        }
        if (contact == null) {
            throw new IllegalStateException("contact is null");
        }

        session.beginTransaction();

        Study study = new Study();
        study.setName(name);
        study.setNameShort(nameShort);
        study.setActivityStatus(ActivityStatus.ACTIVE);
        study.getContacts().add(contact);
        session.save(study);

        studyAddSpecimenTypes(study);

        session.getTransaction().commit();
        return study;
    }

    private void studyAddSpecimenTypes(Study study) {
        if (session == null) {
            throw new IllegalStateException("session not initialized");
        }
        if (study == null) {
            throw new IllegalStateException("study is null");
        }

        SourceSpecimen sourceSpecimen = new SourceSpecimen();
        sourceSpecimen.setSpecimenType(getSpecimenType(SOURCE_SPC_TYPE_NAME));
        sourceSpecimen.setStudy(study);
        session.save(sourceSpecimen);
        study.getSourceSpecimens().add(sourceSpecimen);
        session.save(sourceSpecimen);

        AliquotedSpecimen aliquotedSpecimen = new AliquotedSpecimen();
        aliquotedSpecimen.setStudy(study);
        aliquotedSpecimen.setVolume(new BigDecimal("1.00"));
        aliquotedSpecimen.setQuantity(1);
        aliquotedSpecimen.setSpecimenType(getSpecimenType(ALQ_SPC_TYPE_NAME));
        study.getAliquotedSpecimens().add(aliquotedSpecimen);
        session.save(aliquotedSpecimen);
    }

    private SpecimenType getSpecimenType(String specimenTypeName) {
        SpecimenType specimenType =
            (SpecimenType) session.createCriteria(SpecimenType.class)
                .add(Restrictions.eq("name", specimenTypeName)).uniqueResult();

        if (specimenType == null) {
            throw new IllegalStateException("specimen type not found: " + specimenTypeName);
        }

        return specimenType;
    }

    private Patient createPatientWithProcessingEvent(
        Study study,
        String patientNumber,
        String sourceSpecimenInventoryId,
        Center center) {
        if (session == null) {
            throw new IllegalStateException("session not initialized");
        }
        if (study == null) {
            throw new IllegalStateException("study is null");
        }

        session.beginTransaction();

        Patient patient = new Patient();
        patient.setPnumber(patientNumber);
        patient.setCreatedAt(new Date());
        patient.setStudy(study);
        session.save(patient);

        CollectionEvent collectionEvent = new CollectionEvent();
        collectionEvent.setPatient(patient);
        collectionEvent.setVisitNumber(1);
        patient.getCollectionEvents().add(collectionEvent);

        session.save(collectionEvent);
        session.update(patient);

        OriginInfo originInfo = new OriginInfo();
        originInfo.setCenter(center);
        session.save(originInfo);

        ProcessingEvent processingEvent = new ProcessingEvent();
        processingEvent.setWorksheet(collectionEvent.getPatient().getPnumber());
        processingEvent.setCenter(center);
        processingEvent.setCreatedAt(new Date());
        session.save(processingEvent);

        Specimen specimen = new Specimen();
        specimen.setInventoryId(sourceSpecimenInventoryId);
        specimen.setSpecimenType(getSpecimenType(SOURCE_SPC_TYPE_NAME));
        specimen.setCurrentCenter(center);
        specimen.setCollectionEvent(collectionEvent);
        specimen.setOriginInfo(originInfo);
        specimen.setCreatedAt(new Date());
        specimen.setOriginalCollectionEvent(collectionEvent);
        specimen.setProcessingEvent(processingEvent);

        collectionEvent.getOriginalSpecimens().add(specimen);
        collectionEvent.getAllSpecimens().add(specimen);
        session.update(collectionEvent);
        session.save(specimen);

        session.getTransaction().commit();
        return patient;
    }

    private Site createSite(String name, String nameShort, Set<Study> studies) {
        if (session == null) {
            throw new IllegalStateException("session not initialized");
        }
        if (studies == null) {
            throw new IllegalStateException("studies is null");
        }
        Address address = new Address();
        String city = name;
        address.setCity(city);

        session.beginTransaction();

        Site site = new Site();
        site.setName(name);
        site.setNameShort(nameShort);
        site.setActivityStatus(ActivityStatus.ACTIVE);
        site.setAddress(address);
        site.getStudies().addAll(studies);
        session.save(site);

        session.getTransaction().commit();

        return site;
    }

    private Map<String, ContainerType> createContainerTypes(Site site) {
        ContainerType pallet96 = createContainerType(site, "Pallet96", "P96", 8, 12,
            getContainerLabelingScheme("SBS Standard"));
        pallet96.getSpecimenTypes().add(getSpecimenType(ALQ_SPC_TYPE_NAME));
        session.update(pallet96);

        ContainerType pallet9x9 = createContainerType(site, "Pallet9x9", "P9x9", 9, 9,
            getContainerLabelingScheme("SBS Standard"));
        pallet9x9.getSpecimenTypes().add(getSpecimenType(ALQ_SPC_TYPE_NAME));
        session.update(pallet9x9);

        ContainerType pallet10x10 = createContainerType(site, "Pallet10x10", "P10x10", 10, 10,
            getContainerLabelingScheme("SBS Standard"));
        pallet10x10.getSpecimenTypes().add(getSpecimenType(ALQ_SPC_TYPE_NAME));
        session.update(pallet10x10);

        ContainerType pallet12x12 = createContainerType(site, "Pallet12x12", "P12x12", 12, 12,
            getContainerLabelingScheme("SBS Standard"));
        pallet12x12.getSpecimenTypes().add(getSpecimenType(ALQ_SPC_TYPE_NAME));
        session.update(pallet12x12);

        // Hotel 19 holds all types of pallets
        ContainerType hotel19 = createContainerType(site, "Hotel19", "H19", 19, 1,
            getContainerLabelingScheme("2 char numeric"));
        hotel19.getChildContainerTypes().add(pallet96);
        hotel19.getChildContainerTypes().add(pallet9x9);
        hotel19.getChildContainerTypes().add(pallet10x10);
        hotel19.getChildContainerTypes().add(pallet12x12);
        session.update(hotel19);

        // Hotel 13 only holds Pallet96
        ContainerType hotel13 = createContainerType(site, "Hotel13", "H13", 13, 1,
            getContainerLabelingScheme("2 char numeric"));
        hotel19.getChildContainerTypes().add(pallet96);
        session.update(hotel13);

        ContainerType freezer = createContainerType(site, "Freezer4x10", "FR4x10", 4, 10,
            getContainerLabelingScheme("2 char alphabetic"));
        freezer.setTopLevel(true);
        freezer.getChildContainerTypes().add(hotel19);
        freezer.getChildContainerTypes().add(hotel13);
        session.update(freezer);

        Map<String, ContainerType> result = new HashMap<String, ContainerType>();
        result.put(freezer.getName(), freezer);
        result.put(hotel19.getName(), hotel19);
        result.put(hotel13.getName(), hotel13);
        result.put(pallet96.getName(), pallet96);
        result.put(pallet9x9.getName(), pallet9x9);
        result.put(pallet10x10.getName(), pallet10x10);
        result.put(pallet12x12.getName(), pallet12x12);
        return result;
    }

    private ContainerType createContainerType(Site site, String name, String nameShort,
        Integer rowCapacity, Integer colCapacity, ContainerLabelingScheme labelingScheme) {
        Capacity capacity = new Capacity();
        capacity.setRowCapacity(rowCapacity);
        capacity.setColCapacity(colCapacity);

        ContainerType containerType = new ContainerType();
        containerType.setName(name);
        containerType.setNameShort(nameShort);
        containerType.setSite(site);
        containerType.setCapacity(capacity);
        containerType.setChildLabelingScheme(labelingScheme);
        containerType.setLabelingLayout(LabelingLayout.VERTICAL);
        session.save(containerType);

        return containerType;
    }

    private ContainerLabelingScheme getContainerLabelingScheme(String schemeName) {
        ContainerLabelingScheme scheme =
            (ContainerLabelingScheme) session.createCriteria(ContainerLabelingScheme.class)
                .add(Restrictions.eq("name", schemeName)).uniqueResult();

        if (scheme == null) {
            throw new IllegalStateException("specimen type not found: " + schemeName);
        }

        return scheme;
    }

    private void createFreezer(Site site) {
        if (session == null) {
            throw new IllegalStateException("session not initialized");
        }
        if (site == null) {
            throw new IllegalStateException("studies is null");
        }

        session.beginTransaction();
        containerTypes = createContainerTypes(site);
        createFreezer("01");
        session.getTransaction().commit();
    }

    /**
     * Creates top level container and the first level child at the first position.
     * 
     * containerTypes is a list with 3 items where item 0 is the top level container, 1 the first
     * level child and 2 the second level child.
     */
    private void createFreezer(String topLevelLabel) {
        if (containerTypes.isEmpty()) {
            throw new IllegalStateException("containerTypes list is empty");
        }

        Container topContainer = createContainer(containerTypes.get("Freezer4x10"));
        topContainer.setLabel(topLevelLabel);
        session.save(topContainer);
        session.flush();

        addChildContainer(topContainer, containerTypes.get("Hotel19"), "01AA", 0, 0);
        addChildContainer(topContainer, containerTypes.get("Hotel13"), "01AB", 1, 0);
    }

    private Container createContainer(ContainerType containerType) {
        if (containerType == null) {
            throw new IllegalStateException("container type is null");
        }

        Container container = new Container();
        container.setSite(containerType.getSite());
        container.setContainerType(containerType);
        container.setTopContainer(container);
        return container;
    }

    private void addChildContainer(
        Container parentContainer,
        ContainerType childContainerType,
        String label,
        int row,
        int col) {

        session.flush();
        Container childContainer = createContainer(childContainerType);
        childContainer.setLabel(label);
        session.save(childContainer);

        ContainerPosition pos = new ContainerPosition();
        pos.setRow(row);
        pos.setCol(col);
        pos.setContainer(childContainer);
        childContainer.setPosition(pos);
        childContainer.setTopContainer(parentContainer);
        session.update(childContainer);

        pos.setParentContainer(parentContainer);
        parentContainer.getChildPositions().add(pos);

        session.update(parentContainer);
    }
}
