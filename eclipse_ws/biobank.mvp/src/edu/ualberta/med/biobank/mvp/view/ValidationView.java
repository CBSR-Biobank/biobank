package edu.ualberta.med.biobank.mvp.view;

import edu.ualberta.med.biobank.mvp.validation.ValidationResult;

public interface ValidationView extends BaseView {
    void setValidationResult(ValidationResult validationResult);
}
