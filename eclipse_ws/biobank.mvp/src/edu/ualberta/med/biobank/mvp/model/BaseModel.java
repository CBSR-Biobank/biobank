package edu.ualberta.med.biobank.mvp.model;

import java.util.ArrayList;
import java.util.List;

import com.pietschy.gwt.pectin.client.condition.DelegatingCondition;
import com.pietschy.gwt.pectin.client.condition.OrFunction;
import com.pietschy.gwt.pectin.client.condition.ReducingCondition;
import com.pietschy.gwt.pectin.client.form.FormModel;
import com.pietschy.gwt.pectin.client.form.validation.ValidationPlugin;
import com.pietschy.gwt.pectin.client.form.validation.binding.ValidationBinder;
import com.pietschy.gwt.pectin.client.value.MutableValueModel;
import com.pietschy.gwt.pectin.client.value.ValueModel;
import com.pietschy.gwt.pectin.reflect.ReflectionBeanModelProvider;

public abstract class BaseModel<T> extends FormModel {
    protected final ValidationBinder validationBinder = new ValidationBinder();
    protected final ReflectionBeanModelProvider<T> provider;
    protected final DelegatingCondition dirty = new DelegatingCondition(false);
    private final List<BaseModel<?>> children = new ArrayList<BaseModel<?>>();
    private boolean bound = false;

    public BaseModel(Class<T> beanModelClass) {
        // TODO: could read the .class from the generic parameter?
        // TODO: should get this from an injected provider in the future if ever
        // go the GWT-way since it this specific implementation won't work with
        // GWT
        provider = new ReflectionBeanModelProvider<T>(beanModelClass);
        provider.setAutoCommit(true);

        updateDirtyDelegate();
    }

    public T getValue() {
        return provider.getValue();
    }

    public void setValue(T value) {
        provider.setValue(value);
    }

    public void checkpoint() {
        provider.checkpoint();

        checkpointChildren();
    }

    public ValueModel<Boolean> dirty() {
        return dirty;
    }

    public void revert() {
        provider.revert();

        revertChildren();

        checkpoint();
    }

    public MutableValueModel<T> getMutableValueModel() {
        return provider;
    }

    public void bind() {
        if (!bound) {
            onBind();

            bindChildren();

            bound = true;
        }
    }

    public void unbind() {
        if (bound) {
            bound = false;

            unbindChildren();

            validationBinder.dispose();

            onUnbind();
        }
    }

    public abstract void onBind();

    public abstract void onUnbind();

    public boolean validate() {
        // TODO: validate children as well?
        return ValidationPlugin.getValidationManager(this).validate();
    }

    public <E> void addChild(BaseModel<E> model) {
        children.add(model);

        // TODO: listen to validation of children

        updateDirtyDelegate();
    }

    private void updateDirtyDelegate() {
        List<ValueModel<Boolean>> models = new ArrayList<ValueModel<Boolean>>();
        models.add(provider.dirty());

        for (BaseModel<?> child : children) {
            models.add(child.dirty());
        }

        dirty.setDelegate(new ReducingCondition(new OrFunction(), models));
    }

    private void checkpointChildren() {
        for (BaseModel<?> child : children) {
            child.checkpoint();
        }
    }

    private void revertChildren() {
        for (BaseModel<?> child : children) {
            child.revert();
        }
    }

    private void bindChildren() {
        for (BaseModel<?> child : children) {
            child.bind();
        }
    }

    private void unbindChildren() {
        for (BaseModel<?> child : children) {
            child.unbind();
        }
    }
}
