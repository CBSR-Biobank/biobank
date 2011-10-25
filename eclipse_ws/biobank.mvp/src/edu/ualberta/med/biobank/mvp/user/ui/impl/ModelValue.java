package edu.ualberta.med.biobank.mvp.user.ui.impl;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;

import edu.ualberta.med.biobank.mvp.event.ValidationHandler;
import edu.ualberta.med.biobank.mvp.user.ui.HasModelValue;
import edu.ualberta.med.biobank.mvp.validation.Condition;
import edu.ualberta.med.biobank.mvp.validation.ValidationResult;
import edu.ualberta.med.biobank.mvp.validation.Validator;

public class ModelValue<T> implements HasModelValue<T> {
    private final HandlerManager handlerManager = new HandlerManager(this);
    private final List<HandlerRegistration> registrations =
        new LinkedList<HandlerRegistration>();
    private T value;
    private final LinkedHashMap<Validator<? super T>, Condition> validators =
        null;

    public void addValidator(Validator<? super T> validator, Condition condition) {

    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public void setValue(T value, boolean fireEvents) {
        if (fireEvents) {
            ValueChangeEvent.fireIfNotEqual(this, this.value, value);
        }
        this.value = value;
    }

    @Override
    public HandlerRegistration addValueChangeHandler(
        ValueChangeHandler<T> handler) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        // TODO Auto-generated method stub

    }

    @Override
    public ValidationResult validate() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void clearValidation() {
        // TODO Auto-generated method stub

    }

    @Override
    public ValidationResult getValidationResult() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HandlerRegistration addValidationHandler(ValidationHandler handler) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HasValue<T> getDefaultValue() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HasValue<Boolean> isDirty() {
        // TODO Auto-generated method stub
        return null;
    }
}
