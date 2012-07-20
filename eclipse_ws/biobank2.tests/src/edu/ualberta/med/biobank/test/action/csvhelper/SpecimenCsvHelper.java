package edu.ualberta.med.biobank.test.action.csvhelper;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.csvimport.SpecimenCsvInfo;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.test.NameGenerator;
import edu.ualberta.med.biobank.test.Utils;

@SuppressWarnings("nls")
public class SpecimenCsvHelper {
    private static final NameGenerator nameGenerator = new NameGenerator(
        SpecimenCsvHelper.class.getSimpleName() + new Random());

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
    public static Set<SpecimenCsvInfo> createAllSpecimens(Study study,
        Center originCenter, Center currentCenter, Set<Patient> patients) {
        if (study.getSourceSpecimens().size() == 0) {
            throw new IllegalStateException(
                "study does not have any source specimens");
        }

        if (study.getAliquotedSpecimens().size() == 0) {
            throw new IllegalStateException(
                "study does not have any source specimens");
        }

        Set<SpecimenCsvInfo> specimenInfos = sourceSpecimensCreate(
            originCenter, currentCenter, patients, study.getSourceSpecimens());

        Map<String, String> parentSpecimenInfoMap =
            new HashMap<String, String>();
        for (SpecimenCsvInfo specimenInfo : specimenInfos) {
            parentSpecimenInfoMap.put(specimenInfo.getInventoryId(),
                specimenInfo.getPatientNumber());
        }

        specimenInfos.addAll(aliquotedSpecimensCreate(originCenter,
            currentCenter, parentSpecimenInfoMap,
            study.getAliquotedSpecimens()));

        return specimenInfos;
    }

    public static Set<SpecimenCsvInfo> sourceSpecimensCreate(
        Center originCenter,
        Center currentCenter, Set<Patient> patients,
        Set<SourceSpecimen> sourceSpecimens) {
        Set<SpecimenCsvInfo> specimenInfos =
            new LinkedHashSet<SpecimenCsvInfo>();

        // add parent specimens first
        for (SourceSpecimen ss : sourceSpecimens) {
            for (Patient p : patients) {
                SpecimenCsvInfo specimenInfo = new SpecimenCsvInfo();
                specimenInfo.setInventoryId(nameGenerator.next(String.class));
                specimenInfo.setSpecimenType(ss.getSpecimenType().getName());
                specimenInfo.setCreatedAt(Utils.getRandomDate());
                specimenInfo.setPatientNumber(p.getPnumber());
                specimenInfo.setVisitNumber(1);
                specimenInfo.setCurrentCenter(currentCenter.getNameShort());
                specimenInfo.setOriginCenter(originCenter.getNameShort());
                specimenInfo.setWorksheet(nameGenerator.next(String.class));
                specimenInfo.setSourceSpecimen(true);
                specimenInfos.add(specimenInfo);
            }
        }

        return specimenInfos;
    }

    /**
     * Creates CSV specimens with only aliquoted specimens. Note that parent
     * specimens must already be present in the database.
     */
    public static Set<SpecimenCsvInfo> createAliquotedSpecimens(Study study,
        Center originCenter, Center currentCenter, Set<Specimen> parentSpecimens) {
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

        return aliquotedSpecimensCreate(originCenter, currentCenter,
            parentSpecimenInfoMap, study.getAliquotedSpecimens());
    }

    /**
     * Creates aliquotedSpecimens.size() specimens for each parentSpecimen.
     * 
     * specimenInfoMap is a map of: specimen inventory id => patient number
     */
    private static Set<SpecimenCsvInfo> aliquotedSpecimensCreate(
        Center originCenter,
        Center currentCenter, Map<String, String> parentSpecimenInfoMap,
        Set<AliquotedSpecimen> aliquotedSpecimens) {
        Set<SpecimenCsvInfo> specimenInfos =
            new LinkedHashSet<SpecimenCsvInfo>();

        for (Entry<String, String> parentSpecimenInfo : parentSpecimenInfoMap
            .entrySet()) {
            for (AliquotedSpecimen as : aliquotedSpecimens) {
                SpecimenCsvInfo specimenInfo = new SpecimenCsvInfo();
                specimenInfo.setInventoryId(nameGenerator
                    .next(String.class));
                specimenInfo.setParentInventoryId(parentSpecimenInfo.getKey());
                specimenInfo
                    .setSpecimenType(as.getSpecimenType().getName());
                specimenInfo.setCreatedAt(Utils.getRandomDate());
                specimenInfo
                    .setPatientNumber(parentSpecimenInfo.getValue());
                specimenInfo.setVisitNumber(1);
                specimenInfo.setCurrentCenter(currentCenter.getNameShort());
                specimenInfo.setOriginCenter(originCenter.getNameShort());
                specimenInfos.add(specimenInfo);
            }
        }

        return specimenInfos;
    }

}
