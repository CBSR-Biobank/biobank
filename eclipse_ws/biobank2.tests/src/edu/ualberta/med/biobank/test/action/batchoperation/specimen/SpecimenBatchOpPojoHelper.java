package edu.ualberta.med.biobank.test.action.batchoperation.specimen;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpInputPojo;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.test.NameGenerator;
import edu.ualberta.med.biobank.test.Utils;

/**
 *
 * @author Nelson Loyola
 *
 */
@SuppressWarnings("nls")
class SpecimenBatchOpPojoHelper {
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

    public void fillContainersWithSpecimenBatchOpPojos(List<SpecimenBatchOpInputPojo> specimenCsvInfos,
                                                       Set<Container>                 containers,
                                                       boolean                        useProductBarcode) {

        // fill as many containers as space will allow
        int count = 0;
        for (Container container : containers) {
            ContainerType ctype = container.getContainerType();

            int maxRows =
                container.getContainerType().getCapacity().getRowCapacity();
            int maxCols =
                container.getContainerType().getCapacity().getColCapacity();

            for (int r = 0; r < maxRows; ++r) {
                for (int c = 0; c < maxCols; ++c) {
                    if (count >= specimenCsvInfos.size()) break;

                    SpecimenBatchOpInputPojo csvInfo =
                        specimenCsvInfos.get(count);
                    RowColPos pos = new RowColPos(r, c);
                    csvInfo.setPalletPosition(ctype.getPositionString(pos));

                    if (useProductBarcode) {
                       csvInfo.setPalletProductBarcode(container.getProductBarcode());
                    } else {
                       csvInfo.setPalletLabel(container.getLabel());
                       csvInfo.setRootContainerType(ctype.getNameShort());
                    }

                    count++;
                }
            }
        }
    }

    public void addComments(Set<SpecimenBatchOpInputPojo> specimenCsvInfos) {
        for (SpecimenBatchOpInputPojo specimenCsvInfo : specimenCsvInfos) {
            specimenCsvInfo.setComment(nameGenerator.next(String.class));
        }
    }
}
