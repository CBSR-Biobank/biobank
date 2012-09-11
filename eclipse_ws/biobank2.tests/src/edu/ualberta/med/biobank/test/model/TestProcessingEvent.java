package edu.ualberta.med.biobank.test.model;

import javax.validation.ConstraintViolationException;

import junit.framework.Assert;

import org.junit.Test;

import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.test.AssertConstraintViolation;
import edu.ualberta.med.biobank.test.TestDb;
import edu.ualberta.med.biobank.test.model.util.HasXHelper;
import edu.ualberta.med.biobank.validator.constraint.Unique;

public class TestProcessingEvent extends TestDb {
    @Test
    public void expectedActivityStatusIds() {
        HasXHelper.checkExpectedActivityStatusIds(session,
            factory.createProcessingEvent());
    }

    @Test
    public void duplicateWorksheet() {
        ProcessingEvent pe1 = factory.createProcessingEvent();
        ProcessingEvent pe2 = factory.createProcessingEvent();
        pe2.setWorksheet(pe1.getWorksheet());

        try {
            session.update(pe2);
            session.flush();
            Assert.fail("processing event worksheet should be unique");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation().withAnnotationClass(Unique.class)
                .withAttr("properties", new String[] { "worksheet" })
                .withRootBean(pe2)
                .assertIn(e);
        }
    }
}
