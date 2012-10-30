package edu.ualberta.med.biobank.model.center;

import edu.ualberta.med.biobank.DbTest;

public class CenterTest extends DbTest {
    // @Test
    // public void emptyName() {
    // HasXHelper.checkEmptyName(session, factory.createCenter());
    // }
    //
    // @Test
    // public void duplicateName() {
    // HasXHelper.checkDuplicateName(session,
    // factory.createCenter(),
    // factory.createCenter());
    // }
    //
    // @Test
    // public void emptyNameShort() {
    // HasXHelper.checkEmptyNameShort(session, factory.createCenter());
    // }
    //
    // @Test
    // public void duplicateNameShort() {
    // HasXHelper.checkDuplicateName(session,
    // factory.createCenter(),
    // factory.createCenter());
    // }
    //
    // @Test
    // public void nullAddress() {
    // }
    //
    // @Test
    // public void deleteWithSrcDispatches() {
    // Transaction tx = session.beginTransaction();
    //
    // Center sender = factory.createCenter();
    // Center receiver = factory.createCenter();
    // factory.createDispatch(sender, receiver);
    //
    // try {
    // session.delete(sender);
    // tx.commit();
    // Assert.fail("cannot delete a center with srcDispatches");
    // } catch (ConstraintViolationException e) {
    // new ConstraintViolationAssertion().withAnnotationClass(Empty.class)
    // .withAttr("property", "srcDispatches")
    // .assertIn(e);
    // }
    // }
    //
    // @Test
    // public void deleteWithDstDispatches() {
    // Transaction tx = session.beginTransaction();
    //
    // Center sender = factory.createCenter();
    // Center receiver = factory.createCenter();
    // factory.createDispatch(sender, receiver);
    //
    // try {
    // session.delete(receiver);
    // tx.commit();
    // Assert.fail("cannot delete a center with dstDispatches");
    // } catch (ConstraintViolationException e) {
    // new ConstraintViolationAssertion().withAnnotationClass(Empty.class)
    // .withAttr("property", "dstDispatches")
    // .assertIn(e);
    // }
    // }
    //
    // @Test
    // public void deleteWithContainers() {
    // Transaction tx = session.beginTransaction();
    //
    // Container<?> container = factory.createContainer();
    //
    // try {
    // Center site = container.getTree().getOwningCenter();
    // session.delete(site);
    // tx.commit();
    // Assert.fail("cannot delete site with containers");
    // } catch (ConstraintViolationException e) {
    // new ConstraintViolationAssertion().withAnnotationClass(Empty.class)
    // .withAttr("property", "containers")
    // .assertIn(e);
    // }
    // }
    //
    // @Test
    // public void deleteWithContainerTypes() {
    // Transaction tx = session.beginTransaction();
    //
    // ContainerType containerType = factory.createContainerType();
    //
    // try {
    // Center center = containerType.getCenter();
    // session.delete(center);
    // tx.commit();
    // Assert.fail("cannot delete site with container types");
    // } catch (ConstraintViolationException e) {
    // new ConstraintViolationAssertion().withAnnotationClass(Empty.class)
    // .withAttr("property", "containerTypes")
    // .assertIn(e);
    // }
    // }
    //
    // @Test
    // public void deleteWithProcessingEvents() {
    // Transaction tx = session.beginTransaction();
    //
    // Center center = factory.createCenter();
    // ProcessingEvent event = factory.createProcessingEvent();
    //
    // if (!event.getCenter().equals(center)) {
    // Assert.fail("unexpected center");
    // }
    //
    // try {
    // session.delete(center);
    // tx.commit();
    // Assert.fail("cannot delete site with container types");
    // } catch (ConstraintViolationException e) {
    // new ConstraintViolationAssertion().withAnnotationClass(Empty.class)
    // .withAttr("property", "processingEvents")
    // .assertIn(e);
    // }
    // }
}
