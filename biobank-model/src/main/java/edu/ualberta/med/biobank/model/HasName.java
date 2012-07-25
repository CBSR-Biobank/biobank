package edu.ualberta.med.biobank.model;

import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;

public interface HasName {
    public String getName();

    public void setName(String name);

    @SuppressWarnings("nls")
    public static class PropertyName {
        private static final Bundle bundle = new CommonBundle();

        public static final LString NAME = bundle.trc(
            "model",
            "Name").format();
    }
}
