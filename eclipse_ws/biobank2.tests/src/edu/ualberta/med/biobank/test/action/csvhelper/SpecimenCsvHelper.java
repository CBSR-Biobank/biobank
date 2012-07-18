package edu.ualberta.med.biobank.test.action.csvhelper;

import java.io.IOException;
import java.util.LinkedHashSet;
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

    @SuppressWarnings("nls")
    public void specimensCreateCsv(String csvname, Study study,
        Center originCenter,
        Center currentCenter, Set<Patient> patients,
        Set<SourceSpecimen> sourceSpecimens,
        Set<AliquotedSpecimen> aliquotedSpecimens)
        throws IOException {

        Set<SpecimenCsvInfo> specimenInfos =
            new LinkedHashSet<SpecimenCsvInfo>();
        Set<SpecimenCsvInfo> parentSpecimenInfos =
            new LinkedHashSet<SpecimenCsvInfo>();

        // add parent specimens first
        for (SourceSpecimen ss : sourceSpecimens) {
            for (Patient p : patients) {
                SpecimenCsvInfo specimenInfo = new SpecimenCsvInfo();
                specimenInfo.setInventoryId(nameGenerator.next(String.class));
                specimenInfo.setSpecimenType(ss.getSpecimenType().getName());
                specimenInfo.setCreatedAt(Utils.getRandomDate());
                specimenInfo.setStudyName(study.getNameShort());
                specimenInfo.setPatientNumber(p.getPnumber());
                specimenInfo.setVisitNumber(1);
                specimenInfo.setCurrentCenter(currentCenter.getNameShort());
                specimenInfo.setOriginCenter(originCenter.getNameShort());
                specimenInfo.setWorksheet(nameGenerator.next(String.class));
                specimenInfo.setSourceSpecimen(true);
                specimenInfos.add(specimenInfo);
                parentSpecimenInfos.add(specimenInfo);
            }
        }

        specimenInfos.addAll(aliquotedSpecimensCreate(study, originCenter,
            currentCenter, parentSpecimenInfos, aliquotedSpecimens));

        SpecimenCsvWriter.write(csvname, specimenInfos);
    }

    public Set<SpecimenCsvInfo> aliquotedSpecimensCreate(Study study,
        Center originCenter, Center currentCenter,
        Set<SpecimenCsvInfo> parentSpecimenInfos,
        Set<AliquotedSpecimen> aliquotedSpecimens) {
        Set<SpecimenCsvInfo> specimenInfos =
            new LinkedHashSet<SpecimenCsvInfo>();

        // add aliquoted specimens
        for (SpecimenCsvInfo parentSpecimenInfo : parentSpecimenInfos) {
            for (AliquotedSpecimen as : aliquotedSpecimens) {
                SpecimenCsvInfo specimenInfo = new SpecimenCsvInfo();
                specimenInfo.setInventoryId(nameGenerator
                    .next(String.class));
                specimenInfo.setParentInventoryID(parentSpecimenInfo
                    .getInventoryId());
                specimenInfo
                    .setSpecimenType(as.getSpecimenType().getName());
                specimenInfo.setCreatedAt(Utils.getRandomDate());
                specimenInfo.setStudyName(study.getNameShort());
                specimenInfo
                    .setPatientNumber(parentSpecimenInfo.getPatientNumber());
                specimenInfo.setVisitNumber(1);
                specimenInfo.setCurrentCenter(currentCenter.getNameShort());
                specimenInfo.setOriginCenter(originCenter.getNameShort());
                specimenInfos.add(specimenInfo);
            }
        }

        return specimenInfos;
    }

}
