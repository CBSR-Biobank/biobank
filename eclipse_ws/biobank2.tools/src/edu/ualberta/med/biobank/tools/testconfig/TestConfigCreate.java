package edu.ualberta.med.biobank.tools.testconfig;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;
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

    private static String USAGE =
        "Usage: testconfigcreate\n\n"
            + "\tReads options from db.properties file.";

    private static final Logger log = LoggerFactory.getLogger(TestConfigCreate.class);

    private static final String SOURCE_SPC_TYPE_NAME = "10mL lavender top EDTA tube";

    private static final String ALQ_SPC_TYPE_NAME = "Cells500";

    private final SessionProvider sessionProvider;

    private final Session session;

    private final String globalAdminUserLogin = "testuser";

    private final User globalAdminUser;

    public static void main(String[] argv) {
        try {
            GenericAppArgs args = new GenericAppArgs(argv);
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
        Study study = createStudy("Study1", "ST1", clinic.getContacts().iterator().next());

        createPatientWithCollectionEvent(study, clinic);

        Set<Study> studies = new HashSet<Study>();
        studies.add(study);

        createSite("Site1", "Site1", studies);
        createSite("Site2", "Site2", studies);

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
        SpecimenType specimenType = (SpecimenType) session.createCriteria(SpecimenType.class)
            .add(Restrictions.eq("name", specimenTypeName)).uniqueResult();

        if (specimenType == null) {
            throw new IllegalStateException("specimen type not found: " + specimenTypeName);
        }

        return specimenType;
    }

    private void createPatientWithCollectionEvent(Study study, Clinic clinic) {
        if (session == null) {
            throw new IllegalStateException("session not initialized");
        }
        if (study == null) {
            throw new IllegalStateException("study is null");
        }
        Patient patient = new Patient();
        patient.setPnumber("1100");
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
        originInfo.setCenter(clinic);
        session.save(originInfo);

        Specimen specimen = new Specimen();
        specimen.setInventoryId("A100");
        specimen.setSpecimenType(getSpecimenType(SOURCE_SPC_TYPE_NAME));
        specimen.setCurrentCenter(clinic);
        specimen.setCollectionEvent(collectionEvent);
        specimen.setOriginInfo(originInfo);
        specimen.setCreatedAt(new Date());
        specimen.setOriginalCollectionEvent(collectionEvent);

        collectionEvent.getOriginalSpecimens().add(specimen);
        collectionEvent.getAllSpecimens().add(specimen);
        session.save(specimen);
        session.update(collectionEvent);
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

}
