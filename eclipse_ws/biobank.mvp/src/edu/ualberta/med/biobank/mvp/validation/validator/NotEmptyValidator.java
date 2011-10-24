package edu.ualberta.med.biobank.mvp.validation.validator;

import edu.ualberta.med.biobank.mvp.validation.Level;
import edu.ualberta.med.biobank.mvp.validation.ValidationMessage;
import edu.ualberta.med.biobank.mvp.validation.ValidationResultCollector;
import edu.ualberta.med.biobank.mvp.validation.Validator;

public class NotEmptyValidator implements Validator<String> {
    private final String field;

    public NotEmptyValidator(String field) {
        this.field = field;
    }

    @Override
    public void validate(String value, ValidationResultCollector results) {
        if (value == null || value.isEmpty()) {
            // TODO: this is a terrible ValidationMessage, replace!
            results.add(new ValidationMessage() {
                @Override
                public Level getLevel() {
                    return Level.ERROR;
                }

                @Override
                public String getMessage() {
                    return "HEYYYYYYYYYYY!";
                }
            });
        }
    }

}
