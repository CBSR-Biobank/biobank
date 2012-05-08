package edu.ualberta.med.biobank.test.model;

import javax.validation.ConstraintViolationException;

import junit.framework.Assert;

import org.hibernate.Transaction;
import org.junit.Test;

import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenPosition;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.test.AssertMore;
import edu.ualberta.med.biobank.test.AssertMore.Attr;
import edu.ualberta.med.biobank.test.DbTest;
import edu.ualberta.med.biobank.test.model.util.HasXHelper;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.constraint.model.impl.ValidContainerTypeValidator;

public class TestContainerType extends DbTest {
    @Test
    public void duplicateNameDifferentSite() {
        Transaction tx = session.getTransaction();

        ContainerType original = factory.createContainerType();

        factory.createSite();
        ContainerType duplicate = factory.createContainerType();
        duplicate.setName(original.getName());

        try {
            session.update(duplicate);
            session.flush();
        } catch (Exception e) {
            tx.rollback();
            Assert.fail("two container types can have the same name if they" +
                " are at different sites");
        }
    }

    @Test
    public void duplicateSiteAndName() {
        Transaction tx = session.getTransaction();

        ContainerType original = factory.createContainerType();
        ContainerType duplicate = factory.createContainerType();
        duplicate.setName(original.getName());

        try {
            session.update(duplicate);
            tx.commit();
            Assert.fail("cannot have two container types at the same site" +
                " with the same name");
        } catch (ConstraintViolationException e) {
            tx.rollback();
            AssertMore.assertContainsAnnotation(e, Unique.class,
                new Attr("properties", new String[] { "site", "name" }));
        }
    }

    @Test
    public void duplicateNameShortDifferentSite() {
        Transaction tx = session.getTransaction();

        ContainerType original = factory.createContainerType();

        factory.createSite();
        ContainerType duplicate = factory.createContainerType();
        duplicate.setNameShort(original.getNameShort());

        try {
            session.update(duplicate);
            session.flush();
        } catch (Exception e) {
            tx.rollback();
            Assert.fail("two container types can have the same nameShort if" +
                " they are at different sites");
        }
    }

    @Test
    public void duplicateSiteAndNameShort() {
        Transaction tx = session.getTransaction();

        ContainerType original = factory.createContainerType();
        ContainerType duplicate = factory.createContainerType();
        duplicate.setNameShort(original.getNameShort());

        try {
            session.update(duplicate);
            tx.commit();
            Assert.fail("cannot have two container types at the same site" +
                " with the same nameShort");
        } catch (ConstraintViolationException e) {
            tx.rollback();
            AssertMore.assertContainsAnnotation(e, Unique.class,
                new Attr("properties", new String[] { "site", "nameShort" }));
        }
    }

    @Test
    public void expectedActivityStatusIds() {
        HasXHelper.checkExpectedActivityStatusIds(session,
            factory.createContainerType());
    }

    @Test
    public void removeUsedChildContainerType() {
        Transaction tx = session.beginTransaction();

        Container topContainer = factory.createTopContainer();
        factory.createContainer();

        try {
            ContainerType topContainerType = topContainer.getContainerType();
            topContainerType.getChildContainerTypes().clear();
            session.update(topContainerType);
            tx.commit();
            Assert.fail("cannot remove child container types in use");
        } catch (ConstraintViolationException e) {
            tx.rollback();
            AssertMore.assertContainsTemplate(e,
                ValidContainerTypeValidator.ILLEGAL_CHILD_CT_REMOVE);
        }
    }

    @Test
    public void removeUsedSpecimenType() {
        Transaction tx = session.beginTransaction();

        Specimen specimen = factory.createPositionedSpecimen();

        try {
            ContainerType parentCt = specimen.getSpecimenPosition()
                .getContainer().getContainerType();
            parentCt.getSpecimenTypes().remove(specimen.getSpecimenType());
            session.update(parentCt);
            tx.commit();
            Assert.fail("cannot remove specimen types in use");
        } catch (ConstraintViolationException e) {
            tx.rollback();
            AssertMore.assertContainsTemplate(e,
                ValidContainerTypeValidator.ILLEGAL_ST_REMOVE);
        }
    }

