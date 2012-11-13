package edu.ualberta.med.biobank.model;

import edu.ualberta.med.biobank.DbTest;

public class TestContainer extends DbTest {
    // @Test
    // public void nullLabel() {
    // Container<?> c = factory.createContainer();
    // c.setLabel(null);
    //
    // try {
    // session.update(c);
    // session.flush();
    // Assert.fail("null label should not be allowed");
    // } catch (ConstraintViolationException e) {
    // new ConstraintViolationAssertion().withAnnotationClass(NotEmpty.class)
    // .withRootBean(c)
    // .withPropertyPath("label")
    // .assertIn(e);
    // }
    // }
    //
    // @Test
    // public void emptyLabel() {
    // Container<?> c = factory.createContainer();
    // c.setLabel("");
    //
    // try {
    // session.update(c);
    // session.flush();
    // Assert.fail("empty label should not be allowed");
    // } catch (ConstraintViolationException e) {
    // new ConstraintViolationAssertion().withAnnotationClass(NotEmpty.class)
    // .withRootBean(c)
    // .withPropertyPath("label")
    // .assertIn(e);
    // }
    // }
    //
    // @Test
    // public void duplicateLabelDifferentSiteAndContainerType() {
    // Container<?> c1 = factory.createContainer();
    // factory.createCenter(); // new default site
    // factory.createContainerType(); // new default container type
    // Container<?> c2 = factory.createContainer();
    //
    // c2.setLabel(c1.getLabel());
    //
    // try {
    // session.update(c2);
    // session.flush();
    // } catch (Exception e) {
    // Assert.fail("two containers can have the same label if they" +
    // " are at different sites and have different container types");
    // }
    // }
    //
    // @Test
    // public void duplicateLabelAndSiteDifferentContainerType() {
    // Container<?> c1 = factory.createTopContainer();
    // factory.createTopContainerType(); // new default top container type
    // Container<?> c2 = factory.createTopContainer();
    //
    // c2.setLabel(c1.getLabel());
    //
    // try {
    // session.update(c2);
    // session.flush();
    // } catch (Exception e) {
    // Assert.fail("two containers can have the same label if they" +
    // " have different container types");
    // }
    // }
    //
    // @Test
    // public void duplicateLabelAndSiteAndContainerType() {
    // Container<?> c1 = factory.createContainer();
    // Container<?> c2 = factory.createContainer();
    //
    // c2.setLabel(c1.getLabel());
    //
    // try {
    // session.update(c2);
    // session.flush();
    // } catch (ConstraintViolationException e) {
    // new ConstraintViolationAssertion()
    // .withAnnotationClass(Unique.class)
    // .withRootBean(c2)
    // .withAttr("properties",
    // new String[] { "site", "containerType", "label" })
    // .assertIn(e);
    // }
    // }
    //
    // @Test
    // public void duplicateProductBarcodeDifferentSite() {
    // Container<?> c1 = factory.createContainer();
    // factory.createCenter(); // new default site
    // Container<?> c2 = factory.createContainer();
    //
    // c2.setInventoryId(c1.getInventoryId());
    //
    // try {
    // session.update(c2);
    // session.flush();
    // } catch (Exception e) {
    // Assert.fail("two containers can have the same product barcode" +
    // " if they are at different sites");
    // }
    // }
    //
    // @Test
    // public void duplicateProductBarcodeAndSite() {
    // Container<?> c1 = factory.createContainer();
    // Container<?> c2 = factory.createContainer();
    //
    // c2.setInventoryId(c1.getInventoryId());
    //
    // try {
    // session.update(c2);
    // session.flush();
    // } catch (ConstraintViolationException e) {
    // new ConstraintViolationAssertion()
    // .withAnnotationClass(Unique.class)
    // .withRootBean(c2)
    // .withAttr("properties",
    // new String[] { "site", "productBarcode" })
    // .assertIn(e);
    // }
    // }
    //
    // @Test
    // public void deleteWithContainers() {
    // Container<?> topContainer = factory.createTopContainer();
    // factory.createContainer();
    //
    // try {
    // session.delete(topContainer);
    // session.flush();
    // } catch (ConstraintViolationException e) {
    // new ConstraintViolationAssertion().withAnnotationClass(Empty.class)
    // .withRootBean(topContainer)
    // .withAttr("property", "childPositions")
    // .assertIn(e);
    // }
    // }
    //
    // @Test
    // public void deleteWithSpecimens() {
    // Specimen specimen = factory.createPositionedSpecimen();
    // Container<?> container = specimen.getContainer();
    //
    // try {
    // session.delete(container);
    // session.flush();
    // } catch (ConstraintViolationException e) {
    // new ConstraintViolationAssertion().withAnnotationClass(Empty.class)
    // .withRootBean(container)
    // .withAttr("property", "specimenPositions")
    // .assertIn(e);
    // }
    // }
}
