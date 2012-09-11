package edu.ualberta.med.biobank.test.model;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotNull;

import junit.framework.Assert;

import org.hibernate.validator.constraints.NotEmpty;
import org.junit.Test;

import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.test.AssertConstraintViolation;
import edu.ualberta.med.biobank.test.TestDb;
import edu.ualberta.med.biobank.test.model.util.HasXHelper;

public class TestComment extends TestDb {
    @Test
    public void nullMessage() {
        Comment comment = factory.createComment();
        comment.setMessage(null);

        try {
            session.update(comment);
            session.flush();
            Assert.fail("null message should not be allowed");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation().withAnnotationClass(NotEmpty.class)
                .withRootBean(comment)
                .withPropertyPath("message")
                .assertIn(e);
        }
    }

    @Test
    public void emptyMessage() {
        Comment comment = factory.createComment();
        comment.setMessage("");

        try {
            session.update(comment);
            session.flush();
            Assert.fail("empty message should not be allowed");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation().withAnnotationClass(NotEmpty.class)
                .withRootBean(comment)
                .withPropertyPath("message")
                .assertIn(e);
        }
    }

    @Test
    public void nullCreatedAt() {
        HasXHelper.checkNullCreatedAt(session, factory.createComment());
    }

    @Test
    public void nullUser() {
        Comment comment = factory.createComment();
        comment.setUser(null);

        try {
            session.update(comment);
            session.flush();
            Assert.fail("null user should not be allowed");
        } catch (ConstraintViolationException e) {
            new AssertConstraintViolation().withAnnotationClass(NotNull.class)
                .withRootBean(comment)
                .withPropertyPath("user")
                .assertIn(e);
        }
    }
}
