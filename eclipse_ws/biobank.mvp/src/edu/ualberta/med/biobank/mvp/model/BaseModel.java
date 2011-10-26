package edu.ualberta.med.biobank.mvp.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.pietschy.gwt.pectin.client.condition.DelegatingCondition;
import com.pietschy.gwt.pectin.client.condition.OrFunction;
import com.pietschy.gwt.pectin.client.condition.ReducingCondition;
import com.pietschy.gwt.pectin.client.form.Field;
import com.pietschy.gwt.pectin.client.form.FieldModel;
import com.pietschy.gwt.pectin.client.form.FormModel;
import com.pietschy.gwt.pectin.client.form.FormattedFieldModel;
import com.pietschy.gwt.pectin.client.form.FormattedListFieldModel;
import com.pietschy.gwt.pectin.client.form.ListFieldModel;
import com.pietschy.gwt.pectin.client.form.binding.FormBinder;
import com.pietschy.gwt.pectin.client.form.validation.FormValidator;
import com.pietschy.gwt.pectin.client.form.validation.HasValidation;
import com.pietschy.gwt.pectin.client.form.validation.ValidationPlugin;
import com.pietschy.gwt.pectin.client.form.validation.ValidationResult;
import com.pietschy.gwt.pectin.client.form.validation.binding.ValidationBinder;
import com.pietschy.gwt.pectin.client.form.validation.component.ValidationDisplay;
import com.pietschy.gwt.pectin.client.value.MutableValueModel;
import com.pietschy.gwt.pectin.client.value.ValueModel;
import com.pietschy.gwt.pectin.reflect.ReflectionBeanModelProvider;

import edu.ualberta.med.biobank.mvp.util.HandlerRegistrationManager;

/**
 * For use by {@link edu.ualberta.med.biobank.mvp.presenter.IPresenter}-s.
 * {@link BaseModel}-s should only have knowledge of other models and only
 * supply validation. It is the presenter's responsibility to call
 * {@link BaseModel#bind()} and {@link BaseModel#unbind()} on a model and to
 * bind the model's attributes to the view, and bind the validation to the view.
 * <p>
 * However, note that when a model's {@link FieldModel}-s are bound to a widget/
 * component that implements {@link ValidationDisplay}, then the widget will be
 * automatically notified of validation via {@link ValidationResult}.
 * <p>
 * Validation rules should be put in the constructor.
 * 
 * @author jferland
 * 
 * @param <T>
 */
public abstract class BaseModel<T> extends FormModel { // TODO: implement
                                                       // HasValidation
    protected final FormBinder binder = new FormBinder();
    protected final ValidationBinder validationBinder = new ValidationBinder();
    protected final ReflectionBeanModelProvider<T> provider;
    protected final DelegatingCondition dirty = new DelegatingCondition(false);
    private final List<BaseModel<?>> models = new ArrayList<BaseModel<?>>();
    private final HandlerRegistrationManager handlerRegistrationManager =
        new HandlerRegistrationManager();
    private boolean bound = false;

    public BaseModel(Class<T> beanModelClass) {
        // TODO: could read the .class from the generic parameter?
        // TODO: should get this from an injected provider in the future if ever
        // go the GWT-way since it this specific implementation won't work with
        // GWT
        // TODO: should subclass this (ReflectionBeanModelProvider) and use a
        // custom implementation of BeanPropertyListModel and
        // BeanPropertyValueModel.
        // TODO: NEED to override BeanPropertyListModel at least to work with
        // dirty states
        provider = new ReflectionBeanModelProvider<T>(beanModelClass);

        // auto-commit so that the
        provider.setAutoCommit(true);
    }

    public T getValue() {
        return provider.getValue();
    }

    public void setValue(T value) {
        provider.setValue(value);
    }

    /**
     * Creates a new checkpoint for {@link BaseModel#revert()} to revert to, if
     * called, and clears the dirty state (including that of added inner
     * {@link BaseModel}-s).
     */
    public void checkpoint() {
        provider.checkpoint();

        for (BaseModel<?> model : models) {
            model.checkpoint();
        }
    }

    public ValueModel<Boolean> dirty() {
        return dirty;
    }

    public ValueModel<Boolean> valid() {
        return null; // TODO: make a valid-watcher
    }

    public ValueModel<Boolean> validAndDirty() {
        return null; // TODO: implement this!
    }

    public void bindValidationTo(ValidationDisplay validationDisplay) {
        // TODO: this!
    }

