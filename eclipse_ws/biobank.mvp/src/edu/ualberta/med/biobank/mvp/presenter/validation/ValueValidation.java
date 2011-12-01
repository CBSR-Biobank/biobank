package edu.ualberta.med.biobank.mvp.presenter.validation;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasValue;
import com.pietschy.gwt.pectin.client.binding.Disposable;
import com.pietschy.gwt.pectin.client.condition.Condition;
import com.pietschy.gwt.pectin.client.form.validation.EmptyValidationResult;
import com.pietschy.gwt.pectin.client.form.validation.Severity;
import com.pietschy.gwt.pectin.client.form.validation.ValidationResult;
import com.pietschy.gwt.pectin.client.form.validation.ValidationResultImpl;
import com.pietschy.gwt.pectin.client.form.validation.Validator;
import com.pietschy.gwt.pectin.client.form.validation.component.ValidationDisplay;

/**
 * 
 * @author jferland
 * 
 * @param <T> source value type
 */
class ValueValidation<T> extends AbstractValidation implements Disposable {
    private final SourceMonitor sourceMonitor = new SourceMonitor();
    private final List<ConditionalValidator> validators =
        new ArrayList<ConditionalValidator>();
    private final HasValue<T> source;

    ValueValidation(HasValue<T> source) {
        this.source = source;

        bindValidationTo(source);

        handlerRegistry.add(source.addValueChangeHandler(sourceMonitor));
    }

    public void add(Validator<? super T> validator, Condition condition) {
        ConditionalValidator conditionalValidator =
            new ConditionalValidator(validator, condition);

        validators.add(conditionalValidator);
    }

    public void bindValidationTo(ValidationDisplay validationDisplay) {
        ValidationBinding binding = new ValidationBinding(validationDisplay);
        handlerRegistry.add(addValidationHandler(binding));
    }

    @Override
    public boolean validate() {
        runValidators();

        updateValidationResult();

        return getValidationResult().contains(Severity.ERROR);
    }

    private void updateValidationResult() {
        ValidationResultImpl result = new ValidationResultImpl();

        for (ConditionalValidator validator : validators) {
            result.addAll(validator.getValidationResult().getMessages());
        }

        setValidationResult(result);
    }

    private void runValidators() {
        for (ConditionalValidator validator : validators) {
            validator.validate();
        }
    }

    private class SourceMonitor implements ValueChangeHandler<T> {
        @Override
        public void onValueChange(ValueChangeEvent<T> event) {
            validate();
        }
    }

    private class ConditionalValidator {
        private final ConditionMonitor conditionMonitor =
            new ConditionMonitor();
        private final CachedValidator<T> validator;
        private final Condition condition;
        private ValidationResult result = EmptyValidationResult.INSTANCE;

        private ConditionalValidator(Validator<? super T> validator,
            Condition condition) {
            this.validator = new CachedValidator<T>(validator);
            this.condition = condition;
        }

        public void validate() {
            if (ConditionUtil.isTrue(condition)) {
                T value = source.getValue();
                ValidationResultImpl result = new ValidationResultImpl();
                validator.validate(value, result);

                this.result = result;

                conditionMonitor.setValidateOnChange(false);
            } else {
                // make sure our ValidationResult is up-to-date
                this.result = EmptyValidationResult.INSTANCE;

                // we were told to validate, but the condition was not true, so,
                // delay validation until the condition becomes true
                conditionMonitor.setValidateOnChange(true);
            }
        }

        public ValidationResult getValidationResult() {
            return result;
        }

        private void updateValidationResult() {
            ValidationResultImpl result = new ValidationResultImpl();

            // only need to decide whether we pay attention to the
            // ValidationResult, no need to re-validate()
            if (ConditionUtil.isTrue(condition)) {
                result.addAll(validator.getValidationResult().getMessages());
            }

            setValidationResult(result);

            ValueValidation.this.updateValidationResult();
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
    }

    private void bindValidationTo(Object object) {
        if (object instanceof ValidationDisplay) {
            ValidationDisplay validationDisplay = (ValidationDisplay) object;
            bindValidationTo(validationDisplay);
        }
    }
}
