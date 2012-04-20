package edu.ualberta.med.biobank.model.i18n;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.Trn;

@SuppressWarnings("nls")
// @formatter:off
public class AbstractPositionI18n {
    private static final Bundle bundle = new CommonBundle();

    public static final Trn NAME = bundle.trn("Abstract Position", "Abstract Positions");

    public static class Property {
        public static final LString ROW = bundle.trc("AbstractPosition Property",       "Row").format();
        public static final LString COLUMN = bundle.trc("AbstractPosition Property",    "Column").format();
    }
}
