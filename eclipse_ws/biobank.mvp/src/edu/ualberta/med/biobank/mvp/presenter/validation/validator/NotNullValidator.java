package edu.ualberta.med.biobank.mvp.presenter.validation.validator;

import java.text.MessageFormat;

import com.pietschy.gwt.pectin.client.form.validation.ValidationResultCollector;
import com.pietschy.gwt.pectin.client.form.validation.Validator;
import com.pietschy.gwt.pectin.client.form.validation.message.ErrorMessage;

public class NotNullValidator implements Validator<Object> {  
    @SuppressWarnings("nls")
    private static final String MESSAGE = "{0} is required";
    private final String label;

    public NotNullValidator(String label) {
        this.label = label;
    }

    @Override
    public void validate(Object value, ValidationResultCollector results) {
        if (value == null) {
            String message = MessageFormat.format(MESSAGE, label);
            results.add(new ErrorMessage(message));
        }
    }
}
