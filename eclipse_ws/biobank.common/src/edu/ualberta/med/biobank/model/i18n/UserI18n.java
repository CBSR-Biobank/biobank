package edu.ualberta.med.biobank.model.i18n;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.Trnc;

@SuppressWarnings("nls")
public class UserI18n {
    private static final Bundle bundle = new CommonBundle();

    public static final Trnc NAME = bundle.trnc(
        "model",
        "User",
        "Users");

    public static class Property {
        public static final LString EMAIL_ADDRESS = bundle.trc(
            "model",
            "Email").format();
        public static final LString FULL_NAME = bundle.trc(
            "model",
            "Full Name").format();
        public static final LString LOGIN = bundle.trc(
            "model",
            "Login").format();
        public static final LString RECEIVE_BULK_EMAILS = bundle.trc(
            "model",
            "Receive bulk emails").format();
    }
}
