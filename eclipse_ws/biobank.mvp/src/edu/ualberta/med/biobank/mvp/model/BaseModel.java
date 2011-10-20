package edu.ualberta.med.biobank.mvp.model;

import com.pietschy.gwt.pectin.client.form.FieldModel;
import com.pietschy.gwt.pectin.client.form.FormModel;
import com.pietschy.gwt.pectin.client.value.ValueModel;
import com.pietschy.gwt.pectin.reflect.ReflectionBeanModelProvider;

public class BaseModel<T> extends FormModel {
    public final ReflectionBeanModelProvider<T> provider;

    public BaseModel(Class<T> modelClass) {
        provider = new ReflectionBeanModelProvider<T>(modelClass);
    }

    public T getValue() {
        return provider.getValue();
    }

    public void setValue(T value) {
        provider.setValue(value);
    }

    public ValueModel<Boolean> dirty() {
        return provider.dirty();
    }

    protected <E> FieldModel<E> addField(Class<E> fieldClass, String key) {
        return fieldOfType(fieldClass).boundTo(provider, key);
    }
}
