package edu.ualberta.med.biobank.mvp.presenter.validation;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.pietschy.gwt.pectin.client.binding.Disposable;
import com.pietschy.gwt.pectin.client.form.validation.EmptyValidationResult;
import com.pietschy.gwt.pectin.client.form.validation.HasValidation;
import com.pietschy.gwt.pectin.client.form.validation.ValidationEvent;
import com.pietschy.gwt.pectin.client.form.validation.ValidationHandler;
import com.pietschy.gwt.pectin.client.form.validation.ValidationResult;
import com.pietschy.gwt.pectin.client.form.validation.component.ValidationDisplay;

import edu.ualberta.med.biobank.mvp.util.HandlerRegistry;

public abstract class AbstractValidation implements HasValidation, Disposable {
    protected final HandlerRegistry handlerRegistry = new HandlerRegistry();
    private final HandlerManager handlerManager = new HandlerManager(this);
    private ValidationResult validationResult = EmptyValidationResult.INSTANCE;

    public void bindValidationTo(ValidationDisplay validationDisplay) {
        ValidationBinding binding = new ValidationBinding(validationDisplay);
        handlerRegistry.add(addValidationHandler(binding));
    }

    @Override
    public ValidationResult getValidationResult() {
        return validationResult;
    }

    @Override
    public HandlerRegistration addValidationHandler(ValidationHandler handler) {
        return handlerManager.addHandler(ValidationEvent.getType(), handler);
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        handlerManager.fireEvent(event);
    }

    @Override
    public void clear() {
        setValidationResult(EmptyValidationResult.INSTANCE);
    }

    @Override
    public void dispose() {
        handlerRegistry.dispose();
    }

    protected void setValidationResult(ValidationResult result) {
        if (result == null) {
            throw new NullPointerException("validationResult is null");
        }

        this.validationResult = result;
        fireValidationChanged();
    }

    private void fireValidationChanged() {
        ValidationEvent.fire(this, validationResult);
    }
}
