package edu.ualberta.med.biobank.mvp.validation;

import java.util.LinkedHashMap;

import com.google.gwt.user.client.ui.HasValue;

public class ConditionalValidation {
    private final LinkedHashMap<HasValue<Boolean>, HasValidation> validations =
        new LinkedHashMap<HasValue<Boolean>, HasValidation>();

    public void addValidation(HasValidation validation,
        HasValue<Boolean> condition) {

    }
}
