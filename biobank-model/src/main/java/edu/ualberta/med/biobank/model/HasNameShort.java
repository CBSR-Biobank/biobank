package edu.ualberta.med.biobank.model;

import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;

public interface HasNameShort {
    public String getNameShort();

    public void setNameShort(String nameShort);

    @SuppressWarnings("nls")
    public static class PropertyName {
        private static final Bundle bundle = new CommonBundle();

        public static final LString NAME_SHORT = bundle.trc(
            "model",
            "Short Name").format();
    }
}
