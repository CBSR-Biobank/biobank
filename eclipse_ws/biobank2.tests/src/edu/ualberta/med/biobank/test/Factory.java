package edu.ualberta.med.biobank.test;

import java.math.BigInteger;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Group;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Principal;
import edu.ualberta.med.biobank.model.Rank;
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenPosition;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;

/**
 * Tries to make setting up test data easier by requiring the absolute minimum
 * amount of data and remembering the last created object and using that as a
 * default for other objects.
 * 
 * @author Jonathan Ferland
 * 
 */
public class Factory {
    private static final Random R = new Random();

    private final ContainerLabelingSchemeGetter schemeGetter;
    private final NameGenerator nameGenerator;
    private final Session session;

    private Site defaultSite;
    private Center defaultCenter;
    private ContainerType defaultTopContainerType;
    private ContainerType defaultContainerType;
    private SpecimenType defaultSpecimenType;
    private Container defaultTopContainer;
    private Container defaultParentContainer;
    private Container defaultContainer;
    private Specimen defaultSpecimen;
    private ContainerLabelingScheme defaultContainerLabelingScheme;
    private Capacity defaultCapacity = new Capacity(5, 5);
    private Study defaultStudy;
    private Patient defaultPatient;
    private CollectionEvent defaultCollectionEvent;
    private OriginInfo defaultOriginInfo;
    private User defaultUser;
    private Group defaultGroup;
    private Principal defaultPrincipal;
    private Membership defaultMembership;
    private Role defaultRole;

    public Factory(Session session) {
        this(session, new BigInteger(130, R).toString(32));
    }

    public Factory(Session session, String root) {
        this.session = session;
        this.nameGenerator = new NameGenerator(root);
        this.schemeGetter = new ContainerLabelingSchemeGetter();
    }

    public Role getDefaultRole() {
        if (defaultRole == null) {
            defaultRole = createRole();
        }
        return defaultRole;
    }

    public void setDefaultRole(Role defaultRole) {
        this.defaultRole = defaultRole;
    }

    public Center getDefaultCenter() {
        if (defaultCenter == null) {
            defaultCenter = createSite();
        }
        return defaultCenter;
    }

    public void setDefaultCenter(Center defaultCenter) {
        this.defaultCenter = defaultCenter;
    }

    public Group getDefaultGroup() {
        if (defaultGroup == null) {
            defaultGroup = createGroup();
        }
        return defaultGroup;
    }

    public void setDefaultGroup(Group defaultGroup) {
        this.defaultGroup = defaultGroup;
    }

    public Principal getDefaultPrincipal() {
        if (defaultPrincipal == null) {
            defaultPrincipal = createUser();
        }
        return defaultPrincipal;
    }

    public void setDefaultPrincipal(Principal defaultPrincipal) {
        this.defaultPrincipal = defaultPrincipal;
    }

    public User getDefaultUser() {
        if (defaultUser == null) {
            defaultUser = createUser();
        }
        return defaultUser;
    }

    public void setDefaultUser(User defaultUser) {
        this.defaultUser = defaultUser;
    }

    public Membership getDefaultMembership() {
        if (defaultMembership == null) {
            defaultMembership = createMembership();
        }
        return defaultMembership;
    }

    public void setDefaultMembership(Membership defaultMembership) {
        this.defaultMembership = defaultMembership;
    }

    public Container getDefaultParentContainer() {
        return defaultParentContainer;
    }

    public void setDefaultParentContainer(Container defaultParentContainer) {
        this.defaultParentContainer = defaultParentContainer;
    }

    public Container getDefaultTopContainer() {
        if (defaultTopContainer == null) {
            defaultTopContainer = createTopContainer();
        }
        return defaultTopContainer;
    }

    public void setDefaultTopContainer(Container defaultTopContainer) {
        this.defaultTopContainer = defaultTopContainer;
    }

    public ContainerType getDefaultTopContainerType() {
        if (defaultTopContainerType == null) {
            defaultTopContainerType = createTopContainerType();
        }
        return defaultTopContainerType;
    }

