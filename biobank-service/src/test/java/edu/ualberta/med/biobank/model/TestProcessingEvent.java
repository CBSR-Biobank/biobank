package edu.ualberta.med.biobank.model;

import edu.ualberta.med.biobank.DbTest;

public class TestProcessingEvent extends DbTest {
    // @Test
    // public void duplicateWorksheet() {
    // ProcessingEvent pe1 = factory.createProcessingEvent();
    // ProcessingEvent pe2 = factory.createProcessingEvent();
    // pe2.setWorksheet(pe1.getWorksheet());
    //
    // try {
    // session.update(pe2);
    // session.flush();
    // Assert.fail("processing event worksheet should be unique");
    // } catch (ConstraintViolationException e) {
    // new ConstraintViolationAssertion().withAnnotationClass(Unique.class)
    // .withAttr("properties", new String[] { "worksheet" })
    // .withRootBean(pe2)
    // .assertIn(e);
    // }
    // }
}
