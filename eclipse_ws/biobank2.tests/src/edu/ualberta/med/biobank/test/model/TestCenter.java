package edu.ualberta.med.biobank.test.model;

import javax.validation.ConstraintViolationException;

import junit.framework.Assert;

import org.hibernate.Transaction;
import org.junit.Test;

import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.test.AssertMore;
import edu.ualberta.med.biobank.test.AssertMore.Attr;
import edu.ualberta.med.biobank.test.DbTest;
import edu.ualberta.med.biobank.validator.constraint.Empty;
import edu.ualberta.med.biobank.validator.constraint.Unique;

public class TestCenter extends DbTest {
    @Test
    public void duplicateName() {
        Transaction tx = session.beginTransaction();

        Center original = factory.createSite();
        Center duplicate = factory.createSite();

        duplicate.setName(original.getName());

        try {
            session.update(duplicate);
            tx.commit();
            Assert.fail("cannot have two centers with the same name");
        } catch (ConstraintViolationException e) {
            tx.rollback();
            AssertMore.assertContainsAnnotation(e, Unique.class,
                new Attr("properties", new String[] { "name" }));
        }
    }

    @Test
    public void duplicateNameShort() {
        Transaction tx = session.beginTransaction();

        Center original = factory.createSite();
        Center duplicate = factory.createSite();

        duplicate.setNameShort(original.getNameShort());

        try {
            session.update(duplicate);
            tx.commit();
            Assert.fail("cannot have two centers with the same nameShort");
        } catch (ConstraintViolationException e) {
            tx.rollback();
            AssertMore.assertContainsAnnotation(e, Unique.class,
                new Attr("properties", new String[] { "nameShort" }));
        }
    }

    @Test
    public void deleteWithSrcDispatches() {
        Transaction tx = session.beginTransaction();

        Center sender = factory.createSite();
        Center receiver = factory.createSite();
        factory.createDispatch(sender, receiver);

        try {
            session.delete(sender);
            tx.commit();
            Assert.fail("cannot delete a center with srcDispatches");
        } catch (ConstraintViolationException e) {
            tx.rollback();
            AssertMore.assertContainsAnnotation(e, Empty.class,
                new Attr("property", "srcDispatches"));
        }
    }

    @Test
    public void deleteWithDstDispatches() {
        Transaction tx = session.beginTransaction();

        Center sender = factory.createSite();
        Center receiver = factory.createSite();
        factory.createDispatch(sender, receiver);

        try {
            session.delete(receiver);
            tx.commit();
            Assert.fail("cannot delete a center with dstDispatches");
        } catch (ConstraintViolationException e) {
            tx.rollback();
            AssertMore.assertContainsAnnotation(e, Empty.class,
                new Attr("property", "dstDispatches"));
        }
    }
}
