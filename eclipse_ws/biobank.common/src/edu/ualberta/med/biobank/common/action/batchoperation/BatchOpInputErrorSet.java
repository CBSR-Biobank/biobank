package edu.ualberta.med.biobank.common.action.batchoperation;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

import edu.ualberta.med.biobank.common.action.exception.BatchOpErrorsException;
import edu.ualberta.med.biobank.common.action.exception.BatchOpException;
import edu.ualberta.med.biobank.i18n.LString;

/**
 * Server side utility class to aid in recording errors found when interpreting
 * CSV pojos.
 *
 * @author Nelson Loyola
 *
 */
public class BatchOpInputErrorSet implements IBatchOpInputErrorList<LString>,
    Serializable {
    private static final long serialVersionUID = 1L;

    static final int MAX_ERRORS_TO_REPORT = 50;

    final Set<BatchOpException<LString>> errors =
        new TreeSet<BatchOpException<LString>>();

    /**
     * Used to record an individual error in the CSV file. Note that a single
     * line in the CSV file can have multiple errors.
     *
     * @param lineNumber
     * @param message
     * @throws BatchOpErrorsException If the number of errors exceeds
     *             MAX_ERRORS_TO_REPORT then this exception is thrown.
     */
    @Override
    public void addError(int lineNumber, LString message) throws BatchOpErrorsException {
        BatchOpException<LString> importError = new BatchOpException<LString>(lineNumber, message);
        errors.add(importError);
        if (errors.size() > MAX_ERRORS_TO_REPORT) {
            throw new BatchOpErrorsException(errors);
        }
    }

    public void addAll(BatchOpInputErrorSet subset) {
        if (subset == null) return;
        errors.addAll(subset.errors);
    }

    @Override
    public boolean isEmpty() {
        return errors.isEmpty();
    }

    @Override
    public Set<BatchOpException<LString>> getErrors() {
        return errors;
    }

}
