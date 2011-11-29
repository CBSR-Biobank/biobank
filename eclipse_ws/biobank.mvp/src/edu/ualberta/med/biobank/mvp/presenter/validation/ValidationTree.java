package edu.ualberta.med.biobank.mvp.presenter.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.ui.HasValue;
import com.pietschy.gwt.pectin.client.condition.DelegatingCondition;
import com.pietschy.gwt.pectin.client.form.validation.HasValidation;
import com.pietschy.gwt.pectin.client.form.validation.Severity;
import com.pietschy.gwt.pectin.client.form.validation.ValidationEvent;
import com.pietschy.gwt.pectin.client.form.validation.ValidationHandler;
import com.pietschy.gwt.pectin.client.form.validation.ValidationResultImpl;
import com.pietschy.gwt.pectin.client.value.ValueHolder;
import com.pietschy.gwt.pectin.client.value.ValueModel;

import edu.ualberta.med.biobank.mvp.util.HandlerRegistry;

/**
 * Created so presenters can use the validation of other presenters, possibly
 * based on a condition.
 * 
 * @author jferland
 * 
 */
public class ValidationTree extends AbstractValidation {
    private final ValidationMonitor validationMonitor = new ValidationMonitor();
    private final HandlerRegistry handlerReg = new HandlerRegistry();
    private final Map<HasValue<?>, ValueValidation<?>> valueValidations =
        new HashMap<HasValue<?>, ValueValidation<?>>();
    private final List<HasValidation> validations =
        new ArrayList<HasValidation>();
    private final ValueHolder<Boolean> valid = new ValueHolder<Boolean>(true);

    public <T> ValueValidationBuilder<T> validate(HasValue<T> source) {
        return new ValueValidationBuilder<T>(this, source);
    }

    public DelegatingConditionBuilder add(HasValidation validation) {
        DelegatingCondition condition = new DelegatingCondition(true);

        add(new ConditionalValidation(validation, condition));

        return new DelegatingConditionBuilder(condition);
    }

    public ValueModel<Boolean> valid() {
        return valid;
    }

    @Override
    public boolean validate() {
        try {
            validationMonitor.setIgnoreEvents(true);

            boolean valid = runValidations();

            updateValidationResult();

            return valid;
        } finally {
            validationMonitor.setIgnoreEvents(false);
        }
    }

    @Override
    public void clear() {
        try {
            // don't want to pay attention to all the events fired when we clear
            // our validators
            validationMonitor.setIgnoreEvents(true);

            for (HasValidation validation : validations) {
                validation.clear();
            }

            super.clear();
        } finally {
            validationMonitor.setIgnoreEvents(false);
        }
    }

    public void dispose() {
        handlerReg.dispose();
    }

    <T> ValueValidation<T> getValueValidation(HasValue<T> source) {
        @SuppressWarnings("unchecked")
        ValueValidation<T> v =
            (ValueValidation<T>) valueValidations.get(source);
        if (v == null) {
            v = new ValueValidation<T>(source);
        }
        return v;
    }

    private void add(ConditionalValidation validation) {
        validations.add(validation);
        handlerReg.add(validation.addValidationHandler(validationMonitor));
    }

    private boolean runValidations() {
        boolean valid = true;

        for (HasValidation validation : validations) {
            valid = valid && validation.validate();
        }

        return valid;
    }

    private void updateValidationResult() {
        ValidationResultImpl result = new ValidationResultImpl();

        for (HasValidation validation : validations) {
            result.addAll(validation.getValidationResult().getMessages());
        }

        setValidationResult(result);

        valid.setValue(!result.contains(Severity.ERROR));
    }

    private class ValidationMonitor implements ValidationHandler {
        private boolean ignoreEvents = false;

        public boolean isIgnoreEvents() {
            return ignoreEvents;
        }

        public void setIgnoreEvents(boolean ignoreEvents) {
            this.ignoreEvents = ignoreEvents;
        }

        @Override
        public void onValidate(ValidationEvent event) {
            if (!isIgnoreEvents()) {
                doUpdate();
            }
        }

        public void doUpdate() {
            updateValidationResult();
        }
    }
}
