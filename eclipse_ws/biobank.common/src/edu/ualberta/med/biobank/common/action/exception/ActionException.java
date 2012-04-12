package edu.ualberta.med.biobank.common.action.exception;

import edu.ualberta.med.biobank.i18n.HasLocalizedMessage;
import edu.ualberta.med.biobank.i18n.S;

/**
 * 
 * @author Jonathan Ferland
 */
public class ActionException extends RuntimeException
    implements HasLocalizedMessage {
    private static final long serialVersionUID = 1L;

    private final S string;

    public ActionException(S string) {
        this(string, null);
    }

    public ActionException(S string, Throwable cause) {
        super(string.toString(), cause);

        this.string = string;
    }

    @Override
    public String getLocalizedMessage() {
        return string.toString();
    }
}
