package edu.ualberta.med.biobank.model;

import javax.validation.ConstraintViolationException;

import junit.framework.Assert;

import org.junit.Test;

import edu.ualberta.med.biobank.model.study.CollectionEvent;
import edu.ualberta.med.biobank.model.study.Patient;
import edu.ualberta.med.biobank.AssertConstraintViolation;
import edu.ualberta.med.biobank.DbTest;
import edu.ualberta.med.biobank.validator.constraint.Empty;
import edu.ualberta.med.biobank.validator.constraint.Unique;

public class TestPatient extends DbTest {
    @Test
    public void duplicatePnumber() {
        Patient p1 = factory.createPatient();
        Patient p2 = factory.createPatient();
        p2.setPnumber(p1.getPnumber());

        try {
            session.update(p2);
            session.flush();
            Assert.fail("cannot have two patients with the same pnumber");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation().withAnnotationClass(Unique.class)
                .withAttr("properties", new String[] { "pnumber" })
                .withRootBean(p2)
                .assertIn(e);
        }
    }

    @Test
    public void deleteWithCollectionEvents() {
        CollectionEvent ce = factory.createCollectionEvent();
        Patient patient = ce.getPatient();

        try {
            session.delete(patient);
            session.flush();
            Assert.fail("cannot have two patients with the same pnumber");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation().withAnnotationClass(Empty.class)
                .withAttr("property", "collectionEvents")
                .withRootBean(patient)
                .assertIn(e);
        }
    }
}
