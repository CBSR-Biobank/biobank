package edu.ualberta.med.biobank.test.validation;

import javax.validation.ConstraintViolationException;

import junit.framework.Assert;

import org.hibernate.Transaction;
import org.junit.Test;

import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.validator.constraint.model.impl.ValidContainerTypeValidator;

public class TestContainerType extends TestValidation {
    @Test
    public void removeUsedChildContainerType() {
        Transaction tx = session.beginTransaction();

        ContainerType ct2 = factory.createContainerType();
        ContainerType ct1 = factory.createContainerType();
        ct1.setTopLevel(true);
        ct1.getChildContainerTypes().add(ct2);

        Container c1 = factory.createContainer();
        c1.setContainerType(ct1);

        Container c2 = factory.createContainer();
        c2.setContainerType(ct2);

        ContainerPosition cp = new ContainerPosition();
        cp.setParentContainer(c1);
        cp.setRow(0);
        cp.setCol(0);
        cp.setContainer(c2);
        c2.setPosition(cp);

        factory.save();

        try {
            ct1.getChildContainerTypes().clear();
            session.update(ct1);
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

        SpecimenType st = factory.createSpecimenType();
        ContainerType ct = factory.createContainerType();
        ct.setTopLevel(true);
        ct.getSpecimenTypes().add(st);

        factory.createContainer();
        Specimen s = factory.createSpecimen();
        s.setSpecimenPosition(factory.createSpecimenPosition());

        factory.save();

        try {
            ct.getSpecimenTypes().clear();
            session.update(ct);
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

        ContainerType ct2 = factory.createContainerType();

        factory.createSite();
        ContainerType ct1 = factory.createContainerType();

        factory.save();

        try {
            ct1.getChildContainerTypes().add(ct2);
            session.update(ct1);
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

        ContainerType ct = factory.createContainerType();
        ct.setTopLevel(true);

        factory.createSite();
        factory.createContainer();

        try {
            factory.save();
            tx.commit();
            Assert.fail("site of container and its container type must match");
        } catch (org.hibernate.exception.ConstraintViolationException e) {
            tx.rollback();
            AssertMore.assertMessageContains(e.getCause(),
                "FK_Container_containerType");
        }
    }

    @Test
    public void illegalChildContainer() {
        Transaction tx = session.beginTransaction();

        ContainerType ct1 = factory.createContainerType();
        ct1.setTopLevel(true);
        Container c1 = factory.createContainer();

        factory.createContainerType(); // new default
        Container c2 = factory.createContainer();

        ContainerPosition position = new ContainerPosition();
        position.setParentContainer(c1);
        position.setContainer(c2);
        position.setRow(0);
        position.setCol(0);
        c2.setPosition(position);

        try {
            factory.save();
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

        ContainerType ct = factory.createContainerType();
        ct.setTopLevel(true);
        factory.createContainer();
        Specimen s = factory.createSpecimen();
        factory.save();

        try {
            s.setSpecimenPosition(factory.createSpecimenPosition());
            session.update(s);
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

        factory.save();

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
            factory.save();
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

        ContainerType ct2 = factory.createContainerType();
        ContainerType ct1 = factory.createContainerType();
        ct1.setTopLevel(true);
        ct1.getChildContainerTypes().add(ct2);

        Container c1 = factory.createContainer();
        c1.setContainerType(ct1);

        Container c2 = factory.createContainer();
        c2.setContainerType(ct2);

        ContainerPosition cp = new ContainerPosition();
        cp.setParentContainer(c1);
        cp.setRow(0);
        cp.setCol(0);
        cp.setContainer(c2);
        c2.setPosition(cp);

        factory.save();
        
//        factory.createTopContainer();
//        factory.createMiddleContainer(); // 
//        factory.createChildContainer(); // creates a child of middle

        try {
            factory.save();
            tx.commit();
            Assert.fail("cannot change topLevel if there children exist");
        } catch (ConstraintViolationException e) {
            tx.rollback();
            AssertMore.assertContainsTemplate(e,
                ValidContainerTypeValidator.ILLEGAL_CHANGE);
        }
    }

}
