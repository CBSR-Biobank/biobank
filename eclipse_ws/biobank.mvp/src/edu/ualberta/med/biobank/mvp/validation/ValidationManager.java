package edu.ualberta.med.biobank.mvp.validation;

import java.util.IdentityHashMap;
import java.util.Map;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;

import edu.ualberta.med.biobank.mvp.event.ValidationEvent;
import edu.ualberta.med.biobank.mvp.event.ValidationHandler;
import edu.ualberta.med.biobank.mvp.event.ui.HasValidationHandlers;

/**
 * Provides immediate validation.
 * 
 * @author jferland
 * 
 */
public class ValidationManager implements HasValidationHandlers {
    private final Map<HasValidationHandlers, ValidationResult> validationSourceMap =
        null;

    private final IdentityHashMap<HasValue<?>, ValidatedValue<?>> validatedValueMap =
        new IdentityHashMap<HasValue<?>, ValidatedValue<?>>();
    private final HandlerManager handlerManager = new HandlerManager(this);
    private final ValidationHandler validationHandler =
        new ValidationHandler() {
            @Override
            public void onValidate(ValidationEvent event) {
            }
        };

    public ValidationManager() {
        // if the view of the given presenter implements HasValidationResult
        // then set it on them?
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        handlerManager.fireEvent(event);
    }

    @Override
    public HandlerRegistration addValidationHandler(ValidationHandler handler) {
        return handlerManager.addHandler(ValidationEvent.getType(), handler);
    }

    public void watch(HasValidationHandlers validatable) {
        // TODO: make a map of validatables to their validations results, roll
        // them up on an update and fire all to the observers.
    }

    private void doUpdate() {
        // TODO: fire event to listeners
    }

    public <T> ValidatedValue<T> getValidatedValue(HasValue<T> value) {
        @SuppressWarnings("unchecked")
        ValidatedValue<T> validatedValue =
            (ValidatedValue<T>) validatedValueMap.get(value);

        if (validatedValue == null) {
            validatedValue = new ValidatedValue<T>(value);
            validatedValueMap.put(value, validatedValue);
        }

        return validatedValue;
    }

    private class ValidationSource {
    }
}
