package edu.ualberta.med.biobank.test.validation;

import javax.validation.ConstraintViolationException;

import junit.framework.Assert;

import org.hibernate.Transaction;
import org.junit.Test;

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
            ErrorUtil.assertContainsTemplate(e,
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
            ErrorUtil.assertContainsTemplate(e,
                ValidContainerTypeValidator.ILLEGAL_ST_REMOVE);
        }
    }

    @Test
    public void containerTypeContainerTypeSiteMismatch() {
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
            ErrorUtil.assertMessageContains(e.getCause(),
                "FK_ContainerType_parentContainerTypes");
            tx.rollback();
        }
    }

    @Test
    public void containerTypeContainerSiteMismatch() {
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
            ErrorUtil.assertMessageContains(e.getCause(),
                "FK_Container_containerType");
            tx.rollback();
        }
    }

    @Test
    public void illegalChildContainer() {
        Transaction tx = session.beginTransaction();

        
    }

    @Test
    public void illegalChildSpecimen() {
    }

    @Test
    public void changeCapacity() {
        // capacity cannot be changed after on a container type once containers
        // of this type have been created

    }

    @Test
    public void changeTopLevel() {
        // top level cannot be changed after on a container type once containers
        // of this type have been created

    }

    @Test
    public void changeLabellingScheme() {
        // labeling scheme cannot be changed after on a container type once
        // containers of this type have been created

    }
}
