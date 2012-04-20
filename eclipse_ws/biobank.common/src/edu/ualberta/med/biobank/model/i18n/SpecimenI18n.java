package edu.ualberta.med.biobank.model.i18n;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.Trn;

@SuppressWarnings("nls")
// @formatter:off
public class SpecimenI18n {
    private static final Bundle bundle = new CommonBundle();

    public static final Trn NAME = bundle.trn("Specimen", "Specimens");

    public static class Property {
        public static final LString INVENTORY_ID = bundle.tr("Inventory Id").format();
    }
}
