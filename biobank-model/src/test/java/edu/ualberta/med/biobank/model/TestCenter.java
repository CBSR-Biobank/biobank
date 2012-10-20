package edu.ualberta.med.biobank.model;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotNull;

import junit.framework.Assert;

import org.hibernate.Transaction;
import org.junit.Test;

import edu.ualberta.med.biobank.AssertConstraintViolation;
import edu.ualberta.med.biobank.DbTest;
import edu.ualberta.med.biobank.model.center.Center;
import edu.ualberta.med.biobank.model.util.HasXHelper;
import edu.ualberta.med.biobank.validator.constraint.Empty;

public class TestCenter extends DbTest {
    @Test
    public void emptyName() {
        HasXHelper.checkEmptyName(session, factory.createCenter());
    }

    @Test
    public void duplicateName() {
        HasXHelper.checkDuplicateName(session,
            factory.createCenter(),
            factory.createCenter());
    }

    @Test
    public void emptyNameShort() {
        HasXHelper.checkEmptyNameShort(session, factory.createCenter());
    }

    @Test
    public void duplicateNameShort() {
        HasXHelper.checkDuplicateNameShort(session,
            factory.createCenter(),
            factory.createCenter());
    }

    @Test
    public void nullActivityStatus() {
        HasXHelper.checkNullActivityStatus(session, factory.createCenter());
    }

    @Test
    public void expectedActivityStatusIds() {
        HasXHelper.checkExpectedActivityStatusIds(session,
            factory.createCenter());
    }

    @Test
    public void nullAddress() {
        Center center = factory.createCenter();

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

        Center sender = factory.createCenter();
        Center receiver = factory.createCenter();
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

        Center sender = factory.createCenter();
        Center receiver = factory.createCenter();
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
