package edu.ualberta.med.biobank.action;

public class FutureResult<R extends ActionResult>
    implements ActionResult {
    private static final long serialVersionUID = 1L;

    private R result;

    public R getResult() {
        return result;
    }

    void setResult(R result) {
        this.result = result;
    }
}
