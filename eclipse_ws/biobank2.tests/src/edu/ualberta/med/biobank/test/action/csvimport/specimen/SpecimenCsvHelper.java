package edu.ualberta.med.biobank.test.action.csvimport.specimen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpInputRow;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
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
 * @author loyola
 * 
 */
@SuppressWarnings("nls")
class SpecimenCsvHelper {
    private final NameGenerator nameGenerator;

    SpecimenCsvHelper(NameGenerator nameGenerator) {
        this.nameGenerator = nameGenerator;
    }

    /**
     * Creates a CSV with source specimens and aliquoted specimens.
     * 
     * @param csvname the file name to save the data to.
     * @param study the study the the patients belong to. Note that the study
     *            must have valid source specimens and aliquoted specimens
     *            defined.
     * @param originCenter the center where the specimens came from.
     * @param currentCenter the center where the specimens are stored.
     * @param patients the patients that these specimens will belong to.
     * @throws IOException
     */
    ArrayList<SpecimenBatchOpInputRow> createAllSpecimens(Study study,
        Set<OriginInfo> originInfos, Set<Patient> patients) {
        if (study.getSourceSpecimens().size() == 0) {
            throw new IllegalStateException(
                "study does not have any source specimens");
        }

        if (study.getAliquotedSpecimens().size() == 0) {
            throw new IllegalStateException(
                "study does not have any source specimens");
        }

        ArrayList<SpecimenBatchOpInputRow> specimenInfos =
            sourceSpecimensCreate(
                originInfos, patients, study.getSourceSpecimens());

        Map<String, String> parentSpecimenInfoMap =
            new HashMap<String, String>();
        for (SpecimenBatchOpInputRow specimenInfo : specimenInfos) {
            parentSpecimenInfoMap.put(specimenInfo.getInventoryId(),
                specimenInfo.getPatientNumber());
        }

        specimenInfos.addAll(aliquotedSpecimensCreate(parentSpecimenInfoMap,
            study.getAliquotedSpecimens()));

        return specimenInfos;
    }

    ArrayList<SpecimenBatchOpInputRow> sourceSpecimensCreate(
        Set<OriginInfo> originInfos,
        Set<Patient> patients, Set<SourceSpecimen> sourceSpecimens) {
        ArrayList<SpecimenBatchOpInputRow> specimenInfos =
            new ArrayList<SpecimenBatchOpInputRow>();

        // add parent specimens first
        for (SourceSpecimen ss : sourceSpecimens) {
            for (Patient p : patients) {
                for (OriginInfo originInfo : originInfos) {
                    // create ones with shipment info
                    SpecimenBatchOpInputRow specimenInfo =
                        sourceSpecimenCreate(ss.getSpecimenType().getName(),
                            p.getPnumber(), originInfo.getShipmentInfo()
                                .getWaybill());
                    specimenInfos.add(specimenInfo);
                }

                // create ones without shipment info
                SpecimenBatchOpInputRow specimenInfo =
                    sourceSpecimenCreate(ss.getSpecimenType().getName(),
                        p.getPnumber(), null);
                specimenInfos.add(specimenInfo);
            }
        }

        return specimenInfos;
    }

    /**
     * Creates CSV specimens with only aliquoted specimens. Note that parent
     * specimens must already be present in the database.
     */
    ArrayList<SpecimenBatchOpInputRow> createAliquotedSpecimens(Study study,
        Set<Specimen> parentSpecimens) {
        if (study.getAliquotedSpecimens().size() == 0) {
            throw new IllegalStateException(
                "study does not have any source specimens");
        }

        Map<String, String> parentSpecimenInfoMap =
            new HashMap<String, String>();
        for (Specimen parentSpecimen : parentSpecimens) {
            parentSpecimenInfoMap.put(parentSpecimen.getInventoryId(),
                parentSpecimen.getCollectionEvent().getPatient().getPnumber());
        }

        return aliquotedSpecimensCreate(parentSpecimenInfoMap,
            study.getAliquotedSpecimens());
    }

    /**
     * Creates aliquotedSpecimens.size() specimens for each parentSpecimen.
     * 
     * specimenInfoMap is a map of: specimen inventory id => patient number
     */
    private ArrayList<SpecimenBatchOpInputRow> aliquotedSpecimensCreate(
        Map<String, String> parentSpecimenInfoMap,
        Set<AliquotedSpecimen> aliquotedSpecimens) {
        ArrayList<SpecimenBatchOpInputRow> specimenInfos =
            new ArrayList<SpecimenBatchOpInputRow>();

        for (Entry<String, String> parentSpecimenInfo : parentSpecimenInfoMap
            .entrySet()) {
            for (AliquotedSpecimen as : aliquotedSpecimens) {
                SpecimenBatchOpInputRow specimenInfo =
                    aliquotedSpecimenCreate(parentSpecimenInfo.getKey(),
                        as.getSpecimenType().getName(),
                        parentSpecimenInfo.getValue(), 1);
                specimenInfos.add(specimenInfo);
            }
        }

        return specimenInfos;
    }

    private SpecimenBatchOpInputRow sourceSpecimenCreate(
        String specimenTypeName, String patientNumber, String waybill) {
        SpecimenBatchOpInputRow specimenInfo = aliquotedSpecimenCreate(
            null, specimenTypeName, patientNumber, 1);
        specimenInfo.setWaybill(waybill);
        specimenInfo.setWorksheet(nameGenerator.next(ProcessingEvent.class));
        specimenInfo.setSourceSpecimen(true);
        return specimenInfo;
    }

    public SpecimenBatchOpInputRow aliquotedSpecimenCreate(
        String parentInventoryId, String specimenTypeName,
        String patientNumber,
        int visitNumber) {
        SpecimenBatchOpInputRow specimenInfo = new SpecimenBatchOpInputRow();
        specimenInfo.setInventoryId(nameGenerator.next(Specimen.class));
        specimenInfo.setParentInventoryId(parentInventoryId);
        specimenInfo.setSpecimenType(specimenTypeName);
        specimenInfo.setCreatedAt(Utils.getRandomDate());
        specimenInfo.setPatientNumber(patientNumber);
        specimenInfo.setVisitNumber(visitNumber);
        return specimenInfo;
    }

    public void fillContainersWithSpecimenFromCsv(
        List<SpecimenBatchOpInputRow> specimenCsvInfos,
        Set<Container> containers) {

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

                    SpecimenBatchOpInputRow csvInfo =
                        specimenCsvInfos.get(count);
                    RowColPos pos = new RowColPos(r, c);
                    csvInfo.setPalletPosition(ctype.getPositionString(pos));
                    csvInfo.setPalletLabel(container.getLabel());
                    csvInfo.setPalletProductBarcode(container
                        .getProductBarcode());
                    csvInfo.setRootContainerType(ctype.getNameShort());

                    count++;
                }
            }
        }
    }

    public void addComments(ArrayList<SpecimenBatchOpInputRow> specimenCsvInfos) {
        for (SpecimenBatchOpInputRow specimenCsvInfo : specimenCsvInfos) {
            specimenCsvInfo.setComment(nameGenerator.next(String.class));
        }
    }
}
