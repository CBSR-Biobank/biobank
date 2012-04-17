package edu.ualberta.med.biobank.common.action.exception;

import edu.ualberta.med.biobank.i18n.LocalizedException;
import edu.ualberta.med.biobank.i18n.LString;

/**
 * 
 * @author Jonathan Ferland
 */
public class ActionException extends LocalizedException {
    private static final long serialVersionUID = 1L;

    public ActionException(LString localizedString) {
        super(localizedString);
    }

    public ActionException(LString localizedString, Throwable cause) {
        super(localizedString, cause);
    }
}
