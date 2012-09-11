package edu.ualberta.med.biobank.test.model;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotNull;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.test.AssertConstraintViolation;
import edu.ualberta.med.biobank.test.TestDb;
import edu.ualberta.med.biobank.test.model.util.HasXHelper;

public class TestAliquotedSpecimen extends TestDb {
    @Test
    public void nullActivityStatus() {
        HasXHelper.checkNullActivityStatus(session,
            factory.createAliquotedSpecimen());
    }

    @Test
    public void expectedActivityStatusIds() {
        HasXHelper.checkExpectedActivityStatusIds(session,
            factory.createAliquotedSpecimen());
    }

    @Ignore
    @Test
    public void volumeRange() {
        // TODO: try saving values out of range of the BigDecimal
    }

    @Test
    public void nullSpecimenType() {
        AliquotedSpecimen as = factory.createAliquotedSpecimen();
        as.setSpecimenType(null);

        try {
            session.update(as);
            session.flush();
            Assert.fail("specimen type should not allow null");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation().withAnnotationClass(NotNull.class)
                .withRootBean(as)
                .withPropertyPath("specimenType")
                .assertIn(e);
        }
    }

    @Test
    public void nullStudy() {
        AliquotedSpecimen as = factory.createAliquotedSpecimen();
        as.setStudy(null);

        try {
            session.update(as);
            session.flush();
            Assert.fail("study should not allow null");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation().withAnnotationClass(NotNull.class)
                .withRootBean(as)
                .withPropertyPath("study")
                .assertIn(e);
        }
    }
}
