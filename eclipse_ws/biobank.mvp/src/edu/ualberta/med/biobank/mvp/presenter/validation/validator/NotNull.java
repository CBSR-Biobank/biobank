package edu.ualberta.med.biobank.mvp.presenter.validation.validator;

import java.text.MessageFormat;

import com.pietschy.gwt.pectin.client.form.validation.ValidationResultCollector;
import com.pietschy.gwt.pectin.client.form.validation.Validator;
import com.pietschy.gwt.pectin.client.form.validation.message.ErrorMessage;

public class NotNull implements Validator<Object> {
    private static final String MESSAGE = "{0} is required";
    private String label;

    public NotNull(String label) {
        this.label = label;
    }

    public void validate(Object value, ValidationResultCollector results) {
        if (value == null) {
            String message = MessageFormat.format(MESSAGE, label);
            results.add(new ErrorMessage(message));
        }
    }
}
