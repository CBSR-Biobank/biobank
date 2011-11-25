package edu.ualberta.med.biobank.mvp.presenter.validation;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.HasValue;
import com.pietschy.gwt.pectin.client.condition.Condition;
import com.pietschy.gwt.pectin.client.condition.DelegatingCondition;
import com.pietschy.gwt.pectin.client.form.validation.HasValidation;
import com.pietschy.gwt.pectin.client.form.validation.ValidationEvent;
import com.pietschy.gwt.pectin.client.form.validation.ValidationHandler;
import com.pietschy.gwt.pectin.client.form.validation.ValidationResultImpl;
import com.pietschy.gwt.pectin.client.form.validation.Validator;

import edu.ualberta.med.biobank.mvp.util.HandlerRegManager;

public class ValidationTree extends AbstractValidator {
    private static final Condition TRUE = new DelegatingCondition(true);
    private final ValidatorMonitor validatorMonitor = new ValidatorMonitor();
    private final HandlerRegManager hrManager = new HandlerRegManager();
    private final List<ConditionalValidator> validators =
        new ArrayList<ConditionalValidator>();

    public <T> void validate(HasValue<T> hasValue,
        Validator<T> validator,
        Condition condition) {

        // TODO: create builders, builders will have access to the monitors and
        // can watch.
        // TODO: create a new HasValidation that listens to the has value and
        // automatically re-validates on changes, then add it like any other
        // HasValidation...
    }

    public void add(HasValidation validator) {
        add(validator, TRUE);
    }

    public void add(HasValidation validator, Condition condition) {
        ConditionalValidator entry =
            new ConditionalValidator(validator, condition);
        validators.add(entry);
        hrManager.add(validator.addValidationHandler(validatorMonitor));
    }

    @Override
    public boolean validate() {
        try {
            validatorMonitor.setIgnoreEvents(true);

            boolean valid = runValidators();

            updateValidationResult();

            return valid;
        } finally {
            validatorMonitor.setIgnoreEvents(false);
        }
    }

    @Override
    public void clear() {
        try {
            // don't want to pay attention to all the events fired when we clear
            // our validators
            validatorMonitor.setIgnoreEvents(true);

            for (ConditionalValidator validator : validators) {
                validator.clear();
            }

            super.clear();
        } finally {
            validatorMonitor.setIgnoreEvents(false);
        }
    }

    public void dispose() {
        hrManager.dispose();
    }

    private boolean runValidators() {
        boolean valid = true;

        for (ConditionalValidator validator : validators) {
            valid = valid && validator.validate();
        }

        return valid;
    }

    private void updateValidationResult() {
        ValidationResultImpl result = new ValidationResultImpl();

        for (ConditionalValidator validator : validators) {
            result.addAll(validator.getValidationResult().getMessages());
        }

        setValidationResult(result);
    }

    private class ValidatorMonitor implements ValidationHandler {
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
