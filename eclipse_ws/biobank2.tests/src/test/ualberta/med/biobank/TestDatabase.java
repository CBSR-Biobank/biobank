package test.ualberta.med.biobank;

import gov.nih.nci.system.applicationservice.WritableApplicationService;

import org.junit.Before;

public class TestDatabase {
    protected WritableApplicationService appService;

    @Before
    public void setUp() {
        appService = AllTests.appService;
    }

}
