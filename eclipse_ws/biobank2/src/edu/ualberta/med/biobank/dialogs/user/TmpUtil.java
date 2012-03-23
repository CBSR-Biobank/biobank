package edu.ualberta.med.biobank.dialogs.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang.StringUtils;

import edu.ualberta.med.biobank.gui.common.BgcPlugin;

public class TmpUtil {
    // TODO: time pressure, heh
    public static List<String> getMessages(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        List<String> messages = new ArrayList<String>();

        for (ConstraintViolation<?> violation : violations) {
            String message = violation.getMessage();
            messages.add(message);
        }

        return messages;
    }

    public static void displayException(Throwable t) {
        String message = t.getMessage();

        Throwable cause = t;
        while (cause != null && cause != cause.getCause()) {
            if (cause instanceof ConstraintViolationException) {
                List<String> messages =
                    getMessages((ConstraintViolationException) cause);
                message = StringUtils.join(messages, ";");
                break;
            }
            cause = cause.getCause();
        }

        BgcPlugin.openAsyncError(
            Messages.UserEditDialog_save_error_title, message);
    }
}
