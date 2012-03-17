package edu.ualberta.med.biobank.test.validation;

import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenPosition;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.Study;

/**
 * Tries to make setting up test data easier by requiring the absolute minimum
 * amount of data and remembering the last created object and using that as a
 * default for other objects.
 * 
 * @author jferland
 * 
 */
public class Factory {
    private final NameGenerator nameGenerator;
    private final Session session;

    private Site defaultSite;
    private ContainerType defaultContainerType;
    private SpecimenType defaultSpecimenType;
    private Container defaultContainer;
    private Specimen defaultSpecimen;
    private ContainerLabelingScheme defaultContainerLabelingScheme;
    private Capacity defaultCapacity = new Capacity(5, 5);
    private Study defaultStudy;
    private Patient defaultPatient;
    private CollectionEvent defaultCollectionEvent;
    private OriginInfo defaultOriginInfo;

    private final LinkedList<Object> saveOrUpdate = new LinkedList<Object>();
    private final LinkedList<Object> createdObjects = new LinkedList<Object>();

    public Factory(Session session, String root) {
        this.session = session;
        this.nameGenerator = new NameGenerator(root);
    }

    public OriginInfo getDefaultOriginInfo() {
        if (defaultOriginInfo == null) {
            defaultOriginInfo = createOriginInfo();
        }
        return defaultOriginInfo;
    }

    public void setDefaultOriginInfo(OriginInfo defaultOriginInfo) {
        this.defaultOriginInfo = defaultOriginInfo;
    }

    public Study getDefaultStudy() {
        if (defaultStudy == null) {
            defaultStudy = createStudy();
        }
        return defaultStudy;
    }

    public void setDefaultStudy(Study defaultStudy) {
        this.defaultStudy = defaultStudy;
    }

    public Patient getDefaultPatient() {
        if (defaultPatient == null) {
            defaultPatient = createPatient();
        }
        return defaultPatient;
    }

    public void setDefaultPatient(Patient defaultPatient) {
        this.defaultPatient = defaultPatient;
    }

    public CollectionEvent getDefaultCollectionEvent() {
        if (defaultCollectionEvent == null) {
            defaultCollectionEvent = createCollectionEvent();
        }
        return defaultCollectionEvent;
    }

    public void setDefaultCollectionEvent(CollectionEvent defaultCollectionEvent) {
        this.defaultCollectionEvent = defaultCollectionEvent;
    }

    public Site getDefaultSite() {
        if (defaultSite == null) {
            defaultSite = createSite();
        }
        return defaultSite;
    }

    public void setDefaultSite(Site defaultSite) {
        this.defaultSite = defaultSite;
    }

    public ContainerType getDefaultContainerType() {
        if (defaultContainerType == null) {
            defaultContainerType = createContainerType();
        }
        return defaultContainerType;
    }

    public void setDefaultContainerType(ContainerType defaultContainerType) {
        this.defaultContainerType = defaultContainerType;
    }

    public Container getDefaultContainer() {
        if (defaultContainer == null) {
            defaultContainer = createContainer();
        }
        return defaultContainer;
    }

    public void setDefaultContainer(Container defaultContainer) {
        this.defaultContainer = defaultContainer;
    }

    public ContainerLabelingScheme getDefaultContainerLabelingScheme() {
        if (defaultContainerLabelingScheme == null) {
            defaultContainerLabelingScheme = (ContainerLabelingScheme) session
                .createCriteria(ContainerLabelingScheme.class)
                .add(Restrictions.idEq(1))
                .uniqueResult();
        }
        return defaultContainerLabelingScheme;
    }

    public void setDefaultContainerLabelingScheme(
        ContainerLabelingScheme defaultContainerLabelingScheme) {
        this.defaultContainerLabelingScheme = defaultContainerLabelingScheme;
    }

    public Capacity getDefaultCapacity() {
        return defaultCapacity;
    }

    public void setDefaultCapacity(Capacity defaultCapacity) {
        this.defaultCapacity = defaultCapacity;
    }

    public SpecimenType getDefaultSpecimenType() {
        if (defaultSpecimenType == null) {
            defaultSpecimenType = createSpecimenType();
        }
        return defaultSpecimenType;
    }

    public void setDefaultSpecimenType(SpecimenType defaultSpecimenType) {
        this.defaultSpecimenType = defaultSpecimenType;
    }

    public Specimen getDefaultSpecimen() {
        if (defaultSpecimen == null) {
            defaultSpecimen = createSpecimen();
        }
        return defaultSpecimen;
    }

    public void setDefaultSpecimen(Specimen defaultSpecimen) {
        this.defaultSpecimen = defaultSpecimen;
    }

    public Site createSite() {
        String name = nameGenerator.next(Site.class);

        Site site = new Site();
        site.setName(name);
        site.setNameShort(name);
        site.getAddress().setCity(name);

        setDefaultSite(site);
        addCreatedObject(site);
        return site;
    }

