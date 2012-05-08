package edu.ualberta.med.biobank.test.model;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotNull;

import junit.framework.Assert;

import org.hibernate.Transaction;
import org.junit.Test;

import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.test.AssertConstraintViolation;
import edu.ualberta.med.biobank.test.DbTest;
import edu.ualberta.med.biobank.test.model.util.HasXHelper;
import edu.ualberta.med.biobank.validator.constraint.Empty;

public class TestCenter extends DbTest {
    @Test
    public void emptyName() {
        HasXHelper.checkEmptyName(session, factory.createSite());
    }

    @Test
    public void duplicateName() {
        HasXHelper.checkDuplicateName(session,
            factory.createSite(),
            factory.createSite());
    }

    @Test
    public void emptyNameShort() {
        HasXHelper.checkEmptyNameShort(session, factory.createSite());
    }

    @Test
    public void duplicateNameShort() {
        HasXHelper.checkDuplicateNameShort(session,
            factory.createSite(),
            factory.createSite());
    }

    @Test
    public void nullActivityStatus() {
        HasXHelper.checkNullActivityStatus(session, factory.createSite());
    }

    @Test
    public void expectedActivityStatusIds() {
        HasXHelper.checkExpectedActivityStatusIds(session,
            factory.createSite());
    }

    @Test
    public void nullAddress() {
        Center center = factory.createSite();

        try {
            center.setAddress(null);
            session.update(center);
            session.flush();
            Assert.fail("null address should not be allowed");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation().withAnnotationClass(NotNull.class)
                .withRootBean(center)
                .withPropertyPath("address")
                .assertIn(e);
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
            new AssertConstraintViolation().withAnnotationClass(Empty.class)
                .withAttr("property", "srcDispatches")
                .assertIn(e);
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
            new AssertConstraintViolation().withAnnotationClass(Empty.class)
                .withAttr("property", "dstDispatches")
                .assertIn(e);
        }
    }
}
