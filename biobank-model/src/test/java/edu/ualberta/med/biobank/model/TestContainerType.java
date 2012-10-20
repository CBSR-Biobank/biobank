package edu.ualberta.med.biobank.model;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotNull;

import junit.framework.Assert;

import org.hibernate.Transaction;
import org.junit.Test;

import edu.ualberta.med.biobank.AssertConstraintViolation;
import edu.ualberta.med.biobank.DbTest;
import edu.ualberta.med.biobank.model.center.Container;
import edu.ualberta.med.biobank.model.center.ContainerType;
import edu.ualberta.med.biobank.model.study.Specimen;
import edu.ualberta.med.biobank.model.util.HasXHelper;
import edu.ualberta.med.biobank.validator.constraint.Empty;
import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.constraint.model.impl.ValidContainerTypeValidator;

public class TestContainerType extends DbTest {
    @Test
    public void emptyName() {
        HasXHelper.checkEmptyName(session, factory.createContainerType());
    }

    @Test
    public void duplicateNameDifferentSite() {
        ContainerType original = factory.createContainerType();

        factory.createCenter();
        ContainerType duplicate = factory.createContainerType();
        duplicate.setName(original.getName());

        try {
            session.update(duplicate);
            session.flush();
        } catch (Exception e) {
            Assert.fail("two container types can have the same name if they" +
                " are at different sites");
        }
    }

    @Test
    public void duplicateSiteAndName() {
        ContainerType original = factory.createContainerType();
        ContainerType duplicate = factory.createContainerType();
        duplicate.setName(original.getName());

        try {
            session.update(duplicate);
            session.flush();
            Assert.fail("cannot have two container types at the same site" +
                " with the same name");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation().withAnnotationClass(Unique.class)
                .withAttr("properties", new String[] { "site", "name" })
                .assertIn(e);
        }
    }

    @Test
    public void emptyNameShort() {
        HasXHelper.checkEmptyNameShort(session, factory.createContainerType());
    }

    @Test
    public void duplicateNameShortDifferentSite() {
        Transaction tx = session.getTransaction();

        ContainerType original = factory.createContainerType();

        factory.createCenter();
        ContainerType duplicate = factory.createContainerType();
        duplicate.setName(original.getName());

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
        ContainerType original = factory.createContainerType();
        ContainerType duplicate = factory.createContainerType();
        duplicate.setName(original.getName());

        try {
            session.update(duplicate);
            session.flush();
            Assert.fail("cannot have two container types at the same site" +
                " with the same nameShort");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation().withAnnotationClass(Unique.class)
                .withAttr("properties", new String[] { "site", "nameShort" })
                .assertIn(e);
        }
    }

    @Test
    public void expectedActivityStatusIds() {
        HasXHelper.checkExpectedActivityStatusIds(session,
            factory.createContainerType());
    }

    @Test
    public void nullSite() {
        ContainerType ct = factory.createContainerType();
        ct.setSite(null);

        try {
            session.update(ct);
            session.flush();
            Assert.fail("null site should not be allowed");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation().withAnnotationClass(NotNull.class)
                .withRootBean(ct)
                .withPropertyPath("site")
                .assertIn(e);
        }
    }

    @Test
    public void nullCapacities() {
        ContainerType ct = factory.createContainerType();
        ct.getCapacity().setColCapacity(null);

        try {
            session.update(ct);
            session.flush();
            Assert.fail("null capacity.colCapacity should not be allowed");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation().withAnnotationClass(NotNull.class)
                .withRootBean(ct)
                .withPropertyPath("capacity.colCapacity")
                .assertIn(e);
        }

        ct.getCapacity().setColCapacity(1);
        ct.getCapacity().setRowCapacity(null);

        try {
            session.update(ct);
            session.flush();
            Assert.fail("null capacity.rowCapacity should not be allowed");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation().withAnnotationClass(NotNull.class)
                .withRootBean(ct)
                .withPropertyPath("capacity.rowCapacity")
                .assertIn(e);
        }

        ct.setCapacity(null);

        try {
            session.update(ct);
            session.flush();
            Assert.fail("null capacity should not be allowed");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation().withAnnotationClass(NotNull.class)
                .withRootBean(ct)
                .withPropertyPath("capacity")
                .assertIn(e);
        }
    }

