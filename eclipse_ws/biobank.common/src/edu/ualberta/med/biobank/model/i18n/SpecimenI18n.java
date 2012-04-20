package edu.ualberta.med.biobank.model.i18n;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.Trnc;

@SuppressWarnings("nls")
public class SpecimenI18n {
    private static final Bundle bundle = new CommonBundle();

    public static final Trnc NAME = bundle.trnc(
        "model",
        "Specimen",
        "Specimens");

    public static class Property {
        public static final LString INVENTORY_ID = bundle.trc(
            "model",
            "Inventory Id").format();
    }
}
