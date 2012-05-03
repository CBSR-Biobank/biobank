package edu.ualberta.med.biobank.test.model;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ TestContainerType.class, TestDispatch.class,
    TestDispatchSpecimen.class })
public class ModelSuite {
}
