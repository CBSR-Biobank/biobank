package edu.ualberta.med.biobank.batchoperation;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

import edu.ualberta.med.biobank.common.action.exception.BatchOpErrorsException;
import edu.ualberta.med.biobank.common.action.exception.BatchOpException;

public class ClientBatchOpInputErrorList implements Serializable {
    private static final long serialVersionUID = 1L;

    static final int MAX_ERRORS_TO_REPORT = 50;

    final Set<BatchOpException<String>> errors =
        new TreeSet<BatchOpException<String>>();

    public ClientBatchOpInputErrorList() {

    }

    public void addError(int lineNumber, String message)
        throws BatchOpErrorsException {
        BatchOpException<String> importError =
            new BatchOpException<String>(lineNumber, message);
        errors.add(importError);
        if (errors.size() > MAX_ERRORS_TO_REPORT) {
            throw new ClientBatchOpErrorsException(errors);
        }
    }

    public boolean isEmpty() {
        return errors.isEmpty();
    }

    public Set<BatchOpException<String>> getErrors() {
        return errors;
    }

}
