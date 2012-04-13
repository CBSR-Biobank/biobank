package edu.ualberta.med.biobank.common.action.exception;

import edu.ualberta.med.biobank.i18n.LocalizedException;
import edu.ualberta.med.biobank.i18n.LocalizedString;

/**
 * 
 * @author Jonathan Ferland
 */
public class ActionException extends LocalizedException {
    private static final long serialVersionUID = 1L;

    public ActionException(LocalizedString localizedString) {
        super(localizedString);
    }

    public ActionException(LocalizedString localizedString, Throwable cause) {
        super(localizedString, cause);
    }
}