    public void setDefaultTopContainerType(ContainerType defaultTopContainerType) {
        this.defaultTopContainerType = defaultTopContainerType;
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
            defaultContainerLabelingScheme = getScheme().getSbs();
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
        site.getAddress().setCity("testville");

        setDefaultSite(site);
        session.save(site);
        session.flush();
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
        session.save(containerType);
        session.flush();
        return containerType;
    }

    public ContainerType createTopContainerType() {
        ContainerType oldDefaultContainerType = getDefaultContainerType();
        ContainerType topContainerType = createContainerType();
        topContainerType.setTopLevel(true);

        // restore the old, non-topLevel ContainerType
        setDefaultContainerType(oldDefaultContainerType);
        setDefaultTopContainerType(topContainerType);
        session.update(topContainerType);
        session.flush();
        return topContainerType;
    }

    public Container createContainer() {
        String label = nameGenerator.next(Container.class);

        Container container = new Container();
        container.setSite(getDefaultSite());
        container.setContainerType(getDefaultTopContainerType());
        container.setLabel(label);

        Container parentContainer = getDefaultParentContainer();
        if (parentContainer != null) {
            ContainerType containerType = getDefaultContainerType();
            container.setContainerType(containerType);

            ContainerType parentCt = parentContainer.getContainerType();
            parentCt.getChildContainerTypes().add(containerType);
            containerType.getParentContainerTypes().add(parentCt);

            session.update(parentCt);
            session.flush();

            Integer numChildren = parentContainer.getChildPositions().size();
            Integer row = numChildren / parentCt.getRowCapacity();
            Integer col = numChildren % parentCt.getColCapacity();

            ContainerPosition cp = new ContainerPosition();
            cp.setRow(row);
            cp.setCol(col);

            cp.setContainer(container);
            container.setPosition(cp);

            cp.setParentContainer(parentContainer);
            parentContainer.getChildPositions().add(cp);
        }

        setDefaultContainer(container);
        session.save(container);
        session.flush();
        return container;
    }

    public Container createTopContainer() {
        setDefaultParentContainer(null);
        Container topContainer = createContainer();
        topContainer.setContainerType(getDefaultTopContainerType());

        setDefaultTopContainer(topContainer);
        setDefaultParentContainer(topContainer);
        session.update(topContainer);
        session.flush();
        return topContainer;
    }

    public Container createParentContainer() {
        Container parentContainer = createContainer();
        setDefaultParentContainer(parentContainer);
        session.update(parentContainer);
        session.flush();
        return parentContainer;
    }

    public SpecimenType createSpecimenType() {
        String name = nameGenerator.next(SpecimenType.class);

        SpecimenType specimenType = new SpecimenType();
        specimenType.setName(name);
        specimenType.setNameShort(name);

        setDefaultSpecimenType(specimenType);
        session.save(specimenType);
        session.flush();
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
        session.save(specimen);
        session.flush();
        return specimen;
    }

    public Specimen createPositionedSpecimen() {
        Specimen assignedSpecimen = createSpecimen();

        Container parentContainer = getDefaultContainer();
        ContainerType parentCt = parentContainer.getContainerType();

        parentCt.getSpecimenTypes().add(assignedSpecimen.getSpecimenType());

        session.update(parentCt);
        session.flush();

        Integer numSpecimens = parentContainer.getSpecimenPositions().size();
        Integer row = numSpecimens / parentCt.getRowCapacity();
        Integer col = numSpecimens % parentCt.getColCapacity();

        SpecimenPosition sp = new SpecimenPosition();
        sp.setRow(row);
        sp.setCol(col);
        sp.setPositionString("asdf"); // TODO: set this right

        sp.setSpecimen(assignedSpecimen);
        assignedSpecimen.setSpecimenPosition(sp);

        sp.setContainer(parentContainer);
        parentContainer.getSpecimenPositions().add(sp);

        session.update(assignedSpecimen);
        session.flush();
        return assignedSpecimen;
    }

