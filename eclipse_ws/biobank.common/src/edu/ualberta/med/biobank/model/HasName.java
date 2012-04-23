package edu.ualberta.med.biobank.model;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;

public interface HasName {
    public String getName();

    public String getNameShort();

    @SuppressWarnings("nls")
    public static class Property {
        private static final Bundle bundle = new CommonBundle();

        public static final LString NAME = bundle.trc(
            "model",
            "Name").format();

        public static final LString NAME_SHORT = bundle.trc(
            "model",
            "Short Name").format();
    }
}
