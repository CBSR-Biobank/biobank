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
 * Listens for {@link ValidationEvent}-s from the delegated
 * {@link HasValidation} and for {@link ValueChangeEvent}-s from the
 * {@link Condition}. When either event is received, update the internal
 * {@link ValidationResult} (if the condition is met), which will fire another
 * {@link ValidationEvent}, but from this instance.
 * 
 * @author jferland
 * 
 */
public class DelegatingConditionalValidation extends AbstractValidation
    implements Disposable {
    private final ConditionMonitor conditionMonitor = new ConditionMonitor();
    private final ValidatorMonitor validatorMonitor = new ValidatorMonitor();
    private final HandlerRegManager hrManager = new HandlerRegManager();
    private final HasValidation delegate;
    private final Condition condition;

    public DelegatingConditionalValidation(HasValidation delegate,
        Condition condition) {
        this.delegate = delegate;
        this.condition = condition;

        hrManager.add(condition.addValueChangeHandler(conditionMonitor));
        hrManager.add(delegate.addValidationHandler(validatorMonitor));
    }

    @Override
    public boolean validate() {
        // NOTE: we have to choose between (1) validating whenever we're told or
        // (2) validating only if the condition is met. But if we choose #2,
        // then whenever the condition changes, we must re-validate() in case
        // the condition used to be false, and the HasValidation never had
        // "validate()" called on it. e.g. the condition is false, validate() is
        // called (so the delegate is not validated) then the condition changes
        // to true, but the delegate has never been validated, so, for now, do
        // option #1

        // will call updateValidationResult() through handler
        delegate.validate();

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
        @Override
        public void onValueChange(ValueChangeEvent<Boolean> event) {
            updateValidationResult();
        }
    }

    private class ValidatorMonitor implements ValidationHandler {
        @Override
        public void onValidate(ValidationEvent event) {
            updateValidationResult();
        }
    }
}
