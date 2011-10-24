package edu.ualberta.med.biobank.mvp.validation;

import edu.ualberta.med.biobank.mvp.event.ui.HasValidationHandlers;

public interface HasValidationResult extends HasValidationHandlers {
    ValidationResult getValidationResult();
}
