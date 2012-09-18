package edu.ualberta.med.biobank.common.action.batchoperation;

import java.util.Set;

import edu.ualberta.med.biobank.common.action.exception.BatchOpErrorsException;
import edu.ualberta.med.biobank.common.action.exception.BatchOpException;

/**
 * Interface used by classes that record errors in information originating from
 * CSV files.
 * 
 * @author Nelson Loyola
 * 
 */
public interface IBatchOpInputErrorList<T> {

    public void addError(int lineNumber, T message)
        throws BatchOpErrorsException;

    public boolean isEmpty();

    public Set<BatchOpException<T>> getErrors();

}
