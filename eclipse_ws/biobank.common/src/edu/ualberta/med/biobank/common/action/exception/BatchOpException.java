package edu.ualberta.med.biobank.common.action.exception;

import java.io.Serializable;

public class BatchOpException<T> implements Serializable,
    Comparable<BatchOpException<T>> {
    private static final long serialVersionUID = 1L;

    private final int lineNumber;
    private final T message;

    public BatchOpException(int lineNumber, T message) {
        this.lineNumber = lineNumber;
        this.message = message;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public T getMessage() {
        return message;
    }

    @Override
    public int compareTo(BatchOpException<T> ie) {
        return lineNumber - ie.lineNumber;
    }

}
