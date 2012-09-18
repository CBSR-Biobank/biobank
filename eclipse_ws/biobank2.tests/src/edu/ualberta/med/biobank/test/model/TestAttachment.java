package edu.ualberta.med.biobank.test.model;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotNull;

import junit.framework.Assert;

import org.junit.Test;

import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.test.AssertConstraintViolation;
import edu.ualberta.med.biobank.test.TestDb;

public class TestAttachment extends TestDb {
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
}
