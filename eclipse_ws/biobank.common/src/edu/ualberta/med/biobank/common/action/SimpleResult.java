package edu.ualberta.med.biobank.common.action;

public class SimpleResult<T> implements ActionResult {
    private static final long serialVersionUID = 1L;

    private final T result;

    public SimpleResult(T result) {
        this.result = result;
    }

    public T getResult() {
        return result;
    }
}
