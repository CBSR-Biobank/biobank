package edu.ualberta.med.biobank.common.action.csv;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

import edu.ualberta.med.biobank.common.action.exception.CsvException;
import edu.ualberta.med.biobank.common.action.exception.CsvException.CsvError;
import edu.ualberta.med.biobank.i18n.LString;

/**
 * 
 * @author loyola
 * 
 */
public class CsvErrorList implements Serializable {
    private static final long serialVersionUID = 1L;

    static final int MAX_ERRORS_TO_REPORT = 50;

    final Set<CsvError> errors = new TreeSet<CsvError>();

    public void addError(int lineNumber, LString message)
        throws CsvException {
        CsvError importError = new CsvError(lineNumber, message);
        errors.add(importError);
        if (errors.size() > MAX_ERRORS_TO_REPORT) {
            throw new CsvException(errors);
        }
    }

    public boolean isEmpty() {
        return errors.isEmpty();
    }

    public Set<CsvError> getErrors() {
        return errors;
    }

}