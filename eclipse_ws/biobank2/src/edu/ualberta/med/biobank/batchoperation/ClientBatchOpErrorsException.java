package edu.ualberta.med.biobank.batchoperation;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.supercsv.exception.SuperCSVException;
import org.supercsv.util.CSVContext;

import edu.ualberta.med.biobank.common.action.exception.BatchOpException;

/**
 * Client side exception that can be thrown by a IBatchOpPojoReader.
 * 
 * @author Nelson Loyola
 * 
 */
public class ClientBatchOpErrorsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final Set<BatchOpException<String>> errors =
        new HashSet<BatchOpException<String>>(0);

    public ClientBatchOpErrorsException(Set<BatchOpException<String>> errors) {
        addErrors(errors);
    }

    public ClientBatchOpErrorsException(SuperCSVException e) {
        CSVContext context = e.getCsvContext();
        int lineNumber = context.lineNumber;
        BatchOpException<String> exception =
            new BatchOpException<String>(lineNumber, e.getMessage());
        errors.add(exception);
    }

    public ClientBatchOpErrorsException(IOException e) {
        super(e);
    }

    public void addErrors(Set<BatchOpException<String>> errors) {
        this.errors.addAll(errors);
    }

    public Set<BatchOpException<String>> getErrors() {
        return errors;
    }

}
