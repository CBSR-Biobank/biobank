package edu.ualberta.med.biobank.mvp.presenter.validation.validator;

import java.text.MessageFormat;

import com.pietschy.gwt.pectin.client.form.validation.ValidationResultCollector;
import com.pietschy.gwt.pectin.client.form.validation.Validator;
import com.pietschy.gwt.pectin.client.form.validation.message.ErrorMessage;

public class NotEmpty implements Validator<String> {
    private static final String MESSAGE = "{0} cannot be empty";
    private final String label;

    public NotEmpty(String label) {
        this.label = label;
    }

    @Override
    public void validate(String value, ValidationResultCollector results) {
        if (value == null || value.trim().isEmpty()) {
            String message = MessageFormat.format(MESSAGE, label);
            results.add(new ErrorMessage(message));
        }
    }
}
