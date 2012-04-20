package edu.ualberta.med.biobank.model.i18n;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.Trnc;

@SuppressWarnings("nls")
public class ContactI18n {
    private static final Bundle bundle = new CommonBundle();

    public static final Trnc NAME = bundle.trnc(
        "model",
        "Contact",
        "Contacts");

    public static class Property {
        public static final LString CLINIC = bundle.trc(
            "model",
            "Clinic").format();
        public static final LString EMAIL_ADDRESS = bundle.trc(
            "model",
            "Email Address").format();
        public static final LString FAX_NUMBER = bundle.trc(
            "model",
            "Fax Number").format();
        public static final LString TITLE = bundle.trc(
            "model",
            "Title").format();
        public static final LString MOBILE_NUMBER = bundle.trc(
            "model",
            "Mobile Number").format();
        public static final LString NAME = bundle.trc(
            "model",
            "Name").format();
        public static final LString OFFICE_NUMBER = bundle.trc(
            "model",
            "Office Number").format();
        public static final LString PAGER_NUMBER = bundle.trc(
            "model",
            "Pager Number").format();
    }
}
