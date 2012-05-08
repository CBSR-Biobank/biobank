package edu.ualberta.med.biobank.model;

import java.util.Date;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;

public interface HasCreatedAt {
    public Date getCreatedAt();

    public void setCreatedAt(Date createdAt);

    @SuppressWarnings("nls")
    public static class PropertyName {
        private static final Bundle bundle = new CommonBundle();

        public static final LString CREATED_AT = bundle.trc(
            "model",
            "Created At").format();
    }
}
