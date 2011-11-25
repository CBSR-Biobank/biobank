package edu.ualberta.med.biobank.mvp.presenter.validation;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.pietschy.gwt.pectin.client.binding.Disposable;
import com.pietschy.gwt.pectin.client.condition.Condition;
import com.pietschy.gwt.pectin.client.form.validation.HasValidation;
import com.pietschy.gwt.pectin.client.form.validation.Severity;
import com.pietschy.gwt.pectin.client.form.validation.ValidationEvent;
import com.pietschy.gwt.pectin.client.form.validation.ValidationHandler;
import com.pietschy.gwt.pectin.client.form.validation.ValidationResultImpl;

import edu.ualberta.med.biobank.mvp.util.HandlerRegManager;

/**
 * Calls {@link HasValidation#validate()} depending on whether the given
 * {@link Condition} is met (is true).
 * <p>
 * Listens for {@link ValueChangeEvent}-s from the condition and will re-
 * {@link HasValidation#validate()} whenever the condition value changes.
 * <p>
 * Listens for {@link ValidationEvent}-s from the validator and will re-fire
 * them as coming from this class.
 * 
 * @author jferland
 * 
 */
public class ConditionalValidator extends AbstractValidator
    implements ValidationHandler, ValueChangeHandler<Boolean>, Disposable {
    private final HandlerRegManager hrManager = new HandlerRegManager();
    private final HasValidation validator;
    private final Condition condition;

    public ConditionalValidator(HasValidation validator, Condition condition) {
        this.validator = validator;
        this.condition = condition;

        hrManager.add(condition.addValueChangeHandler(this));
        hrManager.add(validator.addValidationHandler(this));
    }

    @Override
    public void onValueChange(ValueChangeEvent<Boolean> event) {
        validate();
    }

    @Override
    public void onValidate(ValidationEvent event) {
        validate();
    }

    @Override
    public boolean validate() {
        boolean valid = updateValidationResult();
        return valid;
    }

    @Override
    public void dispose() {
        hrManager.dispose();
    }

    private boolean updateValidationResult() {
        ValidationResultImpl result = new ValidationResultImpl();

        // only need to decide whether we pay attention to the ValidationResult,
        // no need to re-validate()
        if (Boolean.TRUE.equals(condition.getValue())) {
            result.addAll(validator.getValidationResult().getMessages());
        }

        setValidationResult(result);

        return result.contains(Severity.ERROR);
    }
}
