package edu.ualberta.med.biobank.common.action.csvimport;

import java.io.Serializable;

public class CsvInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private int lineNumber;

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

}
