package edu.ualberta.med.biobank.mvp.model.validation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.pietschy.gwt.pectin.client.condition.Condition;
import com.pietschy.gwt.pectin.client.condition.DelegatingCondition;
import com.pietschy.gwt.pectin.client.form.validation.HasValidation;
import com.pietschy.gwt.pectin.client.form.validation.ValidationEvent;
import com.pietschy.gwt.pectin.client.form.validation.ValidationHandler;
import com.pietschy.gwt.pectin.client.form.validation.ValidationResultImpl;

public class ValidationTree extends AbstractValidation {
    private static final Condition TRUE = new DelegatingCondition(true);
    private final ValidationMonitor validationMonitor = new ValidationMonitor();
    private final Map<HasValidation, Condition> validators =
        new LinkedHashMap<HasValidation, Condition>();

    public void add(HasValidation validator) {
        add(validator, TRUE);
    }

    public void add(HasValidation validator, Condition condition) {
        if (!validators.containsKey(validator)) {
            validators.put(validator, condition);

            // TODO: listen to validator and condition
            validator.addValidationHandler(new ValidationHandler() {
                @Override
                public void onValidate(ValidationEvent event) {
                    updateValidationResult();
                }
            });
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

    private boolean runValidators() {
        boolean valid = true;

        for (Entry<HasValidation, Condition> entry : validators.entrySet()) {
            HasValidation validator = entry.getKey();
            Condition condition = entry.getValue();

            if (Boolean.TRUE.equals(condition)) {
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
}
