package edu.ualberta.med.biobank.mvp.validation;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map.Entry;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;

import edu.ualberta.med.biobank.mvp.event.ValidationEvent;
import edu.ualberta.med.biobank.mvp.event.ValidationHandler;
import edu.ualberta.med.biobank.mvp.event.ui.HasValidationHandlers;

public class ValidatedValue<T> implements HasValidationHandlers {
    private final HandlerManager handlerManager = new HandlerManager(this);
    private final IdentityHashMap<HasValue<Boolean>, List<Validator<T>>> validatorsMap =
        new IdentityHashMap<HasValue<Boolean>, List<Validator<T>>>();
    private final HasValue<T> value;
    private final ValueChangeHandler<T> valueChangeHandler =
        new ValueChangeHandler<T>() {
            @Override
            public void onValueChange(ValueChangeEvent<T> event) {
                handleValueChange();
            }
        };

    public ValidatedValue(HasValue<T> value) {
        this.value = value;
        value.addValueChangeHandler(valueChangeHandler);
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        handlerManager.fireEvent(event);
    }

    @Override
    public HandlerRegistration addValidationHandler(ValidationHandler handler) {
        return handlerManager.addHandler(ValidationEvent.getType(), handler);
    }

    /**
     * Adds a {@link Validator} for this value.
     * <p>
     * If one {@link Validator} is added with more than one condition, then the
     * {@link Validator} will be run if <em>any</em> of the conditions are true.
     * 
     * @param validator
     * @param condition
     */
    public void addValidator(Validator<? super T> validator,
        HasValue<Boolean> condition) {
        if (validator == null) {
            throw new NullPointerException("validator is null");
        }

        if (condition == null) {
            throw new NullPointerException("condition is null");
        }

        // getValidators(condition).add(validator);
    }

    private List<Validator<T>> getValidators(final HasValue<Boolean> condition) {
        List<Validator<T>> validators = validatorsMap.get(condition);

        if (validators == null) {
            validators = new ArrayList<Validator<T>>();
            validatorsMap.put(condition, validators);

            addConditionChangeHandler(condition);
        }

        return validators;
    }

    private void addConditionChangeHandler(final HasValue<Boolean> condition) {
        condition.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                handleConditionChange(condition);
            }
        });
    }

    private void handleConditionChange(HasValue<Boolean> condition) {
        if (isConditionMet(condition)) {
            List<Validator<T>> validators = getValidators(condition);
            for (Validator<T> validator : validators) {

            }
        }
    }

    private void handleValueChange() {
        ValidationResultImpl result = new ValidationResultImpl();

        for (Entry<HasValue<Boolean>, List<Validator<T>>> entry : validatorsMap
            .entrySet()) {
            HasValue<Boolean> condition = entry.getKey();
            List<Validator<T>> validators = entry.getValue();

            if (isConditionMet(condition)) {

            }
        }

        ValidationEvent event = new ValidationEvent(result);
        handlerManager.fireEvent(event);
    }

    private void setValidationResult(ValidationResult result) {
        if (value instanceof HasValidationResult) {
            ((HasValidationResult) value).setValidationResult(result);
        }
    }

    private void fireValidationEvent(ValidationEvent event) {

    }

    private boolean isConditionMet(HasValue<Boolean> condition) {
        return Boolean.TRUE.equals(condition.getValue());
    }
}