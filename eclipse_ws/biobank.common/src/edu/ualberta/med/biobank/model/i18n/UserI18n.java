package edu.ualberta.med.biobank.model.i18n;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.Trn;

@SuppressWarnings("nls")
// @formatter:off
public class UserI18n {
    private static final Bundle bundle = new CommonBundle();

    public static final Trn NAME = bundle.trn("User", "Users");

    public static class Property {
        public static final LString EMAIL_ADDRESS = bundle.trc("User Property",         "Email").format();
        public static final LString FULL_NAME = bundle.trc("User Property",             "Full Name").format();
        public static final LString LOGIN = bundle.trc("User Property",                 "Login").format();
        public static final LString RECEIVE_BULK_EMAILS = bundle.trc("User Property",   "Receive bulk emails").format();
    }
}