    /**
     * Reverts this {@link BaseModel} to the value it was originally provided
     * with (via {@link BaseModel#setValue(Object)}) or the value when
     * {@link BaseModel#checkpoint()} was last called.
     */
    public void revert() {
        provider.revert();

        // revert inner models before checkpoint()-ing, otherwise the inner
        // models will revert to the checkpoint we just made (and not their
        // "original" value, which would have been overwritten by the
        // checkpoint).
        for (BaseModel<?> model : models) {
            model.revert();
        }

        checkpoint();
    }

    public boolean validate() {
        for (BaseModel<?> model : models) {
            model.validate();
        }

        return ValidationPlugin.getValidationManager(this).validate();
    }

    /**
     * Binds a field to a model. The {@link FieldModel} <em>must</em> belong to
     * this {@link FormModel}.
     * <p>
     * Adds a {@link BaseModel} to this {@link BaseModel}, so that the former is
     * reverted, checkpoint-ed, validated, bound, unbound, dirty-checked, etc.
     * whenever this {@link BaseModel} is.
     * 
     * @param field
     *            bound to the model.
     * @param model
     *            bound to the field.
     * @param binder
     *            used to bind the model and field.
     */
    public <E> void bind(FieldModel<E> field, BaseModel<E> model) {
        if (!field.getFormModel().equals(this)) {
            throw new IllegalArgumentException("field is not from this model");
        }

        models.add(model);
        binder.bind(field).to(model.getMutableValueModel());
    }

    public MutableValueModel<T> getMutableValueModel() {
        return provider;
    }

    public void bind() {
        if (!bound) {
            onBind();

            // TODO: add self as a handler for each of model children, aggregate
            // all and do something like FormValidator, and BaseModel should
            // implement HasValidators

            bindModels();
            addValidationHandlers();
            updateDirtyDelegate();

            validate();

            bound = true;
        }
    }

    public void unbind() {
        if (bound) {
            bound = false;

            unbindModels();
            dirty.setDelegate(provider.dirty());
            handlerRegistrationManager.clear();
            binder.dispose();
            validationBinder.dispose();

            onUnbind();
        }
    }

    public abstract void onBind();

    public abstract void onUnbind();

    private void addValidationHandlers() {
        for (Field<?> field : allFields()) {
            addValidationHandler(field);
        }
    }

    private <E> void addValidationHandler(final Field<E> field) {
        final HasValidation validator = getValidator(field);
        if (validator != null && field instanceof HasValueChangeHandlers) {
            @SuppressWarnings("unchecked")
            HandlerRegistration handlerRegistration =
                ((HasValueChangeHandlers<E>) field)
                    .addValueChangeHandler(new ValueChangeHandler<E>() {
                        @Override
                        public void onValueChange(ValueChangeEvent<E> event) {
                            validator.validate();
                        }
                    });

            handlerRegistrationManager.add(handlerRegistration);

            // TODO: listen to conditions of validation, then re-validate().
            // But how?
        }
    }

    private HasValidation getValidator(Field<?> field) {
        FormValidator form = ValidationPlugin.getValidationManager(this)
            .getFormValidator();

        // unfortunately, FormValidator.getValidator() will create the validator
        // if it doesn't exist, but we only want to get one if it exists
        if (field instanceof FieldModel) {
            return form.getFieldValidator(
                (FieldModel<?>) field,
                false);
        }
        else if (field instanceof FormattedFieldModel) {
            return form.getFieldValidator(
                (FormattedFieldModel<?>) field,
                false);
        }
        else if (field instanceof ListFieldModel) {
            return form.getFieldValidator(
                (ListFieldModel<?>) field,
                false);
        }
        else if (field instanceof FormattedListFieldModel) {
            return form.getFieldValidator(
                (FormattedListFieldModel<?>) field,
                false);
        }

        return null;
    }

    /**
     * Make this {@link BaseModel}'s dirty value depend on the inner
     * {@link BaseModel}-s, if there are any.
     */
    private void updateDirtyDelegate() {
        List<ValueModel<Boolean>> values = new ArrayList<ValueModel<Boolean>>();
        values.add(provider.dirty());

        for (BaseModel<?> model : models) {
            values.add(model.dirty());
        }

        dirty.setDelegate(new ReducingCondition(new OrFunction(), values));
    }

    private void bindModels() {
        for (BaseModel<?> model : models) {
            model.bind();
        }
    }

    private void unbindModels() {
        for (BaseModel<?> model : models) {
            model.unbind();
        }
    }
}
