package edu.ualberta.med.biobank.test.model;

import javax.validation.ConstraintViolationException;

import junit.framework.Assert;

import org.hibernate.Transaction;
import org.junit.Test;

import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.test.AssertConstraintViolation;
import edu.ualberta.med.biobank.test.DbTest;
import edu.ualberta.med.biobank.test.model.util.HasXHelper;
import edu.ualberta.med.biobank.validator.constraint.Empty;
import edu.ualberta.med.biobank.validator.constraint.Unique;

public class TestCollectionEvent extends DbTest {
    @Test
    public void expectedActivityStatusIds() {
        HasXHelper.checkExpectedActivityStatusIds(session,
            factory.createCollectionEvent());
    }

    @Test
    public void deleteWithSpecimens() {
        Transaction tx = session.beginTransaction();

        Specimen specimen = factory.createSpecimen();

        try {
            session.delete(specimen.getCollectionEvent());
            tx.commit();
            Assert.fail("cannot delete a collection event with speicmens");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation().withAnnotationClass(Empty.class)
                .withAttr("property", "allSpecimens")
                .assertIn(e);
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
            new AssertConstraintViolation()
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
