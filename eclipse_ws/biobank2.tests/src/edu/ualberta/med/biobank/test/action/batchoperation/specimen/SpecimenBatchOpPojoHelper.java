package edu.ualberta.med.biobank.test.action.batchoperation.specimen;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.common.action.batchoperation.specimen.IBatchOpSpecimenPositionPojo;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpInputPojo;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenPosition;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.type.LabelingLayout;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.test.Factory;
import edu.ualberta.med.biobank.test.NameGenerator;
import edu.ualberta.med.biobank.test.Utils;

/**
 *
 * @author Nelson Loyola
 *
 */
@SuppressWarnings("nls")
public class SpecimenBatchOpPojoHelper {

    private static Logger log = LoggerFactory.getLogger(SpecimenBatchOpPojoHelper.class);

    private final NameGenerator nameGenerator;

    private int lineNumber;

    private class PatientInfo {
        Patient patient;
        Set<SpecimenBatchOpInputPojo> pojos;

        PatientInfo(Patient patient) {
            this.patient = patient;
            pojos = new LinkedHashSet<SpecimenBatchOpInputPojo>(0);
        }
    }

    SpecimenBatchOpPojoHelper(NameGenerator nameGenerator) {
        this.nameGenerator = nameGenerator;
        this.lineNumber = 0;
    }

    /**
     * Creates specimen BatchOp pojos with source specimens and aliquoted specimens.
     *
     * @param session the hibernate session.
     * @param originInfos the center information for where the specimens originated from and
     *        where they currently are at.
     * @param patients the patients that these specimens will belong to.
     */
    Set<SpecimenBatchOpInputPojo> createAllSpecimens(Session session,
                                                     Set<OriginInfo> originInfos,
                                                     Set<Patient>    patients) {

        checkForSourceSpecimensInDb(session);
        checkForAliquotedSpecimensInDb(session);

        Set<SpecimenBatchOpInputPojo> pojos = new HashSet<SpecimenBatchOpInputPojo>(0);
        Set<PatientInfo> patientData = sourceSpecimensCreate(originInfos, patients);
        for (PatientInfo patientInfo : patientData) {
            pojos.addAll(patientInfo.pojos);
            pojos.addAll(aliquotedSpecimensCreate(patientInfo));
        }
        Assert.assertTrue(pojos.size() > 0);
        assignLineNumbers(pojos);
        return pojos;
    }

    /**
     * Creates specimen BatchOp pojos with only source specimens.
     *
     * @param session the hibernate session.
     * @param originInfos the center information for where the specimens originated from and
     *        where they currently are at.
     * @param patients the patients that these specimens will belong to.
     */
    Set<SpecimenBatchOpInputPojo> sourceSpecimensCreate(Session session,
                                                        Set<OriginInfo> originInfos,
                                                        Set<Patient>    patients) {

        checkForSourceSpecimensInDb(session);
        checkForAliquotedSpecimensInDb(session);

        Set<SpecimenBatchOpInputPojo> pojos = new HashSet<SpecimenBatchOpInputPojo>(0);
        for (Patient patient: patients) {
            Set<SourceSpecimen> sourceSpecimens = patient.getStudy().getSourceSpecimens();
            for (SourceSpecimen ss : sourceSpecimens) {
                for (OriginInfo originInfo : originInfos) {
                    for (CollectionEvent event : patient.getCollectionEvents()) {
                        SpecimenBatchOpInputPojo pojo =
                            sourceSpecimenCreate(patient,
                                                 event,
                                                 originInfo.getShipmentInfo().getWaybill(),
                                                 ss.getSpecimenType().getName());
                        pojos.add(pojo);
                    }
                }
            }
        }
        Assert.assertTrue(pojos.size() > 0);
        assignLineNumbers(pojos);
        return pojos;
    }

