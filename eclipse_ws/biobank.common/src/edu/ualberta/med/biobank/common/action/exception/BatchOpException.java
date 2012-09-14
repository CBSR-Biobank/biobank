package edu.ualberta.med.biobank.common.action.exception;

public class BatchOpException<T> implements Comparable<BatchOpException<T>> {
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
