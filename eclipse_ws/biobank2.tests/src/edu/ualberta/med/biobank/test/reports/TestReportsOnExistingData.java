package edu.ualberta.med.biobank.test.reports;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import edu.ualberta.med.biobank.test.AllTests;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

@RunWith(Suite.class)
@SuiteClasses({ SpecimenRequestTest.class, ContainerCapacityTest.class,
    ContainerEmptyLocationsTest.class, FTAReportTest.class,
    FvLPatientVisitsTest.class, PatientVisitSummaryTest.class,
    QaSpecimensTest.class, SpecimenTypePvCountTest.class,
    SpecimenTypeSUsageTest.class })
public final class TestReportsOnExistingData {
    @BeforeClass
    public static void setUp() throws Exception {
        try {
            AllTests.setUp();
        } catch (Exception e) {
            e.printStackTrace();
        }
        WritableApplicationService appService = AllTests.appService;
        Assert.assertNotNull("setUp: appService is null", appService);

        ReportDataSource dataSource = new CachedReportDataSource(appService);

        AbstractReportTest.setReportDataSource(dataSource);
    }

    @AfterClass
    public static void tearDown() throws Exception {
    }
}
