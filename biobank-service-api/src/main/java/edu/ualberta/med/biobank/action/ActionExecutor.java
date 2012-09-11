package edu.ualberta.med.biobank.action;

public interface ActionExecutor {
    <A extends Action<R>, R extends ActionResult> R run(A action);
}