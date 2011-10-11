package edu.ualberta.med.biobank.mvp.user.ui;

import java.util.Collection;

import com.google.gwt.user.client.ui.HasValue;

public interface HasSelectedValue<T> extends HasValue<T> {
    void setOptions(Collection<T> options);
}
