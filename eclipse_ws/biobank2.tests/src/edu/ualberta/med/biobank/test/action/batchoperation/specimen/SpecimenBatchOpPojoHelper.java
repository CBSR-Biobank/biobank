package edu.ualberta.med.biobank.test.action.batchoperation.specimen;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.hibernate.Session;
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
class SpecimenBatchOpPojoHelper {

    private static Logger log = LoggerFactory.getLogger(SpecimenBatchOpPojoHelper.class);

    private final NameGenerator nameGenerator;

    private int lineNumber;

    SpecimenBatchOpPojoHelper(NameGenerator nameGenerator) {
        this.nameGenerator = nameGenerator;
        this.lineNumber = 0;
    }

    /**
     * Creates specimen BatchOp pojos with source specimens and aliquoted specimens.
     *
     * @param study the study the the patients belong to. Note that the study must have valid source
     *        specimens and aliquoted specimens defined.
     * @param originInfos the center information for where the specimens originated from and are
     *        currently at.
     * @param patients the patients that these specimens will belong to.
     */
    Set<SpecimenBatchOpInputPojo> createAllSpecimens(Study           study,
                                                     Set<OriginInfo> originInfos,
                                                     Set<Patient>    patients) {
        if (study.getSourceSpecimens().size() == 0) {
            throw new IllegalStateException(
                "study does not have any source specimens");
        }

        if (study.getAliquotedSpecimens().size() == 0) {
            throw new IllegalStateException(
                "study does not have any source specimens");
        }

        Set<SpecimenBatchOpInputPojo> specimenInfos = sourceSpecimensCreate(
            originInfos, patients, study.getSourceSpecimens());

        Map<String, String> parentSpecimenInfoMap = new HashMap<String, String>();
        for (SpecimenBatchOpInputPojo specimenInfo : specimenInfos) {
            parentSpecimenInfoMap.put(specimenInfo.getInventoryId(),
                specimenInfo.getPatientNumber());
        }

        specimenInfos.addAll(aliquotedSpecimensCreate(parentSpecimenInfoMap,
                                                      study.getAliquotedSpecimens()));

        return specimenInfos;
    }

    Set<SpecimenBatchOpInputPojo> sourceSpecimensCreate(
        Set<OriginInfo> originInfos,
        Set<Patient> patients, Set<SourceSpecimen> sourceSpecimens) {
        Set<SpecimenBatchOpInputPojo> specimenInfos =
            new HashSet<SpecimenBatchOpInputPojo>();

        // add parent specimens first
        for (SourceSpecimen ss : sourceSpecimens) {
            for (Patient p : patients) {
                for (OriginInfo originInfo : originInfos) {
                    // create ones with shipment info
                    SpecimenBatchOpInputPojo specimenInfo = sourceSpecimenCreate(
                        ss.getSpecimenType().getName(), p.getPnumber(),
                        originInfo.getShipmentInfo().getWaybill());
                    specimenInfos.add(specimenInfo);
                }

                // create ones without shipment info
                SpecimenBatchOpInputPojo specimenInfo = sourceSpecimenCreate(
                    ss.getSpecimenType().getName(), p.getPnumber(), null);
                specimenInfos.add(specimenInfo);
            }
        }

        return specimenInfos;
    }

    /**
     * Creates CSV specimens with only aliquoted specimens. Note that parent specimens must already
     * be present in the database.
     */
    Set<SpecimenBatchOpInputPojo> createAliquotedSpecimens(Study study,
                                                           Collection<Specimen> parentSpecimens) {
        if (study.getAliquotedSpecimens().size() == 0) {
            throw new IllegalStateException("study does not have any aliquoted specimens");
        }

        Map<String, String> parentSpecimenInfoMap = new HashMap<String, String>();
        for (Specimen parentSpecimen : parentSpecimens) {
            parentSpecimenInfoMap.put(parentSpecimen.getInventoryId(),
                parentSpecimen.getCollectionEvent().getPatient().getPnumber());
        }

        return aliquotedSpecimensCreate(parentSpecimenInfoMap, study.getAliquotedSpecimens());
    }