    @Test
    public void nullChildLabelingScheme() {
        ContainerType ct = factory.createContainerType();
        ct.setChildLabelingScheme(null);

        try {
            session.update(ct);
            session.flush();
            Assert.fail("null childLabelingScheme should not be allowed");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation().withAnnotationClass(NotNull.class)
                .withRootBean(ct)
                .withPropertyPath("childLabelingScheme")
                .assertIn(e);
        }
    }

    @Test
    public void removeUsedChildContainerType() {
        Container topContainer = factory.createTopContainer();
        factory.createContainer();

        try {
            ContainerType topContainerType = topContainer.getContainerType();
            topContainerType.getChildContainerTypes().clear();
            session.update(topContainerType);
            session.flush();
            Assert.fail("cannot remove child container types in use");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation()
                .withTemplate(ValidContainerTypeValidator.REMOVED_CONTAINER_TYPES_IN_USE)
                .assertIn(e);
        }
    }

    @Test
    public void removeUsedSpecimenType() {
        Specimen specimen = factory.createPositionedSpecimen();

        try {
            ContainerType parentCt = specimen.getSpecimenPosition()
                .getContainer().getContainerType();
            parentCt.getSpecimenTypes().remove(specimen.getSpecimenType());
            session.update(parentCt);
            session.flush();
            Assert.fail("cannot remove specimen types in use");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation()
                .withTemplate(ValidContainerTypeValidator.REMOVED_VESSELS_IN_USE)
                .assertIn(e);
        }
    }

    @Test
    public void containerTypeChildSiteMismatch() {
        ContainerType childCt = factory.createContainerType();

        factory.createCenter();
        ContainerType topCt = factory.createTopContainerType();

        try {
            topCt.getChildContainerTypes().add(childCt);
            session.update(topCt);
            session.flush();
            Assert.fail("child container types must have the same site");
        } catch (org.hibernate.exception.ConstraintViolationException e) {
            AssertMore.assertMessageContains(e.getCause(),
                "FK_ContainerType_parentContainerTypes");
        }
    }

    @Test
    public void containerContainerTypeSiteMismatch() {
        Container container = factory.createContainer();
        Site newSite = factory.createCenter();

        try {
            container.getContainerType().setSite(newSite);
            session.update(container);
            session.flush();
            Assert.fail("site of container and its container type must match");
        } catch (org.hibernate.exception.ConstraintViolationException e) {
            AssertMore.assertMessageContains(e.getCause(),
                "FK_Container_containerType");
        }
    }

