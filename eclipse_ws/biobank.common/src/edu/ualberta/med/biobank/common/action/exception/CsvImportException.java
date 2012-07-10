package edu.ualberta.med.biobank.common.action.exception;

import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.i18n.LString;

public class CsvImportException extends ActionException {
    private static final long serialVersionUID = 1L;

    private final Set<ImportError> errors = new HashSet<>(0);

    public CsvImportException() {
        super(null);
    }

    public void addError(int lineNumber, LString message) {
        ImportError importError = new ImportError();
        importError.lineNumber = lineNumber;
        importError.message = message;
        errors.add(importError);

    }

    private static class ImportError {
        int lineNumber;
        LString message;
    }

}
