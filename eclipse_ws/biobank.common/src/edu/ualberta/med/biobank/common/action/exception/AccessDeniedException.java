package edu.ualberta.med.biobank.common.action.exception;

import edu.ualberta.med.biobank.i18n.S;

public class AccessDeniedException extends ActionException {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    public AccessDeniedException() {
        super(S.tr("Access Denied"));
    }
}
