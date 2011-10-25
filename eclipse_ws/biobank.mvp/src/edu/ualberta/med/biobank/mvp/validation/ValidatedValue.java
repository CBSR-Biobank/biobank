package edu.ualberta.med.biobank.mvp.validation;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedList;
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
import edu.ualberta.med.biobank.mvp.user.ui.impl.DelegatingHasValue;
import edu.ualberta.med.biobank.mvp.view.ValidationView;

/**
 * Manages a {@link List} of {@link Validator}-s for a single {@link HasValue}.
 * Each {@link Validator} has a condition (a {@link HasValue<Boolean>}) that
 * determines whether the {@link Validator} will be run.
 * <p>
 * Whenever the value or the value of the condition changes, the value will be
 * automatically revalidated, if the condition is true. A
 * {@link ValidationEvent} will be sent when validation is done.
 * <p>
 * If the validated {@link HasValue} also implements the
 * {@link HasValidationResult} interface, then the value will also be informed
 * whenever validation is done.
 * 
 * @author jferland
 * 
 * @param <T>
 */
public class ValidatedValue<T> implements HasValidation {
    private static final DelegatingHasValue<Boolean> TRUE =
        new DelegatingHasValue<Boolean>(true);
    private final HandlerManager handlerManager = new HandlerManager(this);
    private final IdentityHashMap<HasValue<Boolean>, List<Validator<? super T>>> validatorsMap =
        new IdentityHashMap<HasValue<Boolean>, List<Validator<? super T>>>();
    private final List<HandlerRegistration> handlerRegistrations =
        new LinkedList<HandlerRegistration>();
    private ValidationResultImpl validationResult = new ValidationResultImpl();
    private final HasValue<T> value;
    private final ValueChangeHandler<T> valueChangeHandler =
        new ValueChangeHandler<T>() {
            @Override
            public void onValueChange(ValueChangeEvent<T> event) {
                validate();
            }
        };
    private final ValueChangeHandler<Boolean> conditionValueChangeHandler =
        new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                validate();
            }
        };

    public ValidatedValue(HasValue<T> value) {
        this.value = value;
        registerHandler(value.addValueChangeHandler(valueChangeHandler));
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        handlerManager.fireEvent(event);
    }

    @Override
    public HandlerRegistration addValidationHandler(ValidationHandler handler) {
        return handlerManager.addHandler(ValidationEvent.getType(), handler);
    }

    @Override
    public ValidationResult getValidationResult() {
        return validationResult;
    }

    @Override
    public ValidationResult validate() {
        ValidationResultImpl result = new ValidationResultImpl();
        runValidators(result);
        setValidationResult(result);
        return result;
    }

    @Override
    public void clearValidation() {
        setValidationResult(new ValidationResultImpl());
    }

    public void addValidator(Validator<? super T> validator) {
        addValidator(validator, TRUE);
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

        getValidators(condition).add(validator);
    }

    public void unbind() {
        removeHandlers();
        validatorsMap.clear();
    }

    private List<Validator<? super T>> getValidators(
        final HasValue<Boolean> condition) {
        List<Validator<? super T>> validators = validatorsMap.get(condition);

        if (validators == null) {
            validators = new ArrayList<Validator<? super T>>();
            validatorsMap.put(condition, validators);

            registerHandler(condition
                .addValueChangeHandler(conditionValueChangeHandler));
        }

        return validators;
    }

    private void runValidators(ValidationResultCollector collector) {
        runValidators(value.getValue(), collector);
    }

    private void runValidators(T value, ValidationResultCollector collector) {
        for (Entry<HasValue<Boolean>, List<Validator<? super T>>> entry : validatorsMap
            .entrySet()) {
            HasValue<Boolean> condition = entry.getKey();
            List<Validator<? super T>> validators = entry.getValue();

            if (Boolean.TRUE.equals(condition.getValue())) {
                for (Validator<? super T> validator : validators) {
                    validator.validate(value, collector);
                }
            }
        }
    }

    private void setValidationResult(ValidationResultImpl result) {
        if (value instanceof ValidationView) {
            ((ValidationView) value).setValidationResult(result);
        }

        validationResult = result;
        fireValidationEvent();
    }

    private void fireValidationEvent() {
        ValidationEvent event = new ValidationEvent(validationResult);
        handlerManager.fireEvent(event);
    }

    private void removeHandlers() {
        for (HandlerRegistration handlerRegistration : handlerRegistrations) {
            handlerRegistration.removeHandler();
        }
    }

    private void registerHandler(HandlerRegistration handlerRegistration) {
        handlerRegistrations.add(handlerRegistration);
    }
}