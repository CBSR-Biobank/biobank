package edu.ualberta.med.biobank.test.validation;

import javax.validation.ConstraintViolationException;

import junit.framework.Assert;

import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.junit.Test;

import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.validator.constraint.model.impl.ValidContainerTypeValidator;

public class TestContainerType extends TestValidation {
    @Test
    public void removeUsedChildContainerType() {
        Transaction tx = session.beginTransaction();

        Factory factory = new Factory(getMethodNameR());

        Site site = factory.createSite();
        session.save(site);

        factory.setDefaultContainerLabelingScheme(getSbs());

        ContainerType ct1 = factory.createContainerType();
        ct1.setTopLevel(true);

        Container c1 = factory.createContainer();

        ContainerType ct2 = factory.createContainerType();
        Container c2 = factory.createContainer();

        ct1.getChildContainerTypes().add(ct2);

        session.save(ct2);
        session.save(ct1);

        session.flush();

        ContainerPosition cp = new ContainerPosition();

        cp.setParentContainer(c1);
        cp.setRow(0);
        cp.setCol(0);
        cp.setContainer(c2);
        c2.setPosition(cp);

        session.save(c1);
        session.save(c2);

        try {
            ct1.getChildContainerTypes().clear();
            session.update(ct1);
            tx.commit();

            Assert.fail("cannot remove child container types in use");
        } catch (ConstraintViolationException e) {
            ErrorUtil.assertContainsTemplate(e,
                ValidContainerTypeValidator.ILLEGAL_CHILD_CT_REMOVE);
        }
    }

    @Test
    public void removeUsedSpecimenType() {
        Transaction tx = session.beginTransaction();

        Factory factory = new Factory(getMethodNameR());

        Site site = factory.createSite();
        session.save(site);

        factory.setDefaultContainerLabelingScheme(getSbs());

        ContainerType ct1 = factory.createContainerType();
        ct1.setTopLevel(true);

        Container c1 = factory.createContainer();

        ContainerType ct2 = factory.createContainerType();
        Container c2 = factory.createContainer();

        ct1.getChildContainerTypes().add(ct2);

        session.save(ct2);
        session.save(ct1);

        session.flush();

        ContainerPosition cp = new ContainerPosition();

        cp.setParentContainer(c1);
        cp.setRow(0);
        cp.setCol(0);
        cp.setContainer(c2);
        c2.setPosition(cp);

        session.save(c1);
        session.save(c2);

        try {
            ct1.getChildContainerTypes().clear();
            session.update(ct1);
            tx.commit();

            Assert.fail("cannot remove child container types in use");
        } catch (ConstraintViolationException e) {
            ErrorUtil.assertContainsTemplate(e,
                ValidContainerTypeValidator.ILLEGAL_CHILD_CT_REMOVE);
        }
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

    private ContainerLabelingScheme getSbs() {
        return (ContainerLabelingScheme) session
            .createCriteria(ContainerLabelingScheme.class)
            .add(Restrictions.idEq(1))
            .uniqueResult();
    }
}
