package edu.ualberta.med.biobank.mvp.validation;

import java.util.LinkedHashMap;

import com.google.gwt.user.client.ui.HasValue;

class ValueValidation<T> extends AbstractValidation {
    private final LinkedHashMap<Validator<? super T>, HasValue<Boolean>> validators =
        new LinkedHashMap<Validator<? super T>, HasValue<Boolean>>();
    private final HasValue<T> value;

    ValueValidation(HasValue<T> value) {
        this.value = value;
    }

    public void put(Validator<? super T> validator, HasValue<Boolean> condition) {
        validators.put(validator, condition);
    }

    @Override
    protected void doValidation(ValidationResultCollector collector) {
    }

    public static class ValueValidationBuilder<T> {
        public ValueValidationBuilder() {

        }

        public ConditionBuilder using(Validator<? super T> vaidator) {

            return new ConditionBuilder();
        }
    }
}
