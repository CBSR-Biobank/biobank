package edu.ualberta.med.biobank.mvp.presenter.validation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasValue;
import com.pietschy.gwt.pectin.client.binding.Disposable;
import com.pietschy.gwt.pectin.client.condition.Condition;
import com.pietschy.gwt.pectin.client.form.validation.Severity;
import com.pietschy.gwt.pectin.client.form.validation.ValidationEvent;
import com.pietschy.gwt.pectin.client.form.validation.ValidationHandler;
import com.pietschy.gwt.pectin.client.form.validation.ValidationResult;
import com.pietschy.gwt.pectin.client.form.validation.ValidationResultImpl;
import com.pietschy.gwt.pectin.client.form.validation.Validator;
import com.pietschy.gwt.pectin.client.form.validation.component.ValidationDisplay;

import edu.ualberta.med.biobank.mvp.util.HandlerRegManager;

/**
 * 
 * @author jferland
 * 
 * @param <T> source value type
 */
public class ValueValidation<T> extends AbstractValidation implements
    Disposable {
    private final HandlerRegManager hrManager = new HandlerRegManager();
    private final SourceMonitor sourceMonitor = new SourceMonitor();
    private final ConditionMonitor conditionMonitor = new ConditionMonitor();
    private final List<InnerValidator<T>> validators =
        new ArrayList<InnerValidator<T>>();
    private final HasValue<T> source;

    public ValueValidation(HasValue<T> source) {
        this.source = source;

        propagateValidationResultTo(source);

        hrManager.add(source.addValueChangeHandler(sourceMonitor));
    }

    public void add(Validator<T> validator, Condition condition) {
        conditionMonitor.add(condition);
    }

    @Override
    public boolean validate() {
        runValidators();

        updateValidationResult();

        return getValidationResult().contains(Severity.ERROR);
    }

    @Override
    public void dispose() {
        hrManager.dispose();
    }

    private void updateValidationResult() {
        ValidationResultImpl result = new ValidationResultImpl();

        for (InnerValidator<T> validator : validators) {
            result.addAll(validator.getValidationResult().getMessages());
        }

        setValidationResult(result);
    }

    private void runValidators() {
        T value = source.getValue();

        for (InnerValidator<T> validator : validators) {
            validator.validate(value);
        }
    }

    private class SourceMonitor implements ValueChangeHandler<T> {
        @Override
        public void onValueChange(ValueChangeEvent<T> event) {
            validate();
        }
    }

    /**
     * Watches {@link Condition}-s, careful to never monitor the same
     * {@link Condition} twice.
     * <p>
     * When a {@link Condition} is changed, simply update the
     * {@link ValidationResult}, but there is no need to re-run the
     * {@link Validator}-s as they only need to be re-run when the source value
     * changes.
     * 
     * @author jferland
     * 
     */
    private class ConditionMonitor implements ValueChangeHandler<Boolean> {
        private final Set<Condition> conditions = new HashSet<Condition>();

        public void add(Condition condition) {
            if (!conditions.contains(condition)) {
                conditions.add(condition);
                hrManager.add(condition.addValueChangeHandler(this));
            }
        }

        @Override
        public void onValueChange(ValueChangeEvent<Boolean> event) {
            updateValidationResult();
        }
    }

    private void propagateValidationResultTo(Object o) {
        if (o instanceof ValidationDisplay) {
            ValidationDisplay display = (ValidationDisplay) o;
            ValidationPropagator propagator = new ValidationPropagator(display);

            hrManager.add(this.addValidationHandler(propagator));
        }
    }

    /**
     * Whenever we (the {@link ValueValidation} change, notify a
     * {@link ValidationDisplay} of the new {@link ValidationResult}.
     * 
     * @author jferland
     * 
     */
    private class ValidationPropagator implements ValidationHandler {
        private final ValidationDisplay validationDisplay;

        public ValidationPropagator(ValidationDisplay validationDisplay) {
            this.validationDisplay = validationDisplay;
        }

        @Override
        public void onValidate(ValidationEvent event) {
            ValidationResult result = getValidationResult();
            validationDisplay.setValidationResult(result);
        }
    }

    /**
     * Caches a {@link ValidationResult} when validated and returns an empty
     * result if the {@link Condition} is met, otherwise return the true
     * {@link ValidationResult}-s.
     * 
     * @author jferland
     * 
     * @param <T>
     */
    private static class InnerValidator<T> {
        private static final ValidationResultImpl EMPTY_RESULT =
            new ValidationResultImpl();
        private final Validator<T> validator;
        private final Condition condition;
        private ValidationResultImpl result = EMPTY_RESULT;

        private InnerValidator(Validator<T> validator, Condition condition) {
            this.validator = validator;
            this.condition = condition;
        }

        public void validate(T value) {
            ValidationResultImpl result = new ValidationResultImpl();

            validator.validate(value, result);

            setValidationResult(result);
        }

        public ValidationResult getValidationResult() {
            return ConditionUtil.isTrue(condition) ? result : EMPTY_RESULT;
        }

        private void setValidationResult(ValidationResultImpl result) {
            this.result = result;
        }
    }
}