    @Test
    public void containerTypeChildSiteMismatch() {
        Transaction tx = session.beginTransaction();

        ContainerType childCt = factory.createContainerType();

        factory.createSite();
        ContainerType topCt = factory.createTopContainerType();

        try {
            topCt.getChildContainerTypes().add(childCt);
            session.update(topCt);
            tx.commit();
            Assert.fail("child container types must have the same site");
        } catch (org.hibernate.exception.ConstraintViolationException e) {
            tx.rollback();
            AssertMore.assertMessageContains(e.getCause(),
                "FK_ContainerType_parentContainerTypes");
        }
    }

    @Test
    public void containerContainerTypeSiteMismatch() {
        Transaction tx = session.beginTransaction();

        Container container = factory.createContainer();
        Site newSite = factory.createSite();

        try {
            container.getContainerType().setSite(newSite);
            session.update(container);
            tx.commit();
            Assert.fail("site of container and its container type must match");
        } catch (org.hibernate.exception.ConstraintViolationException e) {
            tx.rollback();
            AssertMore.assertMessageContains(e.getCause(),
                "FK_Container_containerType");
        }
    }

    @Test
    public void illegalChildContainerType() {
        Transaction tx = session.beginTransaction();

        Container childContainer = factory.createContainer();

        factory.createTopContainerType(); // new default
        factory.createTopContainer(); // new default
        Container newTopContainer = factory.createTopContainer();

        ContainerPosition position = new ContainerPosition();
        position.setParentContainer(newTopContainer);
        position.setContainer(childContainer);
        position.setRow(0);
        position.setCol(0);
        childContainer.setPosition(position);

        try {
            session.update(childContainer);
            tx.commit();
        } catch (org.hibernate.exception.ConstraintViolationException e) {
            tx.rollback();
            AssertMore.assertMessageContains(e.getCause(),
                "FK_ContainerPosition_containerTypeContainerType");
        }
    }

    @Test
    public void illegalChildSpecimen() {
        Transaction tx = session.beginTransaction();

        Specimen specimen = factory.createSpecimen();
        Container container = factory.createContainer();

        SpecimenPosition sp = new SpecimenPosition();
        sp.setRow(0);
        sp.setCol(0);
        sp.setPositionString("");
        sp.setSpecimen(specimen);
        sp.setContainer(container);

        try {
            session.save(sp);
            tx.commit();
            Assert.fail("legal child specimen types must be defined");
        } catch (org.hibernate.exception.ConstraintViolationException e) {
            tx.rollback();
            AssertMore.assertMessageContains(e.getCause(),
                "FK_SpecimenPosition_containerTypeSpecimenType");
        }
    }

    @Test
    public void multipleChildTypes() {
        Transaction tx = session.beginTransaction();

        ContainerType ct2 = factory.createContainerType();
        SpecimenType st = factory.createSpecimenType();
        ContainerType ct1 = factory.createContainerType();

        try {
            ct1.getChildContainerTypes().add(ct2);
            ct1.getSpecimenTypes().add(st);
            session.update(ct1);
            tx.commit();
            Assert.fail("cannot have child container types and specimen types");
        } catch (ConstraintViolationException e) {
            tx.rollback();
            AssertMore.assertContainsTemplate(e,
                ValidContainerTypeValidator.MULTIPLE_CHILD_TYPES);
        }
    }

    @Test
    public void overCapacity() {
        Transaction tx = session.beginTransaction();

        ContainerType ct = factory.createContainerType();
        ct.setCapacity(new Capacity(100, 100));

        try {
            tx.commit();
            Assert.fail("capacity cannot exceed what can be labeled");
        } catch (ConstraintViolationException e) {
            tx.rollback();
            AssertMore.assertContainsTemplate(e,
                ValidContainerTypeValidator.OVER_CAPACITY);
        }
    }

