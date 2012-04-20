package edu.ualberta.med.biobank.model.i18n;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.Trnc;

@SuppressWarnings("nls")
public class AbstractPositionI18n {
    private static final Bundle bundle = new CommonBundle();

    public static final Trnc NAME = bundle.trnc(
        "model",
        "Abstract Position",
        "Abstract Positions");

    public static class Property {
        public static final LString ROW = bundle.trc(
            "model",
            "Row").format();
        public static final LString COLUMN = bundle.trc(
            "model",
            "Column").format();
    }
}
