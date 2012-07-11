package edu.ualberta.med.biobank.test.action;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.hibernate.Transaction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.common.action.csvimport.SpecimenCsvImportAction;
import edu.ualberta.med.biobank.common.action.csvimport.SpecimenCsvInfo;
import edu.ualberta.med.biobank.common.action.exception.CsvImportException;
import edu.ualberta.med.biobank.common.action.exception.CsvImportException.ImportError;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.test.NameGenerator;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.util.csv.SpecimenCsvWriter;

public class TestSpecimenCsvImport extends ActionTest {

    private static Logger log = LoggerFactory
        .getLogger(TestSpecimenCsvImport.class.getName());

    private final NameGenerator nameGenerator;

    public TestSpecimenCsvImport() {
        super();
        this.nameGenerator =
            new NameGenerator(TestSpecimenCsvImport.class.getSimpleName()
                + getR());
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void testNoErrors() {
        Transaction tx = session.beginTransaction();
        // the site name comes from the CSV file
        Center center = factory.createSite();
        Center clinic = factory.createClinic();
        Study study = factory.createStudy();

        Set<Patient> patients = new HashSet<>();
        patients.add(factory.createPatient());
        patients.add(factory.createPatient());
        patients.add(factory.createPatient());

        Set<SourceSpecimen> sourceSpecimens = new HashSet<>();
        sourceSpecimens.add(factory.createSourceSpecimen());
        sourceSpecimens.add(factory.createSourceSpecimen());
        sourceSpecimens.add(factory.createSourceSpecimen());

        Set<AliquotedSpecimen> aliquotedSpecimens = new HashSet<>();
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());

        tx.commit();

        try {
            specimensCreateAndImportCsv(study, clinic, center, patients,
                sourceSpecimens, aliquotedSpecimens);
        } catch (CsvImportException e) {
            Assert.fail("errors in CVS data");
            showErrorsInLog(e);
        } catch (Exception e) {
            Assert.fail("could not import data");
        }
    }

    private void showErrorsInLog(CsvImportException e) {
        for (ImportError ie : e.getErrors()) {
            log.error("ERROR: line no {}: {}", ie.getLineNumber(),
                ie.getMessage());
        }

    }

    @SuppressWarnings("nls")
    private void specimensCreateAndImportCsv(Study study, Center originCenter,
        Center currentCenter, Set<Patient> patients,
        Set<SourceSpecimen> sourceSpecimens,
        Set<AliquotedSpecimen> aliquotedSpecimens)
        throws IOException {
        final String CSV_NAME = "import_specimens.csv";

        Set<SpecimenCsvInfo> specimenInfos = new LinkedHashSet<>();
        Map<SourceSpecimen, SpecimenCsvInfo> parentSpecimenInfos =
            new HashMap<>();

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
                specimenInfo.setSourceSpecimen(true);
                specimenInfos.add(specimenInfo);
                parentSpecimenInfos.put(ss, specimenInfo);
            }
        }

        // add aliquoted specimens
        for (Entry<SourceSpecimen, SpecimenCsvInfo> entry : parentSpecimenInfos
            .entrySet()) {
            for (AliquotedSpecimen as : aliquotedSpecimens) {
                for (Patient p : patients) {
                    SpecimenCsvInfo specimenInfo = new SpecimenCsvInfo();
                    specimenInfo.setInventoryId(nameGenerator
                        .next(String.class));
                    specimenInfo.setParentInventoryID(entry.getValue()
                        .getInventoryId());
                    specimenInfo
                        .setSpecimenType(as.getSpecimenType().getName());
                    specimenInfo.setCreatedAt(Utils.getRandomDate());
                    specimenInfo.setStudyName(study.getNameShort());
                    specimenInfo.setPatientNumber(p.getPnumber());
                    specimenInfo.setVisitNumber(1);
                    specimenInfo.setCurrentCenter(currentCenter.getNameShort());
                    specimenInfo.setOriginCenter(originCenter.getNameShort());
                    specimenInfo.setWorksheet(nameGenerator.next(String.class));
                    specimenInfos.add(specimenInfo);
                }
            }
        }

        SpecimenCsvWriter.write(CSV_NAME, specimenInfos);
        SpecimenCsvImportAction importAction =
            new SpecimenCsvImportAction(CSV_NAME);
        exec(importAction);
    }
}
