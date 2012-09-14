package edu.ualberta.med.biobank.common.action.batchoperation;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

import edu.ualberta.med.biobank.common.action.exception.BatchOpErrorsException;
import edu.ualberta.med.biobank.common.action.exception.BatchOpException;
import edu.ualberta.med.biobank.i18n.LString;

/**
 * 
 * @author loyola
 * 
 */
public class BatchOpInputErrorList implements Serializable {
    private static final long serialVersionUID = 1L;

    static final int MAX_ERRORS_TO_REPORT = 50;

    final Set<BatchOpException<LString>> errors =
        new TreeSet<BatchOpException<LString>>();

    public void addError(int lineNumber, LString message)
        throws BatchOpErrorsException {
        BatchOpException<LString> importError =
            new BatchOpException<LString>(lineNumber, message);
        errors.add(importError);
        if (errors.size() > MAX_ERRORS_TO_REPORT) {
            throw new BatchOpErrorsException(errors);
        }
    }

    public boolean isEmpty() {
        return errors.isEmpty();
    }

    public Set<BatchOpException<LString>> getErrors() {
        return errors;
    }

}
