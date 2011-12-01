package edu.ualberta.med.biobank.mvp.presenter.validation;

import com.pietschy.gwt.pectin.client.form.validation.ValidationEvent;
import com.pietschy.gwt.pectin.client.form.validation.ValidationHandler;
import com.pietschy.gwt.pectin.client.form.validation.ValidationResult;
import com.pietschy.gwt.pectin.client.form.validation.component.ValidationDisplay;

/**
 * Whenever we (the {@link ValueValidation} change, notify a
 * {@link ValidationDisplay} of the new {@link ValidationResult}.
 * 
 * @author jferland
 * 
 */
class ValidationBinding implements ValidationHandler {
    private final ValidationDisplay validationDisplay;

    public ValidationBinding(ValidationDisplay validationDisplay) {
        this.validationDisplay = validationDisplay;
    }

    @Override
    public void onValidate(ValidationEvent event) {
        ValidationResult result = event.getValidationResult();
        validationDisplay.setValidationResult(result);
    }
}