package edu.ualberta.med.biobank.mvp.validation;

import com.google.gwt.user.client.ui.HasValue;

import edu.ualberta.med.biobank.mvp.user.ui.impl.DelegatingHasValue;

public class ValidatedValueBuilder<T> {
    private ValidatedValue<T> validatedValue;
    private DelegatingHasValue<Boolean> delegatingCondition =
        new DelegatingHasValue<Boolean>(true);

    public ValidatedValueBuilder(ValidationManager validationManager,
        HasValue<T> value) {
        validatedValue = validationManager.getValidatedValue(value);
    }

    public DelegatingConditionBuilder using(Validator<? super T> validator,
        Validator<? super T>... others) {
        validatedValue.addValidator(validator, delegatingCondition);

        for (Validator<? super T> other : others) {
            validatedValue.addValidator(other, delegatingCondition);
        }

        return new DelegatingConditionBuilder(delegatingCondition);
    }
}
