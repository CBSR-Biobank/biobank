package edu.ualberta.med.biobank.test.action;

import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.action.csvimport.SpecimenCsvImportAction;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Study;

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
        Center center = factory.createSite("CBSR");
        Study study = factory.createStudy();

        SpecimenCsvImportAction importAction =
            new SpecimenCsvImportAction("import_specimens.csv");
        exec(importAction);
    }

}
