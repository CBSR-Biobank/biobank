package edu.ualberta.med.biobank.mvp.presenter.validation;

import com.pietschy.gwt.pectin.client.form.validation.ValidationResult;
import com.pietschy.gwt.pectin.client.form.validation.ValidationResultCollector;
import com.pietschy.gwt.pectin.client.form.validation.ValidationResultImpl;
import com.pietschy.gwt.pectin.client.form.validation.Validator;
import com.pietschy.gwt.pectin.client.form.validation.message.ValidationMessage;

/**
 * Remembers the {@link ValidationResult} every time time
 * {@link Validator#validate(Object, ValidationResultCollector)} is called.
 * 
 * @author jferland
 * 
 * @param <T>
 */
class CachedValidator<T> implements Validator<T> {
    private final Validator<? super T> validator;
    private ValidationResultImpl result = new ValidationResultImpl();

    CachedValidator(Validator<? super T> validator) {
        this.validator = validator;
    }

    public ValidationResultImpl getValidationResult() {
        return result;
    }

    @Override
    public void validate(T value, ValidationResultCollector results) {
        ValidationResultImpl result = new ValidationResultImpl();

        validator.validate(value, result);

        for (ValidationMessage message : result.getMessages()) {
            results.add(message);
        }

        this.result = result;
    }

}
