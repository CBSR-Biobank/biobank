package edu.ualberta.med.biobank.mvp.validation;

import java.util.Collection;

public interface ValidationResultAdder {
    /**
     * Adds a new message to the adder.
     * 
     * @param message
     *            the validation message to add.
     */
    void add(ValidationMessage message);

    void addAll(Collection<ValidationMessage> messages);
}
