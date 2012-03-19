package edu.ualberta.med.biobank.test.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import junit.framework.Assert;

public class AssertMore {
    public static void assertContainsTemplate(ConstraintViolationException e,
        String messageTemplate) {
        Collection<String> messageTemplates = new ArrayList<String>();
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            messageTemplates.add(violation.getMessageTemplate());
        }
        if (!messageTemplates.contains(messageTemplate)) {
            Assert.fail(ConstraintViolationException.class.getSimpleName()
                + " does not contain an expected "
                + ConstraintViolation.class.getName()
                + " with a message template of " + messageTemplate
                + ". Instead, it contains the message template(s): "
                + Arrays.toString(messageTemplates.toArray()));
        }
    }

    public static void assertMessageContains(Throwable t, String substring) {
        if (!t.getMessage().contains(substring)) {
            Assert.fail("Expected exception " + t.getClass().getName()
                + "' to contain the substring '" + substring
                + "' but instead got message: '" + t.getMessage() + "'");
        }
    }
}
