package edu.ualberta.med.biobank.common.action.exception;

import java.util.Set;

import edu.ualberta.med.biobank.i18n.LString;

public class BatchOpErrorsException extends ActionException {
    private static final long serialVersionUID = 1L;

    private Set<BatchOpException<LString>> errors;

    public BatchOpErrorsException(Set<BatchOpException<LString>> errors) {
        super(null);
        addErrors(errors);
    }

    public void addErrors(Set<BatchOpException<LString>> errors) {
        this.errors = errors;
    }

    public Set<BatchOpException<LString>> getErrors() {
        return errors;
    }

}
