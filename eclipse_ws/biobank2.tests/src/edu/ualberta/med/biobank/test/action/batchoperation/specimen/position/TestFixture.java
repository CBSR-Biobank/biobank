package edu.ualberta.med.biobank.test.action.batchoperation.specimen.position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenPosition;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.test.Factory;
import edu.ualberta.med.biobank.test.NameGenerator;

public class TestFixture {

    private static Logger log = LoggerFactory.getLogger(TestFixture.class);

    private final Session session;

    private final Factory factory;

    private final NameGenerator nameGenerator;

    private final Set<Patient> patients;

    private final Set<SpecimenType> parentSpecimenTypes;

    private final Set<SpecimenType> childSpecimenTypes;

    private final Set<SpecimenType> allSpecimenTypes;

    private final Set<Specimen> specimens;

    private List<SourceSpecimen> sourceSpecimens;

    private List<AliquotedSpecimen> aliquotedSpecimens;

    TestFixture(Session         session,
                Factory         factory,
                NameGenerator   nameGenerator,
                int             numPatients) {
        this.session = session;
        this.factory = factory;
        this.nameGenerator = nameGenerator;

        patients = new HashSet<Patient>();
        parentSpecimenTypes = new HashSet<SpecimenType>(0);
        childSpecimenTypes = new HashSet<SpecimenType>(0);
        specimens = new HashSet<Specimen>(0);

        session.beginTransaction();
        configureStudy();
        createSpecimens(numPatients);
        allSpecimenTypes = new HashSet<SpecimenType>(parentSpecimenTypes);
        allSpecimenTypes.addAll(childSpecimenTypes);
        session.getTransaction().commit();
    }

    Set<Patient> getPatients() {
        return patients;
    }

    Set<SpecimenType> getParentSpecimenTypes() {
        return parentSpecimenTypes;
    }

    Set<SpecimenType> getChildSpecimenTypes() {
        return childSpecimenTypes;
    }

    public Set<SpecimenType> getAllSpecimenTypes() {
        return allSpecimenTypes;
    }

    Set<Specimen> getSpecimens() {
        return specimens;
    }

    boolean valid() {
        return !parentSpecimenTypes.isEmpty()
            && !childSpecimenTypes.isEmpty()
            && !specimens.isEmpty();
    }

    public Set<Container> createContainers(Set<SpecimenType> specimenTypes) {
        session.beginTransaction();
        factory.createContainerType();
        factory.createTopContainer();
        factory.createParentContainer();

        for (SpecimenType specimenType : specimenTypes) {
            log.trace("TestFixture: added specimen type to child container: " + specimenType.getName());
        }

        ContainerType ctype = factory.createContainerType();
        ctype.getChildContainerTypes().clear();
        ctype.getSpecimenTypes().clear();
        ctype.getSpecimenTypes().addAll(specimenTypes);
        session.save(ctype);

        Set<Container> result = new HashSet<Container>();
        for (int i = 0; i < 3; ++i) {
            Container container = factory.createContainer();
            container.setProductBarcode(nameGenerator.next(Container.class));
            result.add(container);
        }
        session.getTransaction().commit();
        return result;
    }

    void fillContainers(Set<Container> containers, Set<Specimen> specimens) {
        Iterator<Specimen> iterator = specimens.iterator();

        session.beginTransaction();
        for (Container container : containers) {
            ContainerType ctype = container.getContainerType();

            int maxRows = container.getContainerType().getCapacity().getRowCapacity();
            int maxCols = container.getContainerType().getCapacity().getColCapacity();

            for (int r = 0; r < maxRows; ++r) {
                for (int c = 0; c < maxCols; ++c) {
                    if (specimens.isEmpty()) break;

                    Specimen specimen = iterator.next();
                    iterator.remove();
                    RowColPos pos = new RowColPos(r, c);

                    SpecimenPosition specimenPosition = new SpecimenPosition();
                    specimenPosition.setSpecimen(specimen);
                    specimenPosition.setRow(pos.getRow());
                    specimenPosition.setCol(pos.getCol());
                    specimenPosition.setContainer(container);

                    String positionString =
                        ContainerLabelingScheme.getPositionString(pos,
                                                                  ctype.getChildLabelingScheme().getId(),
                                                                  ctype.getCapacity().getRowCapacity(),
                                                                  ctype.getCapacity().getColCapacity(),
                                                                  ctype.getLabelingLayout());

                    specimenPosition.setPositionString(positionString);

                    specimen.setSpecimenPosition(specimenPosition);
                    log.trace("TestFixture: added specimen to container: inventoryId: {}, container: {} ",
                              specimen.getInventoryId(), container.getLabel());
                    session.update(specimen);
                }
            }
        }
        session.getTransaction().commit();
    }

    private void configureStudy() {
        sourceSpecimens = new ArrayList<SourceSpecimen>();
        aliquotedSpecimens = new ArrayList<AliquotedSpecimen>();

        for (int i = 0; i < 3; ++i) {
            SpecimenType parentSpecimenType = factory.createSpecimenType();
            parentSpecimenTypes.add(parentSpecimenType);

            factory.setDefaultSourceSpecimenType(parentSpecimenType);
            sourceSpecimens.add(factory.createSourceSpecimen());
        }

        for (SourceSpecimen sourceSpecimen : sourceSpecimens) {
            SpecimenType parentSpecimenType = sourceSpecimen.getSpecimenType();
            factory.setDefaultSourceSpecimen(sourceSpecimen);

            SpecimenType childSpecimenType = factory.createSpecimenType();
            childSpecimenTypes.add(childSpecimenType);

            log.trace("TestFixture: added specimen type: " + childSpecimenType.getName());

            factory.setDefaultAliquotedSpecimenType(childSpecimenType);
            addChildSpecimenType(parentSpecimenType, childSpecimenType);
            aliquotedSpecimens.add(factory.createAliquotedSpecimen());
        }
    }

    private void createSpecimens(int numPatients) {
        // create specimens for each patient now
        for (int p = 0; p < numPatients; p++) {
            Patient patient = factory.createPatient();
            patients.add(patient);
            factory.createCollectionEvent();

            for (SourceSpecimen sourceSpecimen : sourceSpecimens) {
                SpecimenType parentSpecimenType = sourceSpecimen.getSpecimenType();
                factory.setDefaultSourceSpecimenType(parentSpecimenType);
                specimens.add(factory.createParentSpecimen());

                for (AliquotedSpecimen aliquotedSpecimen : aliquotedSpecimens) {
                    SpecimenType childSpecimenType = aliquotedSpecimen.getSpecimenType();
                    factory.setDefaultAliquotedSpecimenType(childSpecimenType);
                    addChildSpecimenType(parentSpecimenType, childSpecimenType);
                    Specimen specimen = factory.createChildSpecimen();
                    specimens.add(specimen);

                    log.trace("TestFixture: added specimen: inventoryId: " + specimen.getInventoryId()
                              + ", specimen type: " + specimen.getSpecimenType().getName());
                }
            }
        }

    }

    private void addChildSpecimenType(SpecimenType parent, SpecimenType child) {
        Set<SpecimenType> parentSpecimenTypes = new HashSet<SpecimenType>(Arrays.asList(parent));
        Set<SpecimenType> childSpecimenTypes = new HashSet<SpecimenType>(Arrays.asList(child));
        parent.getChildSpecimenTypes().addAll(childSpecimenTypes);
        child.getParentSpecimenTypes().addAll(parentSpecimenTypes);
        session.save(parent);
        session.save(child);
        session.flush();
    }

}
