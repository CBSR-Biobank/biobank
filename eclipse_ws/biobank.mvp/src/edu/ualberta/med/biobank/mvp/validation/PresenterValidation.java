package edu.ualberta.med.biobank.mvp.validation;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gwt.user.client.ui.HasValue;

public class PresenterValidation extends AbstractValidation {
    private final LinkedHashMap<PresenterValidation, HasValue<Boolean>> validators =
        new LinkedHashMap<PresenterValidation, HasValue<Boolean>>();

    @Override
    protected void doValidation(ValidationResultCollector collector) {
        for (Map.Entry<PresenterValidation, HasValue<Boolean>> entry : validators
            .entrySet()) {
            HasValidation validator = entry.getKey();
        }
    }

    public void validate(HasValidation validator) {

    }

    public class ValueValidation<T> extends AbstractValidation {
        private final LinkedHashMap<Validator<? super T>, HasValue<Boolean>> validators =
            new LinkedHashMap<Validator<? super T>, HasValue<Boolean>>();
        private final HasValue<T> value;

        public ValueValidation(HasValue<T> value) {
            this.value = value;
        }

        public void put(Validator<? super T> validator,
            HasValue<Boolean> condition) {
            validators.put(validator, condition);
        }

        @Override
        protected void doValidation(ValidationResultCollector collector) {
        }
    }
}
