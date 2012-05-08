package edu.ualberta.med.biobank.test.model;

import javax.validation.ConstraintViolationException;

import junit.framework.Assert;

import org.hibernate.validator.constraints.NotEmpty;
import org.junit.Test;

import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.test.AssertConstraintViolation;
import edu.ualberta.med.biobank.test.DbTest;

public class TestAddress extends DbTest {
    @Test
    public void nullCity() {
        Address address = new Address();

        try {
            session.save(address);
            session.flush();
            Assert.fail("city should be not null");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation().withAnnotationClass(NotEmpty.class)
                .withRootBean(address)
                .withPropertyPath("city")
                .assertIn(e);
        }
    }

    @Test
    public void emptyCity() {
        Address address = new Address();
        address.setCity("");

        try {
            session.save(address);
            session.flush();
            Assert.fail("city should be not null");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation().withAnnotationClass(NotEmpty.class)
                .withRootBean(address)
                .withPropertyPath("city")
                .assertIn(e);
        }
    }
}
