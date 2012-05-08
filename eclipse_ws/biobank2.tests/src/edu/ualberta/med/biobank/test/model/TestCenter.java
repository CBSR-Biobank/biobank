package edu.ualberta.med.biobank.test.model;

import java.util.HashMap;
import java.util.Map;

import javax.validation.ConstraintViolationException;

import junit.framework.Assert;

import org.hibernate.Transaction;
import org.junit.Test;

import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.test.AssertMore;
import edu.ualberta.med.biobank.test.DbTest;
import edu.ualberta.med.biobank.validator.constraint.Unique;

public class TestCenter extends DbTest {
    @Test
    public void duplicateName() {
        Transaction tx = session.beginTransaction();

        Center original = factory.createSite();
        Center duplicate = factory.createSite();

        duplicate.setName(original.getName());

        try {
            session.update(duplicate);
            tx.commit();
            Assert.fail("cannot have two centers with the same name");
        } catch (ConstraintViolationException e) {
            tx.rollback();

            Map<String, Object> attributes = new HashMap<String, Object>();
            attributes.put("properties", new String[] { "name" });

            AssertMore.assertContainsAnnotation(e, Unique.class, attributes);
        }
    }

    @Test
    public void duplicateNameShort() {
        Transaction tx = session.beginTransaction();

        Center original = factory.createSite();
        Center duplicate = factory.createSite();

        duplicate.setNameShort(original.getNameShort());

        try {
            session.update(duplicate);
            tx.commit();
            Assert.fail("cannot have two centers with the same nameShort");
        } catch (ConstraintViolationException e) {
            tx.rollback();

            Map<String, Object> attributes = new HashMap<String, Object>();
            attributes.put("properties", new String[] { "nameShort" });

            AssertMore.assertContainsAnnotation(e, Unique.class, attributes);
        }
    }

    @Test
    public void deleteWithSrcDispatches() {
    }

    @Test
    public void deleteWithDstDispatches() {
    }
}
