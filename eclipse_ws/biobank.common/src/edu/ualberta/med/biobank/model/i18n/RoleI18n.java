package edu.ualberta.med.biobank.model.i18n;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.Trn;

@SuppressWarnings("nls")
// @formatter:off
public class RoleI18n {
    private static final Bundle bundle = new CommonBundle();

    public static final Trn NAME = bundle.trn("Role", "Roles");

    public static class Property {
        public static final LString NAME = bundle.trc("Role Property", "Name").format();
    }
}
