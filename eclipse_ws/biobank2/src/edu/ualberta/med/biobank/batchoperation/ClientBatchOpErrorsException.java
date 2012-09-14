package edu.ualberta.med.biobank.batchoperation;

import java.util.Set;

import edu.ualberta.med.biobank.common.action.exception.BatchOpException;

public class ClientBatchOpErrorsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private Set<BatchOpException<String>> errors;

    public ClientBatchOpErrorsException(Set<BatchOpException<String>> errors) {
        addErrors(errors);
    }

    public void addErrors(Set<BatchOpException<String>> errors) {
        this.errors = errors;
    }

    public Set<BatchOpException<String>> getErrors() {
        return errors;
    }

}
