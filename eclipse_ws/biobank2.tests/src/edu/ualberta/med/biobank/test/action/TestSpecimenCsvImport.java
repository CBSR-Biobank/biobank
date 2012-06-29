package edu.ualberta.med.biobank.test.action;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.action.csvimport.SpecimenCsvImportAction;
import edu.ualberta.med.biobank.common.action.csvimport.SpecimenCsvInfo;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.util.csv.SpecimenCsvWriter;

public class TestSpecimenCsvImport extends ActionTest {

    private String name;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        name = getMethodNameR();
    }

    @Test
    public void testCompression() throws Exception {
        Transaction tx = session.beginTransaction();
        // the site name comes from the CSV file
        Center center = factory.createSite();
        Center clinic = factory.createClinic();
        Study study = factory.createStudy();
        tx.commit();

        specimensCreateAndImportCsv(study, clinic, center);

    }

    @SuppressWarnings("nls")
    private void specimensCreateAndImportCsv(Study study, Center originCenter,
        Center currentCenter)
        throws IOException {
        final String CSV_NAME = "import_specimens.csv";

        Set<SpecimenCsvInfo> parentSpecimenInfos =
            new HashSet<SpecimenCsvInfo>();

        // add parent specimens first
        Set<SpecimenCsvInfo> specimenInfos = new HashSet<SpecimenCsvInfo>();
        for (int i = 0, n = 10 + getR().nextInt(30); i < n; ++i) {
            SpecimenCsvInfo specimenInfo = new SpecimenCsvInfo();
            specimenInfo.setInventoryId(Utils.getRandomString(10, 15));
            specimenInfo.setSpecimenType("");
            specimenInfo.setCreatedAt(Utils.getRandomDate());
            specimenInfo.setStudyName(study.getNameShort());
            specimenInfo.setPatientNumber("");
            specimenInfo.setVisitNumber(1);
            specimenInfo.setCurrentCenter(currentCenter.getNameShort());
            specimenInfo.setOriginCenter(originCenter.getNameShort());
            specimenInfo.setSourceSpecimen(true);
            specimenInfo.setWorksheet(Utils.getRandomString(5, 10));
            specimenInfos.add(specimenInfo);
            parentSpecimenInfos.add(specimenInfo);
        }

        SpecimenCsvWriter.write(CSV_NAME, specimenInfos);
        SpecimenCsvImportAction importAction =
            new SpecimenCsvImportAction(CSV_NAME);
        exec(importAction);
    }
}
