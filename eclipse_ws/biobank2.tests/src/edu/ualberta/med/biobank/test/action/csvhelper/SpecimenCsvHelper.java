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
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.test.NameGenerator;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.util.csv.SpecimenCsvWriter;

public class SpecimenCsvHelper {
    private final NameGenerator nameGenerator;

    public SpecimenCsvHelper() {
        this.nameGenerator =
            new NameGenerator(SpecimenCsvHelper.class.getSimpleName()
                + new Random());
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
    @SuppressWarnings("nls")
    public void createAllSpecimensCsv(String csvname, Study study,
        Center originCenter, Center currentCenter, Set<Patient> patients)
        throws IOException {

        Set<SpecimenCsvInfo> specimenInfos =
            sourceSpecimensCreate(originCenter, currentCenter, patients,
                study.getSourceSpecimens());

        // TODO throw illegal state exception if study does not have source
        // specimens or aliquoted specimens

        Map<String, String> parentSpecimenInfoMap =
            new HashMap<String, String>();
        for (SpecimenCsvInfo specimenInfo : specimenInfos) {
            parentSpecimenInfoMap.put(specimenInfo.getInventoryId(),
                specimenInfo.getPatientNumber());
        }

        specimenInfos
            .addAll(aliquotedSpecimensCreate(originCenter,
                currentCenter, parentSpecimenInfoMap,
                study.getAliquotedSpecimens()));

        SpecimenCsvWriter.write(csvname, specimenInfos);
    }

    public Set<SpecimenCsvInfo> sourceSpecimensCreate(Center originCenter,
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

    /*
     * specimenInfoMap is a map of: specimen inventory id => patient number
     */
    public Set<SpecimenCsvInfo> aliquotedSpecimensCreate(Center originCenter,
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