    /**
     * Creates specimen BatchOp pojos with only aliquoted specimens.
     *
     * <p>Note that parent specimens must already be present in the database.
     *
     * @param originInfos the center information for where the specimens originated from and
     *        where they currently are at.
     * @param patients the patients that these specimens will belong to.
     */
    Set<SpecimenBatchOpInputPojo> createAliquotedSpecimens(Set<Patient> patients) {
        Set<SpecimenBatchOpInputPojo> pojos = new LinkedHashSet<SpecimenBatchOpInputPojo>();
        for (Patient patient : patients) {
            Set<AliquotedSpecimen> aliquotedSpecimens =
                patient.getStudy().getAliquotedSpecimens();
            for (CollectionEvent event :patient.getCollectionEvents()) {
                for (Specimen parentSpecimen : event.getOriginalSpecimens()) {
                    for (AliquotedSpecimen as : aliquotedSpecimens) {
                        SpecimenBatchOpInputPojo pojo =
                            aliquotedSpecimenCreate(patient,
                                                    event,
                                                    as.getSpecimenType().getName());
                        pojo.setParentInventoryId(parentSpecimen.getInventoryId());
                        pojos.add(pojo);
                    }
                }
            }
        }
        Assert.assertTrue(pojos.size() > 0);
        assignLineNumbers(pojos);
        return pojos;
    }

    /**
     * Creates specimen BatchOp pojos with only source specimens.
     *
     * @param originInfos the center information for where the specimens originated from and
     *        where they currently are at.
     * @param patients the patients that these specimens will belong to.
     */
    private Set<PatientInfo> sourceSpecimensCreate(Set<OriginInfo> originInfos,
                                                   Set<Patient> patients) {
        Set<PatientInfo> patientData = new HashSet<PatientInfo>(0);
        for (Patient patient : patients) {
            PatientInfo patientInfo = new PatientInfo(patient);
            patientData.add(patientInfo);
            for (SourceSpecimen ss : patient.getStudy().getSourceSpecimens()) {
                for (CollectionEvent event : patient.getCollectionEvents()) {
                    for (OriginInfo originInfo : originInfos) {
                        SpecimenBatchOpInputPojo pojo =
                            sourceSpecimenCreate(patient,
                                                 event,
                                                 originInfo.getShipmentInfo().getWaybill(),
                                                 ss.getSpecimenType().getName());
                        patientInfo.pojos.add(pojo);
                    }

                    // create one without shipment info
                    SpecimenBatchOpInputPojo pojo =
                        sourceSpecimenCreate(patient,
                                             event,
                                             null,
                                             ss.getSpecimenType().getName());
                    patientInfo.pojos.add(pojo);
                }
            }
        }
        return patientData;
    }

    public Set<SpecimenBatchOpInputPojo>
    aliquotedSpecimensCreate(Set<Patient>           patients,
                             Set<AliquotedSpecimen> aliquotedSpecimens) {
        Set<SpecimenBatchOpInputPojo> pojos = new LinkedHashSet<SpecimenBatchOpInputPojo>();

        for (Patient patient : patients) {
            for (CollectionEvent ce : patient.getCollectionEvents()) {
                for (AliquotedSpecimen as : aliquotedSpecimens) {
                    SpecimenBatchOpInputPojo pojo =
                        genericSpecimenCreate(null, as.getSpecimenType().getName());
                    pojo.setPatientNumber(patient.getPnumber());
                    pojo.setVisitNumber(ce.getVisitNumber());
                    pojos.add(pojo);
                    log.trace("child specimen patient added: inventoryId: {}, patient: {}, visit: {}",
                             new Object[] {
                                           pojo.getInventoryId(),
                                           patient.getPnumber(),
                                           ce.getVisitNumber()
                    });
                }
            }
        }

        return pojos;
    }

    public SpecimenBatchOpInputPojo genericSpecimenCreate(String parentInventoryId,
                                                            String specimenTypeName) {
        SpecimenBatchOpInputPojo pojo = new SpecimenBatchOpInputPojo();
        pojo.setInventoryId(nameGenerator.next(Specimen.class));

        if (parentInventoryId != null) {
            pojo.setParentInventoryId(parentInventoryId);
        }
        pojo.setSpecimenType(specimenTypeName);
        pojo.setCreatedAt(Utils.getRandomDate());
        log.trace("generic specimen created: inventoryId: {}, parentSpecimen: {}, specimenType: {}",
                 new Object[] {
                               pojo.getInventoryId(),
                               parentInventoryId,
                               specimenTypeName
        });
        return pojo;
    }

