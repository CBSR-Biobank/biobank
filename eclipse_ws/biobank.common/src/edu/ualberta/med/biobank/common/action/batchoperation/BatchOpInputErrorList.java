package edu.ualberta.med.biobank.common.action.batchoperation;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

import edu.ualberta.med.biobank.common.action.exception.CsvImportException;
import edu.ualberta.med.biobank.common.action.exception.CsvImportException.ImportError;
import edu.ualberta.med.biobank.i18n.LString;

/**
 * 
 * @author loyola
 * 
 */
public class BatchOpInputErrorList implements Serializable {
    private static final long serialVersionUID = 1L;

    static final int MAX_ERRORS_TO_REPORT = 50;

    final Set<ImportError> errors = new TreeSet<ImportError>();

    public void addError(int lineNumber, LString message)
        throws CsvImportException {
        ImportError importError = new ImportError(lineNumber, message);
        errors.add(importError);
        if (errors.size() > MAX_ERRORS_TO_REPORT) {
            throw new CsvImportException(errors);
        }
    }

    public boolean isEmpty() {
        return errors.isEmpty();
    }

    public Set<ImportError> getErrors() {
        return errors;
    }

}
