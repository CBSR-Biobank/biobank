package edu.ualberta.med.biobank.mvp.model;

import com.pietschy.gwt.pectin.client.form.FormModel;
import com.pietschy.gwt.pectin.client.value.ValueModel;
import com.pietschy.gwt.pectin.reflect.ReflectionBeanModelProvider;

public class BaseModel<T> extends FormModel {
    protected final ReflectionBeanModelProvider<T> provider;

    public BaseModel(Class<T> beanModelClass) {
        // TODO: could read the .class from the generic parameter?
        provider = new ReflectionBeanModelProvider<T>(beanModelClass);
    }

    public T getValue() {
        return provider.getValue();
    }

    public void setValue(T value) {
        provider.setValue(value);
        provider.commit();
    }

    public ValueModel<Boolean> dirty() {
        return provider.dirty();
    }

    public void setBeanSource(ValueModel<T> beanSource) {
        provider.setBeanSource(beanSource);
    }

    public void revert() {
        provider.revert();
    }
}
