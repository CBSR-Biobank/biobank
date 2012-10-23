package edu.ualberta.med.biobank.model;

import javax.validation.ConstraintViolationException;

import junit.framework.Assert;

import org.hibernate.Transaction;
import org.junit.Test;

import edu.ualberta.med.biobank.AssertConstraintViolation;
import edu.ualberta.med.biobank.DbTest;
import edu.ualberta.med.biobank.model.center.Container;
import edu.ualberta.med.biobank.model.center.ContainerType;
import edu.ualberta.med.biobank.model.util.HasXHelper;
import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.constraint.Unique;

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
    public void nullCenter() {
    }

    @Test
    public void nullContainerScheme() {
    }

    @Test
    public void removeUsedChildContainerType() {
    }

    @Test
    public void removeUsedSpecimenType() {
    }

    @Test
    public void changeTopLevelHavingContainers() {
        // cannot change being topLevel if has containers
    }

    @Test
    public void changeCapacityHavingContainers() {
        // cannot change capacity if in use?
    }

    @Test
    public void changeSchemeHavingContainers() {
        // cannot change a schema if it's in use?
    }

    @Test
    public void changeTopLevelHavingSpecimens() {
        // cannot change topLevel property of a container type if there are
        // containers that use it that have children
    }

    @Test
    public void changeCapacityHavingSpecimens() {
    }

    @Test
    public void deleteWithParent() {
        // cannot delete a container type with a parent
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
}
