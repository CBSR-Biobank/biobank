package edu.ualberta.med.biobank.common.action.exception;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.LocalizedException;

public class AccessDeniedException extends LocalizedException {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    private static final LString MESSAGE = bundle.tr("Access Denied").format();

    public AccessDeniedException() {
        super(MESSAGE);
    }
}
