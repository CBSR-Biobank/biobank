package edu.ualberta.med.biobank.mvp.validation;

public interface HasValidation extends HasValidationResult {
    /**
     * Runs all {@link Validator}-s and updates and returns the
     * {@link ValidationResult}.
     * <p>
     * Too see if there were errors, call
     * {@link ValidationResult#contains(Level.ERROR)}.
     * 
     * @return the {@link ValidationResult} of the validation.
     */
    ValidationResult validate();

    /**
     * Clear the {@link ValidationResult} for this validator.
     */
    void clearValidation();
}