    public ContainerType createContainerType() {
        String name = nameGenerator.next(ContainerType.class);

        ContainerType containerType = new ContainerType();
        containerType.setName(name);
        containerType.setNameShort(name);
        containerType.setSite(getDefaultSite());
        containerType.setCapacity(new Capacity(getDefaultCapacity()));
        containerType
            .setChildLabelingScheme(getDefaultContainerLabelingScheme());

        setDefaultContainerType(containerType);
        addCreatedObject(containerType);
        return containerType;
    }

    public Container createContainer() {
        String label = nameGenerator.next(Container.class);

        Container container = new Container();
        container.setSite(getDefaultSite());
        container.setContainerType(getDefaultContainerType());
        container.setLabel(label);

        setDefaultContainer(container);
        addCreatedObject(container);
        return container;
    }

    public SpecimenType createSpecimenType() {
        String name = nameGenerator.next(SpecimenType.class);

        SpecimenType specimenType = new SpecimenType();
        specimenType.setName(name);
        specimenType.setNameShort(name);

        setDefaultSpecimenType(specimenType);
        addCreatedObject(specimenType);
        return specimenType;
    }

    public Specimen createSpecimen() {
        String name = nameGenerator.next(Specimen.class);

        Specimen specimen = new Specimen();
        specimen.setInventoryId(name);
        specimen.setSpecimenType(getDefaultSpecimenType());
        specimen.setCurrentCenter(getDefaultSite());
        specimen.setCollectionEvent(getDefaultCollectionEvent());
        specimen.setOriginInfo(getDefaultOriginInfo());
        specimen.setCreatedAt(new Date());

        setDefaultSpecimen(specimen);
        addCreatedObject(specimen);
        return specimen;
    }

    public Study createStudy() {
        String name = nameGenerator.next(Study.class);

        Study study = new Study();
        study.setName(name);
        study.setNameShort(name);

        setDefaultStudy(study);
        addCreatedObject(study);
        return study;
    }

    public CollectionEvent createCollectionEvent() {
        CollectionEvent collectionEvent = new CollectionEvent();

        // make sure the patient has this collection events so we can use the
        // set to generate a sensible default visit number.
        Patient patient = getDefaultPatient();
        collectionEvent.setPatient(patient);
        patient.getCollectionEvents().add(collectionEvent);

        int numCEs = patient.getCollectionEvents().size();
        collectionEvent.setVisitNumber(numCEs + 1);

        setDefaultCollectionEvent(collectionEvent);
        addCreatedObject(collectionEvent);
        return collectionEvent;
    }

    public Patient createPatient() {
        String name = nameGenerator.next(Patient.class);

        Patient patient = new Patient();
        patient.setPnumber(name);
        patient.setStudy(getDefaultStudy());
        patient.setCreatedAt(new Date());

        setDefaultPatient(patient);
        addCreatedObject(patient);
        return patient;
    }

    public OriginInfo createOriginInfo() {
        OriginInfo originInfo = new OriginInfo();
        originInfo.setCenter(getDefaultSite());

        // TODO: what about ShippingInfo?

        setDefaultOriginInfo(originInfo);
        addCreatedObject(originInfo);
        return originInfo;
    }

    public SpecimenPosition createSpecimenPosition() {
        SpecimenPosition position = new SpecimenPosition();

        Container container = getDefaultContainer();
        position.setContainer(container);
        position.setSpecimen(getDefaultSpecimen());

        container.getSpecimenPositions().add(position);

        // set a sensible default position (row, col), to do this, make sure we
        // keep the container's SpecimenPosition list up to date.
        int numSpecimens = container.getSpecimenPositions().size();

        ContainerType ct = container.getContainerType();
        position.setRow(numSpecimens / ct.getRowCapacity());
        position.setCol(numSpecimens % ct.getColCapacity());

        addCreatedObject(position);
        return position;
    }

    public class NameGenerator {
        private static final String DELIMITER = "_";

        private final String root;
        private final ConcurrentHashMap<Class<?>, AtomicInteger> suffixes =
            new ConcurrentHashMap<Class<?>, AtomicInteger>();

        private NameGenerator(String root) {
            this.root = root;
        }

        String next(Class<?> klazz) {
            suffixes.putIfAbsent(klazz, new AtomicInteger(1));

            StringBuilder sb = new StringBuilder();
            sb.append(root);
            sb.append(DELIMITER);
            sb.append(suffixes.get(klazz).incrementAndGet());

            return sb.toString();
        }
    }

    public void saveOrUpdate(Session session) {
        for (Object o : saveOrUpdate) {
            session.saveOrUpdate(o);
        }
        saveOrUpdate.clear();
        session.flush();
    }

    private void addCreatedObject(Object o) {
        createdObjects.add(o);
        saveOrUpdate.add(o);
    }
}
