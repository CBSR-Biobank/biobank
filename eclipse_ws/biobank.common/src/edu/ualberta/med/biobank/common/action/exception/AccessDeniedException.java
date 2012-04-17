package edu.ualberta.med.biobank.common.action.exception;

import edu.ualberta.med.biobank.i18n.LString;

public class AccessDeniedException extends ActionException {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    public AccessDeniedException() {
        super(LString.tr("Access Denied"));
    }
}
