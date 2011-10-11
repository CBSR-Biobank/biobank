package edu.ualberta.med.biobank.mvp.user.ui;

import java.util.Collection;

import com.google.gwt.user.client.ui.HasValue;

public interface HasSelectedValues<T> extends HasValue<Collection<T>>,
    HasOptions<T> {
}