    @Test
    public void illegalChildContainerType() {
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
            session.flush();
        } catch (org.hibernate.exception.ConstraintViolationException e) {
            AssertMore.assertMessageContains(e.getCause(),
                "FK_ContainerPosition_containerTypeContainerType");
        }
    }

    @Test
    public void illegalChildSpecimen() {
        Specimen specimen = factory.createChildSpecimen();
        Container container = factory.createContainer();

        SpecimenPosition sp = new SpecimenPosition();
        sp.setRow(0);
        sp.setCol(0);
        sp.setPositionString("");
        sp.setSpecimen(specimen);
        sp.setContainer(container);

        try {
            session.save(sp);
            session.flush();
            Assert.fail("legal child specimen types must be defined");
        } catch (org.hibernate.exception.ConstraintViolationException e) {
            AssertMore.assertMessageContains(e.getCause(),
                "FK_SpecimenPosition_containerTypeSpecimenType");
        }
    }

    @Test
    public void multipleChildTypes() {
        ContainerType ct2 = factory.createContainerType();
        SpecimenType st = factory.createSpecimenType();
        ContainerType ct1 = factory.createContainerType();

        try {
            ct1.getChildContainerTypes().add(ct2);
            ct1.getSpecimenTypes().add(st);
            session.update(ct1);
            session.flush();
            Assert.fail("cannot have child container types and specimen types");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation()
                .withTemplate(ValidContainerTypeValidator.MULTIPLE_CHILD_TYPES)
                .assertIn(e);
        }
    }

    @Test
    public void overCapacity() {
        ContainerType ct = factory.createContainerType();
        ct.setCapacity(new Capacity(100, 100));

        try {
            session.flush();
            Assert.fail("capacity cannot exceed what can be labeled");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation()
                .withTemplate(ValidContainerTypeValidator.OVER_CAPACITY)
                .assertIn(e);
        }
    }

    @Test
    public void changeTopLevelHavingContainers() {
        Container topContainer = factory.createTopContainer();
        factory.createContainer();

        try {
            ContainerType topCt = topContainer.getContainerType();
            topCt.setTopLevel(!topCt.isTopLevel());
            session.update(topCt);
            session.flush();
            Assert.fail("cannot change topLevel if children exist");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation()
                .withTemplate(ValidContainerTypeValidator.ILLEGAL_CHANGE)
                .assertIn(e);
        }
    }

    @Test
    public void changeCapacityHavingContainers() {
        Container topContainer = factory.createTopContainer();
        factory.createContainer();

        try {
            ContainerType topCt = topContainer.getContainerType();
            topCt.getCapacity().setRowCapacity(topCt.getRowCapacity() + 1);
            session.update(topCt);
            session.flush();
            Assert.fail("cannot change capacity if children exist");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation()
                .withTemplate(ValidContainerTypeValidator.ILLEGAL_CHANGE)
                .assertIn(e);
        }
    }

    @Test
    public void changeSchemeHavingContainers() {
        Container topContainer = factory.createTopContainer();
        factory.createContainer();

        try {
            ContainerType topCt = topContainer.getContainerType();
            topCt.setChildLabelingScheme(factory.getScheme()
                .get2CharAlphabetic());

            session.update(topCt);
            session.flush();
            Assert.fail("cannot change labeling scheme if children exist");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation()
                .withTemplate(ValidContainerTypeValidator.ILLEGAL_CHANGE)
                .assertIn(e);
        }
    }

    @Test
    public void changeTopLevelHavingSpecimens() {
        Container topContainer = factory.createTopContainer();
        factory.createPositionedSpecimen();

        try {
            ContainerType topCt = topContainer.getContainerType();
            topCt.setTopLevel(!topCt.isTopLevel());
            session.update(topCt);
            session.flush();
            Assert.fail("cannot change topLevel if children exist");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation()
                .withTemplate(ValidContainerTypeValidator.ILLEGAL_CHANGE)
                .assertIn(e);
        }
    }

    @Test
    public void changeCapacityHavingSpecimens() {
        Container topContainer = factory.createTopContainer();
        factory.createPositionedSpecimen();

        try {
            ContainerType topCt = topContainer.getContainerType();
            topCt.getCapacity().setRowCapacity(topCt.getRowCapacity() + 1);
            session.update(topCt);
            session.flush();
            Assert.fail("cannot change capacity if children exist");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation()
                .withTemplate(ValidContainerTypeValidator.ILLEGAL_CHANGE)
                .assertIn(e);
        }
    }

    @Test
    public void changeSchemeHavingSpecimens() {
        Container topContainer = factory.createTopContainer();
        factory.createPositionedSpecimen();

        try {
            ContainerType topCt = topContainer.getContainerType();
            topCt.setChildLabelingScheme(factory.getScheme()
                .get2CharAlphabetic());

            session.update(topCt);
            session.flush();
            Assert.fail("cannot change labeling scheme if children exist");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation()
                .withTemplate(ValidContainerTypeValidator.ILLEGAL_CHANGE)
                .assertIn(e);
        }
    }

    @Test
    public void deleteWithParent() {
        ContainerType topCt = factory.createTopContainerType();
        ContainerType childCt = factory.createContainerType();

        topCt.getChildContainerTypes().add(childCt);
        session.update(topCt);
        session.flush();

        try {
            session.delete(childCt);
            session.flush();
            Assert.fail("cannot delete a container type with a parent");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation()
                .withAnnotationClass(Empty.class)
                .withAttr("property", "parentContainerTypes")
                .assertIn(e);
        }
    }

    @Test
    public void deleteWithContainer() {
        Container container = factory.createContainer();

        try {
            session.delete(container.getContainerType());
            session.flush();
            Assert.fail("cannot delete a container type used by a container");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation()
                .withAnnotationClass(NotUsed.class)
                .withAttr("by", Container.class)
                .withAttr("property", "containerType")
                .assertIn(e);
        }
    }

    @Test
    public void deleteWithParentContainerTypes() {
        ContainerType topCt = factory.createTopContainerType();
        ContainerType ct = factory.createContainerType();

        topCt.getChildContainerTypes().add(ct);
        session.update(topCt);

        try {
            session.delete(ct);
            session.flush();
            Assert.fail("cannot delete a container type with parent" +
                " container types");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation().withAnnotationClass(Empty.class)
                .withRootBean(ct)
                .withAttr("property", "parentContainerTypes")
                .assertIn(e);
        }
    }
}
