package edu.ualberta.med.biobank.i18n;

import java.io.Serializable;

public interface L10nedMessage extends Serializable {
    public String getMessage();

    public Object getKey();
}
