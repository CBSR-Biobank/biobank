package edu.ualberta.med.biobank.dialogs.user;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang.StringUtils;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;

public class TmpUtil {
    private static final I18n i18n = I18nFactory.getI18n(TmpUtil.class);

    private static BgcLogger logger = BgcLogger.getLogger(TmpUtil.class.getName());

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

    @SuppressWarnings("nls")
    public static void displayException(Throwable t) {
        String message = t.getMessage();

        Throwable cause = t;
        while (cause != null && cause != cause.getCause()) {
            if (cause instanceof ConstraintViolationException) {
                List<String> messages = getMessages((ConstraintViolationException) cause);
                message = StringUtils.join(messages, ";");
                break;
            }
            cause = cause.getCause();
        }

        BgcPlugin.openAsyncError(
            // TR: error dialog title
            i18n.tr("Unable to Save User"),
            message);
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        logger.error(sw.toString());
    }
}
