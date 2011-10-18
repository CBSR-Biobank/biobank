package edu.ualberta.med.biobank.mvp.validation;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

import edu.ualberta.med.biobank.mvp.event.HasValidationHandlers;

public class Validation {
    private final HandlerManager handlerManager = new HandlerManager(this);

    public <T> void validate(final HasValue<T> value,
        final Validator<T> validator) {

    }

    public void watch(HasValidationHandlers validatable) {
        // TODO: make a map of validatables to their validations results, roll
        // them up on an update and fire all to the observers.
    }

    // TODO: Map of HasValue to list of validators for the value, and a handler
    // that runs through all the validators when the value is changed. Then
    // aggregate validation results from all sources (values, and views?)
    private static class ValidatedValue<T> {
        private final HasValue<T> value;
        private final ValidationResultImpl validationResultImpl =
            new ValidationResultImpl();
        private final List<Validator<T>> validators =
            new ArrayList<Validator<T>>();
        private final ValueChangeHandler<T> valueChangeHandler =
            new ValueChangeHandler<T>() {
                @Override
                public void onValueChange(ValueChangeEvent<T> event) {
                    validationResultImpl.clear();
                    for (Validator<T> validator : validators) {
                        validator.validate(value.getValue(),
                            validationResultImpl);
                    }
                }
            };

        public ValidatedValue(HasValue<T> value) {
            this.value = value;
        }

        public void addValidator(Validator<T> validator) {
            validators.add(validator);
        }
    }

    private static class Validatables {
    }

}