    public Study createStudy() {
        String name = nameGenerator.next(Study.class);

        Study study = new Study();
        study.setName(name);
        study.setNameShort(name);

        setDefaultStudy(study);
        session.save(study);
        session.flush();
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
        session.save(collectionEvent);
        session.update(patient);
        session.flush();
        return collectionEvent;
    }

    public Patient createPatient() {
        String name = nameGenerator.next(Patient.class);

        Patient patient = new Patient();
        patient.setPnumber(name);
        patient.setStudy(getDefaultStudy());
        patient.setCreatedAt(new Date());

        setDefaultPatient(patient);
        session.save(patient);
        session.flush();
        return patient;
    }

    public OriginInfo createOriginInfo() {
        OriginInfo originInfo = new OriginInfo();
        originInfo.setCenter(getDefaultSite());

        // TODO: what about ShippingInfo?

        setDefaultOriginInfo(originInfo);
        session.save(originInfo);
        session.flush();
        return originInfo;
    }

    public User createUser() {
        String name = nameGenerator.next(User.class);

        User user = new User();
        user.setLogin(name);
        user.setEmail(name);
        user.setFullName("joe testonson");

        // cheap fix to avoid actually having to create a CSM user
        user.setCsmUserId(-Math.abs(R.nextLong()));

        // must contain at least one Membership
        Membership m = new Membership();
        m.setCenter(getDefaultCenter());
        m.setStudy(getDefaultStudy());
        m.setPrincipal(user);
        user.getMemberships().add(m);

        setDefaultUser(user);
        setDefaultPrincipal(user);
        session.save(user);
        session.flush();
        return user;
    }

    public User createManager() {
        User userManager = createUser();

        Membership m = userManager.getMemberships().iterator().next();
        m.setRank(Rank.MANAGER);

        // they need something to manage
        m.getPermissions().add(PermissionEnum.CLINIC_READ);

        session.update(userManager);
        session.flush();
        return userManager;
    }

    public User createAdmin() {
        User admin = createUser();

        Membership m = admin.getMemberships().iterator().next();
        m.setRank(Rank.ADMINISTRATOR);

        session.update(admin);
        session.flush();
        return admin;
    }

    public Group createGroup() {
        String name = nameGenerator.next(Group.class);

        Group group = new Group();
        group.setName(name);
        group.setDescription(name);

        // must contain at least one Membership
        Membership m = new Membership();
        m.setCenter(getDefaultCenter());
        m.setStudy(getDefaultStudy());
        m.setPrincipal(group);
        group.getMemberships().add(m);

        setDefaultGroup(group);
        setDefaultPrincipal(group);
        session.save(group);
        session.flush();
        return group;
    }

    public Membership createMembership() {
        Membership membership = new Membership();
        membership.setCenter(getDefaultCenter());
        membership.setStudy(getDefaultStudy());

        Principal p = getDefaultPrincipal();
        p.getMemberships().add(membership);
        membership.setPrincipal(p);

        setDefaultMembership(membership);
        session.save(membership);
        session.flush();
        return membership;
    }

    public Role createRole() {
        String name = nameGenerator.next(Role.class);

        Role role = new Role();

        role.setName(name);

        setDefaultRole(role);
        session.save(role);
        session.flush();
        return role;
    }

    public ContainerLabelingSchemeGetter getScheme() {
        return schemeGetter;
    }

    public class ContainerLabelingSchemeGetter {
        public ContainerLabelingScheme getSbs() {
            return (ContainerLabelingScheme) session
                .createCriteria(ContainerLabelingScheme.class)
                .add(Restrictions.idEq(1))
                .uniqueResult();
        }

        public ContainerLabelingScheme get2CharAlphabetic() {
            return (ContainerLabelingScheme) session
                .createCriteria(ContainerLabelingScheme.class)
                .add(Restrictions.idEq(6))
                .uniqueResult();
        }
    }

    public String getName(Class<?> klazz) {
        return nameGenerator.next(klazz);
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
}