    public SpecimenBatchOpInputPojo aliquotedSpecimenCreate(Patient patient,
                                                            CollectionEvent event,
                                                            String specimenTypeName) {
        ++lineNumber;

        SpecimenBatchOpInputPojo pojo = new SpecimenBatchOpInputPojo();
        pojo.setLineNumber(lineNumber);
        pojo.setInventoryId(nameGenerator.next(Specimen.class));

        pojo.setSpecimenType(specimenTypeName);
        pojo.setCreatedAt(Utils.getRandomDate());
        pojo.setLineNumber(-1);
        log.trace("child specimen created: patient: {}, visitNumber: {}, specimenType: {}",
                 new Object[] {
                 patient.getPnumber(),
                 event.getVisitNumber(),
                 specimenTypeName
        });
        return pojo;
    }

    public static <T extends IBatchOpSpecimenPositionPojo> void
    assignPositionsToPojos(Set<T>         pojos,
                           Set<Container> containers,
                           boolean        useProductBarcode) {
        // fill as many containers as space will allow
        Set<T> pojosToAssign = new LinkedHashSet<T>(pojos);
        Iterator<T> iterator = pojosToAssign.iterator();

        for (Container container : containers) {
            ContainerType ctype = container.getContainerType();

            int maxRows = container.getContainerType().getCapacity().getRowCapacity();
            int maxCols = container.getContainerType().getCapacity().getColCapacity();

            for (int r = 0; r < maxRows; ++r) {
                for (int c = 0; c < maxCols; ++c) {
                    if (pojosToAssign.isEmpty()) break;

                    T csvInfo = iterator.next();
                    iterator.remove();
                    RowColPos pos = new RowColPos(r, c);
                    csvInfo.setPalletPosition(ctype.getPositionString(pos));

                    if (useProductBarcode) {
                        csvInfo.setPalletProductBarcode(container.getProductBarcode());
                    } else {
                        csvInfo.setPalletLabel(container.getLabel());
                        csvInfo.setRootContainerType(ctype.getNameShort());
                    }
                }
            }
        }

        if (!pojosToAssign.isEmpty()) {
            throw new IllegalStateException("not enough containers to hold all pojos");
        }
    }

    /*
     * Creates aliquotedSpecimens.size() specimens for each parentSpecimen.
     *
     * pojoMap is a map of: specimen inventory id => patient number
     */
    private Set<SpecimenBatchOpInputPojo> aliquotedSpecimensCreate(PatientInfo patientInfo) {
        Set<SpecimenBatchOpInputPojo> pojos = new LinkedHashSet<SpecimenBatchOpInputPojo>();

        for (SpecimenBatchOpInputPojo parentPojo: patientInfo.pojos) {
            Set<AliquotedSpecimen> aliquotedSpecimens =
                patientInfo.patient.getStudy().getAliquotedSpecimens();
            for (AliquotedSpecimen as : aliquotedSpecimens) {
                SpecimenBatchOpInputPojo pojo =
                    genericSpecimenCreate(parentPojo.getInventoryId(),
                                          as.getSpecimenType().getName());
                pojos.add(pojo);
            }
        }

        return pojos;
    }

    private SpecimenBatchOpInputPojo sourceSpecimenCreate(Patient patient,
                                                          CollectionEvent event,
                                                          String waybill,
                                                          String specimenTypeName) {
        SpecimenBatchOpInputPojo pojo = genericSpecimenCreate(null, specimenTypeName);
        pojo.setPatientNumber(patient.getPnumber());
        pojo.setVisitNumber(event.getVisitNumber());
        pojo.setWaybill(waybill);
        pojo.setWorksheet(nameGenerator.next(ProcessingEvent.class));
        pojo.setSourceSpecimen(true);
        pojo.setLineNumber(-1);
        log.trace("source specimen created: patient: {}, visit: {}, specimenType: {}",
                 new Object[] {
                               patient.getPnumber(),
                               event.getVisitNumber(),
                               specimenTypeName
                 });
        return pojo;
    }

    private void checkForSourceSpecimensInDb(Session session) {
        @SuppressWarnings("unchecked")
        List<SourceSpecimen> sourceSpecimens = session.createCriteria(SourceSpecimen.class)
            .list();
        if (sourceSpecimens.size() == 0) {
            throw new IllegalStateException("database does not have any source specimens");
        }
    }

