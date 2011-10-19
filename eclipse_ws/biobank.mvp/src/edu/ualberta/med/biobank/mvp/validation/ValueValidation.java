package edu.ualberta.med.biobank.mvp.validation;

import java.util.LinkedHashMap;

import com.google.gwt.user.client.ui.HasValue;

public class ValueValidation<T> extends AbstractValidation {
    private final LinkedHashMap<Validator<? super T>, HasValue<Boolean>> validators =
        new LinkedHashMap<Validator<? super T>, HasValue<Boolean>>();
    private final HasValue<T> value;

    public ValueValidation(HasValue<T> value) {
        this.value = value;
    }

    public void put(Validator<? super T> validator, HasValue<Boolean> condition) {
        validators.put(validator, condition);
    }

    @Override
    protected void doValidation(ValidationResultCollector collector) {
    }
}
