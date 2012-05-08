package edu.ualberta.med.biobank.test.model;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ TestAliquotedSpecimen.class, TestCenter.class,
    TestCollectionEvent.class, TestContainer.class, TestContainerType.class,
    TestDispatch.class, TestDispatchSpecimen.class, TestGroup.class,
    TestPrincipal.class, TestProcessingEvent.class, TestRequestSpecimen.class,
    TestSite.class, TestSpecimen.class, TestSpecimenType.class, TestStudy.class })
public class ModelSuite {
}
