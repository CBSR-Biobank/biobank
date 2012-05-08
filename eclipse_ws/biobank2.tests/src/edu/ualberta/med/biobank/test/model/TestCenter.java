package edu.ualberta.med.biobank.test.model;

import javax.validation.ConstraintViolationException;

import junit.framework.Assert;

import org.hibernate.Transaction;
import org.junit.Test;

import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.test.AssertMore;
import edu.ualberta.med.biobank.test.AssertMore.Attr;
import edu.ualberta.med.biobank.test.DbTest;
import edu.ualberta.med.biobank.test.model.util.HasXHelper;
import edu.ualberta.med.biobank.validator.constraint.Empty;

public class TestCenter extends DbTest {
    @Test
    public void duplicateName() {
        HasXHelper.checkDuplicateName(session,
            factory.createSite(),
            factory.createSite());
    }

    @Test
    public void duplicateNameShort() {
        HasXHelper.checkDuplicateNameShort(session,
            factory.createSite(),
            factory.createSite());
    }

    @Test
    public void expectedActivityStatusIds() {
        HasXHelper.checkExpectedActivityStatusIds(session,
            factory.createSite());
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
