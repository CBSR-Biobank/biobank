package edu.ualberta.med.biobank.mvp.presenter.validation;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.pietschy.gwt.pectin.client.binding.Disposable;
import com.pietschy.gwt.pectin.client.condition.Condition;
import com.pietschy.gwt.pectin.client.form.validation.HasValidation;
import com.pietschy.gwt.pectin.client.form.validation.Severity;
import com.pietschy.gwt.pectin.client.form.validation.ValidationEvent;
import com.pietschy.gwt.pectin.client.form.validation.ValidationHandler;
import com.pietschy.gwt.pectin.client.form.validation.ValidationResult;
import com.pietschy.gwt.pectin.client.form.validation.ValidationResultImpl;

import edu.ualberta.med.biobank.mvp.util.HandlerRegManager;

/**
 * A delegating validator, with a condition for the validation result. Useful
 * for conditional validation of a {@link HasValidation}.
 * <p>
 * If the condition is not met, will delay validation until the condition is
 * met.
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
public class LazyDelegatingConditionalValidation extends AbstractValidation
    implements Disposable {
    private final ConditionMonitor conditionMonitor = new ConditionMonitor();
    private final ValidatorMonitor validatorMonitor = new ValidatorMonitor();
    private final HandlerRegManager hrManager = new HandlerRegManager();
    private final HasValidation delegate;
    private final Condition condition;

    public LazyDelegatingConditionalValidation(HasValidation delegate,
        Condition condition) {
        this.delegate = delegate;
        this.condition = condition;

        hrManager.add(condition.addValueChangeHandler(conditionMonitor));
        hrManager.add(delegate.addValidationHandler(validatorMonitor));
    }

    @Override
    public boolean validate() {
        // TODO: decide whether waiting to validate makes sense or not.
        if (ConditionUtil.isTrue(condition)) {
            // will result in updateValidationResult() being called since this
            // listens for events from the delegate
            delegate.validate();

            conditionMonitor.setValidateOnChange(false);
        } else {
            // we were told to validate, but the condition was not true, so,
            // delay validation until the condition becomes true
            conditionMonitor.setValidateOnChange(true);
        }

        // even though the delegate may have an error, we might not if the
        // condition is not true
        return getValidationResult().contains(Severity.ERROR);
    }

    @Override
    public void dispose() {
        hrManager.dispose();
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