    @Test
    public void changeTopLevelHavingContainers() {
        Transaction tx = session.beginTransaction();

        Container topContainer = factory.createTopContainer();
        factory.createContainer();

        try {
            ContainerType topCt = topContainer.getContainerType();
            topCt.setTopLevel(!topCt.getTopLevel());
            session.update(topCt);
            tx.commit();
            Assert.fail("cannot change topLevel if children exist");
        } catch (ConstraintViolationException e) {
            tx.rollback();
            AssertMore.assertContainsTemplate(e,
                ValidContainerTypeValidator.ILLEGAL_CHANGE);
        }
    }

    @Test
    public void changeCapacityHavingContainers() {
        Transaction tx = session.beginTransaction();

        Container topContainer = factory.createTopContainer();
        factory.createContainer();

        try {
            ContainerType topCt = topContainer.getContainerType();
            topCt.getCapacity().setRowCapacity(topCt.getRowCapacity() + 1);
            session.update(topCt);
            tx.commit();
            Assert.fail("cannot change capacity if children exist");
        } catch (ConstraintViolationException e) {
            tx.rollback();
            AssertMore.assertContainsTemplate(e,
                ValidContainerTypeValidator.ILLEGAL_CHANGE);
        }
    }

    @Test
    public void changeSchemeHavingContainers() {
        Transaction tx = session.beginTransaction();

        Container topContainer = factory.createTopContainer();
        factory.createContainer();

        try {
            ContainerType topCt = topContainer.getContainerType();
            topCt.setChildLabelingScheme(factory.getScheme()
                .get2CharAlphabetic());

            session.update(topCt);
            tx.commit();
            Assert.fail("cannot change labeling scheme if children exist");
        } catch (ConstraintViolationException e) {
            tx.rollback();
            AssertMore.assertContainsTemplate(e,
                ValidContainerTypeValidator.ILLEGAL_CHANGE);
        }
    }

    @Test
    public void changeTopLevelHavingSpecimens() {
        Transaction tx = session.beginTransaction();

        Container topContainer = factory.createTopContainer();
        factory.createPositionedSpecimen();

        try {
            ContainerType topCt = topContainer.getContainerType();
            topCt.setTopLevel(!topCt.getTopLevel());
            session.update(topCt);
            tx.commit();
            Assert.fail("cannot change topLevel if children exist");
        } catch (ConstraintViolationException e) {
            tx.rollback();
            AssertMore.assertContainsTemplate(e,
                ValidContainerTypeValidator.ILLEGAL_CHANGE);
        }
    }

    @Test
    public void changeCapacityHavingSpecimens() {
        Transaction tx = session.beginTransaction();

        Container topContainer = factory.createTopContainer();
        factory.createPositionedSpecimen();

        try {
            ContainerType topCt = topContainer.getContainerType();
            topCt.getCapacity().setRowCapacity(topCt.getRowCapacity() + 1);
            session.update(topCt);
            tx.commit();
            Assert.fail("cannot change capacity if children exist");
        } catch (ConstraintViolationException e) {
            tx.rollback();
            AssertMore.assertContainsTemplate(e,
                ValidContainerTypeValidator.ILLEGAL_CHANGE);
        }
    }

    @Test
    public void changeSchemeHavingSpecimens() {
        Transaction tx = session.beginTransaction();

        Container topContainer = factory.createTopContainer();
        factory.createPositionedSpecimen();

        try {
            ContainerType topCt = topContainer.getContainerType();
            topCt.setChildLabelingScheme(factory.getScheme()
                .get2CharAlphabetic());

            session.update(topCt);
            tx.commit();
            Assert.fail("cannot change labeling scheme if children exist");
        } catch (ConstraintViolationException e) {
            tx.rollback();
            AssertMore.assertContainsTemplate(e,
                ValidContainerTypeValidator.ILLEGAL_CHANGE);
        }
    }
}
