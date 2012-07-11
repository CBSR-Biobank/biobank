package edu.ualberta.med.biobank.common.action.exception;

import java.util.Set;

import edu.ualberta.med.biobank.i18n.LString;

public class CsvImportException extends ActionException {
    private static final long serialVersionUID = 1L;

    private Set<ImportError> errors;

    public CsvImportException(Set<ImportError> errors) {
        super(null);
        addErrors(errors);
    }

    public void addErrors(Set<ImportError> errors) {
        this.errors = errors;
    }

    public Set<ImportError> getErrors() {
        return errors;
    }

    public static class ImportError implements Comparable<ImportError> {
        private final int lineNumber;
        private final LString message;

        public ImportError(int lineNumber, LString message) {
            this.lineNumber = lineNumber;
            this.message = message;
        }

        public int getLineNumber() {
            return lineNumber;
        }

        public LString getMessage() {
            return message;
        }

        @Override
        public int compareTo(ImportError ie) {
            return lineNumber - ie.lineNumber;
        }
    }

}
