package edu.ualberta.med.biobank.common.batchoperation;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

import edu.ualberta.med.biobank.common.action.batchoperation.IBatchOpInputErrorList;
import edu.ualberta.med.biobank.common.action.exception.BatchOpErrorsException;
import edu.ualberta.med.biobank.common.action.exception.BatchOpException;

/**
 * Client side utility class to aid in recording errors found when parsing a CSV
 * file.
 *
 * @author Nelson Loyola
 *
 */
public class ClientBatchOpInputErrorList implements
    IBatchOpInputErrorList<String>,
    Serializable {
    private static final long serialVersionUID = 1L;

    static final int MAX_ERRORS_TO_REPORT = 50;

    final Set<BatchOpException<String>> errors =
        new TreeSet<BatchOpException<String>>();

    public ClientBatchOpInputErrorList() {

    }

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
    public void addError(int lineNumber, String message)
        throws BatchOpErrorsException {
        BatchOpException<String> importError =
            new BatchOpException<String>(lineNumber, message);
        errors.add(importError);
        if (errors.size() > MAX_ERRORS_TO_REPORT) {
            throw new ClientBatchOpErrorsException(errors);
        }
    }

    @Override
    public boolean isEmpty() {
        return errors.isEmpty();
    }

    @Override
    public Set<BatchOpException<String>> getErrors() {
        return errors;
    }

}
