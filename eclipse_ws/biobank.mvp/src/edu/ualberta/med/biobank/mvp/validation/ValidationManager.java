package edu.ualberta.med.biobank.mvp.validation;

import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;

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
    private final IdentityHashMap<HasValidationHandlers, ValidationResult> validatablesMap =
        new IdentityHashMap<HasValidationHandlers, ValidationResult>();
    private final IdentityHashMap<HasValue<?>, ValidatedValue<?>> validatedValueMap =
        new IdentityHashMap<HasValue<?>, ValidatedValue<?>>();
    private final List<HandlerRegistration> handlerRegistrations =
        new LinkedList<HandlerRegistration>();
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

    public void watch(HasValidation validatable) {
        // TODO: should have a HasValidation interface that we can ask to
        // validate instead?
        // TODO: look at pectin's validation manager, but when something is
        // added, the "HasValidation" SHOULD BE RUN, so that we immediately have
        // the errors.
        validatablesMap.put(validatable, new ValidationResultImpl());
    }

    public <T> ValidatedValue<T> getValidatedValue(HasValue<T> value) {
        @SuppressWarnings("unchecked")
        ValidatedValue<T> validatedValue =
            (ValidatedValue<T>) validatedValueMap.get(value);

        if (validatedValue == null) {
            validatedValue = new ValidatedValue<T>(value);
            validatedValueMap.put(value, validatedValue);
            watch(validatedValue);
        }

        return validatedValue;
    }

    public void unbind() {
        unbindValidatedValues();
    }

    private void unbindValidatedValues() {
        for (ValidatedValue<?> validatedValue : validatedValueMap.values()) {
            validatedValue.unbind();
        }
        validatedValueMap.clear();
    }
}
