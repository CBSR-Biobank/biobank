package edu.ualberta.med.biobank.test.action.batchoperation.specimen;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import edu.ualberta.med.biobank.test.action.batchoperation.specimen.position.TestPositionBatchOp;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    TestSpecimenBatchOp.class,
    TestGrandchildSpecimenBatchOp.class,
    TestPositionBatchOp.class
})
public class SpecimenBatchOpTestSuite {

}
