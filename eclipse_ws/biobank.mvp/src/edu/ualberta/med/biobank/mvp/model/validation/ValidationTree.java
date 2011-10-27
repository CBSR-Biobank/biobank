package edu.ualberta.med.biobank.mvp.model.validation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.pietschy.gwt.pectin.client.condition.Condition;
import com.pietschy.gwt.pectin.client.condition.DelegatingCondition;
import com.pietschy.gwt.pectin.client.form.validation.HasValidation;
import com.pietschy.gwt.pectin.client.form.validation.ValidationEvent;
import com.pietschy.gwt.pectin.client.form.validation.ValidationHandler;
import com.pietschy.gwt.pectin.client.form.validation.ValidationResultImpl;

import edu.ualberta.med.biobank.mvp.util.HandlerRegManager;

public class ValidationTree extends AbstractValidation {
    private static final Condition TRUE = new DelegatingCondition(true);
    private final ConditionMonitor conditionMonitor = new ConditionMonitor();
    private final ValidationMonitor validationMonitor = new ValidationMonitor();
    private final HandlerRegManager hrManager = new HandlerRegManager();
    private final Map<HasValidation, Condition> validators =
        new LinkedHashMap<HasValidation, Condition>();

    public void add(HasValidation validator) {
        add(validator, TRUE);
    }

    public void add(HasValidation validator, Condition condition) {
        // TODO: figure out what to do if added twice?
        if (!validators.containsKey(validator)) {
            validators.put(validator, condition);

            // TODO: don't listen to the same condition twice?
            hrManager.add(validator.addValidationHandler(validationMonitor));
            hrManager.add(condition.addValueChangeHandler(conditionMonitor));
        }
    }

    @Override
    public boolean validate() {
        try {
            validationMonitor.setIgnoreEvents(true);

            boolean valid = runValidators();

            updateValidationResult();

            return valid;
        }
        finally {
            validationMonitor.setIgnoreEvents(false);
        }
    }

    @Override
    public void clear() {
        try {
            // don't want to pay attention to all the events fired when we clear
            // our validators
            validationMonitor.setIgnoreEvents(true);

            for (HasValidation validator : validators.keySet()) {
                validator.clear();
            }

            super.clear();
        }
        finally {
            validationMonitor.setIgnoreEvents(false);
        }
    }

    public void dispose() {
        hrManager.clear();
    }

    private boolean runValidators() {
        boolean valid = true;

        for (Entry<HasValidation, Condition> entry : validators.entrySet()) {
            HasValidation validator = entry.getKey();
            Condition condition = entry.getValue();

            if (Boolean.TRUE.equals(condition.getValue())) {
                valid = valid && validator.validate();
            }
            else {
                validator.clear();
            }

        }

        return valid;
    }

    private void updateValidationResult() {
        ValidationResultImpl validationResult = new ValidationResultImpl();

        for (HasValidation validator : validators.keySet()) {
            validationResult.addAll(validator.getValidationResult()
                .getMessages());
        }

        setValidationResult(validationResult);
    }

    private class ValidationMonitor implements ValidationHandler {
        private boolean ignoreEvents = false;

        @Override
        public void onValidate(ValidationEvent event) {
            doUpdate();
        }

        private void doUpdate() {
            if (!isIgnoreEvents()) {
                updateValidationResult();
            }
        }

        public boolean isIgnoreEvents() {
            return ignoreEvents;
        }

        public void setIgnoreEvents(boolean ignoreEvents) {
            this.ignoreEvents = ignoreEvents;
        }
    }

    private class ConditionMonitor implements ValueChangeHandler<Boolean> {
        @Override
        public void onValueChange(ValueChangeEvent<Boolean> event) {
            updateValidationResult();
        }
    }
}
