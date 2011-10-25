package edu.ualberta.med.biobank.mvp.validation.validator;

import edu.ualberta.med.biobank.mvp.validation.ValidationResultCollector;
import edu.ualberta.med.biobank.mvp.validation.Validator;

public class NotNullValidator implements Validator {
    public NotNullValidator(String fieldName) {
    }

    @Override
    public void validate(Object value, ValidationResultCollector resultCollector) {
        // TODO Auto-generated method stub

    }
}
