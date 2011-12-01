package edu.ualberta.med.biobank.mvp.presenter.validation;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.pietschy.gwt.pectin.client.binding.Disposable;
import com.pietschy.gwt.pectin.client.condition.Condition;
import com.pietschy.gwt.pectin.client.form.validation.EmptyValidationResult;
import com.pietschy.gwt.pectin.client.form.validation.HasValidation;
import com.pietschy.gwt.pectin.client.form.validation.ValidationEvent;
import com.pietschy.gwt.pectin.client.form.validation.ValidationHandler;
import com.pietschy.gwt.pectin.client.form.validation.ValidationResult;
import com.pietschy.gwt.pectin.client.form.validation.ValidationResultImpl;

/**
 * A delegating validator, with a condition for the validation result. Useful
 * for conditional validation of a {@link HasValidation}.
 * <p>
 * If the condition is not met, will delay validation until the condition is
 * met. Note that if the delegate has never been
 * {@link HasValidation#validate()}-d, then even if the condition is true, the
 * {@link ValidationResult} will be empty, i.e. condtion changes themselves do
 * not cause validation to be done.
 * <p>
 * Listens for {@link ValidationEvent}-s from the delegated
 * {@link HasValidation} and for {@link ValueChangeEvent}-s from the
 * {@link Condition}. When either event is received, update the internal
 * {@link ValidationResult} (if the condition is met), which will fire another
 * {@link ValidationEvent}, but from this instance.
 * 
 * @author jferland
 * 
 */
class ConditionalValidation extends AbstractValidation implements Disposable {
    private final ConditionMonitor conditionMonitor = new ConditionMonitor();
    private final ValidatorMonitor validatorMonitor = new ValidatorMonitor();
    private final HasValidation delegate;
    private final Condition condition;

    ConditionalValidation(HasValidation delegate, Condition condition) {
        this.delegate = delegate;
        this.condition = condition;

        handlerRegistry.add(condition.addValueChangeHandler(conditionMonitor));
        handlerRegistry.add(delegate.addValidationHandler(validatorMonitor));
    }

    @Override
    public boolean validate() {
        boolean valid = true;

        if (ConditionUtil.isTrue(condition)) {
            // will result in updateValidationResult() being called since this
            // listens for events from the delegate
            valid = delegate.validate();

            conditionMonitor.setValidateOnChange(false);
        } else {
            // make sure our ValidationResult is up-to-date
            setValidationResult(EmptyValidationResult.INSTANCE);

            // we were told to validate, but the condition was not true, so,
            // delay validation until the condition becomes true
            conditionMonitor.setValidateOnChange(true);
        }

        return valid;
    }

    @Override
    public void clear() {
        super.clear();

        delegate.clear();
    }

    private void updateValidationResult() {
        ValidationResultImpl result = new ValidationResultImpl();

        // only need to decide whether we pay attention to the ValidationResult,
        // no need to re-validate()
        if (ConditionUtil.isTrue(condition)) {
            result.addAll(delegate.getValidationResult().getMessages());
        }

        setValidationResult(result);
    }

    private class ConditionMonitor implements ValueChangeHandler<Boolean> {
        private boolean validateOnChange = false;

        @Override
        public void onValueChange(ValueChangeEvent<Boolean> event) {
            if (isValidateOnChange()) {
                validate();
            } else {
                updateValidationResult();
            }
        }

        public void setValidateOnChange(boolean validate) {
            this.validateOnChange = validate;
        }

        public boolean isValidateOnChange() {
            return validateOnChange;
        }
    }

    private class ValidatorMonitor implements ValidationHandler {
        @Override
        public void onValidate(ValidationEvent event) {
            updateValidationResult();
        }
    }
}
