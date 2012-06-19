package edu.ualberta.med.biobank.test.action;

import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.action.csvimport.SpecimenCsvImportActtion;

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
        SpecimenCsvImportActtion importAction = new SpecimenCsvImportActtion();
        importAction.setCsvFile("import_specimens.csv");
        exec(importAction);
    }

}
