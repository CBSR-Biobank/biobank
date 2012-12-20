package edu.ualberta.med.biobank.model;

import edu.ualberta.med.biobank.i18n.Bundle;

// FIXME: rename this class to ModelBundle?
// also other projects should not use this class but create their own
public class CommonBundle extends Bundle {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    public CommonBundle() {
        super("i18n.Messages");
    }
}
