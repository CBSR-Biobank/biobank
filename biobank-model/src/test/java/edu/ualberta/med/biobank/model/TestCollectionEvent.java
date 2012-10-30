package edu.ualberta.med.biobank.model;

import java.text.MessageFormat;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import junit.framework.Assert;

import org.hibernate.Transaction;
import org.junit.Test;

import edu.ualberta.med.biobank.ConstraintViolationAssertion;
import edu.ualberta.med.biobank.DbTest;
import edu.ualberta.med.biobank.model.study.CollectionEvent;
import edu.ualberta.med.biobank.validator.constraint.Empty;
import edu.ualberta.med.biobank.validator.constraint.Unique;

public class TestCollectionEvent extends DbTest {
    @Test
    public void deleteWithSpecimens() {
        Transaction tx = session.beginTransaction();

        // Specimen specimen = factory.createParentSpecimen();

        try {
            // session.delete(specimen.getCollectionEvent());
            tx.commit();
            Assert.fail("cannot delete a collection event with speicmens");
        } catch (ConstraintViolationException e) {
            new ConstraintViolationAssertion().withAnnotationClass(Empty.class)
                .withAttr("property", "allSpecimens")
                .assertIn(e);
        }
    }

    @Test
    public void nullVisitNumber() {
        CollectionEvent ce = factory.createCollectionEvent();

        try {
            ce.setVisitNumber(null);
            session.update(ce);
            session.flush();
            Assert.fail("null visit number should not be allowed");
        } catch (ConstraintViolationException e) {
            new ConstraintViolationAssertion().withAnnotationClass(NotNull.class)
                .withRootBean(ce)
                .withPropertyPath("visitNumber")
                .assertIn(e);
        }
    }

    @Test
    public void illegalVisitNumber() {
        CollectionEvent ce = factory.createCollectionEvent();

        final Integer[] illegalValues = new Integer[] { -1, 0 };

        for (Integer illegalValue : illegalValues) {
            try {
                ce.setVisitNumber(illegalValue);
                session.update(ce);
                session.flush();
                Assert.fail(MessageFormat.format(
                    "visit number ''{0}''should not be allowed", illegalValue));
            } catch (ConstraintViolationException e) {
                new ConstraintViolationAssertion()
                    .withAnnotationClass(Min.class)
                    .withRootBean(ce)
                    .withPropertyPath("visitNumber")
                    .assertIn(e);
            }
        }
    }

    @Test
    public void duplicateVisitNumberAndPatient() {
        CollectionEvent ce1 = factory.createCollectionEvent();
        CollectionEvent ce2 = factory.createCollectionEvent();

        Assert.assertSame("must have the same patients",
            ce1.getPatient(), ce2.getPatient());

        try {
            ce2.setVisitNumber(ce1.getVisitNumber());
            session.update(ce2);
            session.flush();
            Assert.fail("cannot have duplicate visit number for same patient");
        } catch (ConstraintViolationException e) {
            new ConstraintViolationAssertion()
                .withAnnotationClass(Unique.class)
                .withAttr("properties",
                    new String[] { "patient", "visitNumber" })
                .assertIn(e);
        }
    }

    @Test
    public void duplicateVisitNumberDifferentPatient() {
        CollectionEvent ce1 = factory.createCollectionEvent();
        factory.createPatient();
        CollectionEvent ce2 = factory.createCollectionEvent();

        Assert.assertNotSame("must have different patients",
            ce1.getPatient(), ce2.getPatient());

        ce2.setVisitNumber(ce1.getVisitNumber());
        session.update(ce2);
        session.flush();
    }
}
