package edu.ualberta.med.biobank.mvp.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.pietschy.gwt.pectin.client.condition.Condition;
import com.pietschy.gwt.pectin.client.condition.Conditions;
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
import com.pietschy.gwt.pectin.client.form.validation.Severity;
import com.pietschy.gwt.pectin.client.form.validation.ValidationEvent;
import com.pietschy.gwt.pectin.client.form.validation.ValidationHandler;
import com.pietschy.gwt.pectin.client.form.validation.ValidationPlugin;
import com.pietschy.gwt.pectin.client.form.validation.ValidationResult;
import com.pietschy.gwt.pectin.client.form.validation.binding.ValidationBinder;
import com.pietschy.gwt.pectin.client.form.validation.component.ValidationDisplay;
import com.pietschy.gwt.pectin.client.value.MutableValueModel;
import com.pietschy.gwt.pectin.client.value.ValueHolder;
import com.pietschy.gwt.pectin.client.value.ValueModel;
import com.pietschy.gwt.pectin.reflect.ReflectionBeanModelProvider;

import edu.ualberta.med.biobank.mvp.model.validation.ValidationTree;
import edu.ualberta.med.biobank.mvp.util.HandlerRegManager;

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
public abstract class BaseModel<T> extends FormModel {
    protected final ReflectionBeanModelProvider<T> provider;
    private final FormBinder binder = new FormBinder();
    private final ValidationBinder validationBinder = new ValidationBinder();
    private final ValidationTree validationTree = new ValidationTree();
    private final DelegatingCondition dirty = new DelegatingCondition(false);
    private final ValueHolder<Boolean> valid = new ValueHolder<Boolean>(false);
    private final List<BaseModel<?>> models = new ArrayList<BaseModel<?>>();
    private final HandlerRegManager hrManager = new HandlerRegManager();
    private final ValidationMonitor validationMonitor = new ValidationMonitor();
    private boolean bound = false;

    @SuppressWarnings("unchecked")
    private final Condition validAndDirty = Conditions.and(valid, dirty);

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

        // auto-commit so models can be bound to other models and are
        // automatically updated instantly
        provider.setAutoCommit(true);

        validationTree.add(getFormValidator());
        validationTree.addValidationHandler(validationMonitor);
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
        return valid;
    }

    public ValueModel<Boolean> validAndDirty() {
        return validAndDirty;
    }

    public void bindValidationTo(ValidationDisplay validationDisplay) {
        validationBinder.bindValidationOf(validationTree).to(validationDisplay);
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
        return validationTree.validate();
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

        validationTree.add(model.validationTree);
    }

    public MutableValueModel<T> getMutableValueModel() {
        return provider;
    }

    public void bind() {
        if (!bound) {
            // bind inner models before binding ourself
            bindModels();

            onBind();

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

            onUnbind();

            dirty.setDelegate(provider.dirty());
            hrManager.clear();
            binder.dispose();
            validationBinder.dispose();
            validationTree.dispose();
        }
    }

    public abstract void onBind();

    public abstract void onUnbind();

    protected FormValidator getFormValidator() {
        return ValidationPlugin.getValidationManager(this).getFormValidator();
    }

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

            hrManager.add(handlerRegistration);

            // TODO: listen to conditions of validation, then re-validate().
            // But how?
        }
    }

    private HasValidation getValidator(Field<?> field) {
        FormValidator formValidator = getFormValidator();

        // unfortunately, FormValidator.getValidator() will create the validator
        // if it doesn't exist, but we only want to get one if it exists
        if (field instanceof FieldModel) {
            return formValidator.getFieldValidator(
                (FieldModel<?>) field,
                false);
        }
        else if (field instanceof FormattedFieldModel) {
            return formValidator.getFieldValidator(
                (FormattedFieldModel<?>) field,
                false);
        }
        else if (field instanceof ListFieldModel) {
            return formValidator.getFieldValidator(
                (ListFieldModel<?>) field,
                false);
        }
        else if (field instanceof FormattedListFieldModel) {
            return formValidator.getFieldValidator(
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

    private void updateValidity(ValidationResult result) {
        boolean isValid = !result.contains(Severity.ERROR);
        valid.setValue(isValid);
    }

    private class ValidationMonitor implements ValidationHandler {
        @Override
        public void onValidate(ValidationEvent event) {
            updateValidity(event.getValidationResult());
        }
    }
}
