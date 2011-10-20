package edu.ualberta.med.biobank.mvp.validation;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;

import edu.ualberta.med.biobank.mvp.event.ValidationEvent;
import edu.ualberta.med.biobank.mvp.event.ValidationHandler;
import edu.ualberta.med.biobank.mvp.view.ValidationView;

public abstract class AbstractValidation implements HasValidation {
    private final HandlerManager handlerManager = new HandlerManager(this);
    private final List<HandlerRegistration> registrations =
        new LinkedList<HandlerRegistration>();
    private ValidationResultImpl validationResult = new ValidationResultImpl();

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
    public ValidationResult validate() {
        ValidationResultImpl result = new ValidationResultImpl();
        doValidation(result);
        setValidationResult(result);
        return result;
    }

    @Override
    public void clearValidation() {
        setValidationResult(new ValidationResultImpl());
    }

    // TODO: more?
    public void unbind() {
        removeRegisteredHandlers();
    }

    public void updateView(ValidationView view) {
    }

    protected abstract void doValidation(ValidationResultCollector collector);

    protected void setValidationResult(ValidationResultImpl result) {
        this.validationResult = result;
        fireValidationEvent();
    }

    protected void fireValidationEvent() {
        ValidationEvent event = new ValidationEvent(validationResult);
        fireEvent(event);
    }

    protected void registerHandler(HandlerRegistration registration) {
        registrations.add(registration);
    }

    protected void removeRegisteredHandlers() {
        for (HandlerRegistration registration : registrations) {
            registration.removeHandler();
        }
        registrations.clear();
    }

    protected static boolean isConditionMet(HasValue<Boolean> condition) {
        return Boolean.TRUE.equals(condition.getValue());
    }
}