    private void checkForAliquotedSpecimensInDb(Session session) {
        @SuppressWarnings("unchecked")
        List<Integer> aliquotedSpecimens = session.createCriteria(AliquotedSpecimen.class)
            .list();
        if (aliquotedSpecimens.size() == 0) {
            throw new IllegalStateException("database does not have any aliquoted specimens");
        }
    }

    private void assignLineNumbers(Set<SpecimenBatchOpInputPojo> pojos) {
        int count = 0;
        for (SpecimenBatchOpInputPojo pojo : pojos) {
            pojo.setLineNumber(count);
            count++;
        }

    }

    public static Set<SpecimenType> getStudySourceAndAliquotSpecimentTypes(Study study) {
        Set<SpecimenType> specimenTypes = new HashSet<SpecimenType>();
        for (SourceSpecimen source : study.getSourceSpecimens()) {
            specimenTypes.add(source.getSpecimenType());
        }
        for (AliquotedSpecimen aq : study.getAliquotedSpecimens()) {
            specimenTypes.add(aq.getSpecimenType());
        }
        return specimenTypes;
    }

    public static <T extends IBatchOpSpecimenPositionPojo>
    void addComments(Set<T> pojos, NameGenerator nameGenerator) {
        for (T pojo : pojos) {
            pojo.setComment(nameGenerator.next(String.class));
        }
    }

    public static void fillContainerWithSecimens(Session session,
                                                 Factory factory,
                                                 Container container,
                                                 Patient patient) {
        session.beginTransaction();
        factory.createSourceSpecimen();
        factory.setDefaultPatient(patient);

        ContainerType type = container.getContainerType();
        Capacity capacity = type.getCapacity();
        Integer rowCapacity = capacity.getRowCapacity();
        Integer colCapacity = capacity.getColCapacity();
        int labelingSchemeId = type.getChildLabelingScheme().getId();
        LabelingLayout layout = type.getLabelingLayout();

        for (int r = 0; r < rowCapacity; ++r) {
            for (int c = 0; c < colCapacity; ++c) {
                RowColPos rcp = new RowColPos(r, c);
                String positionString = ContainerLabelingScheme.getPositionString(rcp,
                                                                                  labelingSchemeId,
                                                                                  rowCapacity,
                                                                                  colCapacity,
                                                                                  layout);
                Specimen specimenWithPosition = factory.createParentSpecimen();
                log.trace("placing specimen at: {}", positionString);

                SpecimenPosition pos = new SpecimenPosition();
                pos.setSpecimen(specimenWithPosition);
                pos.setRow(r);
                pos.setCol(c);
                pos.setContainer(container);
                pos.setPositionString(positionString);
                session.save(pos);
                session.flush();
            }
        }
        session.getTransaction().commit();
    }

    // adds allowed specimen types to the leaf containers' container types
    public static Set<Container> createContainers(Session session,
                                                  Factory factory,
                                                  NameGenerator nameGenerator,
                                                  Set<SpecimenType> specimenTypes,
                                                  int numContainers) {
        session.beginTransaction();
        factory.createContainerType();
        factory.createTopContainer();
        factory.createParentContainer();

        ContainerType ctype = factory.createContainerType();
        ctype.getChildContainerTypes().clear();
        ctype.getSpecimenTypes().clear();
        ctype.getSpecimenTypes().addAll(specimenTypes);
        session.save(ctype);

        Set<Container> result = new LinkedHashSet<Container>();
        for (int i = 0; i < numContainers; ++i) {
            Container container = factory.createContainer();
            container.setProductBarcode(nameGenerator.next(Container.class));
            result.add(container);
        }
        session.getTransaction().commit();

        return result;
    }

    public static Set<Container> createContainers(Session session,
                                                  Factory factory,
                                                  NameGenerator nameGenerator,
                                                  SpecimenType specimenType,
                                                  int numContainers) {
        Set<SpecimenType> specimenTypes = new LinkedHashSet<SpecimenType>(0);
        specimenTypes.add(specimenType);
        return createContainers(session, factory, nameGenerator, specimenTypes, numContainers);
    }
}
