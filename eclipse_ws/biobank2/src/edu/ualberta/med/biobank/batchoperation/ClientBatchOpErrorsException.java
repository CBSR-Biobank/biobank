package edu.ualberta.med.biobank.batchoperation;

import java.io.IOException;
import java.util.Set;

import org.supercsv.exception.SuperCSVException;

import edu.ualberta.med.biobank.common.action.exception.BatchOpException;

/**
 * Client side exception that can be thrown by a IBatchOpPojoReader.
 * 
 * @author Nelson Loyola
 * 
 */
public class ClientBatchOpErrorsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private Set<BatchOpException<String>> errors;

    public ClientBatchOpErrorsException(Set<BatchOpException<String>> errors) {
        addErrors(errors);
    }

    public ClientBatchOpErrorsException(SuperCSVException e) {
        super(e);
    }

    public ClientBatchOpErrorsException(IOException e) {
        super(e);
    }

    public void addErrors(Set<BatchOpException<String>> errors) {
        this.errors = errors;
    }

    public Set<BatchOpException<String>> getErrors() {
        return errors;
    }

}
