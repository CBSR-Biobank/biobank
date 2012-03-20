package edu.ualberta.med.biobank.test.action;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import edu.ualberta.med.biobank.test.action.security.SecuritySuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ SecuritySuite.class, TestActivityStatus.class,
    TestClinic.class, TestCollectionEvent.class, TestContainer.class,
    TestContainerType.class, TestDispatch.class, TestPatient.class,
    TestProcessingEvent.class, TestRequest.class, TestResearchGroup.class,
    TestShipment.class, TestSite.class, TestSpecimen.class, TestStudy.class })
public class ActionSuite {
}
