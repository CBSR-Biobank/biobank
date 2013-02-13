package edu.ualberta.med.biobank.test.action;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import edu.ualberta.med.biobank.test.action.batchoperation.patient.TestPatientCsvImport;
import edu.ualberta.med.biobank.test.action.batchoperation.shipment.TestShipmentCsvInfo;
import edu.ualberta.med.biobank.test.action.batchoperation.specimen.TestSpecimenBatchOp;
import edu.ualberta.med.biobank.test.action.security.SecuritySuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    SecuritySuite.class,
    TestClinic.class,
    TestCollectionEvent.class,
    TestContainer.class,
    TestContainerLabelingScheme.class,
    TestContainerType.class,
    TestDispatch.class,
    TestPatient.class,
    TestPatientCsvImport.class,
    TestProcessingEvent.class,
    TestRequest.class,
    TestResearchGroup.class,
    TestShipment.class,
    TestShipmentCsvInfo.class,
    TestShippingMethod.class,
    TestSite.class,
    TestSpecimen.class,
    TestSpecimenBatchOp.class,
    TestStudy.class })
public class ActionSuite {
}
