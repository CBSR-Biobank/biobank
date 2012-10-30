package edu.ualberta.med.biobank.model;

import edu.ualberta.med.biobank.DbTest;

public class TestPatient extends DbTest {
    // @Test
    // public void duplicatePnumber() {
    // Patient p1 = factory.createPatient();
    // Patient p2 = factory.createPatient();
    // p2.setPnumber(p1.getPnumber());
    //
    // try {
    // session.update(p2);
    // session.flush();
    // Assert.fail("cannot have two patients with the same pnumber");
    // } catch (ConstraintViolationException e) {
    // new ConstraintViolationAssertion().withAnnotationClass(Unique.class)
    // .withAttr("properties", new String[] { "pnumber" })
    // .withRootBean(p2)
    // .assertIn(e);
    // }
    // }
    //
    // @Test
    // public void deleteWithCollectionEvents() {
    // CollectionEvent ce = factory.createCollectionEvent();
    // Patient patient = ce.getPatient();
    //
    // try {
    // // public void duplicateName() {
    // // HasXHelper.checkDuplicateName(session,
    // // factory.createGroup(),
    // // factory.createGroup());
    // // }
    // session.delete(patient);
    // session.flush();
    // Assert.fail("cannot have two patients with the same pnumber");
    // } catch (ConstraintViolationException e) {
    // new ConstraintViolationAssertion().withAnnotationClass(Empty.class)
    // .withAttr("property", "collectionEvents")
    // .withRootBean(patient)
    // .assertIn(e);
    // }
    // }
}
