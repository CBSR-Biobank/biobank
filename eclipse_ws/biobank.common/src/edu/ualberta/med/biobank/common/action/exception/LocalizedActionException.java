package edu.ualberta.med.biobank.common.action.exception;

import edu.ualberta.med.biobank.common.i18n.IFormattable;
import edu.ualberta.med.biobank.common.i18n.LocalizedMessage;

public class LocalizedActionException extends ActionException {
    private static final long serialVersionUID = 1L;

    private final LocalizedMessage message;

    public LocalizedActionException(IFormattable formattable, Object... objects) {
        this.message = new LocalizedMessage(formattable, objects);
    }

    public LocalizedMessage getTheThing() {
        return message;
    }
}
