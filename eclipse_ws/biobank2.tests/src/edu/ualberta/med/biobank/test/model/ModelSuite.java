package edu.ualberta.med.biobank.test.model;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ TestCenter.class, TestContainerType.class,
    TestDispatch.class, TestDispatchSpecimen.class, TestSite.class })
public class ModelSuite {
}
