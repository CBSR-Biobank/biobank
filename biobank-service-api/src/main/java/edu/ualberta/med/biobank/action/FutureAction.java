package edu.ualberta.med.biobank.action;

public class FutureAction<A extends Action<R>, R extends ActionResult>
    implements Action<FutureResult<R>> {
    private static final long serialVersionUID = 1L;

    private final A action;
    private final FutureResult<R> result;

    public FutureAction(A action) {
        this.action = action;
        this.result = new FutureResult<R>();
    }

    public A getAction() {
        return action;
    }
    
    public FutureResult<R> getResult() {
        return result;
    }
}
