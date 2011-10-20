package edu.ualberta.med.biobank.mvp.user.ui;

import com.google.gwt.user.client.ui.HasValue;

import edu.ualberta.med.biobank.mvp.validation.HasValidation;

public interface HasModelValue<T> extends HasValue<T>, HasValidation {
    HasValue<T> getDefaultValue();

    HasValue<Boolean> isDirty();
}