    /**
     * Creates aliquotedSpecimens.size() specimens for each parentSpecimen.
     *
     * specimenInfoMap is a map of: specimen inventory id => patient number
     */
    private Set<SpecimenBatchOpInputPojo> aliquotedSpecimensCreate(Map<String, String>    parentSpecimenInfoMap,
                                                                   Set<AliquotedSpecimen> aliquotedSpecimens) {
        Set<SpecimenBatchOpInputPojo> specimenInfos = new HashSet<SpecimenBatchOpInputPojo>();

        for (Entry<String, String> parentSpecimenInfo : parentSpecimenInfoMap.entrySet()) {
            for (AliquotedSpecimen as : aliquotedSpecimens) {
                SpecimenBatchOpInputPojo specimenInfo =
                    aliquotedSpecimenCreate(parentSpecimenInfo.getKey(),
                                            as.getSpecimenType().getName());
                specimenInfos.add(specimenInfo);
            }
        }

        return specimenInfos;
    }

    private SpecimenBatchOpInputPojo sourceSpecimenCreate(String specimenTypeName,
                                                          String patientNumber,
                                                          String waybill) {
        SpecimenBatchOpInputPojo specimenInfo = aliquotedSpecimenCreate(null, specimenTypeName);
        specimenInfo.setPatientNumber(patientNumber);
        specimenInfo.setVisitNumber(1);
        specimenInfo.setWaybill(waybill);
        specimenInfo.setWorksheet(nameGenerator.next(ProcessingEvent.class));
        specimenInfo.setSourceSpecimen(true);
        specimenInfo.setLineNumber(-1);
        return specimenInfo;
    }

    public Set<SpecimenBatchOpInputPojo>
    aliquotedSpecimensCreate(Set<Patient>           patients,
                             Set<AliquotedSpecimen> aliquotedSpecimens) {
        Set<SpecimenBatchOpInputPojo> specimenInfos = new HashSet<SpecimenBatchOpInputPojo>();

        for (Patient patient : patients) {
            for (CollectionEvent ce : patient.getCollectionEvents()) {
                for (AliquotedSpecimen as : aliquotedSpecimens) {
                    SpecimenBatchOpInputPojo specimenInfo =
                        aliquotedSpecimenCreate(null, as.getSpecimenType().getName());
                    specimenInfo.setPatientNumber(patient.getPnumber());
                    specimenInfo.setVisitNumber(ce.getVisitNumber());

                    specimenInfos.add(specimenInfo);
                }
            }
        }

        return specimenInfos;
    }

    public SpecimenBatchOpInputPojo aliquotedSpecimenCreate(String parentInventoryId,
                                                            String specimenTypeName) {
        ++lineNumber;

        SpecimenBatchOpInputPojo specimenInfo = new SpecimenBatchOpInputPojo();
        specimenInfo.setLineNumber(lineNumber);
        specimenInfo.setInventoryId(nameGenerator.next(Specimen.class));

        if (parentInventoryId != null) {
            specimenInfo.setParentInventoryId(parentInventoryId);
        }
        specimenInfo.setSpecimenType(specimenTypeName);
        specimenInfo.setCreatedAt(Utils.getRandomDate());
        specimenInfo.setLineNumber(-1);
        return specimenInfo;
    }

    public static <T extends IBatchOpSpecimenPositionPojo> void
    assignPositionsToPojos(Set<T>         pojos,
                           Set<Container> containers,
                           boolean        useProductBarcode) {
        // fill as many containers as space will allow
        Set<T> pojosToAssign = new HashSet<T>(pojos);
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
        factory.createContainerType();
        factory.createTopContainer();
        factory.createParentContainer();

        ContainerType ctype = factory.createContainerType();
        ctype.getChildContainerTypes().clear();
        ctype.getSpecimenTypes().clear();
        ctype.getSpecimenTypes().addAll(specimenTypes);
        session.save(ctype);

        Set<Container> result = new HashSet<Container>();
        for (int i = 0; i < numContainers; ++i) {
            Container container = factory.createContainer();
            container.setProductBarcode(nameGenerator.next(Container.class));
            result.add(container);
        }

        return result;
    }

    public static Set<Container> createContainers(Session session,
                                                  Factory factory,
                                                  NameGenerator nameGenerator,
                                                  SpecimenType specimenType,
                                                  int numContainers) {
        Set<SpecimenType> specimenTypes = new HashSet<SpecimenType>(0);
        specimenTypes.add(specimenType);
        return createContainers(session, factory, nameGenerator, specimenTypes, numContainers);
    }
}
