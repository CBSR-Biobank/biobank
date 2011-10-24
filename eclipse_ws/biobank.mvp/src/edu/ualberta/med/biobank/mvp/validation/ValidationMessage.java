package edu.ualberta.med.biobank.mvp.validation;

public interface ValidationMessage {
    Level getLevel();

    String getMessage();
}
