package edu.ualberta.med.biobank.test.model;

import javax.validation.ConstraintViolationException;

import junit.framework.Assert;

import org.hibernate.Transaction;
import org.junit.Test;

import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.test.AssertConstraintViolation;
import edu.ualberta.med.biobank.test.DbTest;
import edu.ualberta.med.biobank.validator.constraint.NotUsed;

public class TestClinic extends DbTest {
    @Test
    public void deleteWithContactWithoutStudies() {
        Assert.fail("not sure what the expected behavior is");
        // currently will fail because of contacts, options may be:
        // (1) cascade delete the contacts?
        // (2) don't allow it?
    }

    @Test
    public void deleteWithContactWithStudies() {
        Transaction tx = session.beginTransaction();

        Contact contact = factory.createContact();
        Study study = factory.createStudy();
        study.getContacts().add(contact);
        session.update(study);
        session.flush();

        try {
            session.delete(contact.getClinic());
            tx.commit();
            Assert.fail("cannot delete a clinic with contacts with a study");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation().withAnnotationClass(NotUsed.class)
                .withAttr("by", Study.class)
                .withAttr("property", "contacts.clinic")
                .assertIn(e);
        }
    }
}
