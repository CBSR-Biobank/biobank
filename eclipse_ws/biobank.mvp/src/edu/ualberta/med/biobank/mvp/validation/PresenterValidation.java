package edu.ualberta.med.biobank.mvp.validation;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gwt.user.client.ui.HasValue;

import edu.ualberta.med.biobank.mvp.validation.ValueValidation.ValueValidationBuilder;

public class PresenterValidation extends AbstractValidation {
    private final LinkedHashMap<PresenterValidation, HasValue<Boolean>> validators =
        new LinkedHashMap<PresenterValidation, HasValue<Boolean>>();

    @Override
    protected void doValidation(ValidationResultCollector collector) {
        for (Map.Entry<PresenterValidation, HasValue<Boolean>> entry : validators
            .entrySet()) {
            HasValidation validator = entry.getKey();
        }
    }

    public ConditionBuilder validate(HasValidation validator) {
        return null;
    }

    public <T> ValueValidationBuilder<T> validate(HasValue<T> value) {
        return new ValueValidationBuilder<T>();
    }
}
