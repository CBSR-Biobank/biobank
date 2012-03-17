package edu.ualberta.med.biobank.test.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import junit.framework.Assert;

public class ErrorUtil {
    public static void assertContainsTemplate(ConstraintViolationException e,
        String messageTemplate) {
        Collection<String> messageTemplates = new ArrayList<String>();
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            messageTemplates.add(violation.getMessageTemplate());
        }
        if (!messageTemplates.contains(messageTemplate)) {
            Assert.fail(ConstraintViolationException.class.getSimpleName()
                + " does not contain an expected "
                + ConstraintViolation.class.getSimpleName()
                + " with a message template of " + messageTemplate
                + ". Instead, it contains the message template(s): "
                + Arrays.toString(messageTemplates.toArray()));
        }
    }
}
