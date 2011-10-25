package edu.ualberta.med.biobank.mvp.validation;

import java.util.LinkedHashMap;

import com.google.gwt.user.client.ui.HasValue;

public class AbstractTreeValidation<T extends HasValidation> extends
    AbstractValidation {
    private final LinkedHashMap<Validator<? super T>, HasValue<Boolean>> validators =
        new LinkedHashMap<Validator<? super T>, HasValue<Boolean>>();

    @Override
    protected void doValidation(ValidationResultCollector collector) {
        // TODO Auto-generated method stub

    }

}
