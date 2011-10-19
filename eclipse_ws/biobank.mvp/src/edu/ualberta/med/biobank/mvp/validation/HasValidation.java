package edu.ualberta.med.biobank.mvp.validation;

public interface HasValidation extends HasValidationResult {
    /**
     * Runs all {@link Validator}-s and updates and returns the
     * {@link ValidationResult}.
     * 
     * @return the {@link ValidationResult} of the validation.
     */
    ValidationResult validate();

    /**
     * Clear the {@link ValidationResult} for this validator.
     */
    void clear();
}
