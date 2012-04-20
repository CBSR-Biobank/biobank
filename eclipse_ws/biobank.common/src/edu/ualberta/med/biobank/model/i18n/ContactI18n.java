package edu.ualberta.med.biobank.model.i18n;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.Trn;

@SuppressWarnings("nls")
// @formatter:off
public class ContactI18n {
    private static final Bundle bundle = new CommonBundle();

    public static final Trn NAME = bundle.trn("Contact", "Contacts");
    
    public static class Property {
        public static final LString CLINIC = ClinicI18n.NAME.format(1);
        public static final LString EMAIL_ADDRESS = bundle.trc("Contact Property",  "Email Address").format();
        public static final LString FAX_NUMBER = bundle.trc("Contact Property",     "Fax Number").format();
        public static final LString TITLE = bundle.trc("Contact Property",          "Title").format();
        public static final LString MOBILE_NUMBER = bundle.trc("Contact Property",  "Mobile Number").format();
        public static final LString NAME = bundle.trc("Contact Property",           "Name").format();
        public static final LString OFFICE_NUMBER = bundle.trc("Contact Property",  "Office Number").format();
        public static final LString PAGER_NUMBER = bundle.trc("Contact Property",   "Pager Number").format();
    }
}
