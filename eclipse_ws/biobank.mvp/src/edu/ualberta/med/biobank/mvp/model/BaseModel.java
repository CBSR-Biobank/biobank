package edu.ualberta.med.biobank.mvp.model;

import com.pietschy.gwt.pectin.client.form.FormModel;
import com.pietschy.gwt.pectin.client.form.validation.ValidationPlugin;
import com.pietschy.gwt.pectin.client.value.MutableValueModel;
import com.pietschy.gwt.pectin.client.value.ValueModel;
import com.pietschy.gwt.pectin.reflect.ReflectionBeanModelProvider;

public class BaseModel<T> extends FormModel {
    protected final ReflectionBeanModelProvider<T> provider;

    public BaseModel(Class<T> beanModelClass) {
        // TODO: could read the .class from the generic parameter?
        // TODO: should get this from an injected provider in the future if ever
        // go the GWT-way since it this specific implementation won't work with
        // GWT
        provider = new ReflectionBeanModelProvider<T>(beanModelClass);
    }

    public T getValue() {
        return provider.getValue();
    }

    public void setValue(T value) {
        provider.setValue(value);
        provider.commit(); // clear dirty
    }

    public ValueModel<Boolean> dirty() {
        return provider.dirty();
    }

    public void setBeanSource(ValueModel<T> beanSource) {
        provider.setBeanSource(beanSource);
    }

    public void revert() {
        provider.revert();
        provider.commit(); // clear dirty
    }

    public MutableValueModel<T> getMutableValueModel() {
        return provider;
    }

    public void bind() {

    }

    public void unbind() {

    }

    public boolean validate() {
        return ValidationPlugin.getValidationManager(this).validate();
    }
}
