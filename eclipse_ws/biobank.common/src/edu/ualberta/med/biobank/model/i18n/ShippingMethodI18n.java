package edu.ualberta.med.biobank.model.i18n;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.Trnc;

@SuppressWarnings("nls")
public class ShippingMethodI18n {
    private static final Bundle bundle = new CommonBundle();

    public static final Trnc NAME = bundle.trnc(
        "model",
        "Shipping Method",
        "Shipping Methods");

    public static class Property {
    }
}
