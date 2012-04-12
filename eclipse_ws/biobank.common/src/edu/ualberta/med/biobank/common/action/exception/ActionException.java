package edu.ualberta.med.biobank.common.action.exception;

import edu.ualberta.med.biobank.i18n.HasLocalizedString;
import edu.ualberta.med.biobank.i18n.LocalizedString;

/**
 * 
 * @author Jonathan Ferland
 */
public class ActionException extends RuntimeException
    implements HasLocalizedString {
    private static final long serialVersionUID = 1L;

    private final LocalizedString localizedString;

    public ActionException(LocalizedString localizedString) {
        this(localizedString, null);
    }

    public ActionException(LocalizedString localizedString, Throwable cause) {
        super(localizedString.toString(), cause);

        this.localizedString = localizedString;
    }

    @Override
    public LocalizedString getLocalizedString() {
        return localizedString;
    }
}
