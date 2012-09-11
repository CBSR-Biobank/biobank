package edu.ualberta.med.biobank.common.action.exception;

import java.util.Set;

import edu.ualberta.med.biobank.i18n.LString;

public class CsvException extends ActionException {
    private static final long serialVersionUID = 1L;

    private Set<CsvError> errors;

    public CsvException(Set<CsvError> errors) {
        super(null);
        addErrors(errors);
    }

    public void addErrors(Set<CsvError> errors) {
        this.errors = errors;
    }

    public Set<CsvError> getErrors() {
        return errors;
    }

    public static class CsvError implements Comparable<CsvError> {
        private final int lineNumber;
        private final LString message;

        public CsvError(int lineNumber, LString message) {
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
        public int compareTo(CsvError ie) {
            return lineNumber - ie.lineNumber;
        }
    }

}
